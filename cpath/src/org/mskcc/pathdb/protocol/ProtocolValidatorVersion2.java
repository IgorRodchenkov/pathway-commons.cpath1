package org.mskcc.pathdb.protocol;

import java.util.HashSet;

/**
 * Validates Client/Browser Request, Version 1.0.
 *
 * @author cPath Dev Team.
 */
class ProtocolValidatorVersion2 {
    /**
     * Protocol Request.
     */
    private ProtocolRequest request;

    /**
     * Protocol Constants.
     */
    private ProtocolConstantsVersion2 constants = new ProtocolConstantsVersion2();

    /**
     * Constructor.
     *
     * @param request Protocol Request.
     */
    ProtocolValidatorVersion2 (ProtocolRequest request) {
        this.request = request;
    }

    /**
     * Validates the Request object.
     *
     * @throws ProtocolException  Indicates Violation of Protocol.
     * @throws NeedsHelpException Indicates user requests/needs help.
     */
    public void validate() throws ProtocolException, NeedsHelpException {
        validateVersion();        
        validateCommand();
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
                            + "' is not specified." + ProtocolValidator.HELP_MESSAGE);
        }
        HashSet set = constants.getValidCommands();
        if (!set.contains(request.getCommand())) {
            throw new ProtocolException(ProtocolStatusCode.BAD_COMMAND,
                    "Command:  '" + request.getCommand()
                            + "' is not recognized." + ProtocolValidator.HELP_MESSAGE);
        }
        if (request.getCommand().equals(ProtocolConstants.COMMAND_HELP)) {
            throw new NeedsHelpException();
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
        if (command.equals(ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS)) {
            // ust have a query parameter
            if (!qExists) {
                errorFlag = true;
            }
        }
        if (errorFlag) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument:  '" + ProtocolRequest.ARG_QUERY
                            + "' is not specified." + ProtocolValidator.HELP_MESSAGE,
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
                            + "' is not specified." + ProtocolValidator.HELP_MESSAGE);
        }
        if (!request.getVersion().equals(ProtocolConstantsVersion2.VERSION_2)) {
            throw new ProtocolException
                    (ProtocolStatusCode.VERSION_NOT_SUPPORTED,
                            "This data service currently only supports "
                                    + "version 2.0." + ProtocolValidator.HELP_MESSAGE);
        }
    }
}