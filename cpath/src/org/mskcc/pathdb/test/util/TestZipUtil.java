package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.pathdb.util.ZipUtil;

/**
 * Tests the ZipUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestZipUtil extends TestCase {

    /**
     * Tests Roundtripping of Zipped Data.
     * @throws Exception All Exceptions.
     */
    public void testZipUtil() throws Exception {
        String testString = "Hello, World!";
        byte zippedData[] = ZipUtil.zip(testString);
        String unzippedData = ZipUtil.unzip(zippedData);
        assertEquals(testString + "\n", unzippedData);
    }
}
