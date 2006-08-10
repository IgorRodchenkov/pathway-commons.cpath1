// $Id: TransformBioPaxToCPathRecords.java,v 1.8 2006-08-10 21:23:27 cerami Exp $
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

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.util.rdf.RdfConstants;

import java.util.ArrayList;

/**
 * Transforms BioPAX RDF Elements into CPathRecord Objects, in preparation
 * for submission to database.
 *
 * @author Ethan Cerami
 */
public class TransformBioPaxToCPathRecords {
    private ArrayList resourceList;
    private ArrayList cPathRecordList;
    private ArrayList idList;
    private BioPaxConstants bioPaxConstants;

    /**
     * Constructor.
     *
     * @param resourceList ArrayList of JDOM Element Objects.
     * @throws JDOMException JDOM/XML Error.
     */
    public TransformBioPaxToCPathRecords(ArrayList resourceList)
            throws JDOMException {
        this.resourceList = resourceList;
        this.bioPaxConstants = new BioPaxConstants();
        this.cPathRecordList = new ArrayList();
        this.idList = new ArrayList();
        createCPathRecords();
    }

    /**
     * Gets List of cPath Records. This list appears in the exact same order
     * as the ID List.
     *
     * @return ArrayList of cPath Record Objects.
     */
    public ArrayList getcPathRecordList() {
        return cPathRecordList;
    }

    /**
     * Gets List of RDF Ids.  This list appears in the exact same order
     * as the cPath Record List.
     *
     * @return ArrayList of String Objects.
     */
    public ArrayList getIdList() {
        return idList;
    }

    /**
     * Creates cPath Record Objects for all top-level resources.
     */
    private void createCPathRecords() throws JDOMException {
        for (int i = 0; i < resourceList.size(); i++) {

            //  Extract Next Resource
            Element resource = (Element) resourceList.get(i);
            String bioPaxNamespaceUri = resource.getNamespaceURI();
            String resourceName = resource.getName();
            CPathRecordType type = determineCPathRecordType(resourceName);
            String shortName = extractShortName(resource, bioPaxNamespaceUri);
            String name = extractName(resource, bioPaxNamespaceUri);
            if (shortName.equals(CPathRecord.NA_STRING) && name != null) {
                shortName = name;
            }
            int taxonomyId = this.extractNcbiTaxonomyId(resource,
                    bioPaxNamespaceUri);

            //  Extract the current RDF ID
            Attribute rdfId = resource.getAttribute(RdfConstants.ID_ATTRIBUTE,
                    RdfConstants.RDF_NAMESPACE);
            idList.add(rdfId.getValue());

            //  Create Corresponding cPath Record
            CPathRecord record = new CPathRecord();
            record.setName(shortName);
            record.setDescription(name);
            record.setNcbiTaxonomyId(taxonomyId);
            record.setType(type);
            record.setSpecType(resourceName);
            record.setXmlType(XmlRecordType.BIO_PAX);

            //  Add to Global List
            cPathRecordList.add(record);
        }
    }

    /**
     * Extracts the Short Name via XPath.
     */
    private String extractShortName(Element resource, String bioPaxNamespaceUri)
            throws JDOMException {
        XPath xpath = XPath.newInstance("bp:SHORT-NAME");
        xpath.addNamespace("bp", bioPaxNamespaceUri);
        Element e = (Element) xpath.selectSingleNode(resource);
        if (e != null) {
            return e.getTextNormalize();
        } else {
            return CPathRecord.NA_STRING;
        }
    }

    /**
     * Extracts the Name via XPath.
     */
    private String extractName(Element resource, String bioPaxNamespaceUri)
            throws JDOMException {
        XPath xpath = XPath.newInstance("bp:NAME");
        xpath.addNamespace("bp", bioPaxNamespaceUri);
        Element e = (Element) xpath.selectSingleNode(resource);
        if (e != null) {
            return e.getTextNormalize();
        } else {
            return CPathRecord.NA_STRING;
        }
    }

    /**
     * Extracts the NCBI TaxonomyID via XPath.
     */
    private int extractNcbiTaxonomyId(Element resource,
            String bioPaxNamespaceUri)
            throws JDOMException {
        XPath xpath = XPath.newInstance
                ("bp:ORGANISM/bp:bioSource/bp:TAXON-XREF/*/bp:ID");
        xpath.addNamespace("bp", bioPaxNamespaceUri);
        Element e = (Element) xpath.selectSingleNode(resource);
        try {
            if (e != null) {
                return Integer.parseInt(e.getTextNormalize());
            } else {
                return CPathRecord.TAXONOMY_NOT_SPECIFIED;
            }
        } catch (NumberFormatException exception) {
            return CPathRecord.TAXONOMY_NOT_SPECIFIED;
        }
    }

    /**
     * Determines type of CPath Record.
     */
    private CPathRecordType determineCPathRecordType(String resourceName) {
        CPathRecordType type = null;
        if (bioPaxConstants.isPathway(resourceName)) {
            type = CPathRecordType.PATHWAY;
        } else if (bioPaxConstants.isInteraction(resourceName)) {
            type = CPathRecordType.INTERACTION;
        } else {
            type = CPathRecordType.PHYSICAL_ENTITY;
        }
        return type;
    }
}
