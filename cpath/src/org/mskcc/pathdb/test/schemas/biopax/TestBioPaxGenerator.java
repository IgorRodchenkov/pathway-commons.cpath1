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
package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxGenerator;

/**
 * Tests the BioPaxGenerator Class.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxGenerator extends TestCase {
    private String testName;

    /**
     * Tests the Relationship Xref Generator.
     *
     * @throws Exception All Exceptions.
     */
    public void testRelationshipXrefGenerator() throws Exception {
        testName = "Create Relationship XRefs";
        ExternalReference ref = new ExternalReference("AFFYMETRIX",
                "1919_at");

        Element refElement = BioPaxGenerator.generateRelationshipXref(ref,
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE);

        //  Element must be attached to a Doument for XPath Queries to work
        Document doc = new Document();
        doc.setRootElement(refElement);

        //  Verify that DB/ID are OK
        assertEquals("XREF", refElement.getName());
        XPath xpath = XPath.newInstance("bp:XREF/*/bp:DB");
        xpath.addNamespace("bp",
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element dbElement = (Element) xpath.selectSingleNode(doc);
        assertEquals("AFFYMETRIX", dbElement.getTextNormalize());

        assertEquals("XREF", refElement.getName());
        xpath = XPath.newInstance("bp:XREF/*/bp:ID");
        xpath.addNamespace("bp",
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element idElement = (Element) xpath.selectSingleNode(doc);
        assertEquals("1919_at", idElement.getTextNormalize());
    }

    /**
     * Tests the Unification Xref Generator.
     *
     * @throws Exception All Exceptions.
     */
    public void testUnificationXrefGenerator() throws Exception {
        testName = "Create Unifications XRefs";
        ExternalReference ref = new ExternalReference("CPATH", "1");

        Element refElement = BioPaxGenerator.generateRelationshipXref(ref,
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE);

        //  Element must be attached to a Doument for XPath Queries to work
        Document doc = new Document();
        doc.setRootElement(refElement);

        //  Verify that DB/ID are OK
        assertEquals("XREF", refElement.getName());
        XPath xpath = XPath.newInstance("bp:XREF/*/bp:DB");
        xpath.addNamespace("bp",
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element dbElement = (Element) xpath.selectSingleNode(doc);
        assertEquals("CPATH", dbElement.getTextNormalize());

        assertEquals("XREF", refElement.getName());
        xpath = XPath.newInstance("bp:XREF/*/bp:ID");
        xpath.addNamespace("bp",
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element idElement = (Element) xpath.selectSingleNode(doc);
        assertEquals("1", idElement.getTextNormalize());
    }

    /**
     * Gets Test Description.
     *
     * @return Description.
     */
    public String getName() {
        return "Test that we can generate new BioPAX Elements, such as XRefs:  " + testName;
    }
}
