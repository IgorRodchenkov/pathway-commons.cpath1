package org.mskcc.pathdb.test.web;

import junit.framework.TestCase;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.test.TestConstants;

/**
 * Tests Protocol via HttpClient Library.
 *
 * @author Ethan Cerami
 */
public class TestProtocol extends TestCase {

    /**
     * Tests a Valid Request.
     * @throws Exception All Errors
     */
    public void testValidRequest() throws Exception {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(TestConstants.LOCAL_HOST_URL);
        NameValuePair nvps[] = new NameValuePair[5];
        nvps[0] = new NameValuePair(ProtocolRequest.ARG_COMMAND,
                ProtocolConstants.COMMAND_RETRIEVE_INTERACTIONS);
        nvps[1] = new NameValuePair(ProtocolRequest.ARG_DB,
                ProtocolConstants.DATABASE_GRID);
        nvps[2] = new NameValuePair(ProtocolRequest.ARG_FORMAT,
                ProtocolConstants.FORMAT_PSI);
        nvps[3] = new NameValuePair(ProtocolRequest.ARG_VERSION,
                ProtocolConstants.CURRENT_VERSION);
        nvps[4] = new NameValuePair(ProtocolRequest.ARG_UID,
                "YCR038C");
        method.setQueryString(nvps);
        int statusCode = client.executeMethod(method);
        //  Ds-status header should be: "ok"
        Header header =
                method.getResponseHeader(ProtocolConstants.DS_HEADER_NAME);
        assertEquals(ProtocolConstants.DS_OK_STATUS, header.getValue());
    }

    /**
     * Tests Invalid Request.
     * @throws Exception All Errors.
     */
    public void testInvalidRequest() throws Exception {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(TestConstants.LOCAL_HOST_URL);
        NameValuePair nvps[] = new NameValuePair[1];
        nvps[0] = new NameValuePair(ProtocolRequest.ARG_COMMAND,
                "get_invalid");
        method.setQueryString(nvps);
        int statusCode = client.executeMethod(method);
        //  Ds-status header should be: "error"
        Header header =
                method.getResponseHeader(ProtocolConstants.DS_HEADER_NAME);
        assertEquals(ProtocolConstants.DS_ERROR_STATUS, header.getValue());
    }
}
