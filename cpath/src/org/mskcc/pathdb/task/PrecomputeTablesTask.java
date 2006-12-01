package org.mskcc.pathdb.task;

import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.PopulateInternalFamilyLookUpTable;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

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
	 * @throws BioPaxRecordSummaryException.
     */
    public void executeTask() throws DaoException, BioPaxRecordSummaryException {
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
	 * @throws BioPaxRecordSummaryException.
     */
    private void savePathwayFamilyMembership (ProgressMonitor pMonitor)
		throws DaoException, BioPaxRecordSummaryException {
        PopulateInternalFamilyLookUpTable populator = new
                PopulateInternalFamilyLookUpTable(pMonitor);
        populator.execute();
    }
}
