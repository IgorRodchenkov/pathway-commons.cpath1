package org.mskcc.pathdb.util;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * XML Validator Utility.
 *
 * @author Ethan Cerami
 */
public class XmlValidator extends DefaultHandler {

    // Default parser name
    protected static final String DEFAULT_PARSER_NAME =
            "org.apache.xerces.parsers.SAXParser";

    // Validation feature id
    protected static final String VALIDATION_FEATURE_ID =
            "http://xml.org/sax/features/validation";

    //  Schema validation feature id.
    protected static final String SCHEMA_VALIDATION_FEATURE_ID =
            "http://apache.org/xml/features/validation/schema";

    // Dynamic validation feature id
    protected static final String DYNAMIC_VALIDATION_FEATURE_ID
            = "http://apache.org/xml/features/validation/dynamic";

    protected static final String EXTERNAL_SCHEMA_LOCATION
            = "http://apache.org/xml/properties/schema/external-schemaLocation";

    private ArrayList errorList;

    /**
     * Validates Specified Document.
     *
     * @param xml XML Document (String Representation).
     * @return ArrayList of SAXExceptions (if any).
     * @throws SAXException Error Parsing Document.
     * @throws IOException  Error Reading Document.
     */
    public ArrayList validate(String xml) throws SAXException, IOException {
        return execute(xml, null);
    }

    /**
     * Validates Specified Document, using designated schema location.
     *
     * @param xml            XML Document (String Representation).
     * @param schemaLocation Schema Location.
     * @return ArrayList of SAXExceptions (if any).
     * @throws SAXException Error Parsing Document.
     * @throws IOException  Error Reading Document.
     */
    public ArrayList validate(String xml, String schemaLocation)
            throws SAXException, IOException {
        return execute(xml, schemaLocation);
    }

    private ArrayList execute(String xmlData, String schemaLocation)
            throws IOException {
        errorList = new ArrayList();
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader
                    (DEFAULT_PARSER_NAME);
            parser.setFeature(VALIDATION_FEATURE_ID, true);
            parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
            if (schemaLocation != null) {
                parser.setProperty(EXTERNAL_SCHEMA_LOCATION, schemaLocation);
            }
            parser.setContentHandler(this);
            parser.setErrorHandler(this);

            StringReader reader = new StringReader(xmlData);
            InputSource source = new InputSource(reader);
            parser.parse(source);
        } catch (SAXException e) {
            errorList.add(e);
        }
        return errorList;
    }

    /**
     * Error.
     *
     * @param ex SAXParseException Object.
     * @throws SAXException SAXException.
     */
    public void error(SAXParseException ex) throws SAXException {
        errorList.add(ex);
    }

    /**
     * Fatal Error.
     *
     * @param ex SAXParseException Object.
     * @throws SAXException SAXException.
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        errorList.add(ex);
    }
}