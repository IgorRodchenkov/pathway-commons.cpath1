package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.ExternalDatabase;
import org.mskcc.pathdb.model.ExternalLink;
import org.mskcc.pathdb.sql.DaoExternalDb;
import org.mskcc.pathdb.sql.DaoExternalLink;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Tests the DaoExternalLink class.
 *
 * @author Ethan Cerami
 */
public class TestDaoExternalLink extends TestCase {

    /**
     * Tests Access to the ExternalLink Table.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        DaoExternalLink db = new DaoExternalLink();
        addSampleRecord();

        // Test getRecordsByCPathId() Method.
        ArrayList list = db.getRecordsByCPathId(1);
        assertEquals(1, list.size());
        ExternalLink link = (ExternalLink) list.get(0);
        ExternalDatabase externalDb = link.getExternalDatabase();
        assertEquals("PIR", externalDb.getName());

        //  Test getRecordsById() Method
        externalDb = null;
        link = db.getRecordById(link.getId());
        externalDb = link.getExternalDatabase();
        assertEquals("PIR", externalDb.getName());
        String webLink = link.getWebLink();
        assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=BVECGL",
                webLink);

        //  Test deleteRecordById() Method.
        boolean success = db.deleteRecordById(link.getId());
        assertTrue(success);

        boolean exists = db.recordExists(link);
        assertTrue(!exists);
    }

    private void addSampleRecord() throws SQLException,
            ClassNotFoundException {
        DaoExternalDb dbTable = new DaoExternalDb();
        ExternalDatabase externalDb = dbTable.getRecordByTerm("PIR");
        DaoExternalLink db = new DaoExternalLink();
        ExternalLink link = new ExternalLink();
        link.setCpathId(1);
        link.setExternalDatabase(externalDb);
        link.setLinkedToId("BVECGL");

        //  First, verify that record does not exist.
        boolean exists = db.recordExists(link);
        assertTrue(!exists);

        //  Second, add record.
        boolean success = db.addRecord(link);
        assertTrue(success);

        //  Third, verify that record now exists.
        exists = db.recordExists(link);
        assertTrue(exists);
    }
}