package org.mskcc.pathdb.controller;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Enumeration of Protocol Status Codes.
 *
 * @author Ethan Cerami
 */
public class ProtocolStatusCode {
    /**
     * Status Code:  OK.
     */
    public static final ProtocolStatusCode OK =
            new ProtocolStatusCode(200);

    /**
     * Status Code:  Bad Command.
     */
    public static final ProtocolStatusCode BAD_COMMAND =
            new ProtocolStatusCode(450);

    /**
     * Status Code:  Bad Format.
     */
    public static final ProtocolStatusCode BAD_FORMAT =
            new ProtocolStatusCode(451);

    /**
     * Status Code:  Bad Request, Missing Arguments.
     */
    public static final ProtocolStatusCode MISSING_ARGUMENTS =
            new ProtocolStatusCode(452);

    /**
     * Status Code:  Bad Request, Invalid Argument.
     */
    public static final ProtocolStatusCode INVALID_ARGUMENT =
            new ProtocolStatusCode(453);

    /**
     * Status Code:  No Results Found.
     */
    public static final ProtocolStatusCode NO_RESULTS_FOUND =
            new ProtocolStatusCode(460);

    /**
     * Status Code:  Version Not Supported.
     */
    public static final ProtocolStatusCode VERSION_NOT_SUPPORTED =
            new ProtocolStatusCode(470);

    /**
     * Status Code:  Internal Server Error.
     */
    public static final ProtocolStatusCode INTERNAL_ERROR =
            new ProtocolStatusCode(500);

    /**
     * HashMap of Error Codes to Error Messages.
     */
    private static HashMap messageMap;

    /**
     * Error Code Number.
     */
    private int errorCode;

    /**
     * Gets Error Code.
     * @return Error Code.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Gets Error Message.
     * @return Error Message.
     */
    public String getErrorMsg() {
        if (messageMap == null) {
            initMessageMap();
        }
        return (String) messageMap.get(new Integer(errorCode));
    }

    /**
     * Gets Complete List of all Status Codes.
     * @return ArrayList of ProtocolStatusCode Objects.
     */
    public static ArrayList getAllStatusCodes() {
        ArrayList list = new ArrayList();
        list.add(ProtocolStatusCode.OK);
        list.add(ProtocolStatusCode.BAD_COMMAND);
        list.add(ProtocolStatusCode.BAD_FORMAT);
        list.add(ProtocolStatusCode.MISSING_ARGUMENTS);
        list.add(ProtocolStatusCode.INVALID_ARGUMENT);
        list.add(ProtocolStatusCode.NO_RESULTS_FOUND);
        list.add(ProtocolStatusCode.VERSION_NOT_SUPPORTED);
        list.add(ProtocolStatusCode.INTERNAL_ERROR);
        return list;
    }

    /**
     * Private Constructor.
     * @param errorCode Error Code.
     */
    private ProtocolStatusCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Initializes the Message Map.
     */
    private void initMessageMap() {
        messageMap = new HashMap();
        messageMap.put (new Integer
                (ProtocolStatusCode.OK.getErrorCode()),
                "OK, data follows");
        messageMap.put (new Integer
                (ProtocolStatusCode.BAD_COMMAND.getErrorCode()),
                "Bad Command (command not recognized)");
        messageMap.put (new Integer(
                ProtocolStatusCode.BAD_FORMAT.getErrorCode()),
                "Bad Data Format (data format not recognized)");
        messageMap.put (new Integer(
                ProtocolStatusCode.MISSING_ARGUMENTS.getErrorCode()),
                "Bad Request (missing arguments)");
        messageMap.put (new Integer(
                ProtocolStatusCode.INVALID_ARGUMENT.getErrorCode()),
                "Bad Request (invalid arguments)");
        messageMap.put (new Integer
                (ProtocolStatusCode.NO_RESULTS_FOUND.getErrorCode()),
                "No Results Found");
        messageMap.put (new Integer
                (ProtocolStatusCode.VERSION_NOT_SUPPORTED.getErrorCode()),
                "Version not supported");
        messageMap.put(new Integer
                (ProtocolStatusCode.INTERNAL_ERROR.getErrorCode()),
                "Internal Server Error");
    }
}