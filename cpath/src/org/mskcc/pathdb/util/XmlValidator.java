package org.mskcc.pathdb.util;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.PrintWriter;

/**
 * Command Line Tool XML Validator.
 *
 * @author Ethan Cerami
 */
public class XmlValidator extends DefaultHandler {

    /** Default parser name. */
    protected static final String DEFAULT_PARSER_NAME =
            "org.apache.xerces.parsers.SAXParser";

    // Validation feature id
    protected static final String VALIDATION_FEATURE_ID =
            "http://xml.org/sax/features/validation";

    //  Schema validation feature id.
    protected static final String SCHEMA_VALIDATION_FEATURE_ID =
            "http://apache.org/xml/features/validation/schema";

    // Schema full checking feature id
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID =
            "http://apache.org/xml/features/validation/schema-full-checking";

    // Dynamic validation feature id
    protected static final String DYNAMIC_VALIDATION_FEATURE_ID
            = "http://apache.org/xml/features/validation/dynamic";

    private int errorCounter = 0;

    /**
     * Prints the Results.
     * @param out PrintWriter Object.
     */
    public void printResults(PrintWriter out) {
        out.println("Total Number of Errors:  " + errorCounter);
        out.flush();

    }

    /**
     * Warning.
     * @param ex SAXParseException Object.
     * @throws SAXException SAXException.
     */
    public void warning(SAXParseException ex) throws SAXException {
        printError("Warning", ex);
    }

    /**
     * Error.
     * @param ex SAXParseException Object.
     * @throws SAXException SAXException.
     */
    public void error(SAXParseException ex) throws SAXException {
        printError("Error", ex);
    }

    /**
     * Fatal Error.
     * @param ex SAXParseException Object.
     * @throws SAXException SAXException.
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        printError("Fatal Error", ex);
    }

    /**
     * Prints the error message.
     */
    protected void printError(String type, SAXParseException ex) {
        errorCounter++;

        System.err.print("[");
        System.err.print(type);
        System.err.print("] ");
        if (ex == null) {
            System.out.println("!!!");
        }
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            System.err.print(systemId);
        }
        System.err.print(':');
        System.err.print(ex.getLineNumber());
        System.err.print(':');
        System.err.print(ex.getColumnNumber());
        System.err.print(": ");
        System.err.print(ex.getMessage());
        System.err.println();
        System.err.flush();

    }

    /**
     * Main program entry point.
     * @param argv Command Line Arguments.
     */
    public static void main(String argv[]) {

        if (argv.length == 0) {
            printUsage();
            System.exit(1);
        }

        XmlValidator validator = new XmlValidator();
        PrintWriter out = new PrintWriter(System.out);
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader
                    (DEFAULT_PARSER_NAME);
            parser.setFeature(VALIDATION_FEATURE_ID, true);
            parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
            parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
            parser.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, true);
            parser.setContentHandler(validator);
            parser.setErrorHandler(validator);
            parser.parse(argv[0]);
            validator.printResults(out);
        } catch (SAXNotRecognizedException e) {
            e.printStackTrace();
        } catch (SAXNotSupportedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the usage.
     */
    private static void printUsage() {
        System.out.println("usage: validate.sh file_name");
    }
}
