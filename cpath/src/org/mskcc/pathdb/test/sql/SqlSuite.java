package org.mskcc.pathdb.test.sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.sql.assembly.AssemblySuite;

/**
 * Suite of all SQL Unit Tests.
 *
 * @author Ethan Cerami
 */
public class SqlSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestDaoImport.class);
        suite.addTestSuite(TestDaoExternalDb.class);
        suite.addTestSuite(TestDaoExternalDbCv.class);
        suite.addTestSuite(TestDaoExternalLink.class);
        suite.addTestSuite(TestDaoCPath.class);
        suite.addTestSuite(TestDaoInternalLink.class);
        suite.addTestSuite(TestImportPsiToCPath.class);
        suite.addTestSuite(TestUpdatePsiInteractor.class);
        suite.addTestSuite(TestDaoXmlCache.class);
        suite.addTestSuite(TestDaoOrganism.class);
        suite.addTestSuite(TestDaoLog.class);
        suite.addTestSuite(TestQueryFileReader.class);
        suite.addTest(AssemblySuite.suite());
        suite.setName("SQL Database Tests");
        return suite;
    }
}