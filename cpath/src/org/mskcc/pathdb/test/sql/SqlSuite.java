package org.mskcc.pathdb.test.sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all SQL Database Unit Tests.
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
        suite.addTestSuite(TestGridProteinService.class);
        suite.addTestSuite(TestGridInteractionService.class);
        suite.setName("Sql Database Tests");
        return suite;
    }
}