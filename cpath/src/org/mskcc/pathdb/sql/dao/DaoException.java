package org.mskcc.pathdb.sql.dao;

/**
 * Exception Occurred while reading/writing data to database.
 *
 * @author Ethan Cerami
 */
public class DaoException extends Exception {

    /**
     * Constructor.
     *
     * @param throwable Throwable Object containing root cause.
     */
    public DaoException(Throwable throwable) {
        super(throwable);
    }
}