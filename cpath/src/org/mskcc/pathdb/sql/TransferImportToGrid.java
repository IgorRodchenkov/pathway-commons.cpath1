package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.services.WriteInteractions;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.util.BatchTool;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Transfers Data from the Import Table to the GRID Table.
 *
 * @author Ethan Cerami
 */
public class TransferImportToGrid extends BatchTool {
    private DaoImport dbImport;

    /**
     * Constructor.
     * @param runningFromCommandLine Running from Command Line.
     * @param xdebug XDebug Object.
     */
    public TransferImportToGrid(boolean runningFromCommandLine, XDebug xdebug) {
        super(runningFromCommandLine, xdebug);
    }

    /**
     * Transfers Data.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     * @throws IOException Error Reading Files.
     * @throws MapperException Error Mapping to PSI.
     * @throws DataServiceException Error Connecting to Data Service.
     */
    public void transferData()
            throws SQLException, ClassNotFoundException,
            IOException, MapperException, DataServiceException {
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
    private void transferRecord(ImportRecord record) throws MapperException,
            DataServiceException, ClassNotFoundException, SQLException {
        ArrayList interactions = new ArrayList();
        String data = record.getData();
        MapPsiToInteractions mapper = new MapPsiToInteractions(data,
                interactions);
        mapper.doMapping();

        DataServiceFactory factory = DataServiceFactory.getInstance();
        WriteInteractions service = (WriteInteractions) factory.getService
                (CPathConstants.WRITE_INTERACTIONS_TO_GRID);
        service.writeInteractions(interactions);

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
                System.out.println("Command line usage:  TransferImportToGrid "
                        + "host_name [datafile]");
            }
            RegisterCPathServices.registerServices();
            if (args.length > 1) {
                loadDataFile(args[1]);
            }
            TransferImportToGrid transfer = new TransferImportToGrid
                    (true, null);
            transfer.transferData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDataFile(String fileName) throws IOException,
            NoSuchAlgorithmException, SQLException, ClassNotFoundException {
        System.out.println("Loading data file:  " + fileName);
        File file = new File(fileName);
        ContentReader reader = new ContentReader();
        String data = reader.retrieveContentFromFile(file);
        DaoImport dbImport = new DaoImport();
        dbImport.addRecord(data);
    }
}