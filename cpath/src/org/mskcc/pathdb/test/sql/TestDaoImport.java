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
package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.model.XmlRecordType;
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
    private static final String DESCRIPTION = "Test Import Record";

    /**
     * Tests the DaoImport Class.
     *
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

            // Verify Description
            assertEquals(DESCRIPTION, record.getDescription());

            // Verify Status
            assertEquals("NEW", record.getStatus());

            // Verify XML Type
            assertEquals(XmlRecordType.PSI_MI, record.getXmlType());

            // Verify MD5 Hash
            assertEquals("Z80/GqKi48wil7K3j88IGQ==", record.getMd5Hash());

            getIndividualRecord(record.getImportId(), record.getMd5Hash());

            record = dbImport.getRecordById(record.getImportId());

            // Verify Start of XML Document.
            String data = record.getData();
            assertTrue(data.startsWith("<?xml version="));

            // Verify End of XML Document.
            int end = data.indexOf("</interactionList>");
            assertEquals(58419, end);
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
     *
     * @throws Exception All Exceptions.
     */
    public void addTestRecords() throws Exception {
        DaoImport dbImport = new DaoImport();
        String sampleData = getTextFromSampleFile();
        dbImport.addRecord(DESCRIPTION, XmlRecordType.PSI_MI, sampleData);
    }

    /**
     * Gets Sample PSI File from local directory.
     *
     * @return Sample PSI-MI Record.
     * @throws IOException Input Output Exception.
     */
    private String getTextFromSampleFile() throws IOException {
        FileReader fileReader = new FileReader
                ("testData/psi_mi/HpDemoDataset_15PPI.xml");
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