/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.schemas.biopax;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.assembly.CPathIdFilter;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.rdf.RdfValidator;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.xml.XmlUtil;
import org.mskcc.pathdb.util.ExternalReferenceUtil;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Imports BioPAX Data into cPath.
 *
 * @author Ethan Cerami.
 */
public class ImportBioPaxToCPath {
    private HashMap idMap = new HashMap();
    private ArrayList resourceList;
    private ArrayList cPathRecordList;
    private ArrayList oldIdList;
    private BioPaxUtil bpUtil;
    private ProgressMonitor pMonitor;
    private ImportSummary importSummary;

    /**
     * Adds BioPAX Data to cPath.
     *
     * @param xml      BioPAX XML String.
     * @param pMonitor ProgressMonitor Object.
     * @return ImportSummary Object.
     * @throws ImportException Error Importing BioPAX Data.
     */
    public ImportSummary addRecord(String xml, ProgressMonitor pMonitor)
            throws ImportException {
        this.pMonitor = pMonitor;
        this.importSummary = new ImportSummary();
        try {
            validateRdf(new StringReader(xml));
            massageBioPaxData(new StringReader(xml));
            storeRecords();
            storeLinks();
        } catch (IOException e) {
            throw new ImportException(e);
        } catch (DaoException e) {
            throw new ImportException(e);
        } catch (JDOMException e) {
            throw new ImportException(e);
        } catch (SAXException e) {
            throw new ImportException(e);
        }
        return importSummary;
    }

    /**
     * Validates RDF Data.
     */
    private void validateRdf(Reader reader) throws IOException, SAXException,
            ImportException {
        RdfValidator rdfValidator = new RdfValidator(reader);
        boolean hasErrors = rdfValidator.hasErrorsOrWarnings();
        pMonitor.setCurrentMessage
                ("Validating BioPAX File with RDF Validator...");
        if (hasErrors) {
            pMonitor.setCurrentMessage(rdfValidator.getReadableErrorList());
            throw new ImportException("RDF Validation Errors");
        }
        pMonitor.setCurrentMessage("   --->  BioPAX RDF is Valid");
    }

    /**
     * Prepares data via BioPaxUtil and TransformBioPaxToCPathRecords.
     *
     * @param reader Reader Object.
     */
    private void massageBioPaxData(Reader reader) throws ImportException,
            DaoException, IOException, JDOMException {

        bpUtil = new BioPaxUtil(reader, pMonitor);

        //  Check for Errors in BioPAX Transformation
        ArrayList errorList = bpUtil.getErrorList();
        if (errorList != null && errorList.size() > 0) {
            for (int i = 0; i < errorList.size(); i++) {
                String errMsg = (String) errorList.get(i);
                pMonitor.setCurrentMessage(errMsg);
            }
            throw new ImportException("BioPAX Import Error");
        }

        resourceList = bpUtil.getTopLevelComponentList();

        TransformBioPaxToCPathRecords transformer =
                new TransformBioPaxToCPathRecords(resourceList);
        cPathRecordList = transformer.getcPathRecordList();
        oldIdList = transformer.getIdList();
    }

    /**
     * Stores RDF Resource Placeholders (without XML) to MySQL.
     */
    private void storeRecords() throws DaoException, JDOMException {
        DaoCPath dao = DaoCPath.getInstance();
        DaoExternalLink externalLinker = DaoExternalLink.getInstance();
        pMonitor.setCurrentMessage("Storing records to MySQL:");
        pMonitor.setMaxValue(cPathRecordList.size());
        for (int i = 0; i < cPathRecordList.size(); i++) {
            CPathRecord record = (CPathRecord) cPathRecordList.get(i);
            String oldId = (String) oldIdList.get(i);

            //  Before we save a new record, check to see if the record
            //  already exists in the database.
            Element resource = (Element) resourceList.get(i);
            ExternalReference refs[] =
                        bpUtil.extractExternalReferences(resource);
            long cPathId = lookUpRecord (record, refs);

            //  If record does not exist, save it.
            if (cPathId == -1) {
                cPathId = dao.addRecord(record.getName(),
                    record.getDescription(),
                    record.getNcbiTaxonomyId(),
                    record.getType(),
                    record.getSpecificType(),
                    XmlRecordType.BIO_PAX,
                    "[PLACE_HOLDER]");

                //  Store External Links
                externalLinker.addMulipleRecords(cPathId, refs, false);

                if (record.getType().equals(CPathRecordType.PATHWAY)) {
                    importSummary.incrementNumPathwaysSaved();
                } else if (record.getType().equals
                        (CPathRecordType.INTERACTION)) {
                    importSummary.incrementNumInteractionsSaved();
                } else {
                    importSummary.incrementNumPhysicalEntitiesSaved();
                }
            }

            //  Store a mapping between the old ID and the new ID
            idMap.put(oldId, new Long(cPathId));

            //  Store cPath ID directly, for later reference
            record.setId(cPathId);

            pMonitor.incrementCurValue();
            ConsoleUtil.showProgress(pMonitor);
        }
    }

