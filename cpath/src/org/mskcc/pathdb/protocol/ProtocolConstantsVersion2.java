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
     * Protocol Version:  2.0
     */
    public static final String VERSION_2 = "2.0";

    /**
     * Gets HashSet of Valid Commands.
     *
     * @return HashMap of Valid Commands.
     */
    public HashSet getValidCommands() {
        if (validCommands == null) {
            validCommands = new HashSet();
            validCommands.add(ProtocolConstants.COMMAND_HELP);
            validCommands.add(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
            validCommands.add(COMMAND_GET_NEIGHBORS);
        }
        return validCommands;
    }
}