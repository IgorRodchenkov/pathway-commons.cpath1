// $Id: ErrorMessage.java,v 1.19 2006-12-23 04:19:53 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.taglib;

import org.apache.lucene.queryParser.ParseException;
import org.apache.struts.util.PropertyMessageResources;
import org.exolab.castor.xml.MarshalException;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.action.admin.AdminWebLogging;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.util.log.LogUtil;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;

/**
 * Outpus all Error Messages/Exceptions to the End User.
 *
 * @author Ethan Cerami
 */
public class ErrorMessage extends HtmlTable {
    private Throwable throwable;
    private static final String STRUTS_MESSAGE_RESOURCE =
            "org.apache.struts.action.MESSAGE";
    private static final String MSG_ERROR_HEADER =
            "error.header";
    private static final String MSG_ERROR_INTERNAL =
            "error.internal";
    private static final String MSG_ERROR_PARSING =
            "error.parsing";
    private static final String MSG_LUCENE_INDEX_NOT_FOUND =
            "error.luceneIndexNotFound";

    /**
     * Sets the Throwable object with error/exception information.
     *
     * @param throwable Throwable Object.
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Executes JSP Custom Tag
     */
    public void subDoStartTag() {
        boolean xdebugFlag = getXDebugFlag();
        ServletContext servletContext = pageContext.getServletContext();
        PropertyMessageResources resource =
                (PropertyMessageResources) servletContext.getAttribute
                        (STRUTS_MESSAGE_RESOURCE);

        String header = resource.getMessage(MSG_ERROR_HEADER);
        this.append("<div>");
        this.append("<h1>" + header + "</h1>");
        Throwable rootCause = getRootCause(throwable);
        outputUserMessage(rootCause, resource);
        logErrorMessage(rootCause);
        if (xdebugFlag) {
            outputDiagnostics(throwable, rootCause);
        }
        this.append("</div>");
    }

    /**
     * Logs the Error Message.
     *
     * @param rootCause Root Cause.
     */
    private void logErrorMessage(Throwable rootCause) {
        HttpServletRequest request = (HttpServletRequest)
                pageContext.getRequest();

        //  Get IP, Host, and Web URL.
        String url = (String) request.getAttribute
                (BaseAction.ATTRIBUTE_URL_BEFORE_FORWARDING);
        String host = request.getRemoteAddr();
        String ip = request.getRemoteHost();

        //  Log to Database or Catalina.out
        LogUtil.logException(rootCause, url, host, ip);
    }

    /**
     * Initializes Custom Tag.
     */
    private boolean getXDebugFlag() {
        boolean xdebugFlag = false;
        HttpSession session = pageContext.getSession();
        ServletRequest request = pageContext.getRequest();
        String xdebugSession = (String) session.getAttribute
                (AdminWebLogging.WEB_LOGGING);
        String xdebugParameter = request.getParameter
                (AdminWebLogging.WEB_LOGGING);
        if (xdebugSession != null || xdebugParameter != null) {
            xdebugFlag = true;
        }
        return xdebugFlag;
    }

    /**
     * Outputs User Message.
     */
    private void outputUserMessage(Throwable rootCause,
            PropertyMessageResources resource) {
        String userMsg = resource.getMessage(MSG_ERROR_INTERNAL);
        if (rootCause instanceof ParseException) {
            userMsg = resource.getMessage(MSG_ERROR_PARSING);
        } else if (rootCause instanceof ProtocolException) {
            ProtocolException pException = (ProtocolException) rootCause;
            userMsg = pException.getWebErrorMessage();
        } else if (rootCause instanceof FileNotFoundException) {
            userMsg = resource.getMessage(MSG_LUCENE_INDEX_NOT_FOUND);
        } else if (rootCause instanceof SAXParseException) {
            SAXParseException sexc = (SAXParseException) rootCause;
            userMsg = "Your XML document contains the "
                    + "following error:<p>" + rootCause.getMessage()
                    + "<p>Error occurred at line number:   "
                    + sexc.getLineNumber()
                    + "<p>Please correct the error and try again.";
        } else if (rootCause instanceof MarshalException) {
            MarshalException mexc = (MarshalException) rootCause;
            userMsg = "Your XML document contains the "
                    + "following error:<p>" + rootCause.getMessage()
                    + "<p>Please correct the error and try again.";
        } else if (rootCause instanceof IllegalArgumentException) {
            userMsg = "Illegal or missing argument:  " + rootCause.getMessage();
        }
        this.append(userMsg);
    }

    /**
     * Outputs Full Diagnostics.
     */
    private void outputDiagnostics(Throwable throwable, Throwable rootCause) {
        append("<p>Root Cause Message:  "
                + rootCause.getMessage());
        append("</p><p>Root Cause Class:  "
                + rootCause.getClass().getName() +"</p>");
        StringWriter writer = new StringWriter();
        PrintWriter pWriter = new PrintWriter(writer);
        throwable.printStackTrace(pWriter);
        append("<pre>\n"
                + writer.toString() + "\n</pre>");
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
