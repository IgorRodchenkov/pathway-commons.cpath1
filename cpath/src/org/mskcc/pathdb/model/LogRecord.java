package org.mskcc.pathdb.model;

import java.util.Date;

/**
 * Encapsulates Log Record Information.
 *
 * @author Ethan Cerami
 */
public class LogRecord {
    public static final String PRIORITY_ERROR = "ERROR";

    private static final String NA_STRING = "N/A";
    private Date date = new Date();
    private String priority = NA_STRING;
    private String message = NA_STRING;
    private String stackTrace = NA_STRING;
    private String webUrl = NA_STRING;
    private String remoteHost = NA_STRING;
    private String remoteIp = NA_STRING;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}