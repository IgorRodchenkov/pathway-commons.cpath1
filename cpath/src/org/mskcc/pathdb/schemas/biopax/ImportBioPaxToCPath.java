// $Id: ImportBioPaxToCPath.java,v 1.41 2009-04-07 17:13:38 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.Reference;
import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.assembly.CPathIdFilter;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.references.BackgroundReferenceService;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.ExternalReferenceUtil;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.xml.XmlUtil;
import org.mskcc.pathdb.util.rdf.RdfQuery;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Imports BioPAX Data into cPath.
 *
 * @author Ethan Cerami.
 */
public class ImportBioPaxToCPath {
    private HashMap idMap = new HashMap();
    private ArrayList cPathRecordList = new ArrayList();
    private ArrayList newRecordFlags;
    private BioPaxUtil bpUtil;
    private ProgressMonitor pMonitor;
    private boolean strictValidation;
    private ImportSummary importSummary;
    private long snapshotId;
    private String xml;
	private boolean importUniprotAnnotation;

	/**
     * Constructor().
     *
     * @param importUniprotAnnotation boolean
	 */
	public ImportBioPaxToCPath(boolean importUniprotAnnotation) {
		this.importUniprotAnnotation = importUniprotAnnotation;
	}

    /**
     * Adds BioPAX Data to cPath.
     *
     * @param xml                       BioPAX XML String.
     * @param snapshotId                External DB Snapshot ID.
     * @param strictValidation          Performs Strict Validation.
     * @param pMonitor                  ProgressMonitor Object.
     * @return ImportSummary Object.
     * @throws ImportException Error Importing BioPAX Data.
     */
    public ImportSummary addRecord (String xml, long snapshotId, boolean strictValidation,
            ProgressMonitor pMonitor) throws ImportException {
        this.xml = xml;
        this.snapshotId = snapshotId;
        this.pMonitor = pMonitor;
        this.strictValidation = strictValidation;
        this.importSummary = new ImportSummary();
        this.newRecordFlags = new ArrayList();
        try {
            massageBioPaxData(new StringReader(xml));
            storeRecords();
            storeLinks();
            storeEvidence();
        } catch (IOException e) {
            throw new ImportException(e);
        } catch (DaoException e) {
            throw new ImportException(e);
        } catch (JDOMException e) {
            throw new ImportException(e);
        } catch (ExternalDatabaseNotFoundException e) {
            throw new ImportException(e);
        }
        return importSummary;
    }

    /**
     * Prepares data via BioPaxUtil and TransformBioPaxToCPathRecords.
     *
     * @param reader Reader Object.
     */
    private void massageBioPaxData (Reader reader) throws ImportException,
            DaoException, IOException, JDOMException {

        bpUtil = new BioPaxUtil(reader, strictValidation, pMonitor);

        //  Check for Errors in BioPAX Transformation
        ArrayList errorList = bpUtil.getErrorList();
        if (errorList != null && errorList.size() > 0) {
            for (int i = 0; i < errorList.size(); i++) {
                String errMsg = (String) errorList.get(i);
                pMonitor.setCurrentMessage(errMsg);
            }
            throw new ImportException("BioPAX Import Error");
        }
    }

    /**
     * Stores RDF Resource Placeholders (without XML) to MySQL.
     */
    private void storeRecords () throws DaoException, JDOMException,
            ExternalDatabaseNotFoundException, IOException {
        pMonitor.setCurrentMessage("Storing Pathway Records to Database:");
        pMonitor.setMaxValue(bpUtil.getNumPathways());
        for (int i = 0; i < bpUtil.getNumPathways(); i++) {
            Element pathway = bpUtil.getPathway(i);
            storeRecord (pathway);
            pathway = null;
        }
        pMonitor.setCurrentMessage("Storing Interaction Records to Database:");
        pMonitor.setMaxValue(bpUtil.getNumInteractions());
        for (int i = 0; i < bpUtil.getNumInteractions(); i++) {
            Element interaction = bpUtil.getInteraction(i);
            storeRecord (interaction);
            interaction = null;
        }
        pMonitor.setCurrentMessage("Storing Physical Entity Records to Database:");
        pMonitor.setMaxValue(bpUtil.getNumPhysicalEntities());
        for (int i = 0; i < bpUtil.getNumPhysicalEntities(); i++) {
            Element pe = bpUtil.getPhysicalEntity(i);
            storeRecord (pe);
            pe = null;
        }
    }

