package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.dao.DaoIdMap;
import org.mskcc.pathdb.model.IdMapRecord;

/**
 * Tests the DaoIdMap class.
 *
 * @author Ethan Cerami
 */
public class TestDaoIdMap extends TestCase {

    /**
     * Tests Data Access:  Create, Get, Delete.
     * @throws Exception
     */
    public void testAccess() throws Exception {
        DaoIdMap dao = new DaoIdMap();

        //  First, try adding a sample record with invalid DB Ids.
        //  This should trigger an exception.
        IdMapRecord record = new IdMapRecord (100, "ABCD", 200, "XYZ");
        try {
            dao.addRecord(record);
            fail ("Illegal Argument Exception should have been thrown.  "
              +" DB1 and DB2 are not stored in the database.");
        } catch (IllegalArgumentException e) {
        }

        //  Now, try adding a sample record, based on external databases
        //  defined in reset.sql
        record = new IdMapRecord (1, "ABCD", 2, "XYZ");
        boolean success = dao.addRecord (record);
        assertTrue (success);

        //  Verify that the record 1:ABCD <--> 2:XYZ now exists within
        //  the database
        IdMapRecord record2 = dao.getRecord(record);
        assertTrue (record2 != null);
        assertEquals (1, record2.getDb1());
        assertEquals (2, record2.getDb2());
        assertEquals ("ABCD", record2.getId1());
        assertEquals ("XYZ", record2.getId2());

        //  Verify that the record 2:XYZ <--> 1:ABCD generates the same hit.
        record2 = dao.getRecord(new IdMapRecord (2, "XYZ", 1, "ABCD"));
        assertTrue (record2 != null);
        assertEquals (1, record2.getDb1());
        assertEquals (2, record2.getDb2());
        assertEquals ("ABCD", record2.getId1());
        assertEquals ("XYZ", record2.getId2());

        //  Now Delete it
        success = dao.deleteRecordById(record2.getPrimaryId());
        assertTrue (success);

        //  Verify that record is indeed deleted.
        record2 = dao.getRecord(record);
        assertTrue (record2 == null);
    }
}