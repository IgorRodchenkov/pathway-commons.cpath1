package org.mskcc.pathdb.controller;

import java.util.HashMap;

/**
 * Enumeration of Protocol Status Codes.
 *
 * @author Ethan Cerami
 */
public class ProtocolStatusCode {
    /**
     * Error Code Number.
     */
    private int errorCode;

    /**
     * Error Code Message.
     */
    private String errorMsg;

    /**
     * Status Code:  200 OK.
     */
    public static final ProtocolStatusCode OK =
            new ProtocolStatusCode(200);

    /**
     * Status Code:  400 Bad Command.
     */
    public static final ProtocolStatusCode BAD_COMMAND =
            new ProtocolStatusCode(400);

    /**
     * Status Code:  401 Bad Data Source.
     */
    public static final ProtocolStatusCode BAD_DATA_SOURCE =
            new ProtocolStatusCode(401);

    /**
     * Status Code:  450 Unknown UID.
     */
    public static final ProtocolStatusCode BAD_UID =
            new ProtocolStatusCode(450);

    /**
     * Status Code:  451 Unsupported Format.
     */
    public static final ProtocolStatusCode BAD_FORMAT =
            new ProtocolStatusCode(451);

    /**
     * Status Code:  460 Bad Request, Missing Arguments.
     */
    public static final ProtocolStatusCode MISSING_ARGUMENTS =
            new ProtocolStatusCode(452);

    /**
     * Status Code:  461 Version Not Supported.
     */
    public static final ProtocolStatusCode VERSION_NOT_SUPPORTED =
            new ProtocolStatusCode(453);

    /**
     * Status Code:  500 Internal Server Error.
     */
    public static final ProtocolStatusCode INTERNAL_ERROR =
            new ProtocolStatusCode(500);

    /**
     * HashMap of Error Codes to Error Messages.
     */
    private static HashMap messageMap;

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
        return errorMsg;
    }

    /**
     * Private Constructor.
     * @param errorCode Error Code.
     */
    private ProtocolStatusCode(int errorCode) {
        if (messageMap == null) {
            initMessageMap();
        }
        this.errorCode = errorCode;
        this.errorMsg = (String) messageMap.get(Integer.toString(errorCode));
    }

    /**
     * Initializes the Message Map.
     */
    private void initMessageMap() {
        messageMap = new HashMap();
        //  DAS Adopted Status Codes.
        messageMap.put("200", "OK, data follows");
        messageMap.put("400", "Bad Command (command not recognized)");
        messageMap.put("401", "Bad Data Source (data source not recognized)");

        //  Data Service Specific Status Codes start at 450.
        messageMap.put("450",
                "Bad UID (UID not available in specified database)");
        messageMap.put("451", "Bad Data Format (data format not recognized)");
        messageMap.put("452", "Bad Request (missing arguments)");
        messageMap.put("453", "Version not supported");

        //  Server Errors
        messageMap.put("500", "Internal Server Error");
    }
}