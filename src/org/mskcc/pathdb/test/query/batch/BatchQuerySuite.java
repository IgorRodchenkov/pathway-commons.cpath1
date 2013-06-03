package org.mskcc.pathdb.test.query.batch;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite of all Batch Query Unit Tests.
 *
 * @author Ethan Cerami
*/
public class BatchQuerySuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestPathwayBatchQuery.class);
        suite.setName("Batch Query Tests");
        return suite;
    }
}
