package org.mskcc.pathdb.test.util;


import junit.framework.TestCase;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.util.XmlValidator;

import java.util.ArrayList;

/**
 * Tests the XmlValidator Class.
 *
 * @author Ethan Cerami
 */
public class TestXmlValidator extends TestCase {

    /**
     * Tests XML Validator.
     *
     * @throws Exception All Exceptions.
     */
    public void testValidator() throws Exception {
        XmlValidator validator = new XmlValidator();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/dip_sample.xml");
        ArrayList errors = validator.validate(xml,
                "net:sf:psidev:mi http://www.cbio.mskcc.org/cpath/xml/MIF.xsd");
        assertEquals(0, errors.size());

        xml = reader.retrieveContent("testData/invalid.xml");
        errors = validator.validate(xml,
                "net:sf:psidev:mi http://www.cbio.mskcc.org/cpath/xml/MIF.xsd");
        assertTrue(errors.size() > 0);

        xml = reader.retrieveContent("testData/dip_no_schema_location.xml");
        errors = validator.validate(xml,
                "net:sf:psidev:mi http://psidev.sourceforge.net/mi/"
                + "xml/src/MIF.xsd");
        assertEquals(0, errors.size());
    }
}