package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.DatabaseImport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Tests the DatabaseImport Class.
 *
 * @author Ethan Cerami
 */
public class TestDatabaseImport extends TestCase {

    /**
     * Tests the DatabaseImport Class.
     * @throws Exception All Exceptions.
     */
    public void testDatabaseImport() throws Exception {
        DatabaseImport dbImport = new DatabaseImport();

        //  First Delete All Existing Records.
        ArrayList records = dbImport.getAllImportRecords();
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            dbImport.deleteImportRecord(record.getImportId());
        }

        //  Add Test Records
        addTestRecords();

        //  Verify New Records
        records = dbImport.getAllImportRecords();
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            // Verify Start of XML Document.
            String data = record.getData();
            int begin = data.indexOf("<?xml version=");
            assertEquals(0, begin);

            // Verify End of XML Document.
            int end = data.indexOf("</interactionList>");
            assertEquals(58419, end);

            // Verify Status
            assertEquals("NEW", record.getStatus());

            // Verify MD5 Hash
            assertEquals("Z80/GqKi48wil7K3j88IGQ==", record.getMd5Hash());
        }
    }

    /**
     * Adds Sample Test Records.
     * @throws Exception All Exceptions.
     */
    public void addTestRecords() throws Exception {
        DatabaseImport dbImport = new DatabaseImport();
        String sampleData = getTextFromSampleFile();
        dbImport.addImportRecord(sampleData);
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