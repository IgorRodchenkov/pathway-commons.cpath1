package org.mskcc.pathdb.test.task;

import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;

import org.mskcc.pathdb.task.ParseIdMappingsTask;
import org.mskcc.pathdb.model.IdMapRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoIdMap;

public class TestIdMappingsParser extends TestCase {

    public void testParserWithOutSave() throws Exception {
        File file = new File ("testData/id_map.txt");
        ParseIdMappingsTask task = new ParseIdMappingsTask(file, false);
        ArrayList list = task.parseAndGenerateList();

        //  Validate the first four id mappings.
        validateIdMapRecord(0, list, "Affymetrix", "1552275_3p_s_at",
                "UniGene", "Hs.77646");
        validateIdMapRecord(1, list, "UniGene", "Hs.77646",
                "Swiss-Prot", "AAH08943");
        validateIdMapRecord(2, list, "UniGene", "Hs.77646",
                "Swiss-Prot", "Q727A4");
        validateIdMapRecord(3, list, "Swiss-Prot", "AAH08943",
                "RefSeq", "NP_060241");
    }

    public void testParserWithSave() throws Exception {
        File file = new File ("testData/id_map.txt");
        ParseIdMappingsTask task = new ParseIdMappingsTask(file, false);
        int numRecordsSaved = task.parseAndStoreToDb();

        //  Validate Number of Records Saved
        assertEquals (7, numRecordsSaved);

        //  Now try saving again;  verify 0 records saved
        numRecordsSaved = task.parseAndStoreToDb();
        assertEquals (0, numRecordsSaved);

        //  Delete all records, so that we can rerun this unit test again
        DaoIdMap dao = new DaoIdMap();
        dao.deleteAllRecords();
    }

    private void validateIdMapRecord(int index, ArrayList idList,
            String expectedDb1, String expectedId1, String expectedDb2,
            String expectedId2) throws DaoException {
        IdMapRecord id = (IdMapRecord) idList.get(index);
        DaoExternalDb dao = new DaoExternalDb();
        int db1 = id.getDb1();
        ExternalDatabaseRecord dbRecord1 = dao.getRecordById(db1);
        int db2 = id.getDb2();
        ExternalDatabaseRecord dbRecord2 = dao.getRecordById(db2);
        assertEquals (expectedDb1, dbRecord1.getName());
        assertEquals (expectedId1, id.getId1());
        assertEquals (expectedDb2, dbRecord2.getName());
        assertEquals (expectedId2, id.getId2());
    }
}
