package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.pathdb.util.Md5Util;

/**
 * Tests the MD5 Utility Class.
 *
 * @author Ethan Cerami
 */
public class TestMd5Util extends TestCase {

    /**
     * Tests the Md5Util.createMd5Hash() method.
     *
     * @throws Exception All Exceptions.
     */
    public void testMD5Hash() throws Exception {
        String hash1 = Md5Util.createMd5Hash("testing-1-2-3");
        String hash2 = Md5Util.createMd5Hash("ethan cerami");
        assertEquals("JDK+1OynKkArmOBLktXFdw==", hash1);
        assertEquals("tKLat99Yvf/irKr1be2f2g==", hash2);
    }
}
