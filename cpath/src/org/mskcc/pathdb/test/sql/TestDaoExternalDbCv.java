package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.ExternalDatabase;
import org.mskcc.pathdb.sql.DaoExternalDb;
import org.mskcc.pathdb.sql.DaoExternalDbCv;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Tests the DaoExternalDbCv Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoExternalDbCv extends TestCase {
    private static final String TERM1 = "ACME_TEST_1";
    private static final String TERM2 = "ACME_TEST_2";
    private static final String DB_NAME = "ACME_TEST";

    /**
     * Tests Access.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        int dbId = createSampleDb();

        DaoExternalDbCv dao = new DaoExternalDbCv();

        //  Add Two Terms
        boolean success = dao.addRecord(dbId, TERM1);
        assertTrue(success);
        success = dao.addRecord(dbId, TERM2);
        assertTrue(success);

        //  Get Matching Database
        ExternalDatabase db = dao.getExternalDbByTerm(TERM1);
        assertEquals(DB_NAME, db.getName());
        db = dao.getExternalDbByTerm(TERM2);
        assertEquals(DB_NAME, db.getName());

        //  Get all terms for Database
        ArrayList terms = dao.getTermsByDbId(dbId);
        assertEquals(2, terms.size());
        String term1 = (String) terms.get(0);
        String term2 = (String) terms.get(1);
        assertEquals(TERM1, term1);
        assertEquals(TERM2, term2);

        //  Delete Terms
        success = dao.deleteTermsByDbId(dbId);
        assertTrue(success);
        db = dao.getExternalDbByTerm(TERM1);
        assertTrue(db == null);
        db = dao.getExternalDbByTerm(TERM2);
        assertTrue(db == null);
    }

    private int createSampleDb() throws SQLException, ClassNotFoundException {
        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabase db = new ExternalDatabase();
        db.setName(DB_NAME);
        db.setDescription("Test");
        dao.addRecord(db);
        db = dao.getRecordByName(DB_NAME);
        int dbId = db.getId();
        return dbId;
    }
}