// $Id: TestProtocol.java,v 1.15 2006-02-21 22:51:11 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
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

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the Web Services API Protocol";
    }
}
