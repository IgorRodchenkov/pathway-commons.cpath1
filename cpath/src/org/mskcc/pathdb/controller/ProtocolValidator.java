package org.mskcc.pathdb.controller;

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
     * Constructor.
     * @param request Protocol Request.
     */
    public ProtocolValidator(ProtocolRequest request) {
        this.request = request;
    }

    /**
     * Validates the Request object.
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    public void validate() throws ProtocolException {
        validateCommand();
        validateDatabase();
        validateFormat();
        validateUid();
        validateVersion();
    }

    /**
     * Validates the Command Parameter.
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateCommand() throws ProtocolException {
        if (request.getCommand() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument:  'cmd' is not specified.");
        }
    }

    /**
     * Validates the Database Parameter.
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateDatabase() throws ProtocolException {
        if (request.getDatabase() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument:  'db' is not specified.");
        }
    }

    /**
     * Validates the Format Parameter.
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateFormat() throws ProtocolException {
        if (request.getFormat() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument:  'format' is not specified.");
        }
    }

    /**
     * Validates the UID Parameter.
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateUid() throws ProtocolException {
        if (request.getUid() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument:  'uid' is not specified.");
        }
    }

    /**
     * Validates the Version Parameter.
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateVersion() throws ProtocolException {
        if (request.getVersion() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument: 'version' is not specified.");
        }
        if (!request.getVersion().equals(CURRENT_VERSION)) {
            throw new ProtocolException
                    (ProtocolStatusCode.VERSION_NOT_SUPPORTED,
                            "This data service currently only supports "
                            + "version 1.0.");
        }
    }
}