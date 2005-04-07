package org.mskcc.pathdb.util.rdf;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class RdfErrorHandler implements ErrorHandler {
    private ArrayList warningList = new ArrayList();
    private ArrayList errorList = new ArrayList();
    private ArrayList fatalErrorList = new ArrayList();

    public void warning(SAXParseException exception) throws SAXException {
        warningList.add(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        errorList.add(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        fatalErrorList.add(exception);
    }

    public ArrayList getWarningList() {
        return warningList;
    }

    public ArrayList getErrorList() {
        return errorList;
    }

    public ArrayList getFatalErrorList() {
        return fatalErrorList;
    }
}