    private void storeRecord (Element resource) throws DaoException, JDOMException, IOException,
            ExternalDatabaseNotFoundException {
        TransformBioPaxToCPathRecords transformer = new TransformBioPaxToCPathRecords();
        DaoCPath dao = DaoCPath.getInstance();
        DaoExternalLink externalLinker = DaoExternalLink.getInstance();

        long tempSnapshotId = snapshotId;

        CPathRecord record = transformer.createCPathRecord(resource);
        String oldId = transformer.getRdfId(resource);

        //  Special case:  if this is a physical entity, but not a complex,
        //  store original XML, untouched.
        long physicalEntitySourceId = -1;
        if (record.getType() == CPathRecordType.PHYSICAL_ENTITY
                && !record.getSpecificType().equals(BioPaxConstants.COMPLEX)) {
            physicalEntitySourceId = storePhysicalEntityUntouched(record, resource,
                    tempSnapshotId);
        }

        //  Before we save a new merged record, check to see if the record
        //  already exists in the database.  Existing Records can
        //  be determined via Unification Xrefs.
        ExternalReference unificationXrefs[] = bpUtil.extractUnificationXrefs(resource);
        long cPathId = lookUpRecord(unificationXrefs);

        //  If record does not exist, save it.
        if (cPathId == -1) {
            boolean autoGenerated = false;

            // Don't store snaphot reference for proteins or small molecules.
            if (record.getType() == CPathRecordType.PHYSICAL_ENTITY
                    && !record.getSpecificType().equals(BioPaxConstants.COMPLEX)) {
                tempSnapshotId = -1;
                autoGenerated = true;
            }
            cPathId = dao.addRecord(record.getName(),
                    record.getDescription(),
                    record.getNcbiTaxonomyId(),
                    record.getType(),
                    record.getSpecificType(),
                    XmlRecordType.BIO_PAX,
                    "[PLACE_HOLDER]", tempSnapshotId, autoGenerated);

            //  Find any LinkOut References, such as Entrez Gene IDs or Affymetrix IDs
            ExternalReference xrefs[] = bpUtil.extractXrefs(resource);
            ExternalReference linkOutRefs[] = queryLinkOutService
                    (xrefs);
            appendNewRelationshipXRefs(linkOutRefs, resource);

            //  Find any Unification References, such as other Protein IDs.
            ExternalReference newUnificationRefs[] = queryUnificationService
                    (unificationXrefs);
            appendNewUnificationsXRefs(newUnificationRefs, resource);

			// Find any Interaction - Unification References.
			ExternalReference interactionUnificationRefs[] =
				(record.getType().equals(CPathRecordType.INTERACTION)) ?
				bpUtil.extractInteractionUnificationXrefs(resource) : null;

            //  Merge all references into unified reference list
            ExternalReference unifiedRefs[] =
                    ExternalReferenceUtil.createUnifiedList(xrefs,
                            linkOutRefs);
            unifiedRefs = ExternalReferenceUtil.createUnifiedList(unifiedRefs,
                newUnificationRefs);
            unifiedRefs = (interactionUnificationRefs != null) ?
				ExternalReferenceUtil.createUnifiedList(unifiedRefs, interactionUnificationRefs) :
				unifiedRefs;

            //  Validate All Refs
            externalLinker.validateExternalReferences(unifiedRefs, strictValidation);

            //  Store All Refs
            externalLinker.addMulipleRecords(cPathId, unifiedRefs, false);

            // store publication xrefs
            List<Reference> references = bpUtil.extractPublicationXrefs(resource, "biopax:XREF/biopax:publicationXref");
            if (references.size() > 0) {
                DaoReference daoReference = new DaoReference();
                DaoExternalDb daoExternalDb = new DaoExternalDb();
                for (Reference ref : references) {
                    ExternalDatabaseRecord dbRecord = daoExternalDb.getRecordByName(ref.getDatabase());
                    if (daoReference.getRecord(ref.getId(), dbRecord.getId()) == null) {
                        daoReference.addReference(ref);
                    }
                }
            }

            //  Store the XML Assembly
            String xml = XmlUtil.serializeToXml(resource);
            dao.updateXml(cPathId, xml);

            //  Add to Record List
            cPathRecordList.add(record);
            newRecordFlags.add(Boolean.TRUE);

            //  Store cPath ID directly, for later reference
            record.setId(cPathId);

            if (record.getType().equals(CPathRecordType.PATHWAY)) {
                importSummary.incrementNumPathwaysSaved();
            } else if (record.getType().equals
                    (CPathRecordType.INTERACTION)) {
                importSummary.incrementNumInteractionsSaved();
            } else {
                importSummary.incrementNumPhysicalEntitiesSaved();
            }
        } else {
            CPathRecord existingRecord = new CPathRecord();
            existingRecord.setId(cPathId);
            cPathRecordList.add(existingRecord);
            newRecordFlags.add(Boolean.FALSE);
            if (record.getType().equals(CPathRecordType.PATHWAY)) {
                importSummary.incrementNumPathwaysFound();
            } else if (record.getType().equals
                    (CPathRecordType.INTERACTION)) {
                importSummary.incrementNumInteractionsFound();
            } else {
                importSummary.incrementNumPhysicalEntitiesFound();
            }
        }

        //  Special case:  create a link between the untouched, source physical entity
        //  record and the merged, cPath generated physical entiry record
        if (physicalEntitySourceId >= 0) {
            DaoSourceTracker daoSourceTracker = new DaoSourceTracker();
            daoSourceTracker.addRecord(physicalEntitySourceId, cPathId);
        }

        //  Store a mapping between the old ID and the new ID
        idMap.put(oldId, new Long(cPathId));

        pMonitor.incrementCurValue();
        ConsoleUtil.showProgress(pMonitor);
    }

