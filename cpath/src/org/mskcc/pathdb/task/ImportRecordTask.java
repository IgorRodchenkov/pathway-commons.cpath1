/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.task;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.ImportBioPaxToCPath;
import org.mskcc.pathdb.schemas.psi.ImportPsiToCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.sql.transfer.ImportException;

/**
 * Task to Import a Single New Data Record into cPath.
 *
 * @author Ethan Cerami.
 */
public class ImportRecordTask extends Task {
    private long importId;
    private ImportSummary summary;
    private boolean validateExternalReferences;
    private boolean removeAllInteractionXRefs;
    private ProgressMonitor pMonitor;

    /**
     * Constructor.
     *
     * @param importId    Import ID.
     * @param validateExternalReferences Flag to Validate All External
     *          References.
     * @param removeAllInteractionXRefs Flag to Remove all Interaction XRefs.
     * @param consoleMode Console Mode.
     */
    public ImportRecordTask(long importId, boolean validateExternalReferences,
            boolean removeAllInteractionXRefs, boolean consoleMode) {
        super("Import PSI-MI/BioPAX Record", consoleMode);
        this.importId = importId;
        this.validateExternalReferences = validateExternalReferences;
        this.removeAllInteractionXRefs = removeAllInteractionXRefs;
        pMonitor = this.getProgressMonitor();
        pMonitor.setConsoleMode(consoleMode);
        pMonitor.setCurrentMessage("Importing PSI-MI/BioPAX Record");
    }

    /**
     * Runs Task.
     */
    public void run() {
        try {
            transferRecord();
        } catch (Throwable e) {
            setThrowable(e);
        }
    }

    /**
     * Transfers Single Import Record.
     *
     * @throws DaoException Database Access Error.
     * @throws ImportException Import Error.
     */
    public void transferRecord() throws DaoException, ImportException {
        DaoImport daoImport = new DaoImport();
        ImportRecord record = daoImport.getRecordById(importId);
        pMonitor.setCurrentMessage("Importing File:  "
                + record.getDescription());
        String xml = record.getData();
        try {
            if (record.getXmlType().equals(XmlRecordType.PSI_MI)) {
                ImportPsiToCPath importer = new ImportPsiToCPath();
                summary = importer.addRecord(xml, validateExternalReferences,
                        removeAllInteractionXRefs, pMonitor);
            } else {
                ImportBioPaxToCPath importer = new ImportBioPaxToCPath();
                summary = importer.addRecord(xml, pMonitor);
            }
            outputSummary(summary);
            daoImport.updateRecordStatus(record.getImportId(),
                    ImportRecord.STATUS_TRANSFERRED);

        } catch (ImportException e) {
            //  If an Import Error occurs, mark the record as invalid
            //  so that we don't keep trying to import it.
            daoImport.updateRecordStatus(importId, ImportRecord.STATUS_INVALID);
            throw e;
        }
    }

    /**
     * Displays Summary of Import.
     *
     * @param summary ImportSummary object.
     */
    private void outputSummary(ImportSummary summary) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Import Summary:  \n");
        buffer.append("-----------------------------------------------\n");
        buffer.append("# of Pathways saved to database:              "
                + summary.getNumPathwaysSaved() + "\n");
        buffer.append("# of Interactions saved to database:          "
                + summary.getNumInteractionsSaved() + "\n");
        buffer.append("# of Interactions clobbered:                  "
                + summary.getNumInteractionsClobbered() + "\n");
        buffer.append("# of Physical Entities found in database:     "
                + summary.getNumPhysicalEntitiesFound() + "\n");
        buffer.append("# of Physical Entities saved to database:     "
                + summary.getNumPhysicalEntitiesSaved() + "\n");
        buffer.append("-----------------------------------------------\n");
        buffer.append("\n");
        String msg = buffer.toString();
        pMonitor.setCurrentMessage(msg);
        pMonitor.setCurrentMessage("Import Complete");
    }
}