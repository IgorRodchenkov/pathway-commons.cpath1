package org.mskcc.pathdb.sql.transfer;

public class MissingDataException extends Exception {
    private String msg;

    /**
     * Constructor.
     *
     * @param humanReadableErrorMessage Error Message
     */
    public MissingDataException(String humanReadableErrorMessage) {
        super(humanReadableErrorMessage);
        this.msg = humanReadableErrorMessage;
    }

    /**
     * Gets Error Message.
     * @return
     */
    public String getMessage() {
        return msg;
    }
}
