package org.mskcc.pathdb.util.rdf;

import com.hp.hpl.jena.rdf.arp.ARP;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * RDF Validator Utility Class.
 *
 * Built on ARP (Another RDF Parser).  Full details available online at:
 * http://www.hpl.hp.com/personal/jjc/arp/
 *
 * ARP is used within Jena, and forms the basis of the W3C RDF Validation
 * service, available online at:  http://www.w3.org/RDF/Validator/.
 *
 * @author Ethan Cerami
 */
public class RdfValidator {
    private RdfErrorHandler errorHandler;

    /**
     * Constructor.
     * @param reader Reader Object.
     * @throws IOException InputOuput Exception.
     * @throws SAXException XML SAX Parsing Error.
     */
    public RdfValidator(Reader reader) throws IOException, SAXException {
        ARP arp = new ARP();
        arp.setStrictErrorMode();
        errorHandler = new RdfErrorHandler();
        arp.setErrorHandler(errorHandler);
        arp.load(reader);
    }

    /**
     * Returns true if the validator encountered any fatal errors, errors,
     * or warnings.
     *
     * @return true or false.
     */
    public boolean hasErrorsOrWarnings () {
        ArrayList allList = new ArrayList();
        allList.addAll(errorHandler.getFatalErrorList());
        allList.addAll(errorHandler.getErrorList());
        allList.addAll(errorHandler.getWarningList());
        if (allList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the Error Handler Object.
     * Provides easy access to all fatal errors, errors, and warnings.
     *
     * @return RdfErrorHandler Object.
     */
    public RdfErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Returns a multi-line String of all fata errors, errors, and warnings
     * (in that order).  Can be presented to the end user.
     *
     * @return multi-line String of error messages.
     */
    public String getReadableErrorList() {
        StringBuffer msg = new StringBuffer();
        ArrayList allList = new ArrayList();
        allList.addAll(errorHandler.getFatalErrorList());
        allList.addAll(errorHandler.getErrorList());
        allList.addAll(errorHandler.getWarningList());

        for (int i = 0; i < allList.size(); i++) {
            SAXParseException exception = (SAXParseException) allList.get(i);
            msg.append ("Error:  " + exception.getMessage());
            msg.append (" [Line=  " + exception.getLineNumber());
            msg.append (", Column=  " + exception.getColumnNumber());
            msg.append("]\n");
        }
        return msg.toString();
    }
}


