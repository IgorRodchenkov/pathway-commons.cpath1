// $Id: TestXmlAssembly.java,v 1.9 2006-06-07 13:54:56 cerami Exp $
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
package org.mskcc.pathdb.test.sql.assembly;

import junit.framework.TestCase;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.OwlConstants;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.rdf.RdfValidator;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.StringReader;
import java.util.List;

/**
 * Tests the XML Assembly Functionality.
 * <p/>
 * <B>Note:</B>  In order for this test to succeed, the dbData/bootstrap.sql
 * file must already be loaded into the database.  To do so, simply type:
 * "ant boot" at the command line.
 *
 * @author Ethan Cerami
 */
public class TestXmlAssembly extends TestCase {
    private String testName;

    /**
     * Tests the PSI-MI Assembly Functionality.
     *
     * @throws Exception All Exceptions
     */
    public void testPsiAssembly() throws Exception {
        testName = "Test PSI-MI Assembly";
        PropertyManager pManager = PropertyManager.getInstance();
        pManager.setProperty(CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION,
                "http://psidev.sourceforge.net/mi/xml/src/MIF.xsd");

        //  Assemble Interaction with specified cPath ID (hard-coded value)
        DaoCPath daoCPath = DaoCPath.getInstance();
        CPathRecord record = daoCPath.getRecordById(4);
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (record, 1, XmlAssemblyFactory.XML_FULL, xdebug);
        String xmlAssembly = assembly.getXmlString();

        //  Verify that Assembled XML Record contains both interactors
        //  and references to those interactors.
        int interactor1 = xmlAssembly.indexOf("<fullName>60 kDa chaperonin");
        int interactor2 = xmlAssembly.indexOf("<fullName>major prion");
        int interactorRef1 = xmlAssembly.indexOf
                ("<proteinInteractorRef ref=\"2\"/>");
        int interactorRef2 = xmlAssembly.indexOf
                ("<proteinInteractorRef ref=\"3\"/>");
        assertTrue(interactor1 >= 1);
        assertTrue(interactor2 >= 1);
        assertTrue(interactorRef1 >= 1);
        assertTrue(interactorRef2 >= 1);

        //  Verify Schema Location
        int index = xmlAssembly.indexOf
                ("http://psidev.sourceforge.net/mi/xml/src/MIF.xsd");
        assertTrue(index > 0);

        //  Verify that CPathIDFilter Works Correctly
        String xml = assembly.getXmlStringWithCPathIdPrefix();
        interactor1 = xml.indexOf("<proteinInteractor id=\"CPATH-2\">");
        interactorRef1 = xml.indexOf
                ("<proteinInteractorRef ref=\"CPATH-2\"/>");
        interactorRef2 = xml.indexOf
                ("<proteinInteractorRef ref=\"CPATH-3\"/>");
        assertTrue(interactor1 >= 1);
        assertTrue(interactorRef1 >= 1);
        assertTrue(interactorRef2 >= 1);
    }

    /**
     * Test BioPAX Assembly:  XML_FULL.
     *
     * @throws Exception All Errors.
     */
    public void testBioPaxAssemblyFull() throws Exception {
        testName = "Test BioPAX Assembly (Full)";
        //  Assemble Interaction with specified cPath ID (hard-coded value)
        XDebug xdebug = new XDebug();

        DaoCPath daoCPath = DaoCPath.getInstance();
        CPathRecord record = daoCPath.getRecordById(7);
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (record, 1, XmlAssemblyFactory.XML_FULL, xdebug);
        String xmlAssembly = assembly.getXmlString();
        Element rootElement = (Element) assembly.getXmlObject();
        List children = rootElement.getChildren();

        //  We should have one OWL element, one catalysis element,
        //  one small molecule, one biochemical reaction, followed
        //  by several small molecules.
        Element child = (Element) children.get(0);
        assertEquals(OwlConstants.OWL_ONTOLOGY_ELEMENT, child.getName());
        child = (Element) children.get(1);
        assertEquals(BioPaxConstants.CATALYSIS, child.getName());
        child = (Element) children.get(2);
        assertEquals(BioPaxConstants.SMALL_MOLECULE, child.getName());
        child = (Element) children.get(3);
        assertEquals(BioPaxConstants.BIOCHEMICAL_REACTION, child.getName());
        child = (Element) children.get(4);
        assertEquals(BioPaxConstants.SMALL_MOLECULE, child.getName());
        child = (Element) children.get(5);
        assertEquals(BioPaxConstants.SMALL_MOLECULE, child.getName());
        child = (Element) children.get(6);
        assertEquals(BioPaxConstants.SMALL_MOLECULE, child.getName());

        //  Check that this is valid RDF
        StringReader reader = new StringReader(xmlAssembly);
        RdfValidator rdfValidator = new RdfValidator(reader);
        assertTrue(!rdfValidator.hasErrorsOrWarnings());

        //  Validate that Root RDF Element has an xml:base attribute
        Attribute baseAttribute =
                rootElement.getAttribute("base", Namespace.XML_NAMESPACE);
        assertEquals(CPathConstants.CPATH_HOME_URI, baseAttribute.getValue());
    }

    /**
     * Test BioPAX Assembly:  XML_ABBREV.
     *
     * @throws Exception All Errors.
     */
    public void testBioPaxAssemblyAbbrev() throws Exception {
        testName = "Test BioPAX Assembly (Abbreviated)";
        //  Assemble Interaction with specified cPath ID (hard-coded value)
        XDebug xdebug = new XDebug();
        long cpathIds[] = new long[1];
        cpathIds[0] = 7;
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly(cpathIds,
                XmlRecordType.BIO_PAX, 1, XmlAssemblyFactory.XML_ABBREV,
                xdebug);
        String xmlAssembly = assembly.getXmlString();

        Element rootElement = (Element) assembly.getXmlObject();
        List children = rootElement.getChildren();

        //  We should have one OWL Element, one Catalysis element, and
        //  that's it.  We shouldn't see the one protein elements and four small
        //  molecule elements that we saw in the above XML_FULL text
        Element child = (Element) children.get(0);
        assertEquals(OwlConstants.OWL_ONTOLOGY_ELEMENT, child.getName());
        child = (Element) children.get(1);
        assertEquals(BioPaxConstants.CATALYSIS, child.getName());
        assertEquals(2, children.size());

        //  Check that this is valid RDF
        StringReader reader = new StringReader(xmlAssembly);
        RdfValidator rdfValidator = new RdfValidator(reader);
        assertTrue(!rdfValidator.hasErrorsOrWarnings());

        //  Validate that Root RDF Element has an xml:base attribute
        Attribute baseAttribute =
                rootElement.getAttribute("base", Namespace.XML_NAMESPACE);
        assertEquals(CPathConstants.CPATH_HOME_URI, baseAttribute.getValue());
    }

    /**
     * Provides an explicit unit test for bug #1009:  "Cannot search for synonyms of any proteins"
     * @throws Exception All Exceptions.
     */
    public void testBioPaxAssemblyAbbrevForSynonyms () throws Exception {
        testName = "Verify that BioPAX Assembly contains synonyms";
        XDebug xdebug = new XDebug();
        long cpathIds[] = new long[1];
        cpathIds[0] = 90;
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly(cpathIds,
                XmlRecordType.BIO_PAX, 1, XmlAssemblyFactory.XML_ABBREV,
                xdebug);
        String xmlAssembly = assembly.getXmlString();
        int index = xmlAssembly.indexOf("bp:SYNONYMS");
        assertTrue (index > 0);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can generate BioPAX/PSI-MI XML Assemblies:  " + testName;
    }
}
