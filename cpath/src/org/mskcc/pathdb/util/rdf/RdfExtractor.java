package org.mskcc.pathdb.util.rdf;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.schemas.biopax.RdfConstants;
import org.mskcc.pathdb.util.xml.XmlUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.HashSet;

/**
 * RdfExtractor class for Extracting Individual RDF Resources from a
 * Global Document.
 * <p/>
 * This class is primarily used to extract a single resource (and all resource
 * depenendencies) from a large RDF document.  This is useful because most of
 * our sample BioPAX documents (even the shorter ones) are rather complex,
 * and I wanted a simple way to create test documents that can be used by our
 * Unit tests.  The idea is to take a large BioPAX document, and extract just
 * a subset of it, and use this distilled subset for specific testing purposes.
 * <p/>
 * To extract an individual RDF resource, the code follows all RDF resource
 * links, and extracts these too.  For example, if we want to extract
 * smallMolecule18 from testData/biopax/biopax1_sample1.owl, we need to
 * extract smallMolecule18, plus all linked resources, such as:  dataSource14
 * and KB_439584_Individual_47.  If these reources point to other elements,
 * we need to extract these too, and on and on.
 *
 * @author Ethan Cerami
 */
public class RdfExtractor {
    private HashMap resourceMap;
    private Stack stack;
    private Element newRoot;
    private HashSet visitedNodes;

    /**
     * Constructor.
     *
     * @param inFile   InFile.
     * @param outFile  OutFile (can be null).
     * @param targetId RDF Target ID.
     * @throws IOException   Error Reading/Writing File.
     * @throws JDOMException XML Error.
     */
    public RdfExtractor(File inFile, File outFile, String targetId)
            throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(inFile);
        resourceMap = new HashMap();

        //  First, identify all resources
        identifyRdfResources(doc);

        //  Extract the Target Element
        Element targetElement = (Element) resourceMap.get(targetId);
        if (targetElement == null) {
            throw new NullPointerException("Target ID:  " + targetId
                    + " not found in XML document.");
        }

        //  Create  New Empty Document
        visitedNodes = new HashSet();
        newRoot = createdNewGlobalDocument(doc);

        stack = new Stack();
        stack.push(targetElement);
        while (!stack.isEmpty()) {
            Element e = (Element) stack.pop();
            extractTarget(e, newRoot);
        }

        //  Output to OutFile
        if (outFile != null) {
            String xml = XmlUtil.serializeToXml(newRoot);
            FileWriter writer = new FileWriter(outFile);
            writer.write(xml);
            writer.flush();
            writer.close();
        }
    }

    /**
     * Extracts the New Root Element.
     *
     * @return New Root Element.
     */
    public Element getExtractedRoot() {
        return this.newRoot;
    }

    private void identifyRdfResources(Document doc) {
        Element root = doc.getRootElement();
        inspectElement(root);
    }

    private void inspectElement(Element e) {
        //  Get an RDF ID Attribute, if there is one
        Attribute idAttribute = e.getAttribute
                (RdfConstants.ID_ATTRIBUTE,
                        RdfConstants.RDF_NAMESPACE);
        if (idAttribute != null) {
            resourceMap.put(idAttribute.getValue(), e);
        }

        List children = e.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            inspectElement(child);
        }
    }

    /**
     * Creates New Global Document.
     *
     * @return New Root Element.
     */
    private Element createdNewGlobalDocument(Document doc) {
        //  Get Root Element and Clone It
        Element root = doc.getRootElement();
        Element clonedRoot = (Element) root.clone();
        clonedRoot.removeContent();

        //  Get Root Document and Clone it
        //  The Root Element must be attached to some Document in order
        //  for XPath Queries to work.
        Document clonedDocument = (Document) doc.clone();
        clonedDocument.removeContent();
        clonedDocument.addContent(clonedRoot);

        return clonedRoot;
    }

    private void extractTarget(Element e, Element newRoot) {
        newRoot.addContent((Element) e.clone());

        List children = e.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);

            //  Get a pointer to an RDF resource, if there is one.
            Attribute pointerAttribute = child.getAttribute
                    (RdfConstants.RESOURCE_ATTRIBUTE,
                            RdfConstants.RDF_NAMESPACE);

            //  If we are pointing to something, push the new resource
            //  onto the stack.
            if (pointerAttribute != null) {
                String uri = RdfUtil.removeHashMark
                        (pointerAttribute.getValue());
                Element resource = (Element) resourceMap.get(uri);
                if (!visitedNodes.contains(uri)) {
                    stack.push(resource);
                    visitedNodes.add(uri);
                }
            }
        }
    }
}