package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Abstract Custom Tag for outputting HTML Tables.
 *
 * @author Ethan Cerami.
 */
public abstract class HtmlTable extends TagSupport {
    private StringBuffer html = new StringBuffer();

    /**
     * Executes JSP Custom Tag
     * @return SKIP_BODY Option.
     * @throws JspException Exception in writing to JspWriter.
     */
    public int doStartTag() throws JspException {
        JspWriter out = null;
        html = new StringBuffer();
        if (pageContext != null) {
            out = pageContext.getOut();
        }
        try {
            subDoStartTag();
            if (out != null) {
                out.println(html.toString());
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
            } catch (Exception exc) {
                e.printStackTrace();
            }
            throw new JspException(e.getMessage(), e);
        }
        return TagSupport.SKIP_BODY;
    }

    /**
     * Must be implemented by subclass.
     * @throws Exception All Exceptions.
     */
    protected abstract void subDoStartTag() throws Exception;

    /**
     * Starts HTML Table.
     */
    protected void startTable(String title) {
        append("<table valign=top width=100% cellpadding=7 border=0 "
                + "cellspacing=0>"
                + "<tr><td colspan=4 bgcolor=#666699><u>"
                + "<b><big>" + title + "</big>"
                + "</b></u><br></td></tr>");
    }


    /**
     * Appends to String Buffer.
     */
    protected void append(String text) {
        html.append(text + "\n");
    }

    /**
     * Start New Html Row.
     */
    protected void startRow() {
        html.append("<TR>\n");
    }

    /**
     * Ends Html Row.
     */
    protected void endRow() {
        html.append("</TR>\n");
    }

    /**
     * Ends HTML Table.
     */
    protected void endTable() {
        append("</table>");
    }

    /**
     * Creates Table Headers.
     * @param headers Array of String headers.
     */
    protected void createTableHeaders(String[] headers) {
        append("<tr bgcolor=#9999cc>");
        for (int i = 0; i < headers.length; i++) {
            append("<TD><font color=#333366>");
            append(headers[i]);
            append("</font></TD>");
        }
        append("</TR>");
    }

    /**
     * Outputs Individial Data Field.
     */
    protected void outputDataField(Object data) {
        outputDataField(data, null);
    }

    /**
     * Outputs Individual Data Field (with URL Link).
     */
    protected void outputDataField(Object data, String url) {
        if (data != null) {
            append("<TD VALIGN=TOP>");
            if (url == null) {
                html.append(data);
            } else {
                outputLink(data.toString(), url);
            }
            append("</TD>");
        } else {
            append("<TD>----</TD>");
        }
    }

    /**
     * Outputs Link.
     */
    protected void outputLink(String name, String url) {
        append("<A HREF=\"" + url + "\">");
        append(name);
        append("</A>");
    }

    /**
     * Outputs Link with Alt Tag
     */
    protected void outputLink(String name, String url, String alt) {
        append("<A TITLE=\"" + alt + "\" HREF=\"" + url + "\">");
        append(name);
        append("</A>");
    }

    /**
     * Gets HTML String.
     * Primarily used by the JUnit Test Case Class.
     * @return HTML String.
     */
    public String getHtml() {
        return html.toString();
    }

    /**
     * Gets Internal Link to "get interactions".
     * @param id Unique ID.
     * @return URL back to CPath.
     */
    protected String getInteractionLink(String id, String format) {
        ProtocolRequest request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID);
        request.setVersion(ProtocolConstants.CURRENT_VERSION);
        request.setFormat(format);
        request.setQuery(id);
        return request.getUri();
    }
}