package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;

/**
 * Tests the DaoExternalLink class.
 *
 * @author Ethan Cerami
 */
public class TestDaoExternalLink extends TestCase {
    private static final String DB_NAME = "PIR";
    private static final String DB_ID = "BVECGL";

    /**
     * Tests Access to the ExternalLinkRecord Table.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        DaoExternalLink db = new DaoExternalLink();
        addSampleRecord();

        // Test getRecordsByCPathId() Method.
        ArrayList list = db.getRecordsByCPathId(1);
        assertEquals(1, list.size());
        ExternalLinkRecord link = (ExternalLinkRecord) list.get(0);
        ExternalDatabaseRecord externalDb = link.getExternalDatabase();
        assertEquals("PIR", externalDb.getName());

        //  Test getRecordsById() Method
        externalDb = null;
        link = db.getRecordById(link.getId());
        externalDb = link.getExternalDatabase();
        assertEquals("PIR", externalDb.getName());
        String webLink = link.getWebLink();
        assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=BVECGL",
                webLink);

        //  Test LookupByExternalRefs
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference(DB_NAME, DB_ID);
        CPathRecord record = db.lookUpByByExternalRefs(refs);
        assertEquals("P06139", record.getName());

        //  Test deleteRecordById() Method.
        boolean success = db.deleteRecordById(link.getId());
        assertTrue(success);

        boolean exists = db.recordExists(link);
        assertTrue(!exists);
    }

    private void addSampleRecord() throws DaoException {
        DaoExternalDb dbTable = new DaoExternalDb();
        ExternalDatabaseRecord externalDb = dbTable.getRecordByTerm(DB_NAME);
        DaoExternalLink db = new DaoExternalLink();
        ExternalLinkRecord link = new ExternalLinkRecord();
        link.setCpathId(1);
        link.setExternalDatabase(externalDb);
        link.setLinkedToId(DB_ID);

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