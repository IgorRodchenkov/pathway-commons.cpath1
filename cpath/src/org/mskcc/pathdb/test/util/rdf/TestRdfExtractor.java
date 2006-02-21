// $Id: TestRdfExtractor.java,v 1.6 2006-02-21 22:51:11 grossb Exp $
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
package org.mskcc.pathdb.test.util.rdf;

import junit.framework.TestCase;
import org.jdom.Attribute;
import org.jdom.Element;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.util.rdf.RdfExtractor;

import java.io.File;
import java.util.List;

/**
 * Tests the RdfExtractor Class.
 *
 * @author Ethan Cerami.
 */
public class TestRdfExtractor extends TestCase {

    /**
     * Tests the RDF Extractor.
     *
     * @throws Exception All Errors.
     */
    public void testRdfExtractor() throws Exception {
        File inFile = new File("testData/biopax/biopax1_sample1.owl");

        //  Extract Everything Needed to recreate smallMolecule18
        RdfExtractor extractor = new RdfExtractor(inFile, null,
                "smallMolecule18");

        Element root = extractor.getExtractedRoot();
        List children = root.getChildren();

        //  There should only be three children
        assertEquals(3, children.size());

        //  Validate the identity of all three children
        for (int i = 0; i < children.size(); i++) {
            Element e = (Element) children.get(i);

            //  Get a pointer to an RDF resource, if there is one.
            Attribute idAttribute = e.getAttribute
                    (RdfConstants.ID_ATTRIBUTE,
                            RdfConstants.RDF_NAMESPACE);
            String id = idAttribute.getValue();

            switch (i) {
                case 0:
                    assertEquals("smallMolecule18", id);
                    break;
                case 1:
                    assertEquals("dataSource14", id);
                    break;
                case 2:
                    assertEquals("KB_439584_Individual_47", id);
            }
        }
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the RDF Extractor Utility Class";
    }
}
