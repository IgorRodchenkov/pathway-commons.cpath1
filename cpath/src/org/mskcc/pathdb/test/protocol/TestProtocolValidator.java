package org.mskcc.pathdb.test.protocol;

import junit.framework.TestCase;
import org.mskcc.pathdb.protocol.*;

import java.util.HashMap;

/**
 * Tests the Protocol Validator.
 *
 * @author Ethan Cerami
 */
public class TestProtocolValidator extends TestCase {

    /**
     * Tests the Protocol Validator.
     *
     * @throws Exception General Error.
     */
    public void testProtocolValidator() throws Exception {
        HashMap map = new HashMap();
        map.put(ProtocolRequest.ARG_COMMAND,
                ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME);
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

    /**
     * Tests the Protocol Validator.
     *
     * @throws Exception General Error.
     */
    public void testEmptyParameterSet() throws Exception {
        HashMap map = new HashMap();
        ProtocolRequest request = new ProtocolRequest(map);
        ProtocolValidator validator = new ProtocolValidator(request);
        try {
            validator.validate();
            fail("ProtocolException should have been thrown");
        } catch (NeedsHelpException e) {
            request.getCommand();  // Do Nothing.
        }
    }
}