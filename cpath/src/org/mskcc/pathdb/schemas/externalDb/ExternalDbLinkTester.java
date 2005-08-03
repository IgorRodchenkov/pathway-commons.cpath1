/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.schemas.externalDb;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import java.io.IOException;

/**
 * Given an External Database Record object, this class connects
 * to the specificed url pattern with a sample id, and returns the
 * HTTP status code.  This provides a simple sanity check to verify that
 * the live links to an external database are still functioning correctly.
 * Verifying that the links actually return the "correct" data is much more
 * difficult, and beyond the scope of what we want to do.
 *
 * @author Ethan Cerami.
 */
public class ExternalDbLinkTester {

    /**
     * Given an External Database Record object, this class connects
     * to the specificed url pattern with a sample id, and returns the
     * HTTP status code.  This provides a simple sanity check to verify that
     * the live links to an external database are still functioning correctly.
     * Verifying that the links actually return the "correct" data is much more
     * difficult, and beyond the scope of what this method can do.
     *
     * @param dbRecord ExternalDatabaseRecord Object.
     * @return HTTP Status Code.
     * @throws HttpException HTTP Error.
     * @throws IOException   I/O Error.
     */
    public static int checkSampleLink(ExternalDatabaseRecord dbRecord)
            throws HttpException, IOException {
        if (dbRecord.getUrl() == null) {
            throw new IllegalArgumentException("External Database Url "
                    + "attribute is null");
        }
        if (dbRecord.getSampleId() == null) {
            throw new IllegalArgumentException("External Database "
                    + "sample ID attribute is null");
        }
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        String url = dbRecord.getUrlWithId(dbRecord.getSampleId());
        HttpMethod method = new GetMethod(url);
        method.setFollowRedirects(true);
        int statusCode = client.executeMethod(method);
        method.releaseConnection();
        return statusCode;
    }
}
