package org.mskcc.pathdb.test.xmlrpc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all XML-RPC Unit Tests.
 *
 * @author Ethan Cerami
 */
public class XmlRpcSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestDataSubmission.class);
        suite.setName("XML-RPC Tests");
        return suite;
    }
}