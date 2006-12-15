package org.mskcc.pathdb.test.sql.transfer;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.transfer.PopulateInternalFamilyLookUpTable;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.util.Profile;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

/**
 * Tests the PopulateInternalFamilyLookUpTable Class.
 *
 * @author Ethan Cerami.
 */
public class TestPopulateInternalFamilyLookUpTable extends TestCase {

    /**
     * Tests the Populator
     * @throws DaoException Database Access Error.
	 * @throws BioPaxRecordSummaryException.
     */
    public void testPopulator() throws DaoException, BioPaxRecordSummaryException {
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);
        PopulateInternalFamilyLookUpTable populator = new PopulateInternalFamilyLookUpTable
                (pMonitor);
        populator.execute();
        DaoInternalFamily dao = new DaoInternalFamily();

        //  Test Glycolysis Pathway
        long ids[] = dao.getDescendentIds(5, CPathRecordType.PHYSICAL_ENTITY);
        assertEquals (55, ids.length);

        //  Test Androgen Pathway
        ids = dao.getDescendentIds(298, CPathRecordType.PHYSICAL_ENTITY);
        assertEquals (95, ids.length);
    }

    //  Keep this here for possible future reference
    private void testReactome() throws Exception {
        ProgressMonitor pMonitor = new ProgressMonitor();
        PopulateInternalFamilyLookUpTable populator = new PopulateInternalFamilyLookUpTable
                (pMonitor);
        DaoInternalFamily dao = new DaoInternalFamily();
        dao.deleteAllRecords();
        long[] ids = populator.getDescendents(9);
        for (int i=0; i<ids.length; i++) {
            System.out.println("Child:  " + ids[i]);
        }
        System.out.println("Num children:  " + ids.length);
    }
}
