package org.mskcc.pathdb.sql.transfer;

/**
 * Indicates that an error occurred during Import of Data.
 *
 * @author Ethan Cerami
 */
public class ImportException extends Exception {

    /**
     * Constructor.
     *
     * @param e Throwable Object.
     */
    public ImportException(Throwable e) {
        super(e);
    }
}
