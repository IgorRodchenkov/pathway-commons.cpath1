package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.services.ReadInteractions;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.DatabaseImport;
import org.mskcc.pathdb.sql.TransferImportToGrid;
import org.mskcc.pathdb.util.CPathConstants;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Tests Transfer of Data from Import Table to Grid Table.
 *
 * @author  Ethan Cerami
 */
public class TestTransferImportToGrid extends TestCase {

    /**
     * Tests Data Transfer.
     * @exception Exception All Exceptions.
     */
    public void testTransfer() throws Exception {
        RegisterCPathServices.registerServices();
        addSampleRecord();
        TransferImportToGrid transfer = new TransferImportToGrid(false);
        transfer.transferData();

        DataServiceFactory factory = DataServiceFactory.getInstance();
        ReadInteractions service = (ReadInteractions) factory.getService
                (CPathConstants.READ_INTERACTIONS_FROM_GRID);
        ArrayList interactions = service.getInteractions("p53");
        assertEquals(4, interactions.size());
    }

    /**
     * Adds the sample Cancer Subway Map.
     */
    private void addSampleRecord() throws IOException,
            NoSuchAlgorithmException, SQLException, ClassNotFoundException {
        ContentReader reader = new ContentReader();
        //File file = new File ("testData/psi_subway.xml");
        File file = new File("testData/dip_psi.xml");
        String psi = reader.retrieveContentFromFile(file);

        DatabaseImport dbImport = new DatabaseImport();
        dbImport.addImportRecord(psi);
    }
}