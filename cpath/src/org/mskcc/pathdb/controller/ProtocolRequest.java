package org.mskcc.pathdb.controller;

import java.util.Map;

/**
 * Encapsulates Request object from client/browser application.
 *
 * @author Ethan Cerami
 */
public class ProtocolRequest {
    /**
     * Command.
     */
    private String command;

    /**
     * Database.
     */
    private String database;

    /**
     * UID.
     */
    private String uid;

    /**
     * Format.
     */
    private String format;

    /**
     * Version.
     */
    private String version;

    /**
     * Constructor.
     * @param parameterMap Map of all Request Parameters.
     */
    public ProtocolRequest(Map parameterMap) {
        this.command = (String) parameterMap.get("cmd");
        this.database = (String) parameterMap.get("db");
        this.uid = (String) parameterMap.get("uid");
        this.format = (String) parameterMap.get("format");
        this.version = (String) parameterMap.get("version");
    }

    /**
     * Gets the UID value.
     * @return uid value.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Gets the Command value.
     * @return command value.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets the Database value.
     * @return database value.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the Format value.
     * @return format value.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets the Version value.
     * @return version value.
     */
    public String getVersion() {
        return version;
    }
}