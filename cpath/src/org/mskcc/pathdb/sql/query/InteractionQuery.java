package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * Abstract Base Class for all queries which return interactions.
 *
 * @author Ethan Cerami
 */
abstract class InteractionQuery {
    protected XDebug xdebug;

    /**
     * Executes Query.
     *
     * @param xdebug XDebug Object.
     * @return XmlAssembly XML Assembly Object.
     * @throws QueryException Error Executing Query.
     */
    public XmlAssembly execute(XDebug xdebug) throws QueryException {
        this.xdebug = xdebug;
        xdebug.logMsg(this, "Executing Interaction Type:  "
                + getClass().getName());
        try {
            return executeSub();
        } catch (Exception e) {
            throw new QueryException(e.getMessage(), e);
        }
    }

    /**
     * Must be subclassed.
     *
     * @return XmlAssembly XML Assembly Object.
     * @throws Exception All Exceptions.
     */
    protected abstract XmlAssembly executeSub() throws Exception;
}