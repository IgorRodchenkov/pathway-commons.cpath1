package org.mskcc.pathdb.test.sql.transfer;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.sql.*;
import org.mskcc.pathdb.test.sql.references.ReferencesSuite;
import org.mskcc.pathdb.test.sql.assembly.AssemblySuite;

/**
 * Suite of all Transfer Unit Tests.
 *
 * @author Ethan Cerami
 */
public class TransferSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestPopulateInternalFamilyLookUpTable.class);
        suite.setName("SQL Transfer Database Tests");
        return suite;
    }
}
