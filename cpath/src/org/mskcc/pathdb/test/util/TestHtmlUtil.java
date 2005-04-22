package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.pathdb.util.html.HtmlUtil;

/**
 * Tests the HtmlUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestHtmlUtil extends TestCase {

    /**
     * Tests the Truncate Words Method.
     */
    public void testTruncationMethod() {
        String truncated = HtmlUtil.truncateLongWords
                ("here is a sequence: AAAAAAAAAAAAAAAAAA more stuff", 10);
        assertEquals ("here is a sequence: AAAAAAAAAA [Cont.] more stuff",
                truncated);
    }
}
