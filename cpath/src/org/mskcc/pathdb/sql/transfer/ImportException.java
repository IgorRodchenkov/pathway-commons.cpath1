package org.mskcc.pathdb.sql.transfer;

/**
 * Indicates that an error occurred during Import of Data.
 *
 * @author Ethan Cerami
 */
public class ImportException extends Exception {

    /**
     * Constructor.
     * @param msg Error Message.
     */
    public ImportException(String msg) {
        super(msg);
    }
}
