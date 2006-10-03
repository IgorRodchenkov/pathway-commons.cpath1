package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.snapshot.SnapshotReader;
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
        SnapshotReader reader = new SnapshotReader
                (new File ("testData/snapshot"));
        ExternalDatabaseSnapshotRecord record = reader.getSnapshotRecord();
        assertEquals ("1.0", record.getSnapshotVersion());
        assertEquals ("UniProt", record.getExternalDatabase().getName());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals ("01/02/2006", format.format(record.getSnapshotDate()));
    }
}
