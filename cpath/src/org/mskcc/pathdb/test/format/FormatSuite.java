package org.mskcc.pathdb.test.format;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.controller.TestProtocolException;
import org.mskcc.pathdb.test.controller.TestProtocolValidator;

/**
 * Suite of all Formatter Unit Tests.
 *
 * @author Ethan Cerami
 */
public class FormatSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestPsiFormatter.class);
        suite.setName("Formatter Tests");
        return suite;
    }
}