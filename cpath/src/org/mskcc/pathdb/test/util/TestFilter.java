package org.mskcc.pathdb.test.util;


import junit.framework.TestCase;
import org.mskcc.pathdb.util.XssFilter;

/**
 * Tests the XssFilter Class.
 *
 * @author Ethan Cerami
 */
public class TestFilter extends TestCase {

    /**
     * Tests XssFilter.
     *
     * @throws Exception All Exceptions.
     */
    public void testFilter() throws Exception {
        String text = XssFilter.filter("<SCRIPT>alert('hello')</SCRIPT>");
        assertEquals("_SCRIPT_alert_'hello'_SCRIPT_", text);
    }
}