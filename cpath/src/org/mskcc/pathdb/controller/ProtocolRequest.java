package org.mskcc.pathdb.controller;

import java.util.Map;

/**
 * Encapsulates Request object from client/browser application.
 *
 * @author Ethan Cerami
 */
public class ProtocolRequest {
    /**
     * Command Argument.
     */
    public static final String ARG_COMMAND = "cmd";

    /**
     * Database Argument.
     */
    public static final String ARG_DB = "db";

    /**
     * Uid Argument.
     */
    public static final String ARG_UID = "uid";

    /**
     * Format Argument.
     */
    public static final String ARG_FORMAT = "format";

    /**
     * Version Argument.
     */
    public static final String ARG_VERSION = "version";

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
     * EmptyParameterSet.
     */
    private boolean emptyParameterSet;

    /**
     * Constructor.
     * @param parameterMap Map of all Request Parameters.
     */
    public ProtocolRequest(Map parameterMap) {
        this.command = (String) parameterMap.get(ProtocolRequest.ARG_COMMAND);
        this.database = (String) parameterMap.get(ProtocolRequest.ARG_DB);
        this.uid = (String) parameterMap.get(ProtocolRequest.ARG_UID);
        this.format = (String) parameterMap.get(ProtocolRequest.ARG_FORMAT);
        this.version = (String) parameterMap.get(ProtocolRequest.ARG_VERSION);

        if (parameterMap.size() == 0) {
            emptyParameterSet = true;
        } else {
            emptyParameterSet = false;
        }
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

    /**
     * Is this an empty request?
     * @return true or false.
     */
    public boolean isEmpty() {
        return this.emptyParameterSet;
    }
}