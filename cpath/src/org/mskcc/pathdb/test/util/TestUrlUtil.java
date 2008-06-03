package org.mskcc.pathdb.test.util;

import org.mskcc.pathdb.util.UrlUtil;
import junit.framework.TestCase;

/**
 * Junit Test for URL Util.
 */
public class TestUrlUtil extends TestCase {

    public void testUrlUtil () {
        String newUrl = UrlUtil.rewriteUrl("http://pathwaycommons.org/pc/record2.do",
                "stable.do");
        assertEquals ("http://pathwaycommons.org/pc/stable.do", newUrl);
    }
}
