package org.mskcc.pathdb.protocol;

import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;

import java.util.ArrayList;
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
    public void validate () throws ProtocolException, NeedsHelpException {
        validateCommand();
        validateVersion();
        validateInputIdType();
        validateDataSources();
        validateQuery();
    }

    /**
     * Validates the Command Parameter.
     *
     * @throws ProtocolException  Indicates Violation of Protocol.
     * @throws NeedsHelpException Indicates user requests/needs help.
     */
    private void validateCommand () throws ProtocolException,
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
        if (request.getCommand().equals(ProtocolConstants.COMMAND_HELP)) {
            throw new NeedsHelpException();
        }
    }

    /**
     * Validates the UID Parameter.
     *
     * @throws ProtocolException Indicates Violation of Protocol.
     */
    private void validateQuery () throws ProtocolException {
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
        if (!qExists) {
            errorFlag = true;
        }
        if (command != null && command.equals(ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST)) {
            if (q != null) {
                String ids[] = q.split("[\\s]");
                if (ids.length > ProtocolConstantsVersion2.MAX_NUM_IDS) {
                    throw new ProtocolException(ProtocolStatusCode.INVALID_ARGUMENT,
                            "To prevent overloading of the system, clients are "
                                    + "restricted to a maximum of "
                                    + ProtocolConstantsVersion2.MAX_NUM_IDS + " IDs at a time.");
                }
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
    private void validateVersion () throws ProtocolException {
        if (request.getVersion() == null) {
            throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                    "Argument: '" + ProtocolRequest.ARG_VERSION
                            + "' is not specified." + ProtocolValidator.HELP_MESSAGE);
        }
        if (!request.getVersion().equals(ProtocolConstantsVersion2.VERSION_2)) {
            throw new ProtocolException
                    (ProtocolStatusCode.VERSION_NOT_SUPPORTED,
                            "The web service API currently only supports "
                                    + "version 2.0." + ProtocolValidator.HELP_MESSAGE);
        }
    }

    private void validateInputIdType () throws ProtocolException {
        String command = request.getCommand();
        if (command != null && command.equals(ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST)) {
            String inputIdType = request.getInputIDType();
            if (inputIdType == null || inputIdType.trim().length() == 0) {
                throw new ProtocolException(ProtocolStatusCode.MISSING_ARGUMENTS,
                        "Argument:  '" + ProtocolRequest.ARG_INPUT_ID_TYPE
                                + "' is not specified." + ProtocolValidator.HELP_MESSAGE);
            } else {
                WebUIBean webBean = CPathUIConfig.getWebUIBean();
                ArrayList supportedIdList = webBean.getSupportedInputIdTypes();
                if (!supportedIdList.contains(inputIdType)) {
                    StringBuffer buf = new StringBuffer();
                    for (int i = 0; i < supportedIdList.size(); i++) {
                        buf.append(supportedIdList.get(i));
                        if (i < supportedIdList.size() - 1) {
                            buf.append(", ");
                        }
                    }
                    throw new ProtocolException(ProtocolStatusCode.INVALID_ARGUMENT,
                            ProtocolRequest.ARG_INPUT_ID_TYPE
                                    + " must be set to one of the following: "
                                    + buf.toString() + ".");
                }
            }
        }
    }

    private void validateDataSources () throws ProtocolException {
        String command = request.getCommand();
        try {
            if (command != null && command.equals
                    (ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST)) {
                String dataSources[] = request.getDataSources();
                if (dataSources != null) {
                    ArrayList masterTermList = getMasterTermList();
                    for (int i = 0; i < dataSources.length; i++) {
                        if (!masterTermList.contains(dataSources[i])) {
                            throw new ProtocolException(ProtocolStatusCode.INVALID_ARGUMENT,
                                    ProtocolRequest.ARG_DATA_SOURCE + ": "
                                            + dataSources[i] + " is not a recognized data source.");
                        }
                    }
                }
            }
        } catch (DaoException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR);
        }
    }

    private ArrayList getMasterTermList () throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllDatabaseSnapshots();
        ArrayList masterTermList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord = (ExternalDatabaseSnapshotRecord)
                    list.get(i);
            String masterTerm = snapshotRecord.getExternalDatabase().getMasterTerm();
            masterTermList.add(masterTerm);
        }
        return masterTermList;
    }
}