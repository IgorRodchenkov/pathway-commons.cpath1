package org.mskcc.pathdb.test.model;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.IdMapRecord;

/**
 * Tests the IdMapRecord.
 *
 * @author Ethan Cerami
 */
public class TestIdMapRecord extends TestCase {

    /**
     * Tests the HashCode Generator for the IdMapRecord.
     */
    public void testHashCode() {
        //  These two records are functionally equivalent
        IdMapRecord record1 = new IdMapRecord(1, "ABC", 2, "XYZ");
        IdMapRecord record2 = new IdMapRecord(2, "XYZ", 1, "ABC");

        //  They should therefore result in identical hash codes
        assertTrue(record1.hashCode() == record2.hashCode());
    }
}
