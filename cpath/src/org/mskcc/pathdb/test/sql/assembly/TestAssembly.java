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
package org.mskcc.pathdb.test.sql.assembly;

import junit.framework.TestCase;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.rdf.RdfValidator;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.OwlConstants;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Attribute;

import java.util.List;
import java.io.StringReader;

/**
 * Tests the XML Assembly Functionality.
 * <p/>
 * <B>Note:</B>  In order for this test to succeed, the dbData/bootstrap.sql
 * file must already be loaded into the database.  To do so, simply type:
 * "ant boot" at the command line.
 *
 * @author Ethan Cerami
 */
public class TestAssembly extends TestCase {

    /**
     * Tests the PSI-MI Assembly Functionality.
     *
     * @throws Exception All Exceptions
     */
    public void testPsiAssembly() throws Exception {
        PropertyManager pManager = PropertyManager.getInstance();
        pManager.setProperty(CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION,
                "http://psidev.sourceforge.net/mi/xml/src/MIF.xsd");

        //  Assemble Interaction with specified cPath ID (hard-coded value)
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (4, 1, xdebug);
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
     * Test BioPAX Assembly.
     * @throws Exception All Errors.
     */
    public void testBioPaxAssembly () throws Exception {
        //  Assemble Interaction with specified cPath ID (hard-coded value)
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (7, 1, xdebug);
        String xmlAssembly = assembly.getXmlString();
        Element rootElement = (Element) assembly.getXmlObject();
        List children = rootElement.getChildren();

        //  We should have one Catalsis element, one Protein element,
        //  and four Small Molecule elements.
        Element child = (Element) children.get(0);
        assertEquals (OwlConstants.OWL_ONTOLOGY_ELEMENT, child.getName());
        child = (Element) children.get(1);
        assertEquals (BioPaxConstants.CATAYLSIS, child.getName());
        child = (Element) children.get(2);
        assertEquals (BioPaxConstants.PROTEIN, child.getName());
        child = (Element) children.get(3);
        assertEquals (BioPaxConstants.SMALL_MOLECULE, child.getName());
        child = (Element) children.get(4);
        assertEquals (BioPaxConstants.SMALL_MOLECULE, child.getName());
        child = (Element) children.get(5);
        assertEquals (BioPaxConstants.SMALL_MOLECULE, child.getName());
        child = (Element) children.get(6);
        assertEquals (BioPaxConstants.SMALL_MOLECULE, child.getName());

        //  Check that this is valid RDF
        StringReader reader = new StringReader (xmlAssembly);
        RdfValidator rdfValidator = new RdfValidator(reader);
        assertTrue (!rdfValidator.hasErrorsOrWarnings());

        //  Validate that Root RDF Element has an xml:base attribute
        Attribute baseAttribute =
                rootElement.getAttribute("base", Namespace.XML_NAMESPACE);
        assertEquals (CPathConstants.CPATH_HOME_URI, baseAttribute.getValue());
        //        System.out.print(xmlAssembly);
    }
}