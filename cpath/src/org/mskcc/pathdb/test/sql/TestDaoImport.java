package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Tests the DaoImport Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoImport extends TestCase {

    /**
     * Tests the DaoImport Class.
     * @throws Exception All Exceptions.
     */
    public void testDatabaseImport() throws Exception {
        DaoImport dbImport = new DaoImport();

        //  First Delete All Existing Records.
        ArrayList records = dbImport.getAllRecords();
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            dbImport.deleteRecordById(record.getImportId());
        }

        //  Add Test Records
        addTestRecords();

        //  Verify New Records
        records = dbImport.getAllRecords();
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            // Verify Start of XML Document.
            String data = record.getData();
            assertTrue(data.startsWith("<?xml version="));

            // Verify End of XML Document.
            int end = data.indexOf("</interactionList>");
            assertEquals(58419, end);

            // Verify Status
            assertEquals("NEW", record.getStatus());

            // Verify MD5 Hash
            assertEquals("Z80/GqKi48wil7K3j88IGQ==", record.getMd5Hash());

            getIndividualRecord(record.getImportId(), record.getMd5Hash());
        }
    }

    /**
     * Tests the getRecordById() method.
     */
    private void getIndividualRecord(int id, String hash) throws DaoException {
        DaoImport dbImport = new DaoImport();
        ImportRecord record = dbImport.getRecordById(id);
        assertEquals(hash, record.getMd5Hash());
    }

    /**
     * Adds Sample Test Records.
     * @throws Exception All Exceptions.
     */
    public void addTestRecords() throws Exception {
        DaoImport dbImport = new DaoImport();
        String sampleData = getTextFromSampleFile();
        dbImport.addRecord(sampleData);
    }

    /**
     * Gets Sample PSI File from local directory.
     * @return Sample PSI-MI Record.
     * @throws IOException Input Output Exception.
     */
    private String getTextFromSampleFile() throws IOException {
        FileReader fileReader = new FileReader
                ("testData/HpDemoDataset_15PPI.xml");
        StringBuffer data = new StringBuffer();
        BufferedReader reader = new BufferedReader(fileReader);
        String line = reader.readLine();
        while (line != null) {
            data.append(line + "\n");
            line = reader.readLine();
        }
        return data.toString();
    }
}