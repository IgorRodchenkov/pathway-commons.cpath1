package org.mskcc.pathdb.sql;

/**
 * Indicates an Empty Result Set.
 *
 * @author Ethan Cerami
 */
public class EmptySetException extends Exception {

    /**
     * Constructor.
     * @param msg Error Message.
     */
    public EmptySetException(String msg) {
        super(msg);
    }
}
