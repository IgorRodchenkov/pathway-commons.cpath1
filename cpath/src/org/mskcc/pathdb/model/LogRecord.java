package org.mskcc.pathdb.model;

import java.util.Date;

/**
 * Encapsulates Log Record Information.
 *
 * @author Ethan Cerami
 */
public class LogRecord {
    /**
     * Log Priority:  Error.
     */
    public static final String PRIORITY_ERROR = "ERROR";

    private static final String NA_STRING = "N/A";
    private Date date = new Date();
    private String priority = NA_STRING;
    private String message = NA_STRING;
    private String stackTrace = NA_STRING;
    private String webUrl = NA_STRING;
    private String remoteHost = NA_STRING;
    private String remoteIp = NA_STRING;

    /**
     * Gets Date/Time Stamp.
     *
     * @return Date Object.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets Date/Time Stamp.
     *
     * @param date Date Object.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets Log Priority, e.g. PRIORITY_ERROR
     *
     * @return Log Priority.
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Gets Log Priority, e.g. PRIORITY_ERROR.
     *
     * @param priority Log Priority.
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Gets Log Message.
     *
     * @return Log Message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets Log Message.
     *
     * @param message Log Message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets Stack Trace.
     *
     * @return Stack Trace.
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets Stack Trace.
     *
     * @param stackTrace Stack Trace.
     */
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    /**
     * Gets Web URL assocated with Log Message.
     *
     * @return Web URL.
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * Sets Web URL associated with Log Message.
     *
     * @param webUrl Web URL.
     */
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    /**
     * Gets Remote Host associated with Log Message.
     *
     * @return Remote Host Name.
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Sets Remote Host associated with Log Message.
     *
     * @param remoteHost Remote Host Name.
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /**
     * Gets Remote IP Addess associated with Log Message.
     *
     * @return Remote IP Address.
     */
    public String getRemoteIp() {
        return remoteIp;
    }

    /**
     * Sets Remote IP Address associated with Log Message.
     *
     * @param remoteIp Remote IP Address.
     */
    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}