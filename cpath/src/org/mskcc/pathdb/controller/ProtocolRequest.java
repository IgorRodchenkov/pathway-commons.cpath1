package org.mskcc.pathdb.controller;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mskcc.pathdb.model.PagedResult;

import java.util.Map;

/**
 * Encapsulates Request object from client/browser application.
 *
 * @author Ethan Cerami
 */
public class ProtocolRequest implements PagedResult {
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
     * Start Index Argument.
     */
    public static final String ARG_START_INDEX = "startIndex";

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
     * Start Index.
     */
    private int startIndex;

    /**
     * EmptyParameterSet.
     */
    private boolean emptyParameterSet;

    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';


    /**
     * Constructor.
     */
    public ProtocolRequest() {
        this.version = "1.0";
        this.startIndex = 0;
    }

    /**
     * Constructor.
     * @param parameterMap Map of all Request Parameters.
     */
    public ProtocolRequest(Map parameterMap) {
        this.command = (String) parameterMap.get(ProtocolRequest.ARG_COMMAND);
        this.database = (String) parameterMap.get(ProtocolRequest.ARG_DB);
        this.uid = (String) parameterMap.get(ProtocolRequest.ARG_UID);
        this.uid = massageUid(uid);
        this.format = (String) parameterMap.get(ProtocolRequest.ARG_FORMAT);
        this.version = (String) parameterMap.get(ProtocolRequest.ARG_VERSION);
        String startStr =
                (String) parameterMap.get(ProtocolRequest.ARG_START_INDEX);
        if (startStr != null) {
            this.startIndex = Integer.parseInt(startStr);
        } else {
            this.startIndex = 0;
        }
        if (parameterMap.size() == 0) {
            emptyParameterSet = true;
        } else {
            emptyParameterSet = false;
        }
    }

    /**
     * Massages the UID such that No Database Error Occur.
     * 0.  Trim and make upper case.
     * 1.  Replace single quotes with double quotes.
     */
    private String massageUid(String temp) {
        if (temp != null && temp.length() > 0) {
            temp = temp.replace(SINGLE_QUOTE, DOUBLE_QUOTE);
            temp = temp.toUpperCase();
            return temp;
        } else {
            return null;
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
     * Sets Command Argument.
     * @param command Command Argument.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Sets Database Argument.
     * @param database Database Argument.
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * Sets the UID Argument.
     * @param uid UID Argument.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Sets the Format Argument.
     * @param format Format Argument.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets the Version Argument.
     * @param version Version Argument.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the Start Index.
     * @return Start Index.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Sets the Start Index.
     * @param startIndex Start Index Int.
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Is this an empty request?
     * @return true or false.
     */
    public boolean isEmpty() {
        return this.emptyParameterSet;
    }

    /**
     * Gets URI.
     * @return URI String.
     */
    public String getUri() {
        String uri = null;
        String url = "webservice";
        GetMethod method = new GetMethod(url);
        NameValuePair nvps[] = new NameValuePair[6];
        nvps[0] = new NameValuePair(ARG_VERSION, version);
        nvps[1] = new NameValuePair(ARG_COMMAND, command);
        nvps[2] = new NameValuePair(ARG_DB, database);
        nvps[3] = new NameValuePair(ARG_UID, uid);
        nvps[4] = new NameValuePair(ARG_FORMAT, format);
        nvps[5] = new NameValuePair(ARG_START_INDEX,
                Long.toString(startIndex));
        method.setQueryString(nvps);
        try {
            uri = method.getURI().getEscapedURI();
        } catch (URIException e) {
            uri = null;
        }
        return uri;
    }
}