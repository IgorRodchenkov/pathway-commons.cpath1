package org.mskcc.pathdb.test.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all Utility Unit Tests.
 *
 * @author Ethan Cerami
 */
public class UtilSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestZipUtil.class);
        suite.addTestSuite(TestMd5Util.class);
        suite.addTestSuite(TestPsiUtil.class);
        suite.addTestSuite(TestXmlValidator.class);
        suite.setName("Utility Tests");
        return suite;
    }
}