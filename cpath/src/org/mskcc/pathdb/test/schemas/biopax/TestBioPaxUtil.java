// $Id: TestBioPaxUtil.java,v 1.19 2006-11-16 15:45:31 cerami Exp $
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
package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.schemas.biopax.BioPaxUtil;
import org.mskcc.pathdb.schemas.biopax.OwlConstants;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.util.rdf.RdfUtil;
import org.mskcc.pathdb.util.rdf.RdfValidator;

import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests the BioPax Utility Class.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxUtil extends TestCase {
    private Set rdfIdSet = new HashSet();
    private ProgressMonitor pMonitor = new ProgressMonitor();
    private String testName;

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(TestBioPaxUtil.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Tests a BioPAX Level 1 File that we know is "legal".
     *
     * @throws Exception All Exceptions.
     */
    public void testLegalLevel1File() throws Exception {
        testName = "Test a Legal Level 1 BioPAX File";
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample1.owl");
        BioPaxUtil util = new BioPaxUtil(file, false, pMonitor);

        assertEquals(1, util.getNumPathways());
        assertEquals(4, util.getNumInteractions());
        assertEquals(7, util.getNumPhysicalEntities());
        assertEquals(0, util.getErrorList().size());

        StringWriter writer = new StringWriter();
        Element pathway = util.getPathway(0);
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        out.output(pathway, writer);

        //  Validate that XPath queries work on individual resource elements
        XPath xpath = XPath.newInstance("//@rdf:resource");
        xpath.addNamespace("rdf", RdfConstants.RDF_NAMESPACE_URI);
        List links = xpath.selectNodes(pathway);
        assertEquals(6, links.size());

        //  Before the hierarchical transformation, we had something like this:
        //  <bp:STEP-INTERACTIONS>
        //  After the transformation, we should have something like this:
        //  <bp:STEP-INTERACTIONS rdf:resource="#catalysis43" />
        //  In other words, the internal resource is replaced with a link.
        assertTrue(writer.toString().indexOf
                ("<bp:STEP-INTERACTIONS rdf:resource=") > 1);

        //  Before the hierarchical transformation, we had something like this:
        //  <bp:DATA-SOURCE rdf:resource="#dataSource14"/>
        //  After the transformation, we should have something like this:
        //  <bp:dataSource rdf:ID="dataSource14">
        //  In other words, the link to the resource is replaced with the actual
        //  resource
        assertTrue(writer.toString().indexOf
                ("<bp:dataSource rdf:ID=") > 1);

        //  Before the hierarchical transformation, we had something like this:
        //  <bp:ORGANISM rdf:resource="#bioSource33"/>
        //  After the transformation, we should have something like this:
        //  <bp:ORGANISM/>
        //     <bp:bioSource rdf:ID="bioSource33">
        Element organism = (Element) XPath.selectSingleNode
                (pathway, "//bp:pathway/bp:ORGANISM");
        List children = organism.getChildren();
        assertTrue("ORANISM Element should have an RDF Resource child.",
                children.size() > 0);

        //  Ensure that there are no duplicate RDF IDs.
        //  If we have an duplicate RDF IDs, the file is not valid RDF,
        //  and the unit test will fail.
        checkForDuplicateRdfIds(pathway);

        //  Validate that the resulting document is valid RDF
        writer = new StringWriter();
        Document doc = new Document();
        Element rdfElement = new Element ("RDF", RdfConstants.RDF_NAMESPACE);
        rdfElement.addContent((Element) pathway.clone());
        doc.addContent(rdfElement);
        out.output(doc, writer);
        StringReader reader = new StringReader(writer.toString());
        RdfValidator rdfValidator = new RdfValidator(reader);
        assertTrue("Newly generated XML document contains invalid RDF",
                !rdfValidator.hasErrorsOrWarnings());

        //  Test the extractXrefs Method
        //  We should find a total of 3 XRefs.
        ExternalReference refs[] = util.extractXrefs(pathway);
        assertEquals(3, refs.length);
        ExternalReference ref0 = refs[0];
        ExternalReference ref1 = refs[1];
        ExternalReference ref2 = refs[2];
        assertEquals("aMAZE", ref0.getDatabase());
        assertEquals("aMAZEProcess0000000027", ref0.getId());
        assertEquals("PubMed", ref1.getDatabase());
        assertEquals("2549346", ref1.getId());
        assertEquals("Reactome", ref2.getDatabase());
        assertEquals("69091", ref2.getId());

        //  Test the extractUnificationXrefs Method
        //  We should find a total of 2 Xrefs.
        ExternalReference unificationRefs[] =
                util.extractUnificationXrefs(pathway);
        assertEquals(2, unificationRefs.length);
        ref0 = unificationRefs[0];
        ref1 = unificationRefs[1];
        assertEquals("aMAZE", ref0.getDatabase());
        assertEquals("aMAZEProcess0000000027", ref0.getId());
        assertEquals("Reactome", ref1.getDatabase());
        assertEquals("69091", ref1.getId());

    }

    /**
     * Tests a BioPAX Level 1 Files that we know is "illegal".
     * The file used contains a pointer to an RDF resource which does not
     * exist in the document.
     *
     * @throws Exception All Exceptions.
     */
    public void testInvalidRdfLinks() throws Exception {
        testName = "Detect Invalid RDF Links";
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample2.owl");
        BioPaxUtil util = new BioPaxUtil(file, true, pMonitor);

        ArrayList errorList = util.getErrorList();

        assertEquals(1, util.getNumPathways());
        assertEquals(4, util.getNumInteractions());
        assertEquals(7, util.getNumPhysicalEntities());
        assertEquals(1, errorList.size());
        String error = (String) errorList.get(0);
        assertEquals("Element:  PHYSICAL-ENTITY [resource:#smallMolecule2300] references:  " +
                "smallMolecule2300, but no such resource exists in document.", error);
    }

    /**
     * Tests a BioPAX Level 1 Files that we know is "illegal".
     * The file used contains redundant RDF IDs.
     *
     * @throws Exception All Exceptions.
     */
    public void testRedundantRdfIds() throws Exception {
        testName = "Detect Redundant RDF IDs";
        FileReader file = new FileReader("testData/biopax/biopax1_sample3.owl");
        BioPaxUtil util = new BioPaxUtil(file, false, pMonitor);

        ArrayList errorList = util.getErrorList();

        assertEquals(1, util.getNumPathways());
        assertEquals(4, util.getNumInteractions());
        assertEquals(7, util.getNumPhysicalEntities());
        assertEquals(1, errorList.size());
        String error = (String) errorList.get(0);
        assertEquals("Element:  [Element: <bp:smallMolecule "
                + "[Namespace: http://www.biopax.org/release/biopax-level1.owl#]/>] "
                + "declares RDF ID/ABOUT:  smallMolecule23, but a resource with this "
                + "ID/ABOUT already exists.", error);
    }

    /**
     * Tests a BioPAX Level 1 Files that we know is "illegal".
     * The file used references a database which does not exit in cPath.
     *
     * @throws Exception All Exceptions.
     */
    public void testInvalidDatabaseXrefs() throws Exception {
        testName = "Detect Invalid Database XRefs";
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample4.owl");
        BioPaxUtil util = new BioPaxUtil(file, true, pMonitor);
        ArrayList errorList = util.getErrorList();

        assertTrue(errorList.size() > 0);
        String error = (String) errorList.get(0);
        assertTrue(error.startsWith("XREF Element references a database "
                + "which does not exist in cPath:  GLUE."));
    }

    /**
     * Tests a Sample Circular Pathway.
     * Previously, this test resulted in an infinite loop.
     *
     * @throws Exception All Exceptions.
     */
    public void testCircularPathway() throws Exception {
        testName = "Detect Circular Pathways";
        FileReader file = new FileReader
                ("testData/biopax/circular_example.owl");
        BioPaxUtil util = new BioPaxUtil(file, false, pMonitor);
    }

    /**
     * Checks for Duplicate RDF IDs.  Test will fail if any duplicates
     * are found.
     */
    private void checkForDuplicateRdfIds(Element e) {
        //  Get an RDF ID Attribute, if there is one
        Attribute idAttribute = e.getAttribute
                (RdfConstants.ID_ATTRIBUTE, RdfConstants.RDF_NAMESPACE);

        if (idAttribute != null) {
            String key = idAttribute.getValue();
            if (rdfIdSet.contains(key)) {
                fail("Document contains duplicate RDF IDs:  " + key);
            }
            rdfIdSet.add(key);
        }

        //  Traverse through all children.
        List children = e.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            checkForDuplicateRdfIds(child);
        }
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the BioPAX Utility Class:  " + testName;
    }
}
