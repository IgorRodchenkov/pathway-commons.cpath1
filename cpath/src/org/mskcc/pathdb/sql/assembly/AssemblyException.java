package org.mskcc.pathdb.sql.assembly;

/**
 * Encapsulates an Error in XML Assembly.
 *
 * @author Ethan Cerami
 */
public class AssemblyException extends Exception {

    /**
     * Constructor.
     * @param throwable Any Throwable Object.
     */
    public AssemblyException (Throwable throwable) {
        super (throwable.getMessage(), throwable);
    }
}