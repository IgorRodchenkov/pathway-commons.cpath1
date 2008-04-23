package org.mskcc.pathdb.test.tool;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.util.*;
import org.mskcc.pathdb.test.util.rdf.RdfUtilSuite;

/**
 * Suite of all Tool Unit Tests.
 *
 * @author Ethan Cerami
 */
public class ToolSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestUniProtParser.class);
        suite.addTestSuite(TestEntrezGeneParser.class);
        suite.setName("Tool Tests");
        return suite;
    }
}
