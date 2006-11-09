package org.mskcc.pathdb.test.sql.transfer;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.transfer.PopulateInternalFamilyLookUpTable;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.util.Profile;

/**
 * Tests the PopulateInternalFamilyLookUpTable Class.
 *
 * @author Ethan Cerami.
 */
public class TestPopulateInternalFamilyLookUpTable extends TestCase {

    /**
     * Tests the Populator
     * @throws DaoException Database Access Error.
     */
    public void testPopulator() throws DaoException {
        JdbcUtil.setCommandLineFlag(true);
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
}
