package org.mskcc.pathdb.sql.transfer;

import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.util.BatchTool;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Transfers Data from the Import Table to the GRID Table.
 *
 * @author Ethan Cerami
 */
public class TransferImportToCPath extends BatchTool {
    private DaoImport dbImport;

    /**
     * Constructor.
     * @param runningFromCommandLine Running from Command Line.
     * @param xdebug XDebug Object.
     */
    public TransferImportToCPath(boolean runningFromCommandLine,
            XDebug xdebug) {
        super(runningFromCommandLine, xdebug);
    }

    /**
     * Transfers Data.
     * @throws java.io.IOException Error Reading Files.
     * @throws DaoException Error Retrieving Data.
     * @throws ImportException Error Importing Data.
     */
    public void transferData() throws IOException, DaoException,
            ImportException {
        outputMsg("Transferring Import Records");
        dbImport = new DaoImport();
        ArrayList records = dbImport.getAllRecords();
        if (records.size() == 0) {
            outputMsg("No records to transferData");
        }
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            String status = record.getStatus();
            outputMsg("Checking record:  " + record.getImportId()
                    + ", Status:  " + record.getStatus());
            if (status.equals(DaoImport.STATUS_NEW)) {
                outputMsg("   -->  Transferring record");
                transferRecord(record);
            } else {
                outputMsg("    -->  Already Transferred");
            }
        }
        outputMsg("Transfer Complete");
    }

    /**
     * Transfers Single Import Record.
     */
    private void transferRecord(ImportRecord record) throws ImportException,
            DaoException {
        String xml = record.getData();
        ImportPsiToCPath importer = new ImportPsiToCPath();
        importer.addRecord(xml);
        dbImport.markRecordAsTransferred(record.getImportId());
    }

    /**
     * Main method.
     * @param args Command Line Argument.
     * @throws Exception All Exceptions.
     */
    public static void main(String[] args) throws Exception {
        try {
            if (args.length > 0) {
                PropertyManager manager = PropertyManager.getInstance();
                manager.setProperty(PropertyManager.DB_LOCATION, args[0]);
            } else {
                System.out.println("Command line usage:  TransferImportToCPath "
                        + "host_name [datafile]");
            }
            RegisterCPathServices.registerServices();
            if (args.length > 1) {
                loadDataFile(args[1]);
            }
            TransferImportToCPath transfer = new TransferImportToCPath
                    (true, null);
            transfer.transferData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDataFile(String fileName)
            throws IOException, DaoException {
        System.out.println("Loading data file:  " + fileName);
        File file = new File(fileName);
        ContentReader reader = new ContentReader();
        String data = reader.retrieveContentFromFile(file);
        DaoImport dbImport = new DaoImport();
        dbImport.addRecord(data);
    }
}