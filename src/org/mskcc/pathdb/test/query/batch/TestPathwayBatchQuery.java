package org.mskcc.pathdb.test.query.batch;

import junit.framework.TestCase;
import org.mskcc.pathdb.task.PrecomputeTablesTask;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.query.batch.PathwayBatchQuery;
import org.mskcc.pathdb.query.batch.PhysicalEntityWithPathwayList;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Tests the PathwayBatchQuery Class.
 *
 * @author Ethan Cerami
 */
public class TestPathwayBatchQuery extends TestCase {
    private static String ATF2_REF_SEQ_ID = "NP_001871";

    /**
     * To get started, populate all the internal family tables (Required).
     * @throws Exception All Exceptions.
     */
    protected void setUp () throws Exception {
        //  Start cache with clean slate
        EhCache.initCache();
        EhCache.resetAllCaches();

        //  Set up snapshot records
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        dao.deleteAllRecords();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date date = format.parse("01/01/1970");
        long id = dao.addRecord(19, date, "FICTIONAL_RELEASE", 0, 0, 0);
        assertTrue (id > 0);

        //  Populate internal family tables
        PrecomputeTablesTask precomputer = new PrecomputeTablesTask (false, new XDebug());
        precomputer.executeTask();
    }

    /**
     * Tests the Pathway Batch Query.
     * @throws DaoException Database Exception.
     * @throws BioPaxRecordSummaryException Error creating BioPaxRecordSummary.
     */
    public void testPathwayBatchQuery() throws DaoException, BioPaxRecordSummaryException {
        PathwayBatchQuery batchQuery = new PathwayBatchQuery();
        String ids [] = new String[1];
        ids[0] = ATF2_REF_SEQ_ID;
        ArrayList<PhysicalEntityWithPathwayList> list =
            batchQuery.executeBatchQuery(ids, ExternalDatabaseConstants.REF_SEQ);
        PhysicalEntityWithPathwayList peWithPathwayList = list.get(0);
        assertEquals (ATF2_REF_SEQ_ID, peWithPathwayList.getExternalId());
        assertEquals (ExternalDatabaseConstants.REF_SEQ,
                peWithPathwayList.getExternalDb().getMasterTerm());
        ArrayList<BioPaxRecordSummary> pathwayList = peWithPathwayList.getPathwayList();
        assertEquals (2, pathwayList.size());
        BioPaxRecordSummary pathway0 = pathwayList.get(0);
        BioPaxRecordSummary pathway1 = pathwayList.get(1);
        assertEquals ("TGFR", pathway0.getName());
        assertEquals ("AndrogenReceptor", pathway1.getName());
        String output = batchQuery.outputTabDelimitedText(list);
        String lines[] = output.split("\n");
        assertEquals ("REF_SEQ:NP_001871\tTGFR\tREACTOME\t108", lines[1]);

        //  Now try to filter by data source;  by Reactome --> 2 hits
        String dataSources[] = new String[1];
        dataSources[0] = "REACTOME";
        list = batchQuery.executeBatchQuery(ids, ExternalDatabaseConstants.REF_SEQ,
                dataSources);
        peWithPathwayList = list.get(0);
        pathwayList = peWithPathwayList.getPathwayList();
        assertEquals (2, pathwayList.size());

        //  Now try to filter by data source;  by UniProt --> 0 hits
        dataSources[0] = "UNIPROT";
        list = batchQuery.executeBatchQuery(ids, ExternalDatabaseConstants.REF_SEQ,
                dataSources);
        peWithPathwayList = list.get(0);
        pathwayList = peWithPathwayList.getPathwayList();
        assertEquals (0, pathwayList.size());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the batch queries";
    }
}
