package org.mskcc.pathdb.test.sql.assembly;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.sql.*;

/**
 * Suite of all SQL Assembly Unit Tests.
 *
 * @author Ethan Cerami
 */
public class AssemblySuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestAssembly.class);
        suite.setName("SQL Assembly Database Tests");
        return suite;
    }
}