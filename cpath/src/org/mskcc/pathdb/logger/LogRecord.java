package org.mskcc.pathdb.logger;

import java.util.Date;

/**
 * Encapsulates Basic Log Information.
 *
 * @author cerami
 */
public class LogRecord {
    private Date date;
    private String priority;
    private String logger;
    private String message;
    private String ip;

    /**
     * Constructor.
     *
     * @param date     Date Time Stamp.
     * @param priority Log Priority.
     * @param logger   Logger.
     * @param message  Message.
     * @param ip       remote IP Address.
     */
    public LogRecord(Date date, String priority, String logger,
            String message, String ip) {
        this.date = date;
        this.priority = priority;
        this.logger = logger;
        this.message = message;
        this.ip = ip;
    }

    /**
     * Gets Date Time Stamp
     *
     * @return Date object.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets Log Priority.
     *
     * @return Priority String.
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Gets Logger.
     *
     * @return Logger String.
     */
    public String getLogger() {
        return logger;
    }

    /**
     * Gets Log Message.
     *
     * @return Log Message String.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets Remote IP for Client.
     *
     * @return Remote IP Address.
     */
    public String getRemoteIp() {
        return this.ip;
    }
}