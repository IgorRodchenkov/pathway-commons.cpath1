package org.mskcc.pathdb.test.controller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all Controller Unit Tests.
 *
 * @author Ethan Cerami
 */
public class ControllerSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestProtocolException.class);
        suite.addTestSuite(TestProtocolValidator.class);
        suite.setName("Controller Tests");
        return suite;
    }
}