    /**
     * Based on External References, determine if this record already exists.
     * If the record exists, its cPath ID will be returned.  Otherwise,
     * this method returns the value -1.
     */
    private long lookUpRecord (CPathRecord record, ExternalReference refs[])
        throws DaoException {
        DaoExternalLink daoExternalLinker = DaoExternalLink.getInstance();

        if (record.getType().equals(CPathRecordType.INTERACTION)
            || record.getType().equals(CPathRecordType.PATHWAY)) {

            //  Filter to INTERACTION_PATHWAY_UNIFICATION XREFs Only.
            ExternalReference[] filteredRefs =
                    ExternalReferenceUtil.filterByReferenceType
                    (refs, ReferenceType.INTERACTION_PATHWAY_UNIFICATION);
            if (filteredRefs.length > 0) {
                ArrayList records = daoExternalLinker.lookUpByExternalRefs
                        (filteredRefs);
                if (records.size() > 0) {
                    CPathRecord existingRecord = (CPathRecord) records.get(0);
                    System.out.println ("Hit:  " + existingRecord.getName());
                    return existingRecord.getId();
                }
            }
        }
        return -1;
    }

    /**
     * 1)  Update all Internal Links to point to correct cPath IDs.
     * 2)  Stores the newly modified XML to MySQL.
     * 3)  Store Internal Links to cPath.
     * 4)  Store New Organisms.
     */
    private void storeLinks() throws JDOMException, IOException,
            DaoException {
        //  Update all JDOM Elements in Resource List to reference new
        //  cPath IDs.
        UpdateRdfLinks linker = new UpdateRdfLinks();
        linker.updateInternalLinks
                (resourceList, idMap, CPathIdFilter.CPATH_PREFIX);

        //  1)  Store the newly modified XML to MySQL
        //  2)  Store the Internal Links to MySQL
        //  3)  Store the External Links to MySQL
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoInternalLink internalLinker = new DaoInternalLink();
        DaoExternalLink externalLinker = DaoExternalLink.getInstance();

        pMonitor.setCurrentMessage("Storing Internal Links to MySQL:");
        pMonitor.setMaxValue(cPathRecordList.size());
        for (int i = 0; i < cPathRecordList.size(); i++) {
            CPathRecord record = (CPathRecord) cPathRecordList.get(i);
            Element resource = (Element) resourceList.get(i);
            String xml = XmlUtil.serializeToXml(resource);
            daoCPath.updateXml(record.getId(), xml);

            //  Store Internal Links
            long internalLinks[] = linker.getInternalLinks(record.getId());
            if (internalLinks != null && internalLinks.length > 0) {
                internalLinker.addRecords(record.getId(), internalLinks);
            }

            pMonitor.incrementCurValue();
            ConsoleUtil.showProgress(pMonitor);

            //  Conditionally Store New Organism
            conditionallySaveOrganism(record, resource);

        }
    }

    /**
     * Saves Organism Data, if it is new.
     */
    private void conditionallySaveOrganism(CPathRecord record, Element resource)
            throws DaoException, JDOMException {
        if (record.getNcbiTaxonomyId() != CPathRecord.TAXONOMY_NOT_SPECIFIED) {
            DaoOrganism daoOrganism = new DaoOrganism();
            if (!daoOrganism.recordExists(record.getNcbiTaxonomyId())) {
                XPath xpath = XPath.newInstance
                        ("bp:ORGANISM/bp:bioSource/bp:NAME");
                xpath.addNamespace("bp", resource.getNamespaceURI());
                Element speciesName = (Element)
                        xpath.selectSingleNode(resource);
                if (speciesName != null) {
                    daoOrganism.addRecord(record.getNcbiTaxonomyId(),
                            speciesName.getTextNormalize(), null);
                }
            }
        }
    }
}