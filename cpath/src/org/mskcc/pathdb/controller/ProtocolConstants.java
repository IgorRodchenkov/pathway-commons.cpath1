package org.mskcc.pathdb.controller;

import java.util.HashMap;

/**
 * Protocol Constants.
 *
 * @author Ethan Cerami
 */
public class ProtocolConstants {
    /**
     * HashMap of Valid Commands.
     */
    private static HashMap validCommands;

    /**
     * HashMap of Valid Formats.
     */
    private static HashMap validFormats;

    /**
     * HashMap of Valid Databases.
     */
    private static HashMap validDatabases;

    /**
     * GRID Database.
     */
    public static final String DATABASE_GRID = "grid";

    /**
     * PSI Format.
     */
    public static final String FORMAT_PSI = "psi";

    /**
     * PSI Format.
     */
    public static final String FORMAT_HTML = "html";

    /**
     * Default Result Set Format.
     */
    public static final String FORMAT_RS = "rs";

    /**
     * Currently Supported Version.
     */
    public static final String CURRENT_VERSION = "1.0";

    /**
     * Retrieve Interaction Command.
     */
    public static final String COMMAND_RETRIEVE_INTERACTIONS =
            "retrieve_interactions";

    /**
     * Retrieve Go Command.
     */
    public static final String COMMAND_RETRIEVE_GO = "retrieve_go";

    /**
     * Help Command.
     */
    public static final String COMMAND_HELP = "help";

    /**
     * Data Service Header Name.
     */
    public static final String DS_HEADER_NAME = "Ds-status";

    /**
     * Data Service Header Value:  OK.
     */
    public static final String DS_OK_STATUS = "ok";

    /**
     * Data Service Header Value:  ERROR.
     */
    public static final String DS_ERROR_STATUS = "error";

    /**
     * Gets HashMap of Valid Commands.
     * @return HashMap of Valid Commands.
     */
    public HashMap getValidCommands() {
        if (validCommands == null) {
            validCommands = new HashMap();
            validCommands.put(COMMAND_RETRIEVE_INTERACTIONS, null);
            validCommands.put(COMMAND_RETRIEVE_GO, null);
            validCommands.put(COMMAND_HELP, null);
        }
        return validCommands;
    }

    /**
     * Gets HashMap of Valid Formats.
     * @return HashMap of Valid Formats.
     */
    public HashMap getValidFormats() {
        if (validFormats == null) {
            validFormats = new HashMap();
            validFormats.put(FORMAT_PSI, null);
            validFormats.put(FORMAT_RS, null);
            validFormats.put(FORMAT_HTML, null);
        }
        return validFormats;
    }

    /**
     * Gets HashMap of Valid Databases.
     * @return HashMap of Valid Databases.
     */
    public HashMap getValidDatabases() {
        if (validDatabases == null) {
            validDatabases = new HashMap();
            validDatabases.put(DATABASE_GRID, null);
        }
        return validDatabases;
    }
}