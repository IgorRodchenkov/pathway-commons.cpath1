package org.mskcc.pathdb.test.logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all Formatter Unit Tests.
 *
 * @author Ethan Cerami
 */
public class LoggerSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestAdminLogger.class);
        suite.setName("Logger Tests");
        return suite;
    }
}