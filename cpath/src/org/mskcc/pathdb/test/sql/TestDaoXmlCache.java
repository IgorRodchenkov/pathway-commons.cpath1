package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * Tests the DaoXmlCache Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoXmlCache extends TestCase {
    private static final String HASH_KEY = "sander#bader#123";

    /**
     * Tests DaoXmlCache.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        XDebug xdebug = new XDebug();
        DaoXmlCache dao = new DaoXmlCache(xdebug);

        //  Add a new record, and verify success.
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (4, 1, xdebug);
        String originalXml = assembly.getXmlString();
        assembly.setNumHits(100);

        boolean success = dao.addRecord(HASH_KEY, assembly);
        assertTrue(success);

        //  Get the record, and match xml content.
        assembly = dao.getXmlAssemblyByKey(HASH_KEY);
        assertTrue(assembly.getXmlString().length() > 50);
        assertEquals(originalXml, assembly.getXmlString());
        assertEquals(100, assembly.getNumHits());

        //  Update the record, and verify success.
        assembly = XmlAssemblyFactory.createXmlAssembly(4, 1, xdebug);
        assembly.setNumHits(200);

        success = dao.updateXmlAssemblyByKey(HASH_KEY, assembly);
        assertTrue(success);

        //  Get the record again, and match against new xml content.
        assembly = dao.getXmlAssemblyByKey(HASH_KEY);
        assertTrue(assembly.getXmlString().length() > 50);
        assertEquals(originalXml, assembly.getXmlString());
        assertEquals(200, assembly.getNumHits());

        //  Delete record, and verify success.
        success = dao.deleteRecordByKey(HASH_KEY);
        assertTrue(success);

        //  Verify that record is no longer here, and that no exceptions
        //  occur.
        assembly = dao.getXmlAssemblyByKey(HASH_KEY);
        assertEquals(null, assembly);
    }
}