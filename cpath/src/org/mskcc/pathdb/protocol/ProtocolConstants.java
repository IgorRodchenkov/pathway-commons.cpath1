package org.mskcc.pathdb.protocol;

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
     * PSI Format.
     */
    public static final String FORMAT_XML = "xml";

    /**
     * PSI Format.
     */
    public static final String FORMAT_HTML = "html";

    /**
     * Currently Supported Version.
     */
    public static final String CURRENT_VERSION = "1.0";

    /**
     * Get Interactions By Interactor Name.
     */
    public static final String COMMAND_GET_BY_INTERACTOR_NAME_XREF =
            "get_by_interactor_name_xref";

    /**
     * Get Interactions By Interactor ID.
     */
    public static final String COMMAND_GET_BY_INTERACTOR_ID =
            "get_by_interactor_id";

    /**
     * Get Interactions By Interactor Taxonomy ID.
     */
    public static final String COMMAND_GET_BY_ORGANISM = "get_by_organism";

    /**
     * Get Interactions By Interactor Keyword.
     */
    public static final String COMMAND_GET_BY_KEYWORD = "get_by_keyword";

    /**
     * Get Interactions By Interaction Database.
     */
    public static final String COMMAND_GET_BY_DATABASE =
            "get_by_database";

    /**
     * Get Interactions By Interaction PubMedId.
     */
    public static final String COMMAND_GET_BY_PMID =
            "get_by_pmid";

    /**
     * Get Interactions By Interaction Type.
     */
    public static final String COMMAND_GET_BY_EXPERIMENT_TYPE =
            "get_by_experiment_type";

    /**
     * Get Organism List.
     */
    public static final String COMMAND_GET_ORGANISM_LIST =
            "get_organism_list";

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
     *
     * @return HashMap of Valid Commands.
     */
    public HashMap getValidCommands() {
        if (validCommands == null) {
            validCommands = new HashMap();
            validCommands.put(COMMAND_GET_BY_INTERACTOR_NAME_XREF, null);
            validCommands.put(COMMAND_GET_BY_INTERACTOR_ID, null);
            validCommands.put(COMMAND_GET_BY_ORGANISM, null);
            validCommands.put(COMMAND_GET_BY_KEYWORD, null);
            validCommands.put(COMMAND_GET_BY_DATABASE, null);
            validCommands.put(COMMAND_GET_BY_PMID, null);
            validCommands.put(COMMAND_GET_BY_EXPERIMENT_TYPE, null);
            validCommands.put(COMMAND_HELP, null);
        }
        return validCommands;
    }

    /**
     * Gets HashMap of Valid Formats.
     *
     * @return HashMap of Valid Formats.
     */
    public HashMap getValidFormats() {
        if (validFormats == null) {
            validFormats = new HashMap();
            validFormats.put(FORMAT_XML, null);
            validFormats.put(FORMAT_HTML, null);
        }
        return validFormats;
    }
}