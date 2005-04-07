package org.mskcc.pathdb.test.sql;

import org.mskcc.pathdb.sql.dao.DaoIdGenerator;
import junit.framework.TestCase;

/**
 * Tests the DaoID Generator Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoIdGenerator extends TestCase {

    /**
     * Tests the ID Generator.
     * @throws Exception All Exceptions.
     */
    public void testIdGenerator() throws Exception {
        DaoIdGenerator dao = new DaoIdGenerator();

        //  Reset the ID Generator, so that we start at 0.
        dao.resetIdGenerator();

        //  Get Two IDs
        int id1 = parseId(dao.getNextId());
        int id2 = parseId(dao.getNextId());

        //  Verify that the IDs are sequential, and start at 0.
        assertEquals (0, id1);
        assertEquals (1, id2);
    }

    /**
     * Parses an ID String with a cPath Prefix.
     */
    private int parseId(String idStr) {
        return Integer.parseInt
                (idStr.substring
                (DaoIdGenerator.CPATH_LOCAL_ID_PREFIX.length()));
    }
}
