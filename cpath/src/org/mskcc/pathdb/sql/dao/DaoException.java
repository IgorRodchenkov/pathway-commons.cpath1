package org.mskcc.pathdb.sql.dao;

/**
 * Exception Occurred while reading/writing data to database.
 *
 * @author Ethan Cerami
 */
public class DaoException extends Exception {

    /**
     * Constructor.
     * @param msg Error Message.
     */
    public DaoException(String msg) {
        super(msg);
    }
}
