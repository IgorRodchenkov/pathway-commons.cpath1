package org.mskcc.pathdb.test.web;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all Web Unit Tests.
 *
 * @author Ethan Cerami
 */
public class WebSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestProtocol.class);
        suite.setName("Web Tests");
        return suite;
    }
}