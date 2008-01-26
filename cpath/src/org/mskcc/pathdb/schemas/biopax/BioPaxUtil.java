// $Id: BioPaxUtil.java,v 1.35 2008-01-26 21:39:25 grossben Exp $
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

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoIdGenerator;
import org.mskcc.pathdb.sql.dao.ExternalDatabaseNotFoundException;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.util.rdf.RdfUtil;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.xml.XmlUtil;
import org.mskcc.pathdb.model.Reference;
import org.mskcc.pathdb.model.CPathRecord;

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
    private BioPaxConstants bioPaxConstants = new BioPaxConstants();
    private ArrayList errorList = new ArrayList();
    private Document bioPaxDoc;
    private Namespace bioPaxNamespace;
    private HashMap localIdMap;
    private ProgressMonitor pMonitor;
    private boolean debugFlag = false;
    private boolean strictValidation = false;
    private int numElements;

    /**
     * Constructor.
     *
     * @param reader                    Reader Object.
     * @param strictValidation                    Perform strictValidation validation.
     *                                  Setting to true will check all RDF Resource Links
     *                                  and all external reference links.
     * @param pMonitor                  ProgressMonitor Object.
     * @throws IOException   Input/Output Error.
     * @throws JDOMException XML Error.
     * @throws DaoException  Database Access Error.
     */
    public BioPaxUtil(Reader reader, boolean strictValidation, ProgressMonitor pMonitor)
            throws IOException, JDOMException, DaoException {
        this.strictValidation = strictValidation;
        this.pMonitor = pMonitor;
        //  Read in File via JDOM SAX Builder
        SAXBuilder builder = new SAXBuilder();
        bioPaxDoc = builder.build(reader);

        //  Get Root Element
        Element root = bioPaxDoc.getRootElement();

        //  First Step:  Inspect Tree to categorize all RDF Resources
        pMonitor.setCurrentMessage("Categorizing BioPAX Resources...");
        categorizeResources(root);

        //  Second Step:  Validate that all RDF links point to actual
        //  RDF Resources, defined in the document.
        if (strictValidation) {
            pMonitor.setCurrentMessage("Validating RDF Links, total number of elements: "
                + numElements);
            pMonitor.setMaxValue(numElements);
            validateResourceLinks(root);
        }

        //  Third Step:  Validate and/or add missing external references to database.
        pMonitor.setCurrentMessage("Validating All External References");
        validateAndOrAddExternalReferences(root);
    }

	/**
	 * Method to return root element of the biopax document.
	 *
	 * @return Element
	 */
	public Element getRootElement() {
		return bioPaxDoc.getRootElement();
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
     * Gets Number of Pathways.
     *
     * @return Number of Pathways.
     */
    public int getNumPathways() {
        return pathwayList.size();
    }

    /**
     * Gets Pathway at specified index.
     * <P>Performs lazy processing of pathway element.
     * @param index Index
     * @return JDOM Element with Pathway Data
     */
    public Element getPathway (int index) throws DaoException {
        Element pathway = (Element) pathwayList.get(index);
        return lazyLoad(pathway);
    }

    /**
     * Gets Number of Interactions.
     *
     * @return Number of Interactions.
     */
    public int getNumInteractions() {
        return interactionList.size();
    }

    /**
     * Gets Interaction at specified index.
     * <P>Performs lazy processing of interaction element.
     * @param index Index
     * @return JDOM Element with Interaction Data
     */
    public Element getInteraction (int index) throws DaoException {
        Element interaction = (Element) interactionList.get(index);
        return lazyLoad(interaction);
    }

    /**
     * Gets Number of Physical Entities.
     *
     * @return ArrayList of JDOM Element Objects.
     */
    public int getNumPhysicalEntities() {
        return physicalEntityList.size();
    }

    /**
     * Gets Physical Entity at specified index.
     * <P>Performs lazy processing of physical entity element.
     * @param index Index
     * @return JDOM Element with Physical Entity Data
     */
    public Element getPhysicalEntity (int index) throws DaoException {
        Element pe = (Element) physicalEntityList.get(index);
        return lazyLoad(pe);
    }

    /**
     * Lazy Load Processing.
     */
    private Element lazyLoad (Element e) throws DaoException {
        Element clone = (Element) e.clone();

        //  Make it hierarchical
        makeHierachical(clone, true);

        //  Add Cloned Element to new document;  otherwise, XPath queries will not work
        Document doc = new Document();
        doc.addContent(clone);

        return clone;
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
            if (dbName != null && dbName.trim().length() > 0
                    && dbId != null  && dbId.trim().length() > 0) {
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
    public ExternalReference[] extractInteractionUnificationXrefs(Element e)
            throws JDOMException {
		return extractUnificationXrefs(e, "biopax:DATA-SOURCE/*/biopax:XREF/biopax:unificationXref");
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
		return extractUnificationXrefs(e, null);
	}

    /**
     * Extracts All Unification XREF Data within the specified Element.
     *
     * @param e JDOM Element.
     * @param query String
     * @return Array of External Reference Objects.
     * @throws JDOMException JDOM Error.
     */
    public ExternalReference[] extractUnificationXrefs(Element e, String query)
            throws JDOMException {
		query = (query == null) ? "biopax:XREF/biopax:unificationXref" : query;
        XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("biopax", e.getNamespaceURI());
        List xrefs = xpath.selectNodes(e);
        ArrayList refs = new ArrayList();
        for (int i = 0; i < xrefs.size(); i++) {
            Element xref = (Element) xrefs.get(i);
            String dbName = xref.getChildText("DB", e.getNamespace());
            String dbId = xref.getChildText("ID", e.getNamespace());
            if (dbName != null && dbName.trim().length() > 0
                    && dbId != null  && dbId.trim().length() > 0) {
                refs.add(new ExternalReference(dbName, dbId));
            }
        }
        return (ExternalReference[])
                refs.toArray(new ExternalReference[refs.size()]);
    }

    /**
     * Extracts All Publication XREF Data within the specified Element.
     *
     * @param e JDOM Element.
	 * @param query String.
     * @return List<Reference>.
     * @throws JDOMException JDOM Error.
     */
    public List<Reference> extractPublicationXrefs(Element e, String query)
            throws JDOMException {
		XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("biopax", e.getNamespaceURI());
        List xrefs = xpath.selectNodes(e);
        ArrayList refs = new ArrayList();
        for (int i = 0; i < xrefs.size(); i++) {
            Element xref = (Element) xrefs.get(i);
            String dbId = xref.getChildText("ID", e.getNamespace());
            String dbName = xref.getChildText("DB", e.getNamespace());
			String year = xref.getChildText("YEAR", e.getNamespace());
			String title = xref.getChildText("TITLE", e.getNamespace());
			xpath = XPath.newInstance("biopax:AUTHORS");
			xpath.addNamespace("biopax", e.getNamespaceURI());
			List<Element> authorsList = xpath.selectNodes(xref);
			String[] authors;
			if (authorsList.size() > 0) {
				int lc = -1;
				authors = new String[authorsList.size()];
				for (Element author : authorsList) {
					if (author != null && author.getTextNormalize().length() > 0) {
						authors[++lc]  = author.getTextNormalize();
					}
				}
			}
			else {
				authors = new String[1]; authors[0] = CPathRecord.NA_STRING;
			}
			String source = xref.getChildText("SOURCE", e.getNamespace());
			// create reference object to store - if we have an id
            if (dbId != null  && dbId.trim().length() > 0) {
				Reference reference = new Reference();
				reference.setId(dbId);
				reference.setDatabase((dbName != null && dbName.trim().length() > 0) ? dbName : CPathRecord.NA_STRING);
				reference.setYear((year != null && year.trim().length() > 0) ? year : CPathRecord.NA_STRING);
				reference.setTitle((title != null && title.trim().length() > 0) ? title : CPathRecord.NA_STRING);
				reference.setAuthors(authors);
				reference.setSource((source != null && source.trim().length() > 0) ? source : CPathRecord.NA_STRING);
                refs.add(reference);
            }
        }
        return refs;
    }

    /**
     * Categorizes the document into top-level components:  pathways,
     * interactions, and physical entities.
     */
    private void categorizeResources(Element e) {
        numElements++;

        //  First, separate out any OWL Specific Elements
        String namespaceUri = e.getNamespaceURI();
        if (namespaceUri.equals(OwlConstants.OWL_NAMESPACE_URI)) {
            return;
        }

		// get rdf id (or about attribute)
		Attribute idAttribute = e.getAttribute(RdfConstants.ID_ATTRIBUTE,
											   RdfConstants.RDF_NAMESPACE);
		// if rdf id attribute is null, try about
		idAttribute = (idAttribute == null) ?
			e.getAttribute(RdfConstants.ABOUT_ATTRIBUTE,
						   RdfConstants.RDF_NAMESPACE) : idAttribute;

		// do we have an attribute to process ?
        if (idAttribute != null) {
            //  Store element to hashmap, keyed by RDF ID
            if (rdfResources.containsKey(idAttribute.getValue())) {
                errorList.add(new String("Element:  " + e
                        + " declares RDF ID/ABOUT:  " + idAttribute.getValue()
                        + ", but a resource with this ID/ABOUT already exists."));
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
        pMonitor.incrementCurValue();
        ConsoleUtil.showProgress(pMonitor);

        //  Ignore all OWL Specific Elements
        String namespaceUri = e.getNamespaceURI();
        if (namespaceUri.equals(OwlConstants.OWL_NAMESPACE_URI)) {
            return;
        }

        //  Get an RDF Resource Attribute, if available
        Attribute resourceAttribute = e.getAttribute (RdfConstants.RESOURCE_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);

        if (resourceAttribute != null) {
            String key = RdfUtil.removeHashMark(resourceAttribute.getValue());
            if (!rdfResources.containsKey(key)) {
                List attributeList = e.getAttributes();
                StringBuffer attributeText = new StringBuffer();
                for (int i=0; i<attributeList.size(); i++) {
                    Attribute attrib = (Attribute) attributeList.get(i);
                    attributeText.append (attrib.getName() + ":" + attrib.getValue());
                }
                errorList.add(new String("Element:  " + e.getName()
                        + " [" + attributeText + "]"
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
    private void makeHierachical(Element e, boolean isTopLevelResource)
        throws DaoException {
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
            Attribute idAttribute = e.getAttribute(RdfConstants.ID_ATTRIBUTE,
												   RdfConstants.RDF_NAMESPACE);
			// if rdf id attribute is null, try about
			idAttribute = (idAttribute == null) ?
				e.getAttribute(RdfConstants.ABOUT_ATTRIBUTE,
							   RdfConstants.RDF_NAMESPACE) : idAttribute;

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

                if (uri.length() > 0) {
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
                } else {
                    throw new NullPointerException ("Empty RDF reference # found.  "
                        + "Check BioPAX source");
                }
            }
        }

        //  Traverse through all children.
        if (keepTraversingTree) {
            List children = e.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Element child = (Element) children.get(i);
                makeHierachical(child, false);
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

        // if rdf id attribute is null, try about
		rdfId = (rdfId == null) ?
			e.getAttribute(RdfConstants.ABOUT_ATTRIBUTE,
						   RdfConstants.RDF_NAMESPACE) : rdfId;

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
     * new internally generated local ID.
     *
     * @param e Element
     */
    private Element replaceResourceWithClone(Element e) throws DaoException {
        //  First, clone the element
        //  This is a "deep" clone
        Element clone = (Element) e.clone();

        //  Get the RDF ID
		String attributeToProcess = RdfConstants.ID_ATTRIBUTE;
        Attribute rdfId = e.getAttribute(attributeToProcess, RdfConstants.RDF_NAMESPACE);
		if (rdfId == null) {
			attributeToProcess = RdfConstants.ABOUT_ATTRIBUTE;
			rdfId = e.getAttribute(attributeToProcess, RdfConstants.RDF_NAMESPACE);
		}
		if (rdfId != null) {
			//  Remove Existing RDF ID
			clone.removeAttribute(attributeToProcess, RdfConstants.RDF_NAMESPACE);
			//  Add New RDF ID, based on locally generated algorithm
			clone.setAttribute(attributeToProcess, getNextId(), RdfConstants.RDF_NAMESPACE);
		}

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
     */
    private void validateAndOrAddExternalReferences (Element element)
            throws JDOMException, DaoException {

        //  Get DB Children of All XREF Elements
        //  Not all BioPAX documents will use an agreed namespace prefix,
        //  e.g. biopax, bp, etc.
        //  However, the Jaxen API provides an easy way around this via
        //  the addNamespace method.
        XPath xpath = XPath.newInstance("//biopax:DB");
        if (bioPaxNamespace != null) {
            xpath.addNamespace("biopax", bioPaxNamespace.getURI());
            List xrefs = xpath.selectNodes(element);
            pMonitor.setMaxValue(xrefs.size());
            for (int i = 0; i < xrefs.size(); i++) {
                Element dbElement = (Element) xrefs.get(i);
                String dbTerm = dbElement.getTextNormalize();
                if (dbTerm.trim().length() > 0) {
                    ExternalReference refs[] = new ExternalReference[1];
                    refs[0] = new ExternalReference(dbTerm, "BLANK_ID");
                    DaoExternalLink dao = DaoExternalLink.getInstance();
                    try {
                        dao.validateExternalReferences(refs, !strictValidation);
                    } catch (ExternalDatabaseNotFoundException e) {
                        errorList.add(new String("XREF Element references a "
                                + "database which does not exist in cPath:  "
                                + dbTerm + ".  Occurred in:  " + dbElement));
                    }
                }
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
            }
        }
    }
}
