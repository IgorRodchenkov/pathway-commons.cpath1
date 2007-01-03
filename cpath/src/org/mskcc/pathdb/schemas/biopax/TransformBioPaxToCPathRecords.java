// $Id: TransformBioPaxToCPathRecords.java,v 1.11 2007-01-03 16:37:15 cerami Exp $
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
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Transforms BioPAX RDF Elements into CPathRecord Objects, in preparation
 * for submission to database.
 *
 * @author Ethan Cerami
 */
public class TransformBioPaxToCPathRecords {
    private BioPaxConstants bioPaxConstants;

    /**
     * Constructor.
     *
     */
    public TransformBioPaxToCPathRecords () {
        this.bioPaxConstants = new BioPaxConstants();
    }

    /**
     * Gets List of RDF Ids.  This list appears in the exact same order
     * as the cPath Record List.
     *
     * @return ArrayList of String Objects.
     */
    public String getRdfId (Element resource) {
        //  Extract the current RDF ID
        Attribute rdfId = resource.getAttribute(RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);
        // try ABOUT attribute if necessary
        if (rdfId == null) {
            rdfId = resource.getAttribute(RdfConstants.ABOUT_ATTRIBUTE,
                    RdfConstants.RDF_NAMESPACE);
        }
        return rdfId.getValue();
    }

    /**
     * Creates cPath Record Objects for all top-level resources.
     */
    public CPathRecord createCPathRecord (Element resource) throws JDOMException {
        String bioPaxNamespaceUri = resource.getNamespaceURI();
        String resourceName = resource.getName();
        CPathRecordType type = determineCPathRecordType(resourceName);
        String label = getLabel (resource, bioPaxNamespaceUri);
        String description = extractName(resource, bioPaxNamespaceUri);
        int taxonomyId = this.extractNcbiTaxonomyId(resource, bioPaxNamespaceUri);

        //  Create Corresponding cPath Record
        CPathRecord record = new CPathRecord();
        record.setName(label);
        if (description != null) {
            record.setDescription(description);
        } else {
            record.setDescription(CPathRecord.NA_STRING);
        }
        record.setNcbiTaxonomyId(taxonomyId);
        record.setType(type);
        record.setSpecType(resourceName);
        record.setXmlType(XmlRecordType.BIO_PAX);
        return record;
    }

    /**
     * Extracts the Short Name via XPath.
     */
    private String extractShortName (Element resource, String bioPaxNamespaceUri)
            throws JDOMException {
        XPath xpath = XPath.newInstance("bp:SHORT-NAME");
        xpath.addNamespace("bp", bioPaxNamespaceUri);
        Element e = (Element) xpath.selectSingleNode(resource);
        if (e != null) {
            return e.getTextNormalize();
        } else {
            return null;
        }
    }

    /**
     * Extracts the Name via XPath.
     */
    private String extractName (Element resource, String bioPaxNamespaceUri)
            throws JDOMException {
        XPath xpath = XPath.newInstance("bp:NAME");
        xpath.addNamespace("bp", bioPaxNamespaceUri);
        Element e = (Element) xpath.selectSingleNode(resource);
        if (e != null) {
            return e.getTextNormalize();
        } else {
            return null;
        }
    }

    private ArrayList extractSynonyms(Element resource, String bioPaxNamespaceUri)
        throws JDOMException {
        ArrayList synList = new ArrayList();
        XPath xpath = XPath.newInstance("/*/bp:SYNONYMS");
        xpath.addNamespace("bp", bioPaxNamespaceUri);
        List eList = xpath.selectNodes(resource);
        if (eList != null) {
            for (int i=0; i<eList.size(); i++) {
                Element e = (Element) eList.get(i);
                synList.add(e.getTextNormalize());
            }
        }
        return synList;
    }

    private String getLabel (Element resource, String bioPaxNamespaceUri)
        throws JDOMException {

        List synList = extractSynonyms(resource, bioPaxNamespaceUri);
        String name;

        // precedence 1: short name
        name = extractShortName(resource, bioPaxNamespaceUri);
        if (name != null && name.length() > 0) {
            return name;
        }

        // precedence 2:  name
        name = extractName(resource, bioPaxNamespaceUri);
        if (name != null && name.length() > 0) {
            //  Mini-Hack for Reactome names
            if (name.startsWith("UniProt:")) {
                if (synList.size() > 0) {
                    return (String) synList.get(0);
                } else {
                    StringTokenizer tokenizer = new StringTokenizer(name, " ");
                    return (String) tokenizer.nextElement();
                }
            } else {
                return name;
            }
        }

        // precedence 3:  shortest synonym
        int shortestSynonymIndex = -1;
        if (synList != null && synList.size() > 0) {
            int minLength = -1;
            for (int lc = 0; lc < synList.size(); lc++) {
                String synonym = (String) synList.get(lc);
                if (minLength == -1 || synonym.length() < minLength) {
                    minLength = synonym.length();
                    shortestSynonymIndex = lc;
                }
            }
        } else {
            return CPathRecord.NA_STRING;
        }

        // set name to return
        if (shortestSynonymIndex > -1) {
            name = (String) synList.get(shortestSynonymIndex);
        }

        // outta here
        return name;
    }

    /**
     * Extracts the NCBI TaxonomyID via XPath.
     */
    private int extractNcbiTaxonomyId (Element resource,
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
    private CPathRecordType determineCPathRecordType (String resourceName) {
        CPathRecordType type;
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
