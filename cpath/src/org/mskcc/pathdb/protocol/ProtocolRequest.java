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
package org.mskcc.pathdb.protocol;

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
     * Default Max Number of Hits.
     */
    public static final int DEFAULT_MAX_HITS = 10;

    /**
     * Command Argument.
     */
    public static final String ARG_COMMAND = "cmd";

    /**
     * Uid Argument.
     */
    public static final String ARG_QUERY = "q";

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
     * Max Hits Argument.
     */
    public static final String ARG_MAX_HITS = "maxHits";

    /**
     * Organism Argument.
     */
    public static final String ARG_ORGANISM = "organism";

    /**
     * Command.
     */
    private String command;

    /**
     * Query Parameter.
     */
    private String query;

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
     * Max Hits.
     */
    private String maxHits;

    /**
     * Organism Parameter.
     */
    private String organism;

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
        this.organism = "";
        this.maxHits = Integer.toString(DEFAULT_MAX_HITS);
    }

    /**
     * Constructor.
     *
     * @param parameterMap Map of all Request Parameters.
     */
    public ProtocolRequest(Map parameterMap) {
        this.command = (String) parameterMap.get(ProtocolRequest.ARG_COMMAND);
        this.query = (String) parameterMap.get(ProtocolRequest.ARG_QUERY);
        this.query = massageQuery(query);
        this.format = (String) parameterMap.get(ProtocolRequest.ARG_FORMAT);
        this.version = (String) parameterMap.get(ProtocolRequest.ARG_VERSION);
        this.maxHits = (String) parameterMap.get(ProtocolRequest.ARG_MAX_HITS);
        this.organism = (String) parameterMap.get
                (ProtocolRequest.ARG_ORGANISM);
        if (maxHits == null) {
            maxHits = Integer.toString(DEFAULT_MAX_HITS);
        }
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
    private String massageQuery(String temp) {
        if (temp != null && temp.length() > 0) {
            temp = temp.replace(SINGLE_QUOTE, DOUBLE_QUOTE);
            return temp;
        } else {
            return null;
        }
    }

    /**
     * Gets the Query value.
     *
     * @return query value.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets the Command value.
     *
     * @return command value.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets the Format value.
     *
     * @return format value.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets the Version value.
     *
     * @return version value.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets Command Argument.
     *
     * @param command Command Argument.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Sets the Query Argument.
     *
     * @param query Query Argument.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Sets the Format Argument.
     *
     * @param format Format Argument.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets the Version Argument.
     *
     * @param version Version Argument.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the Start Index.
     *
     * @return Start Index.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Sets the Start Index.
     *
     * @param startIndex Start Index Int.
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Gets Max Number of Hits.
     *
     * @return Max Number Hits
     */
    public int getMaxHitsInt() {
        if (maxHits.equals("unbounded")) {
            return Integer.MAX_VALUE;
        } else {
            return Integer.parseInt(maxHits);
        }
    }

    /**
     * Max hits (String value).
     *
     * @return Max Hits (String value.)
     */
    public String getMaxHits() {
        return this.maxHits;
    }

    /**
     * Sets Max Number of hits.
     *
     * @param str Max Number of Hits
     */
    public void setMaxHits(String str) {
        this.maxHits = str;
    }

    /**
     * Gets Organism.
     *
     * @return Organism String.
     */
    public String getOrganism() {
        return this.organism;
    }

    /**
     * Sets Organism.
     *
     * @param organism Organism String.
     */
    public void setOrganism(String organism) {
        this.organism = organism;
    }

    /**
     * Is this an empty request?
     *
     * @return true or false.
     */
    public boolean isEmpty() {
        return this.emptyParameterSet;
    }

    /**
     * Gets URI.
     *
     * @return URI String.
     */
    public String getUri() {
        GetMethod method = new GetMethod("webservice.do");
        return createUri(method);
    }

    /**
     * Gets URL Parameter String
     *
     * @return URL Parameter String.
     */
    public String getUrlParameterString() {
        GetMethod method = new GetMethod();
        return createUri(method).substring(1);
    }

    private String createUri(GetMethod method) {
        String uri;
        NameValuePair nvps[] = new NameValuePair[7];
        nvps[0] = new NameValuePair(ARG_VERSION, version);
        nvps[1] = new NameValuePair(ARG_COMMAND, command);
        nvps[2] = new NameValuePair(ARG_QUERY, query);
        nvps[3] = new NameValuePair(ARG_FORMAT, format);
        nvps[4] = new NameValuePair(ARG_START_INDEX,
                Long.toString(startIndex));
        nvps[5] = new NameValuePair(ARG_ORGANISM, organism);
        nvps[6] = new NameValuePair(ARG_MAX_HITS, maxHits);
        method.setQueryString(nvps);
        try {
            uri = method.getURI().getEscapedURI();
        } catch (URIException e) {
            uri = null;
        }
        return uri;
    }
}