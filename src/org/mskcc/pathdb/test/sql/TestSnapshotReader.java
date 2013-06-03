package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.snapshot.SnapshotReader;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Tests the Snapshot Reader Utility class.
 *
 * @author Ethan Cerami.
 */
public class TestSnapshotReader extends TestCase {

    /**
     * Tests the Snapshot Reader with a sample file.
     * @throws Exception All Errors.
     */
    public void testSnapshotReader () throws Exception {

        //  Fetch #1;  should be retrieved from file/DB
        SnapshotReader reader = new SnapshotReader (new File ("testData/snapshot"), "db.info");
        ExternalDatabaseSnapshotRecord record = reader.getSnapshotRecord();
        assertEquals ("1.0", record.getSnapshotVersion());
        assertEquals ("UniProt", record.getExternalDatabase().getName());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals ("01/02/2006", format.format(record.getSnapshotDate()));
        assertEquals (false, reader.isCachedResult());

        //  Fetch #2;  should be retrieved from static cache.
        reader = new SnapshotReader (new File ("testData/snapshot"), "db.info");
        record = reader.getSnapshotRecord();
        assertEquals ("1.0", record.getSnapshotVersion());
        assertEquals ("UniProt", record.getExternalDatabase().getName());
        format = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals ("01/02/2006", format.format(record.getSnapshotDate()));
        assertEquals (true, reader.isCachedResult());

        //  Fetch #3;  contains invalid data
        try {
            new SnapshotReader (new File ("testData/snapshot"), "db_invalid1.info");
            fail ("ImportException should have been thrown");
        } catch (ImportException e) {
            assertTrue (e != null);
        }
    }
}
