package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.schemas.psi.Entry;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.InteractionList;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.DaoImport;
import org.mskcc.pathdb.sql.TransferImportToCPath;
import org.mskcc.pathdb.sql.query.InteractionQuery;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Tests Transfer of Data from Import Table to Grid Table.
 *
 * @author  Ethan Cerami
 */
public class TestTransferImportToCPath extends TestCase {

    /**
     * Tests Data Transfer.
     * @exception Exception All Exceptions.
     */
    public void testTransfer() throws Exception {
        XDebug xdebug = new XDebug();
        RegisterCPathServices.registerServices();
        addSampleRecord();
        TransferImportToCPath transfer = new TransferImportToCPath
                (false, xdebug);
        transfer.transferData();

        InteractionQuery query = new InteractionQuery("p53");
        EntrySet entrySet = query.getEntrySet();
        Entry entry = entrySet.getEntry(0);
        InteractionList interactions = entry.getInteractionList();
        assertEquals(4, interactions.getInteractionCount());
    }

    /**
     * Adds the sample Cancer Subway Map.
     */
    private void addSampleRecord() throws IOException,
            NoSuchAlgorithmException, SQLException, ClassNotFoundException {
        ContentReader reader = new ContentReader();
        File file = new File("testData/psi_subway.xml");
        String psi = reader.retrieveContentFromFile(file);

        DaoImport dbImport = new DaoImport();
        dbImport.addRecord(psi);
    }
}