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
     * @param dbRecord          ExternalDatabaseRecord Object.
     * @return                  HTTP Status Code.
     * @throws HttpException    HTTP Error.
     * @throws IOException      I/O Error.
     */
    public static int checkSampleLink (ExternalDatabaseRecord dbRecord)
            throws HttpException, IOException {
        if (dbRecord.getUrl() == null) {
            throw new IllegalArgumentException ("External Database Url "
                + "attribute is null");
        }
        if (dbRecord.getSampleId() == null) {
            throw new IllegalArgumentException ("External Database "
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
