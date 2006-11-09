package org.mskcc.pathdb.task;

import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.PopulateInternalFamilyLookUpTable;

/**
 * Pre-computed specific tables.
 *
 * @author Ethan Cerami.
 */
public class PrecomputeTablesTask extends Task {
    private XDebug xdebug;

    /**
     * Constructor.
     *
     * @param consoleMode Running in Console Mode.
     * @param xdebug      XDebug Object.
     */
    public PrecomputeTablesTask(boolean consoleMode, XDebug xdebug) {
        super("Precompute Tables", consoleMode);
        this.xdebug = xdebug;
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Precomputing tables");
    }


    /**
     * Runs the Task.
     */
    public void run() {
        try {
            executeTask();
        } catch (Exception e) {
            setThrowable(e);
        }
    }

    /**
     * Executes Task.
     * @throws DaoException Data access error.
     */
    public void executeTask() throws DaoException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Storing Pathway Membership Data");
        savePathwayFamilyMembership(pMonitor);
        pMonitor.setCurrentMessage("Done");
        xdebug.stopTimer();
    }

    /**
     * Saves Family Membership information for pathways only.
     *
     * @throws DaoException Database access error.
     */
    private void savePathwayFamilyMembership (ProgressMonitor pMonitor) throws DaoException {
        PopulateInternalFamilyLookUpTable populator = new
                PopulateInternalFamilyLookUpTable(pMonitor);
        populator.execute();
    }
}
