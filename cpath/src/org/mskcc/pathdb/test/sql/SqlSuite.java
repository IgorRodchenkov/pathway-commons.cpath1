package org.mskcc.pathdb.test.sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all SQL Unit Tests.
 *
 * @author Ethan Cerami
 */
public class SqlSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestDatabaseImport.class);
        suite.addTestSuite(TestGridInteractorTable.class);
        suite.addTestSuite(TestGridInteractionTable.class);
        suite.setName("SQL Database Tests");
        return suite;
    }
}