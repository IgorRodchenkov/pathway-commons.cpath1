package org.mskcc.pathdb.task;

import org.mskcc.pathdb.util.XmlValidator;
import org.mskcc.dataservices.util.ContentReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Simple Command Line Tool for Validating XML.
 *
 * @author Ethan Cerami.
 */
public class ValidateXmlTask {
    private File file;

    /**
     * Constructor.
     * @param file XML File.
     */
    public ValidateXmlTask (File file) {
        this.file = file;
    }

    /**
     * Validates the XML File, and outputs error messages to the Standard
     * Console.
     * @return  true if valid;  false otherwise.
     * @throws IOException Input/Output Exception.
     * @throws SAXException XML SAX Error.
     */
    public boolean validate(boolean showErrors)
            throws IOException, SAXException {
        System.out.println("Validating XML File:   " + file.getAbsolutePath());
        XmlValidator validator = new XmlValidator();
        ContentReader reader = new ContentReader ();
        String xmlStr = reader.retrieveContent(file.getAbsolutePath());
        ArrayList errors = validator.validate(xmlStr);
        if (errors != null && errors.size() > 0) {
            System.out.println("XML File is Invalid.");
            for (int i=0; i<errors.size(); i++) {
                SAXParseException saxException =
                        (SAXParseException) errors.get(i);
                if (showErrors) {
                    System.out.println("Error #" + i + ":  " +
                            saxException.getMessage() + "  [Line:  "
                            + saxException.getLineNumber() + ", Column:  "
                            + saxException.getColumnNumber());
                }
            }
            return false;
        } else {
            System.out.println("XML File is Valid.");
            return true;
        }
    }
}
