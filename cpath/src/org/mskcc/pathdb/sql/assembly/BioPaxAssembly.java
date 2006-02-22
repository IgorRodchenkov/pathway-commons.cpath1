// $Id: BioPaxAssembly.java,v 1.10 2006-02-22 22:47:51 grossb Exp $
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
package org.mskcc.pathdb.sql.assembly;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxElementFilter;
import org.mskcc.pathdb.schemas.biopax.OwlConstants;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.xml.XmlUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Encapsulates a Complete BioPAX XML Assembly Document.
 * This document is well-formed and valid RDF.
 *
 * @author Ethan Cerami
 */
public class BioPaxAssembly implements XmlAssembly {
    private XDebug xdebug;
    private String xml;
    private LinkedHashMap nodesVisited;
    private Element globalRoot;
    private Document globalDoc;
    private int numHits = 0;
    private ArrayList queue;
    private int mode;

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssemblyFactory.
     *
     * @param recordList ArrayList of CPathRecord objects.  Each CPathRecord
     *                   contains a Pathway or an Interaction.
     * @param mode       Mode must be one of XML_ABBREV, XML_FULL.
     * @param xdebug     XDebug Object.
     * @throws AssemblyException Error In Assembly.
     */
    BioPaxAssembly(ArrayList recordList, int mode, XDebug xdebug)
            throws AssemblyException {
        this.nodesVisited = new LinkedHashMap();
        this.queue = recordList;
        this.mode = mode;
        this.xdebug = xdebug;
        try {
            xdebug.logMsg(this, "Start: Creating BioPAX Assembly Document");
            if (mode == XmlAssemblyFactory.XML_ABBREV) {
                xdebug.logMsg(this, "Mode set to:  XML_ABBREV");
            } else {
                xdebug.logMsg(this, "Mode set to:  XML_FULL");
            }
            traverseRecordsInQueue();
            assembleRdfDocument();
            xdebug.logMsg(this, "End: Creating BioPAX Assembly Document");
        } catch (DaoException e) {
            throw new AssemblyException(e);
        } catch (IOException e) {
            throw new AssemblyException(e);
        } catch (JDOMException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssembly Factory.
     *
     * @param xmlDocumentComplete Complete XML Document.
     * @param xdebug              XDebug Object.
     * @throws AssemblyException Error In Assembly.
     */
    BioPaxAssembly(String xmlDocumentComplete, XDebug xdebug)
            throws AssemblyException {
        this.xml = xmlDocumentComplete;
        this.xdebug = xdebug;
        try {
            if (xml != null) {
                SAXBuilder builder = new SAXBuilder();
                globalDoc = builder.build(new StringReader(xml));
                globalRoot = globalDoc.getRootElement();
            }
        } catch (JDOMException e) {
            throw new AssemblyException(e);
        } catch (IOException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssembly Factory.
     *
     * @param xdebug XDebug Object.
     */
    BioPaxAssembly(XDebug xdebug) {
        this.xdebug = xdebug;
        this.xml = null;
    }

    /**
     * Gets XML Record Type.
     *
     * @return XmlRecordType.BIO_PAX
     */
    public XmlRecordType getXmlType() {
        return XmlRecordType.BIO_PAX;
    }

    /**
     * Gets Comlete Xml Assembly (in String form).
     *
     * @return XML Document String.
     */
    public String getXmlString() {
        return xml;
    }

    /**
     * Gets Complete Xml Assembly (in String form).
     * All Internal IDs are converted from the form: 1234 to CPATH-1234.
     * See org.mskcc.pathdb.sql.assembly.CPathIdFilter for more details.
     *
     * @return XML Document String.
     * @throws AssemblyException Error Assembling XML Document.
     */
    public String getXmlStringWithCPathIdPrefix() throws AssemblyException {
        return xml;
    }

    /**
     * Gets Complete XML Assembly (in object form).
     *
     * @return Java Object encapsulating XML Document.
     */
    public Object getXmlObject() {
        return globalRoot;
    }

    /**
     * Indicates if Assembly is Empty (contains no data).
     *
     * @return true or false.
     */
    public boolean isEmpty() {
        if (globalRoot == null
                || globalRoot.getChildren().size() == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets Total Number of Records.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     *
     * @return int number of records.
     */
    public int getNumHits() {
        return numHits;
    }

    /**
     * Sets Total Number of Records.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     *
     * @param numRecords Total Number of Records.
     */
    public void setNumHits(int numRecords) {
        numHits = numRecords;
    }

    private void traverseRecordsInQueue() throws DaoException {
        for (int i = 0; i < queue.size(); i++) {
            CPathRecord record = (CPathRecord) queue.get(i);
            traverseNode(record);
        }
    }

    /**
     * Traverses an Individual cPath Record Node.
     * Performs a depth-first search.
     */
    private void traverseNode(CPathRecord record) throws DaoException {
        //  If we have visited this node already, return immediately.
        if (nodesVisited.containsKey(new Long(record.getId()))) {
            return;
        }

        xdebug.logMsg(this, "Traversing Record:  " + record.getName()
                + ", Type:  " + record.getType()
                + ", XmlType:  " + record.getXmlType()
                + ", cPath ID:  " + record.getId());

        //  Only traverse BioPAX Records
        if (!record.getXmlType().equals(XmlRecordType.BIO_PAX)) {
            return;
        }

        //  Add to List of Nodes Visited
        nodesVisited.put(new Long(record.getId()), record);

        //  If XML_FULL Mode, recursively traverse and get all linked resources.
        if (mode == XmlAssemblyFactory.XML_FULL) {
            //  Get All Links
            DaoInternalLink internalLinker = new DaoInternalLink();
            ArrayList internalLinks =
                    internalLinker.getTargetsWithLookUp(record.getId());

            //  Iterate through all Links, e.g. children
            for (int i = 0; i < internalLinks.size(); i++) {
                CPathRecord child = (CPathRecord) internalLinks.get(i);
                if (!nodesVisited.containsKey(new Long(child.getId()))) {
                    traverseNode(child);
                }
            }
        }
    }

    private void assembleRdfDocument() throws IOException, JDOMException {
        //  Create New XML Document
        globalDoc = new Document();

        //  Create New RDF Root Element
        globalRoot = new Element(RdfConstants.RDF_ROOT_NAME,
                RdfConstants.RDF_NAMESPACE);

        //  Add BioPAX Level 2 Namespace Declaration
        globalRoot.addNamespaceDeclaration
                (BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE);

        //  Add OWL Namespace Declaration
        globalRoot.addNamespaceDeclaration(OwlConstants.OWL_NAMESPACE);

        //  Add XML Base Declaration (Annoying Requirement of Protege)
        globalRoot.setAttribute("base", CPathConstants.CPATH_HOME_URI,
                Namespace.XML_NAMESPACE);

        //  Add Default Namespace Declaration (Annoying Requirement of Protege)
        globalRoot.addNamespaceDeclaration(Namespace.getNamespace("",
                CPathConstants.CPATH_HOME_URI + "#"));

        //  Add OWL Import Element, so that we can Import BioPAX Ontology
        Element owlImports = new Element(OwlConstants.OWL_IMPORTS_ELEMENT,
                OwlConstants.OWL_NAMESPACE);
        String bioPaxLevel2NamespaceUri =
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI.replaceAll
                ("#", "");
        owlImports.setAttribute(RdfConstants.RESOURCE_ATTRIBUTE,
                bioPaxLevel2NamespaceUri, RdfConstants.RDF_NAMESPACE);
        Element owlOntology = new Element(OwlConstants.OWL_ONTOLOGY_ELEMENT,
                OwlConstants.OWL_NAMESPACE);
        owlOntology.setAttribute(RdfConstants.ABOUT_ATTRIBUTE, "",
                RdfConstants.RDF_NAMESPACE);
        owlOntology.addContent(owlImports);
        globalRoot.addContent(owlOntology);

        //  Set Root Element
        globalDoc.setRootElement(globalRoot);

        //  Iterate through all visited nodes, and add as children or RDF root
        Iterator iter = nodesVisited.values().iterator();
        while (iter.hasNext()) {
            CPathRecord record = (CPathRecord) iter.next();
            String xml = record.getXmlContent();
            SAXBuilder builder = new SAXBuilder();
            Document localDoc = builder.build(new StringReader(xml));
            Element localRoot = localDoc.getRootElement();
            localRoot.detach();
            updateNamespace(localRoot);

            //  If we are in XML_ABBREV mode, strip down to core elements only.
            if (mode == XmlAssemblyFactory.XML_ABBREV) {
                BioPaxElementFilter.retainCoreElementsOnly(localRoot);
            }

            globalRoot.addContent(localRoot);
        }

        //  Serialize to XML String
        xml = XmlUtil.serializeToXml(globalDoc);
    }

    /**
     * Update Namespace of All Elements to BioPAX Level 2.
     */
    private void updateNamespace(Element e) {
        e.setNamespace(BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE);
        List children = e.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            updateNamespace(child);
        }
    }
}
