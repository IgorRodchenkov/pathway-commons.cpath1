package org.mskcc.pathdb.test.protocol;

import junit.framework.TestCase;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;

/**
 * Tests the ProtocolException object.
 *
 * @author Ethan Cerami
 */
public class TestProtocolException extends TestCase {
    /**
     * Expected snippet of XML Error message.
     */
    private static final String EXPECTED_ERROR_MSG =
            "<error_msg>Bad Command (command not recognized)</error_msg>";

    /**
     * Verifies that ProtocolException returns an XML document.
     *
     * @throws Exception General Error.
     */
    public void testProtocolException() throws Exception {
        ProtocolException pe = new ProtocolException
                (ProtocolStatusCode.BAD_COMMAND);
        String xml = pe.toXml();
        int index = xml.indexOf(EXPECTED_ERROR_MSG);
        assertTrue("Verifying XML Error Document Contents", index > 0);
    }
}