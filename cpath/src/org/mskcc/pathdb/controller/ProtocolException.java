package org.mskcc.pathdb.controller;

import org.apache.log4j.Logger;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Encapsulates a Violation of the Data Service Protocol.
 *
 * @author cerami
 */
public class ProtocolException extends Exception {
    /**
     * Status Code Object.
     */
    private ProtocolStatusCode statusCode;

    /**
     * Error Message Details.
     */
    private String xmlErrorMessage;

    /**
     *  Error Message for Displaying to End User of Web Interface.
     */
    private String webErrorMessage;

    /**
     * Error XML Element Name.
     */
    private static final String ERROR_ELEMENT = "error";

    /**
     * Error Code XML Element Name.
     */
    private static final String ERROR_CODE_ELEMENT = "error_code";

    /**
     * Error Message XML Element Name.
     */
    private static final String ERROR_MESSAGE_ELEMENT = "error_msg";

    /**
     * Error Message XML Element Name.
     */
    private static final String ERROR_DETAILS_ELEMENT = "error_details";

    /**
     * Logger.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor.
     * @param statusCode Protocol Status Code.
     */
    public ProtocolException(ProtocolStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Constructor.
     * @param statusCode Protocol Status Code.
     * @param details Error Message Details.
     */
    public ProtocolException(ProtocolStatusCode statusCode, String details) {
        this.statusCode = statusCode;
        this.xmlErrorMessage = details;
    }

    /**
     * Constructor.
     * @param statusCode Protocol Status Code.
     * @param xmlErrorMessage Error Message Details (for xml user)
     * @param webErrorMessage Error Message Details (for web user)
     */
    public ProtocolException(ProtocolStatusCode statusCode,
            String xmlErrorMessage, String webErrorMessage) {
        this.statusCode = statusCode;
        this.xmlErrorMessage = xmlErrorMessage;
        this.webErrorMessage = webErrorMessage;
    }

    /**
     * Constructor.
     * @param statusCode Protocol Status Code.
     * @param e Root Exception.
     */
    public ProtocolException(ProtocolStatusCode statusCode, Exception e) {
        super(e);
        this.statusCode = statusCode;
        this.xmlErrorMessage = "Internal Error";
    }

    /**
     * Gets the Status Code.
     * @return Status Code.
     */
    public ProtocolStatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Gets XML Error Message
     * @return Error Details String.
     */
    public String getXmlErrorMessage() {
        return this.xmlErrorMessage;
    }

    /**
     * Gets Web Error Message
     * @return Error Details String.
     */
    public String getWebErrorMessage() {
        if (webErrorMessage != null) {
            return webErrorMessage;
        } else {
            return this.xmlErrorMessage;
        }
    }

    /**
     * Gets Error Message.
     * @return Error Message.
     */
    public String getMessage() {
        return new String(statusCode.getErrorCode() + ":  "
                + statusCode.getErrorMsg() + ": " + this.xmlErrorMessage);
    }

    /**
     * Gets XML Representation of Error.
     * @return XML String containing Error Message.
     */
    public String toXml() {
        Document document = createXmlDocument();
        XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent(true);
        outputter.setNewlines(true);
        StringWriter writer = new StringWriter();
        try {
            outputter.output(document, writer);
        } catch (IOException e) {
            logger.error
                    ("Exception thrown while outputting XML Error Protocol:  "
                    + e.getMessage());
        }
        return writer.toString();
    }

    /**
     * Creates JDOM Representation of XML Document.
     * @return JDOM Document object.
     */
    private Document createXmlDocument() {
        Document document = new Document();
        Element errorElement = new Element(ERROR_ELEMENT);
        Element errorCodeElement = new Element(ERROR_CODE_ELEMENT);
        errorCodeElement.setText(Integer.toString(statusCode.getErrorCode()));
        Element errorMsgElement = new Element(ERROR_MESSAGE_ELEMENT);
        errorMsgElement.setText(statusCode.getErrorMsg());
        document.setRootElement(errorElement);
        errorElement.addContent(errorCodeElement);
        errorElement.addContent(errorMsgElement);

        if (xmlErrorMessage != null) {
            Element errorDetailsElement = new Element(ERROR_DETAILS_ELEMENT);
            errorDetailsElement.setText(xmlErrorMessage);
            errorElement.addContent(errorDetailsElement);
        } else if (this.getCause() != null) {
            Element errorDetailsElement = new Element(ERROR_DETAILS_ELEMENT);
            StringWriter writer = new StringWriter();
            PrintWriter pwriter = new PrintWriter(writer);
            this.getCause().printStackTrace(pwriter);
            CDATA cdata = new CDATA(writer.toString());
            errorDetailsElement.addContent(cdata);
            errorElement.addContent(errorDetailsElement);
        }
        return document;
    }
}