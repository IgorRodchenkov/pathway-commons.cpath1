package org.mskcc.pathdb.test.form;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.model.TestBackgroundReferencePair;

/**
 * Suite of all Form Unit Tests.
 *
 * @author Ethan Cerami
 */
public class FormSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestFeedbackForm.class);
        suite.setName("Form Tests");
        return suite;
    }
}