    /**
     * Stores Original, Untouched Physical Entity Record.
     */
    private long storePhysicalEntityUntouched (CPathRecord record,
            Element assembledResource, long snapshotId)
            throws DaoException, IOException {
        DaoCPath dao = DaoCPath.getInstance();
        String xml = XmlUtil.serializeToXml(assembledResource);
        long cPathId = dao.addRecord(record.getName(), record.getDescription(),
                record.getNcbiTaxonomyId(), record.getType(),
                record.getSpecificType(), XmlRecordType.BIO_PAX,
                xml, snapshotId, false);
        return cPathId;
    }

    /**
     * Appends New Relationship XRefs to the Existing XML Resource.
     */
    private void appendNewRelationshipXRefs (ExternalReference linkOutRefs[], Element e)
            throws DaoException {
        if (linkOutRefs != null && linkOutRefs.length > 0) {
            for (int i = 0; i < linkOutRefs.length; i++) {
                BioPaxGenerator.appendRelationshipXref
                        (linkOutRefs[i], e);
            }
        }
    }

    /**
     * Appends New Unifications XRefs to the Existing XML Resource.
     */
    private void appendNewUnificationsXRefs (ExternalReference linkOutRefs[], Element e)
            throws DaoException {
        if (linkOutRefs != null && linkOutRefs.length > 0) {
            for (int i = 0; i < linkOutRefs.length; i++) {
                BioPaxGenerator.appendUnificationXref (linkOutRefs[i], e);
            }
        }
    }


