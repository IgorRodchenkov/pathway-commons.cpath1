package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;

/**
 * Tests the DaoXmlCache Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoXmlCache extends TestCase {
    private static final String HASH_KEY = "sander#bader#123";
    private static final String XML_MSG_1 = "<msg>Hello, World</msg>";
    private static final String XML_MSG_2 = "<msg>Hello, Moon</msg>";

    /**
     * Tests DaoXmlCache.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        DaoXmlCache dao = new DaoXmlCache();

        //  Add a new record, and verify success.
        boolean success = dao.addRecord (HASH_KEY, XML_MSG_1);
        assertTrue (success);

        //  Get the record, and match xml content.
        String xml = dao.getXmlByKey(HASH_KEY).trim();
        assertEquals (XML_MSG_1, xml);

        //  Update the record, and verify success.
        success = dao.updateXmlByKey(HASH_KEY, XML_MSG_2);
        assertTrue (success);

        //  Get the record again, and match against new xml content.
        xml = dao.getXmlByKey(HASH_KEY).trim();
        assertEquals (XML_MSG_2, xml);

        //  Delete record, and verify success.
        success = dao.deleteRecordByKey(HASH_KEY);
        assertTrue (success);

        //  Verify that record is no longer here, and that no exceptions
        //  occur.
        xml = dao.getXmlByKey(HASH_KEY);
        assertEquals (null, xml);
    }
}