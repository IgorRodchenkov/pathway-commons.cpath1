package org.mskcc.pathdb.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;

/**
 * HtmlFormatter for Outputting LogRecords to HTML Table.
 * This is easier to read than the default SimpleFormatter provided
 * by the Sun Logging API.
 * See /jsp/log.jsp for the JSP page that goes with this.
 *
 * @author Ethan Cerami
 */
public class HtmlFormatter extends Formatter {

    /**
     * Gets HTML Format.
     * @param logRecord Log Record.
     * @return Html String.
     */
    public String format(LogRecord logRecord) {
        StringBuffer out = new StringBuffer();
        out.append("<TR>");
        Date date = new Date (logRecord.getMillis());
        outputField(out, date.toString());
        outputField(out, logRecord.getLevel().getName());
        outputField(out, logRecord.getSourceClassName());
        outputField(out, logRecord.getMessage());
        out.append("</TR>");
        out.append("\n");
        return out.toString();
    }

    /**
     * Outputs HTML Field.
     * @param out StringBuffer object.
     * @param value Value to output.
     */
    private void outputField(StringBuffer out, String value) {
        out.append("<TD>");
        out.append(value);
        out.append("</TD>");
    }
}