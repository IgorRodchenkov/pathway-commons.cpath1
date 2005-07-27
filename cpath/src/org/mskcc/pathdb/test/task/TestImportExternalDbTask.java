package org.mskcc.pathdb.test.task;

import junit.framework.TestCase;

import java.io.File;

import org.mskcc.pathdb.task.ImportExternalDbTask;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

/**
 * Tests the Importing of External Databases.
 *
 * @author Ethan Cerami
 */
public class TestImportExternalDbTask extends TestCase {

    /**
     * Tests the Importing of External Databases.
     * @throws Exception All Exceptions.
     */
    public void testImport() throws Exception {
        File file = new File ("testData/externalDb/external_db.xml");
        ImportExternalDbTask task = new ImportExternalDbTask (file, false,
                false);
        int numRecords = task.importFile();
        assertEquals (2, numRecords);

        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord = dao.getRecordByTerm("YHO");
        assertEquals ("Yahoo", dbRecord.getName());

        //  Try adding again.  This should fail, as MySQL maintains that
        //  database names are unique.
        try {
            task.importFile();
            fail ("DaoException should have been thrown.");
        } catch (DaoException e) {
            String msg = e.getMessage();
            System.out.println(msg);
        }
    }
}
