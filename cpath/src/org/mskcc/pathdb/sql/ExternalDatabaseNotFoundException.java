package org.mskcc.pathdb.sql;

/**
 * Indicates that the requested External Database could not be found.
 *
 * @author Ethan Cerami
 */
public class ExternalDatabaseNotFoundException extends Exception {

    /**
     * Constructor.
     * @param msg Error Message.
     */
    public ExternalDatabaseNotFoundException(String msg) {
        super(msg);
    }
}
