// $Id: BioPaxUtil.java,v 1.16 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoIdGenerator;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.rdf.RdfUtil;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.xml.XmlUtil;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * BioPax Utility Class.
 *
 * @author Ethan Cerami
 */
public class BioPaxUtil {
    private HashMap rdfResources = new HashMap();
    private ArrayList pathwayList = new ArrayList();
    private ArrayList interactionList = new ArrayList();
    private ArrayList physicalEntityList = new ArrayList();
    private ArrayList ontologyList = new ArrayList();
    private BioPaxConstants bioPaxConstants = new BioPaxConstants();
    private ArrayList errorList = new ArrayList();
    private Document bioPaxDoc;
    private Element reorganizedRoot;
    private Namespace bioPaxNamespace;
    private HashMap localIdMap;
    private ProgressMonitor pMonitor;
    private boolean debugFlag = false;

    /**
     * Constructor.
     *
     * @param reader   Reader Object.
     * @param pMonitor ProgressMonitor Object.
     * @throws IOException   Input/Output Error.
     * @throws JDOMException XML Error.
     * @throws DaoException  Database Access Error.
     */
    public BioPaxUtil(Reader reader, ProgressMonitor pMonitor)
            throws IOException, JDOMException, DaoException {
        this.pMonitor = pMonitor;
        //  Read in File via JDOM SAX Builder
        SAXBuilder builder = new SAXBuilder();
        bioPaxDoc = builder.build(reader);

        //  Get Root Element
        Element root = bioPaxDoc.getRootElement();

        //  First Step:  Inspect Tree to categorize all RDF Resources
        pMonitor.setCurrentMessage("Categorizing BioPAX Resources");
        categorizeResources(root);

        //  Second Step:  Validate that all RDF links point to actual
        //  RDF Resources, defined in the document.
        pMonitor.setCurrentMessage("Validating RDF Links");
        validateResourceLinks(root);

        //  Third Step:  Make Hierarchical
        if (errorList.size() == 0) {
            pMonitor.setCurrentMessage("Preparing Pathway Elements:  "
                    + "[" + pathwayList.size() + " Pathways]");
            pMonitor.setMaxValue(pathwayList.size());
            for (int i = 0; i < pathwayList.size(); i++) {
                Element pathway = (Element) pathwayList.get(i);
                makeHierachical(pathway, BioPaxConstants.PATHWAY, true);
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
            }
            pMonitor.setCurrentMessage("Preparing Interaction Elements:  "
                    + "[" + interactionList.size() + " Interactions]");
            pMonitor.setMaxValue(interactionList.size());
            for (int i = 0; i < interactionList.size(); i++) {
                Element interaction = (Element) interactionList.get(i);
                makeHierachical(interaction, BioPaxConstants.INTERACTION, true);
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
            }

            pMonitor.setCurrentMessage
                    ("Preparing Physical Entity Elements:  "
                    + " [" + physicalEntityList.size()
                    + " Physical Entities]");
            pMonitor.setMaxValue(physicalEntityList.size());
            for (int i = 0; i < physicalEntityList.size(); i++) {
                Element physicalEntity = (Element) physicalEntityList.get(i);
                makeHierachical(physicalEntity,
                        BioPaxConstants.PHYSICAL_ENTITY, true);
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
            }

            //  Fourth Step:  Create New Global Document
            pMonitor.setCurrentMessage("Creating New Global BioPAX Document");
            this.reorganizedRoot = createdNewGlobalDocument();

            //  Fifth Step:  Validate All External References
            validateExternalReferences(reorganizedRoot);
        }
    }

    /**
     * Gets HashMap of All RDF Resources, keyed by RDF ID.
     *
     * @return HashMap of All RDF Resources, keyed by RDF ID.
     */
    public HashMap getRdfResourceMap() {
        return rdfResources;
    }

    /**
     * Gets list of Pathway Resources.
     *
     * @return ArrayList of JDOM Element Objects.
     */
    public ArrayList getPathwayList() {
        return pathwayList;
    }

    /**
     * Gets List of Interaction Resources.
     *
     * @return ArrayList of JDOM Element Objects.
     */
    public ArrayList getInteractionList() {
        return interactionList;
    }

