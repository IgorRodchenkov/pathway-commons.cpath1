package org.mskcc.pathdb.sql.dao;

/**
 * Exception Occurred while reading/writing data to database.
 *
 * @author Ethan Cerami
 */
public class DaoException extends Exception {
    private String msg;

    /**
     * Constructor.
     * @param msg Error Message.
     */
    public DaoException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * Gets Message.
     * @return Error Message.
     */
    public String getMessage() {
        return this.msg;
    }
}
