package org.mskcc.pathdb.task;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;

/**
 * Task to Import New Data Records into cPath.
 *
 * @author Ethan Cerami.
 */
public class ImportRecordTask extends Task {
    private ProgressMonitor pMonitor;
    private long importId;
    private ImportSummary summary;

    /**
     * Constructor.
     *
     * @param importId Import ID.
     * @param verbose  Verbose Flag.
     */
    public ImportRecordTask(long importId, boolean verbose) {
        super("Import PSI-MI Record");
        this.setVerbose(verbose);
        this.importId = importId;
        this.pMonitor = new ProgressMonitor();
        pMonitor.setCurrentMessage("Import PSI-MI Record");
    }

    /**
     * Runs Task.
     */
    public void run() {
        try {
            transferRecord(importId);
        } catch (Exception e) {
            setException(e);
            e.printStackTrace();
        }
    }

    /**
     * Gets the Progress Monitor.
     *
     * @return Progress Monitor.
     */
    public ProgressMonitor getProgressMonitor() {
        return pMonitor;
    }

    /**
     * Transfers Single Import Record.
     */
    private void transferRecord(long importId) throws ImportException,
            DaoException {
        DaoImport dbImport = new DaoImport();
        ImportRecord record = dbImport.getRecordById(importId);
        String xml = record.getData();
        ImportPsiToCPath importer = new ImportPsiToCPath();
        summary = importer.addRecord(xml, true, true, pMonitor);
        pMonitor.setCurrentMessage("Importing Complete<BR>-->  Total Number "
                + "of Interactions Processed:  "
                + summary.getNumInteractionsSaved());
        dbImport.markRecordAsTransferred(record.getImportId());
    }
}