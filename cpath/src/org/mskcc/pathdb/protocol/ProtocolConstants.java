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

import java.util.HashMap;
import java.util.HashSet;

/**
 * Protocol Constants.
 *
 * @author Ethan Cerami
 */
public class ProtocolConstants {
    /**
     * HashSet of Valid Commands.
     */
    private static HashSet validCommands;

    /**
     * HashMap of Valid Formats.
     */
    private static HashSet validFormats;

    /**
     * XML Format.
     */
    public static final String FORMAT_XML = "xml";

    /**
     * PSI-MI XML Format.
     */
    public static final String FORMAT_PSI_MI = "psi_mi";

    /**
     * BioPAX Format.
     */
    public static final String FORMAT_BIO_PAX = "biopax";

    /**
     * HTML Format.
     */
    public static final String FORMAT_HTML = "html";

    /**
     * Count Only Format.
     */
    public static final String FORMAT_COUNT_ONLY = "count_only";

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
     * Default Max Number of Hits.
     */
    public static final int DEFAULT_MAX_HITS = 10;

    /**
     * Maximum Number of Hits you can Request at Once.
     */
    public static final int MAX_NUM_HITS = 100;

    /**
     * Gets HashSet of Valid Commands.
     *
     * @return HashMap of Valid Commands.
     */
    public HashSet getValidCommands() {
        if (validCommands == null) {
            validCommands = new HashSet();
            validCommands.add(COMMAND_GET_BY_INTERACTOR_NAME_XREF);
            validCommands.add(COMMAND_GET_BY_INTERACTOR_ID);
            validCommands.add(COMMAND_GET_BY_ORGANISM);
            validCommands.add(COMMAND_GET_BY_KEYWORD);
            validCommands.add(COMMAND_GET_BY_DATABASE);
            validCommands.add(COMMAND_GET_BY_PMID);
            validCommands.add(COMMAND_GET_BY_EXPERIMENT_TYPE);
            validCommands.add(COMMAND_HELP);
        }
        return validCommands;
    }

    /**
     * Gets HashMap of Valid Formats.
     *
     * @return HashMap of Valid Formats.
     */
    public HashSet getValidFormats() {
        if (validFormats == null) {
            validFormats = new HashSet();
            validFormats.add(FORMAT_XML);
            validFormats.add(FORMAT_PSI_MI);
            validFormats.add(FORMAT_BIO_PAX);
            validFormats.add(FORMAT_HTML);
            validFormats.add(FORMAT_COUNT_ONLY);
        }
        return validFormats;
    }
}