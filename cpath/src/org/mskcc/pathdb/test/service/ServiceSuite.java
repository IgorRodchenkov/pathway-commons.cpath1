package org.mskcc.pathdb.test.service;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all SQL Unit Tests.
 *
 * @author Ethan Cerami
 */
public class ServiceSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestReadInteractorsFromGrid.class);
        suite.addTestSuite(TestReadInteractionsFromGrid.class);
        suite.addTestSuite(TestWriteInteractorsToGrid.class);
        suite.addTestSuite(TestWriteInteractionsToGrid.class);
        suite.setName("Data Service Tests");
        return suite;
    }
}