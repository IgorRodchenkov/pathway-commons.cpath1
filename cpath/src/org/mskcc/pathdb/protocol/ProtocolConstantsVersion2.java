package org.mskcc.pathdb.protocol;

import java.util.HashSet;

/**
 * Protocol Constants, Version 2.0.
 *
 * @author cPath Dev Team.
 */
public class ProtocolConstantsVersion2 {
    /**
     * HashSet of Valid Commands.
     */
    private static HashSet validCommands;

    /**
     * Get Neighbors Command.
     */
    public static final String COMMAND_GET_NEIGHBORS = "get_neighbors";

    /**
     * Get Pathway List Command.
     */
    public static final String COMMAND_GET_PATHWAY_LIST = "get_pathway_list";

    /**
     * ID_LIST Format / Output.
     */
    public static final String FORMAT_ID_LIST = "id_list";

    /**
     * Protocol Version:  2.0
     */
    public static final String VERSION_2 = "2.0";

    /**
     * Max Number of Input IDs that can be specified.
     */
    public static final int MAX_NUM_IDS = 25;

    /**
     * Gets HashSet of Valid Commands.
     *
     * @return HashMap of Valid Commands.
     */
    public HashSet getValidCommands() {
        if (validCommands == null) {
            validCommands = new HashSet();
            validCommands.add(ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID);
            validCommands.add(ProtocolConstants.COMMAND_HELP);
            validCommands.add(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
            validCommands.add(COMMAND_GET_NEIGHBORS);
            validCommands.add(COMMAND_GET_PATHWAY_LIST);
        }
        return validCommands;
    }
}