    /**
     * Gets List of Physical Entity Resources.
     *
     * @return ArrayList of JDOM Element Objects.
     */
    public ArrayList getPhysicalEntityList() {
        return physicalEntityList;
    }

    /**
     * Gets a List of all Pathways, Interactions, and Physical Entities.
     *
     * @return ArrayList of JDOM Element Objects.
     */
    public ArrayList getTopLevelComponentList() {
        ArrayList list = new ArrayList();
        list.addAll(pathwayList);
        list.addAll(interactionList);
        list.addAll(physicalEntityList);
        return list;
    }

    /**
     * Gets List of Errors.
     *
     * @return ArrayList of String Objects.
     */
    public ArrayList getErrorList() {
        return errorList;
    }

    /**
     * Gets Newly Reorganized BioPax XML Document.
     *
     * @return XML Root Element.
     */
    public Element getReorganizedXml() {
        return this.reorganizedRoot;
    }

    /**
     * Extracts All XREF Data within the specified Element.
     *
     * @param e JDOM Element.
     * @return Array of External Reference Objects.
     * @throws JDOMException JDOM Error.
     */
    public ExternalReference[] extractXrefs(Element e)
            throws JDOMException {
        XPath xpath = XPath.newInstance("biopax:XREF/*");
        xpath.addNamespace("biopax", e.getNamespaceURI());
        List xrefs = xpath.selectNodes(e);
        ArrayList refs = new ArrayList();
        for (int i = 0; i < xrefs.size(); i++) {
            Element xref = (Element) xrefs.get(i);
            String dbName = xref.getChildText("DB", e.getNamespace());
            String dbId = xref.getChildText("ID", e.getNamespace());
            if (dbName != null && dbId != null) {
                refs.add(new ExternalReference(dbName, dbId));
            }
        }
        return (ExternalReference[])
                refs.toArray(new ExternalReference[refs.size()]);
    }

    /**
     * Extracts All Unification XREF Data within the specified Element.
     *
     * @param e JDOM Element.
     * @return Array of External Reference Objects.
     * @throws JDOMException JDOM Error.
     */
    public ExternalReference[] extractUnificationXrefs(Element e)
            throws JDOMException {
        XPath xpath = XPath.newInstance("biopax:XREF/biopax:unificationXref");
        xpath.addNamespace("biopax", e.getNamespaceURI());
        List xrefs = xpath.selectNodes(e);
        ArrayList refs = new ArrayList();
        for (int i = 0; i < xrefs.size(); i++) {
            Element xref = (Element) xrefs.get(i);
            String dbName = xref.getChildText("DB", e.getNamespace());
            String dbId = xref.getChildText("ID", e.getNamespace());
            if (dbName != null && dbId != null) {
                refs.add(new ExternalReference(dbName, dbId));
            }
        }
        return (ExternalReference[])
                refs.toArray(new ExternalReference[refs.size()]);
    }

    /**
     * Creates New Global Document.
     *
     * @return New Root Element.
     */
    private Element createdNewGlobalDocument() {
        //  Get Root Element and Clone It
        Element root = bioPaxDoc.getRootElement();
        Element clonedRoot = (Element) root.clone();
        clonedRoot.removeContent();

        //  Get Root Document and Clone it
        //  The Root Element must be attached to some Document in order
        //  for XPath Queries to work.
        Document clonedDocument = (Document) bioPaxDoc.clone();
        clonedDocument.removeContent();
        Document emptyClonedDocument = (Document) clonedDocument.clone();
        clonedDocument.addContent(clonedRoot);

        //  Add Any OWL Specific Resources
        for (int i = 0; i < ontologyList.size(); i++) {
            Element ontologyElement = (Element) ontologyList.get(i);
            clonedRoot.addContent((Element) ontologyElement.clone());
        }

        //  Next, add all Pathways
        for (int i = 0; i < pathwayList.size(); i++) {
            Element pathway = (Element) pathwayList.get(i);
            clonedRoot.addContent((Element) pathway.clone());
            attachToClonedDocument(pathway, emptyClonedDocument);
        }

        //  Next, add all Interactions
        for (int i = 0; i < interactionList.size(); i++) {
            Element interaction = (Element) interactionList.get(i);
            clonedRoot.addContent((Element) interaction.clone());
            attachToClonedDocument(interaction, emptyClonedDocument);
        }

        //  Next, add all Physical Entities
        for (int i = 0; i < physicalEntityList.size(); i++) {
            Element physicalEntity = (Element) physicalEntityList.get(i);
            clonedRoot.addContent((Element) physicalEntity.clone());
            attachToClonedDocument(physicalEntity, emptyClonedDocument);
        }
        return clonedRoot;
    }

