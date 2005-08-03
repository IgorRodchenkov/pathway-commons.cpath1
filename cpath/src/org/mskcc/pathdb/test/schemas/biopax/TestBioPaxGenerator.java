package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.schemas.biopax.BioPaxGenerator;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.util.xml.XmlUtil;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.xpath.XPath;

import java.util.List;

/**
 * Tests the BioPaxGenerator Class.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxGenerator extends TestCase {

    /**
     * Tests the Xref Generator.
     * @throws Exception All Exceptions.
     */
    public void testXrefGenerator() throws Exception {
        ExternalReference ref = new ExternalReference ("AFFYMETRIX",
                "1919_at");

        Element refElement = BioPaxGenerator.generateRelationshipXref(ref,
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE);

        //  Element must be attached to a Doument for XPath Queries to work
        Document doc = new Document ();
        doc.setRootElement(refElement);

        //  Verify that DB/ID are OK
        assertEquals ("XREF", refElement.getName());
        XPath xpath = XPath.newInstance("bp:XREF/*/bp:DB");
        xpath.addNamespace("bp",
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element dbElement = (Element) xpath.selectSingleNode(doc);
        assertEquals ("AFFYMETRIX", dbElement.getTextNormalize());

        assertEquals ("XREF", refElement.getName());
        xpath = XPath.newInstance("bp:XREF/*/bp:ID");
        xpath.addNamespace("bp",
                BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element idElement = (Element) xpath.selectSingleNode(doc);
        assertEquals ("1919_at", idElement.getTextNormalize());
    }
}
