package org.mskcc.pathdb.test.controller;

import junit.framework.TestCase;
import org.mskcc.pathdb.controller.ProtocolException;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.controller.ProtocolStatusCode;
import org.mskcc.pathdb.controller.ProtocolValidator;

import java.util.HashMap;

/**
 * Tests the Protocol Validator.
 *
 * @author Ethan Cerami
 */
public class TestProtocolValidator extends TestCase {

    /**
     * Tests the Protocol Validator.
     * @throws Exception General Error.
     */
    public void testProtocolValidator() throws Exception {
        HashMap map = new HashMap();
        ProtocolRequest request = new ProtocolRequest(map);
        ProtocolValidator validator = new ProtocolValidator(request);
        try {
            validator.validate();
            fail("ProtocolException should have been thrown");
        } catch (ProtocolException e) {
            ProtocolStatusCode statusCode = e.getStatusCode();
            assertEquals(ProtocolStatusCode.MISSING_ARGUMENTS, statusCode);
        }

    }

}