    /**
     * Based on Unification XRefs, determine if this record already exists.
     * If the record exists, its cPath ID will be returned.  Otherwise,
     * this method returns the value -1.
     */
    private long lookUpRecord (ExternalReference unificationXrefs[])
            throws DaoException {
        DaoExternalLink daoExternalLinker = DaoExternalLink.getInstance();

        if (unificationXrefs != null && unificationXrefs.length > 0) {
            ArrayList records = daoExternalLinker.lookUpByExternalRefs
                    (unificationXrefs);
            if (records.size() > 0) {
                CPathRecord existingRecord = (CPathRecord) records.get(0);
                return existingRecord.getId();
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
    private void storeLinks () throws JDOMException, IOException, DaoException {
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoInternalLink internalLinker = new DaoInternalLink();

        pMonitor.setCurrentMessage("Storing Internal Links to Database:");
        pMonitor.setMaxValue(cPathRecordList.size());
        for (int i = 0; i < cPathRecordList.size(); i++) {

            Boolean newRecord = (Boolean) newRecordFlags.get(i);

            //  Only do this for new records;  existing records stay as is.
            if (newRecord.booleanValue()) {

                //  Get the Current XML
                CPathRecord localRecord = (CPathRecord) cPathRecordList.get(i);
                CPathRecord record = daoCPath.getRecordById(localRecord.getId());
                String xml = record.getXmlContent();
                SAXBuilder builder = new SAXBuilder();
                Document bioPaxDoc = builder.build(new StringReader(xml));
                Element resource = bioPaxDoc.getRootElement();

                //  Update XML to reference new cPath IDs.
                UpdateRdfLinks linker = new UpdateRdfLinks();
                linker.updateInternalLinks (resource, idMap, CPathIdFilter.CPATH_PREFIX);

                //  Append a Unification XREF for cPath
                ExternalReference cpathRef = new ExternalReference
                        (CPathConstants.CPATH_DB_NAME,
                                Long.toString(record.getId()));
                BioPaxGenerator.appendUnificationXref(cpathRef, resource);

                //  Serialize XML and store updated XML
                xml = XmlUtil.serializeToXml(resource);
                daoCPath.updateXml(record.getId(), xml);

                //  Store Internal Links
                long internalLinks[] = linker.getInternalLinks(record.getId());
                if (internalLinks != null && internalLinks.length > 0) {
                    internalLinker.addRecords(record.getId(), internalLinks);
                }

                //  Conditionally Store New Organism
                conditionallySaveOrganism(record, resource);
            }
            pMonitor.incrementCurValue();
            ConsoleUtil.showProgress(pMonitor);
        }
    }

	/**
	 * Stores evidence information.
	 */
	private void storeEvidence() throws JDOMException, DaoException {
        pMonitor.setCurrentMessage("Extracting PubMed References...");

        //  First, do a quick string match.  If there are no matches for EVIDENCE
        //  we avoid the lengthy RDF Query.
        if (xml.contains("EVIDENCE")) {
            // store publication xrefs
            Element root = bpUtil.getRootElement();
            org.mskcc.pathdb.util.rdf.RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());
            List<Reference> references = new ArrayList<Reference>();
            List<Element> evidences = evidences = rdfQuery.getNodes(root, "EVIDENCE");
            if (evidences != null) {
                for (Element evidence : evidences) {
                    references.addAll(bpUtil.extractPublicationXrefs(evidence, "biopax:XREF/biopax:publicationXref"));
                }
            }
            evidences = rdfQuery.getNodes(root, "*/EVIDENCE");
            if (evidences != null) {
                for (Element evidence : evidences) {
                    references.addAll(bpUtil.extractPublicationXrefs(evidence, "biopax:evidence/biopax:XREF/biopax:publicationXref"));
                }
            }

            if (references.size() > 0) {
                pMonitor.setCurrentMessage("Storing Evidence and PubMed References to Database:");
                pMonitor.setMaxValue(references.size());
                DaoReference daoReference = new DaoReference();
                DaoExternalDb daoExternalDb = new DaoExternalDb();
                for (Reference ref : references) {
                    ExternalDatabaseRecord dbRecord = daoExternalDb.getRecordByName(ref.getDatabase());
                    if (daoReference.getRecord(ref.getId(), dbRecord.getId()) == null) {
                        ref.setIsEvidenceReference(true);
                        daoReference.addReference(ref);
                    }
                    pMonitor.incrementCurValue();
                    ConsoleUtil.showProgress(pMonitor);
                }
            }
        }
    }

    /**
     * Saves Organism Data, if it is new.
     */
    private void conditionallySaveOrganism (CPathRecord record, Element resource)
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
										  speciesName.getTextNormalize(), null, !importUniprotAnnotation);
                }
            }
			else {
				Organism org = daoOrganism.getOrganismByTaxonomyId(record.getNcbiTaxonomyId());
				// we are not importing uniprot annotation and current fromPathwayOrInteraction
				// flag is false - update organism record and set fromPathwayOrInteraction to true
				if (!importUniprotAnnotation && !org.fromPathwayOrInteraction()) {
					daoOrganism.updateFromPathwayOrInteraction(record.getNcbiTaxonomyId(), true);
				}
			}
        }
    }

    /**
     * Queries the Background Reference Service for a list of LINK_OUT
     * References.
     *
     * @param unificationRefs Array of External Reference Objects.
     * @return Array of External Reference Objects.
     */
    private ExternalReference[] queryLinkOutService (ExternalReference[]
            unificationRefs) throws DaoException {

        //  Only execute query if we have existing references.
        if (unificationRefs != null && unificationRefs.length > 0) {
            BackgroundReferenceService refService =
                    new BackgroundReferenceService();
            ArrayList linkOutRefs =
                    refService.getLinkOutReferences(unificationRefs);

            //  Return the complete linkout list.
            return (ExternalReference[]) linkOutRefs.toArray
                    (new ExternalReference[linkOutRefs.size()]);
        } else {
            return null;
        }
    }

    /**
     * Queries the Background Reference Service for a list of UNIFICATION
     * References.
     *
     * @param unificationRefs Array of External Reference Objects.
     * @return Array of External Reference Objects.
     */
    private ExternalReference[] queryUnificationService (ExternalReference[]
            unificationRefs) throws DaoException {

        //  Only execute query if we have existing references.
        if (unificationRefs != null && unificationRefs.length > 0) {
            BackgroundReferenceService refService =
                    new BackgroundReferenceService();
            HashSet normalizedUnificationRefs =
                    refService.createNormalizedXRefSet(unificationRefs);
            ArrayList newUnificationRefs =
                    refService.getUnificationReferences(unificationRefs);

            //  Return the new unification xrefs only
            newUnificationRefs.removeAll(normalizedUnificationRefs);
            return (ExternalReference[]) newUnificationRefs.toArray
                    (new ExternalReference[newUnificationRefs.size()]);
        } else {
            return null;
        }
    }
}
