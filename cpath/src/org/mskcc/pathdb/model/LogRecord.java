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
        if (priority == null) {
            return NA_STRING;
        } else {
            return priority;
        }
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
        if (message == null) {
            return NA_STRING;
        } else {
            return message;
        }
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
        if (stackTrace == null) {
            return NA_STRING;
        } else {
            return stackTrace;
        }
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
        if (webUrl == null) {
            return NA_STRING;
        } else {
            return webUrl;
        }
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
        if (remoteIp == null) {
            return NA_STRING;
        } else {
            return remoteIp;
        }
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