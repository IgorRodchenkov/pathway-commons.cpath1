package org.mskcc.pathdb.test.web;

import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.test.TestConstants;

/**
 * Tests Protocol via HttpClient Library.
 *
 * @author Ethan Cerami
 */
public class TestProtocol extends TestCase {

    /**
     * Tests Invalid Request.
     *
     * @throws Exception All Errors.
     */
    public void testInvalidRequest() throws Exception {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(TestConstants.LOCAL_HOST_URL);
        NameValuePair nvps[] = new NameValuePair[2];
        nvps[0] = new NameValuePair(ProtocolRequest.ARG_COMMAND,
                "get_invalid");
        nvps[1] = new NameValuePair(ProtocolRequest.ARG_FORMAT,
                ProtocolConstants.FORMAT_XML);
        method.setQueryString(nvps);
        int statusCode = client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        int index = response.indexOf("<error_code>450</error_code>");
        assertTrue(index > 0);
    }
}
