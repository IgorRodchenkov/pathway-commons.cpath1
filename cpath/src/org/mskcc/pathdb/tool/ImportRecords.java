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
package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.util.ArrayList;

/**
 * Command Line Tool for Importing Data from the CPath Import Table to
 * the main CPath tables.
 *
 * @author Ethan Cerami
 */
public class ImportRecords {
    private DaoImport dbImport;

    /**
     * Transfers Data.
     *
     * @param validateExternalReferences Flag to Validate External References.
     * @throws DaoException    Data Access Error.
     * @throws ImportException Data Import Error.
     */
    public void transferData(boolean validateExternalReferences)
            throws DaoException, ImportException {
        System.out.println("Transferring Import Records");
        transferAllImportRecords(validateExternalReferences);
        System.out.println("Transfer Complete");
    }

    /**
     * Transfers all Import Records.
     */
    private void transferAllImportRecords(boolean validateExternalReferences)
            throws DaoException, ImportException {
        dbImport = new DaoImport();
        ArrayList records = dbImport.getAllRecords();
        if (records.size() == 0) {
            System.out.println("No records to transferData");
        }
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            String status = record.getStatus();
            System.out.println("Checking record:  " + record.getImportId()
                    + ", Status:  " + record.getStatus());
            if (status.equals(ImportRecord.STATUS_NEW)) {
                System.out.println("   -->  Transferring record");
                transferRecord(record.getImportId(),
                        validateExternalReferences);
            } else {
                System.out.println("    -->  Already Transferred");
            }
        }
    }

    /**
     * Transfers Single Import Record.
     */
    private void transferRecord(long importId,
            boolean validateExternalReferences) throws ImportException,
            DaoException {
        ProgressMonitor pMonitor = new ProgressMonitor();
        ImportRecord record = dbImport.getRecordById(importId);
        String xml = record.getData();
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml,
                validateExternalReferences, true, pMonitor);
        this.outputSummary(summary);
        dbImport.markRecordAsTransferred(record.getImportId());
    }

    /**
     * Displays Summary of Import.
     *
     * @param summary ImportSummary object.
     */
    private void outputSummary(ImportSummary summary) {
        System.out.println();
        System.out.println("Import Summary:  ");
        System.out.println("-----------------------------------------------");
        System.out.println("# of Matching Interactors found in DB:  "
                + summary.getNumInteractorsFound());
        System.out.println("# of Interactors saved to DB:           "
                + summary.getNumInteractorsSaved());
        System.out.println("# of Interactions saved to DB:          "
                + summary.getNumInteractionsSaved());
        System.out.println("-----------------------------------------------");
        System.out.println();
    }
}