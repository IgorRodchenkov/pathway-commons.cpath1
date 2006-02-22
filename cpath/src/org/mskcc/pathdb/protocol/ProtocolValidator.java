// $Id: ProtocolValidator.java,v 1.17 2006-02-22 22:47:50 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.servlet.CPathUIConfig;

import java.util.HashSet;

/**
 * Validates Client/Browser Request.
 *
 * @author Ethan Cerami
 */
public class ProtocolValidator {
    /**
     * Protocol Request.
     */
    private ProtocolRequest request;

    /**
     * Current Version Implemented.
     */
    private static final String CURRENT_VERSION = "1.0";

    /**
     * Help Message
     */
    private static final String HELP_MESSAGE = "  Please try again.";

    /**
     * Protocol Constants.
     */
    private ProtocolConstants constants = new ProtocolConstants();

    /**
     * Constructor.
     *
     * @param request Protocol Request.
     */
    public ProtocolValidator(ProtocolRequest request) {
        this.request = request;
    }

    /**
     * Validates the Request object.
     *
     * @throws ProtocolException  Indicates Violation of Protocol.
     * @throws NeedsHelpException Indicates user requests/needs help.
     */
    public void validate() throws ProtocolException, NeedsHelpException {
        validateEmptySet();
        validateCommand();
        validateMaxHits();
        validateVersion();
        validateFormat();
        validateQuery();
    }

    /**
     * Validates the Command Parameter.
     *
     * @throws ProtocolException  Indicates Violation of Protocol.
     * @throws NeedsHelpException Indicates user requests/needs help.
     */
    private void validateCommand() throws ProtocolException,
            NeedsHelpException {
        if (request.getCommand() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument:  '" + ProtocolRequest.ARG_COMMAND
                            + "' is not specified." + HELP_MESSAGE);
        }
        HashSet set;
        if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) {
            set = constants.getValidPsiMiCommands();
        } else {
            //  Special Case
            set = constants.getValidBioPaxCommands();
            if (request.getCommand().equals
                    (ProtocolConstants.COMMAND_GET_BY_KEYWORD)) {
                if (request.getFormat() != null && request.getFormat().equals
                        (ProtocolConstants.FORMAT_BIO_PAX)) {
                    throw new ProtocolException(ProtocolStatusCode.BAD_FORMAT,
                            "BioPAX format not supported for this command.");
                }
            }
        }
        if (!set.contains(request.getCommand())) {
            throw new ProtocolException(ProtocolStatusCode.BAD_COMMAND,
                    "Command:  '" + request.getCommand()
                            + "' is not recognized." + HELP_MESSAGE);
        }
        if (request.getCommand().equals(ProtocolConstants.COMMAND_HELP)) {
            throw new NeedsHelpException();
        }
    }

    /**
     * Validates the MaxHits Parameter.
     *
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateMaxHits() throws ProtocolException {
        if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) {
            int maxHits = request.getMaxHitsInt();
            if (maxHits > ProtocolConstants.MAX_NUM_HITS) {
                throw new ProtocolException(ProtocolStatusCode.INVALID_ARGUMENT,
                        "To prevent overloading of the system, clients are "
                                + "restricted to a maximum of "
                                + ProtocolConstants.MAX_NUM_HITS + " hits at a time.");
            }
        }
    }

    /**
     * Validates the Format Parameter.
     *
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateFormat() throws ProtocolException {
        if (request.getFormat() == null) {
            throw new ProtocolException
                    (ProtocolStatusCode.MISSING_ARGUMENTS,
                            "Argument:  '" + ProtocolRequest.ARG_FORMAT
                                    + "' is not specified." + HELP_MESSAGE);
        }
        HashSet set;
        if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) {
            set = constants.getValidPsiMiFormats();
        } else {
            set = constants.getValidBioPaxFormats();
        }
        if (!set.contains(request.getFormat())) {
            throw new ProtocolException(ProtocolStatusCode.BAD_FORMAT,
                    "Format:  '" + request.getFormat()
                            + "' is not recognized."
                            + HELP_MESSAGE);
        }
    }

    /**
     * Validates the UID Parameter.
     *
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateQuery() throws ProtocolException {
        String command = request.getCommand();
        String q = request.getQuery();
        String org = request.getOrganism();
        boolean qExists = true;
        boolean organismExists = true;
        boolean errorFlag = false;
        if (q == null || q.length() == 0) {
            qExists = false;
        }
        if (org == null || org.length() == 0) {
            organismExists = false;
        }

        if (command.equals(ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID)) {
            // ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID must have a
            // query parameter.  All other commands must have either a query
            // parameter or an organism paramter.
            if (!qExists) {
                errorFlag = true;
            }

            //  Verify that query parameter is an int/long number.
            try {
                Long.parseLong(q);
            } catch (NumberFormatException e) {
                throw new ProtocolException(ProtocolStatusCode.INVALID_ARGUMENT,
                        "Argument:  '" + ProtocolRequest.ARG_QUERY
                                + "' must be an integer value." + HELP_MESSAGE,
                        "Query parameter must be an integer value. "
                                + "Please try again.");
            }
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST)) {
            // ProtocolConstants.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST can appear
            // without a query parameter or an organism parameter.
            return;
        } else {
            if (!qExists && !organismExists) {
                errorFlag = true;
            }
        }

        if (errorFlag) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument:  '" + ProtocolRequest.ARG_QUERY
                            + "' is not specified." + HELP_MESSAGE,
                    "You did not specify a query term.  Please try again.");
        }
    }

    /**
     * Validates the Version Parameter.
     *
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateVersion() throws ProtocolException {
        if (request.getVersion() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument: '" + ProtocolRequest.ARG_VERSION
                            + "' is not specified." + HELP_MESSAGE);
        }
        if (!request.getVersion().equals(CURRENT_VERSION)) {
            throw new ProtocolException
                    (ProtocolStatusCode.VERSION_NOT_SUPPORTED,
                            "This data service currently only supports "
                                    + "version 1.0." + HELP_MESSAGE);
        }
    }

    /**
     * Checks if no arguments are specified.
     * If none are specified, throws NeedsHelpException.
     *
     * @throws NeedsHelpException Indicates user requests/needs help.
     */
    private void validateEmptySet() throws NeedsHelpException {
        if (request.isEmpty()) {
            throw new NeedsHelpException();
        }
    }
}
