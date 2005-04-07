/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.protocol;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;

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
     * Error Message for Displaying to End User of Web Interface.
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
     * Constructor.
     *
     * @param statusCode Protocol Status Code.
     */
    public ProtocolException(ProtocolStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Constructor.
     *
     * @param statusCode Protocol Status Code.
     * @param details    Error Message Details.
     */
    public ProtocolException(ProtocolStatusCode statusCode, String details) {
        this.statusCode = statusCode;
        this.xmlErrorMessage = details;
    }

    /**
     * Constructor.
     *
     * @param statusCode      Protocol Status Code.
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
     *
     * @param statusCode Protocol Status Code.
     * @param e          Root Exception.
     */
    public ProtocolException(ProtocolStatusCode statusCode, Exception e) {
        super(e);
        this.statusCode = statusCode;
    }

    /**
     * Gets the Status Code.
     *
     * @return Status Code.
     */
    public ProtocolStatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Gets XML Error Message
     *
     * @return Error Details String.
     */
    public String getXmlErrorMessage() {
        return this.xmlErrorMessage;
    }

    /**
     * Gets Web Error Message
     *
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
     *
     * @return Error Message.
     */
    public String getMessage() {
        return new String(statusCode.getErrorCode() + ":  "
                + statusCode.getErrorMsg() + ": " + this.xmlErrorMessage);
    }

    /**
     * Gets XML Representation of Error.
     *
     * @return XML String containing Error Message.
     */
    public String toXml() {
        Document document = createXmlDocument();
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        StringWriter writer = new StringWriter();
        try {
            outputter.output(document, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * Creates JDOM Representation of XML Document.
     *
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

        Throwable rootCause = getRootCause(this);
        if (xmlErrorMessage != null) {
            Element errorDetailsElement = new Element(ERROR_DETAILS_ELEMENT);
            errorDetailsElement.setText(xmlErrorMessage);
            errorElement.addContent(errorDetailsElement);
        } else if (rootCause != null) {
            Element errorDetailsElement = new Element(ERROR_DETAILS_ELEMENT);
            StringWriter writer = new StringWriter();
            PrintWriter pwriter = new PrintWriter(writer);
            rootCause.printStackTrace(pwriter);
            CDATA cdata = new CDATA(writer.toString());
            errorDetailsElement.addContent(cdata);
            errorElement.addContent(errorDetailsElement);
        }
        return document;
    }

    /**
     * Gets the Root Cause of this Exception.
     */
    private Throwable getRootCause(Throwable throwable) {
        Stack stack = new Stack();
        stack.push(throwable);
        try {
            Throwable temp = throwable.getCause();
            while (temp != null) {
                stack.push(temp);
                temp = temp.getCause();
            }
            return (Throwable) stack.pop();
        } catch (NullPointerException e) {
            return throwable;
        }
    }
}