    /**
     * Attaches Lone Element to an Empty Document, so that local XPath Queries
     * work.
     */
    private void attachToClonedDocument(Element e, Document d) {
        Document cloneDoc = (Document) d.clone();
        e.detach();
        cloneDoc.addContent(e);
    }

    /**
     * Categorizes the document into top-level components:  pathways,
     * interactions, and physical entities.
     */
    private void categorizeResources(Element e) {

        //  First, separate out any OWL Specific Elements
        String namespaceUri = e.getNamespaceURI();
        if (namespaceUri.equals(OwlConstants.OWL_NAMESPACE_URI)) {
            ontologyList.add(e);
            return;
        }

        //  Get an RDF ID Attribute, if available
        Attribute idAttribute = e.getAttribute(RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);
        if (idAttribute != null) {
            //  Store element to hashmap, keyed by RDF ID
            if (rdfResources.containsKey(idAttribute.getValue())) {
                errorList.add(new String("Element:  " + e
                        + " declares RDF ID:  " + idAttribute.getValue()
                        + ", but a resource with this ID already exists."));
            } else {
                rdfResources.put(idAttribute.getValue(), e);
            }
        }

        //  Categorize into separate bins
        String name = e.getName();
        if (bioPaxConstants.isPathway(name)) {
            extractBioPaxNamespace(e);
            pathwayList.add(e);
        } else if (bioPaxConstants.isInteraction((name))) {
            extractBioPaxNamespace(e);
            interactionList.add(e);
        } else if (bioPaxConstants.isPhysicalEntity(name)) {
            extractBioPaxNamespace(e);
            physicalEntityList.add(e);
        }

        //  Traverse through all children of current element
        List children = e.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            categorizeResources(child);
        }
    }

    private void extractBioPaxNamespace(Element e) {
        //  Determine what namespace this element uses.
        //  This enables us to determine which level of BioPAX we are using.
        //  It's also useful for performing XPath Queries later on.
        if (bioPaxNamespace == null) {
            bioPaxNamespace = e.getNamespace();
        }
    }

    /**
     * Validates that all RDF Links are valid.
     */
    private void validateResourceLinks(Element e) {
        //  Get an RDF Resource Attribute, if available
        Attribute resourceAttribute = e.getAttribute
                (RdfConstants.RESOURCE_ATTRIBUTE,
                        RdfConstants.RDF_NAMESPACE);

        //  Ignore all OWL Specific Elements
        String namespaceUri = e.getNamespaceURI();
        if (namespaceUri.equals(OwlConstants.OWL_NAMESPACE_URI)) {
            return;
        }

        if (resourceAttribute != null) {
            String key = RdfUtil.removeHashMark(resourceAttribute.getValue());
            if (!rdfResources.containsKey(key)) {
                errorList.add(new String("Element:  " + e
                        + " references:  " + key + ", but no such resource "
                        + "exists in document."));
            }
        }

        //  Traverse through all children of current element
        List children = e.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            validateResourceLinks(child);
        }
    }

    /**
     * Make the Specified Element Hierarchical.
     * <p/>
     * In examining each element, there are two main cases to consider:
     * <p/>
     * <p/>
     * Case 1:  The element has an RDF ID attribute.  This means that the
     * element is itself an RDF resource.
     * <p/>
     * Subcase 1A:  The element is a BioPAX entity.  In this case, we
     * excise (cut) the element from the parent, and modify it's immediate
     * parent to point to it via an RDF resource link attribute.  Then,
     * stop walking the tree.  We are done.
     * <p/>
     * Subcase 1B:  The element is not a BioPAX entity.  In this case,
     * we clone the element, replace the original element with the newly
     * cloned element, and and give the clone a new internally
     * generated local ID.  We then continue walking down the new tree.
     * <p/>
     * Case 2:  The element has an RDF reource attribute.  This means that the
     * element points to an RDF resource.
     * <p/>
     * Subcase 2A:  Follow the pointer.  If we point to a BioPAX entity,
     * do nothing.  Stop walking the tree.
     * <p/>
     * Subcase 2B:  Follow the pointer.  If we do not point to an entity
     * resource, we clone the resource, make it a child of the current
     * element, give the clone a new internally generated local ID, and
     * replace the RDF resource attribute with an RDF ID attribute.  We
     * then continue walking down the new tree.
     */
    private void makeHierachical(Element e, String type,
            boolean isTopLevelResource) throws DaoException {
        boolean keepTraversingTree = true;

        if (debugFlag) {
            if (isTopLevelResource) {
                logMsg("Start:  Making Hierarchical");
            }
            try {
                logMsg("We are here:\n" + XmlUtil.serializeToXml(e));
            } catch (IOException exc) {
            }
        }

        //  If this is a top-level resource, just keep on walking.
        if (isTopLevelResource) {
            localIdMap = new HashMap();
        } else {

            //  Get a pointer to an RDF resource, if there is one.
            Attribute pointerAttribute = e.getAttribute
                    (RdfConstants.RESOURCE_ATTRIBUTE,
                            RdfConstants.RDF_NAMESPACE);

            //  Get an RDF ID Attribute, if there is one
            Attribute idAttribute = e.getAttribute
                    (RdfConstants.ID_ATTRIBUTE,
                            RdfConstants.RDF_NAMESPACE);

            if (idAttribute != null) {
                //  Case 1:  The element has an RDF ID attribute.
                if (bioPaxConstants.isBioPaxEntity(e.getName())) {
                    logMsg("Branching:  Subcase 1A");
                    //  Subcase 1A:  This is a BioPAX Entity
                    exciseResource(e);
                    keepTraversingTree = false;
                } else {
                    //  Subcase 1B:  This is not a BioPAX Entity
                    logMsg("Branching:  Subcase 1B");
                    e = replaceResourceWithClone(e);
                }
            } else if (pointerAttribute != null) {
                //  Case 2:  The element has an RDF Resource/Pointer Attribute
                //  Figure out what we are pointing to
                String uri = RdfUtil.removeHashMark
                        (pointerAttribute.getValue());

                Element referencedResource = (Element) rdfResources.get(uri);
                if (localIdMap.containsKey(uri)) {
                    //  If we have already been here, stop traversing.
                    //  Prevents Circular References.
                    logMsg("Preventing Circular Reference:  " + uri);

                    //  Remove the Existing RDF Pointer
                    e.removeAttribute(RdfConstants.RESOURCE_ATTRIBUTE,
                            RdfConstants.RDF_NAMESPACE);

                    //  Replace with locally generated ID
                    String newId = "#" + (String) localIdMap.get(uri);
                    e.setAttribute(RdfConstants.RESOURCE_ATTRIBUTE, newId,
                            RdfConstants.RDF_NAMESPACE);
                    keepTraversingTree = false;
                } else if (bioPaxConstants.isBioPaxEntity
                        (referencedResource.getName())) {
                    //  Case 2A:  We are pointing at a Hinge Element
                    logMsg("Branching:  Subcase 2A");
                    keepTraversingTree = false;
                } else {
                    //  Case 2B:  We are not pointing at a Hinge Element
                    //  Clone the resource, and keep walking down the
                    //  new subtree.
                    logMsg("Branching:  Subcase 2B");
                    e = replaceReferenceWithResource(pointerAttribute, e);
                }
            }
        }

        //  Traverse through all children.
        if (keepTraversingTree) {
            List children = e.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Element child = (Element) children.get(i);
                makeHierachical(child, type, false);
            }
        }
    }

    /**
     * Debug Messages, used for Development Purposes.
     *
     * @param msg String msg.
     */
    private void logMsg(String msg) {
        if (debugFlag) {
            System.out.println(msg);
        }
    }

    /**
     * Replaces the Specified RDF Link with a clone of the Actual Resource.
     * The newly cloned resource also gets a new locally generated ID.
     */
    private Element replaceReferenceWithResource(Attribute pointerAttribute,
            Element e) throws DaoException {
        String uri = RdfUtil.removeHashMark(pointerAttribute.getValue());

        //  Look up resource in global hashmap
        Element child = (Element) rdfResources.get(uri);

        //  Clone the resource
        Element clonedChild = (Element) child.clone();

        //  Remove the Existing RDF ID
        clonedChild.removeAttribute(RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);

        //  Add New RDF ID, based on locally generated algorithm
        String newId = getNextId();
        clonedChild.setAttribute(RdfConstants.ID_ATTRIBUTE, newId,
                RdfConstants.RDF_NAMESPACE);

        //  Add cloned resource to parent element
        e.addContent(clonedChild);

        //  Remove resourceLink fom parent
        e.removeAttribute(pointerAttribute);

        //  Store the ID Mapping between old ID and New Id, for later reference
        localIdMap.put(uri, newId);

        return clonedChild;
    }

    /**
     * Excises (cuts) the specified element from the tree, and modifies
     * it's immediate parent to point to it via an RDF resource link attribute.
     *
     * @param e Element.
     */
    private void exciseResource(Element e) {
        //  First, get the parent
        Element parent = e.getParentElement();

        //  Get the RDF ID
        Attribute rdfId = e.getAttribute(RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);

        //  Add RDF Resource Attribute to Parent
        Attribute resourceAttribute = new Attribute
                (RdfConstants.RESOURCE_ATTRIBUTE,
                        "#" + rdfId.getValue(), RdfConstants.RDF_NAMESPACE);
        parent.setAttribute(resourceAttribute);

        //  Remove all content from parent
        parent.removeContent();
    }

    /**
     * Performs a deep clone of the specified element, and swaps the
     * clone in place of the original element.  The new clone also gets a
     * new internally  generated local ID.
     *
     * @param e Element
     */
    private Element replaceResourceWithClone(Element e) throws DaoException {
        //  First, clone the element
        //  This is a "deep" clone
        Element clone = (Element) e.clone();

        //  Remove Existing RDF ID
        clone.removeAttribute(RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);

        //  Add New RDF ID, based on locally generated algorithm
        clone.setAttribute(RdfConstants.ID_ATTRIBUTE, getNextId(),
                RdfConstants.RDF_NAMESPACE);

        //  Obtain parent of original element
        Element parent = e.getParentElement();

        //  Detach the original element from the tree
        e.detach();

        //  Attach clone to the original parent
        parent.addContent(clone);

        return clone;
    }

    /**
     * Gets Next Local ID.
     */
    private String getNextId() throws DaoException {
        DaoIdGenerator dao = new DaoIdGenerator();
        return dao.getNextId();
    }

    /**
     * Validates that All External References point to databases which
     * already exist within cPath.
     * <P>
     * All invalid databases are added to the errorList Object, and can
     * therefore be presented to the end-user.
     */
    private void validateExternalReferences(Element root)
            throws JDOMException, DaoException {
        DaoExternalDb dao = new DaoExternalDb();
        pMonitor.setCurrentMessage("Validating All External References:");

        //  Get DB Children of All XREF Elements
        //  Not all BioPAX documents will use an agreed namespace prefix,
        //  e.g. biopax, bp, etc.
        //  However, the Jaxen API provides an easy way around this via
        //  the addNamespace method.
        XPath xpath = XPath.newInstance("//biopax:XREF/*/biopax:DB");
        xpath.addNamespace("biopax", bioPaxNamespace.getURI());
        List xrefs = xpath.selectNodes(root);
        pMonitor.setMaxValue(xrefs.size());
        for (int i = 0; i < xrefs.size(); i++) {
            Element dbElement = (Element) xrefs.get(i);
            String dbTerm = dbElement.getTextNormalize();
            ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbTerm);
            if (dbRecord == null) {
                errorList.add(new String("XREF Element references a "
                        + "database which does not exist in cPath:  "
                        + dbTerm + ".  Occurred in:  " + dbElement));
            }
            pMonitor.incrementCurValue();
            ConsoleUtil.showProgress(pMonitor);
        }
    }
}
