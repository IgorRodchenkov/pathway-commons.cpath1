package org.mskcc.pathdb.test.util.rdf;

import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.Attribute;
import org.mskcc.pathdb.util.rdf.RdfExtractor;
import org.mskcc.pathdb.schemas.biopax.RdfConstants;

import java.io.File;
import java.util.List;

/**
 * Tests the RdfExtractor Class.
 *
 * @author Ethan Cerami.
 */
public class TestRdfExtractor extends TestCase {

    public void testRdfExtractor() throws Exception {
        File inFile = new File("testData/biopax/biopax1_sample1.owl");

        //  Extract Everything Needed to recreate smallMolecule18
        RdfExtractor extractor = new RdfExtractor(inFile, null,
                "smallMolecule18");

        Element root = extractor.getExtractedRoot();
        List children = root.getChildren();

        //  There should only be three children
        assertEquals (3, children.size());

        //  Validate the identity of all three children
        for (int i=0; i<children.size(); i++) {
            Element e = (Element) children.get(i);

            //  Get a pointer to an RDF resource, if there is one.
            Attribute idAttribute = e.getAttribute
                    (RdfConstants.ID_ATTRIBUTE,
                            RdfConstants.RDF_NAMESPACE);
            String id = idAttribute.getValue();

            switch (i) {
                case 0:
                    assertEquals ("smallMolecule18", id);
                    break;
                case 1:
                    assertEquals ("dataSource14", id);
                    break;
                case 2:
                    assertEquals ("KB_439584_Individual_47", id);
            }
        }
    }

    /**
     * Gets Name of Test.
     * @return Name of Test.
     */
    public String getName() {
        return "Tests the RDF Extractor Utility Class";
    }
}
