package org.mskcc.pathdb.sql.query;

/**
 * Indicates that an error occurred during a CPath Query.
 *
 * @author Ethan Cerami
 */
public class QueryException extends Exception {

    /**
     * Constructor.
     * @param msg Error Message.
     */
    public QueryException(String msg) {
        super(msg);
    }
}
