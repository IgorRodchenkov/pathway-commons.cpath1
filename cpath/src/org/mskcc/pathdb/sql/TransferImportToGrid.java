package org.mskcc.pathdb.sql;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.services.WriteInteractions;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.core.DataServiceException;

import java.util.ArrayList;
import java.sql.SQLException;
import java.io.IOException;

/**
 * Transfers Data from the Import Table to the GRID Table.
 *
 * @author Ethan Cerami
 */
public class TransferImportToGrid {
    private DatabaseImport dbImport;
    private String location;
    private boolean verbose;

    /**
     * Constructor.
     * @param verbose Turn verbosity on or off.
     */
    public TransferImportToGrid (boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Transfers Data.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     * @throws IOException Error Reading Files.
     * @throws MapperException Error Mapping to PSI.
     * @throws DataServiceException Error Connecting to Data Service.
     */
    public void transferData ()
            throws SQLException, ClassNotFoundException,
            IOException, MapperException, DataServiceException {
        outputVerbose ("Transferring Import Records");
        dbImport = new DatabaseImport();
        ArrayList records = dbImport.getAllImportRecords();
        if (records.size()==0) {
            outputVerbose ("No records to transfer");
        }
        for (int i=0; i<records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            String status = record.getStatus();
            outputVerbose ("Checking record:  "+record.getImportId()+
                        ", Status:  "+record.getStatus());
            if (status.equals(DatabaseImport.STATUS_NEW)) {
                outputVerbose ("   -->  Transferring record");
                transferRecord(record);
            } else {
                outputVerbose ("    -->  Already Transferred");
            }
        }
        outputVerbose ("Transfer Complete");
    }

    /**
     * Sets Location of the Database.
     * @param location Database Location
     */
    public void setDatabaseLocation(String location) {
        this.location = location;
    }

    /**
     * Conditionally output messages to the console.
     * @param message User message.
     */
    private void outputVerbose (String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    /**
     * Transfers Single Import Record.
     */
    private void transferRecord (ImportRecord record) throws MapperException,
            DataServiceException, ClassNotFoundException, SQLException {
        ArrayList interactions = new ArrayList();
        String data = record.getData();
        MapPsiToInteractions mapper = new MapPsiToInteractions(data,
                interactions);
        mapper.doMapping();

        DataServiceFactory factory = DataServiceFactory.getInstance();
        WriteInteractions service = (WriteInteractions) factory.getService
                (CPathConstants.WRITE_INTERACTIONS_TO_GRID);
        if (location != null) {
            service.setLocation(location);
        }
        service.writeInteractions(interactions);

        dbImport.markRecordAsTransferred(record.getImportId());
    }

    /**
     * Main method.
     * @param args Command Line Argument.
     * @throws Exception All Exceptions.
     */
    public static void main(String[] args) throws Exception {
        RegisterCPathServices.registerServices();
        TransferImportToGrid transfer = new TransferImportToGrid(true);
        if (args.length > 0) {
            transfer.setDatabaseLocation(args[0]);
        }
        transfer.transferData();
    }
}