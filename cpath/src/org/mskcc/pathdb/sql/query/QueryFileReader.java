package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.util.XssFilter;

import javax.servlet.http.HttpUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Reads in a Text File of Relative URLs and returns a List of
 * Protocol Request objects.
 * <p/>
 * <P>
 * Sample file format:
 * </P>
 * <PRE>
 * # Retrieve all Human Data
 * webservice?version=1.0&cmd=get_by_interactor_tax_id&q=9606&q=&maxHits= \
 * unbounded&format=psi
 * </PRE>
 *
 * @author Ethan Cerami
 */
public class QueryFileReader {

    /**
     * Read in Text File, and extract Protocol Request Objects.
     *
     * @param fileName File Name.
     * @return ArrayList of ProtocolRequest objects.
     * @throws IOException Error Reading in File.
     */
    public ArrayList getProtocolRequests(String fileName)
            throws IOException {
        ArrayList list = new ArrayList();
        FileReader reader = new FileReader(fileName);
        BufferedReader buf = new BufferedReader(reader);
        String line = buf.readLine();
        while (line != null) {
            if (!line.startsWith("#") && line.length() > 0) {
                Hashtable params1 = HttpUtils.parseQueryString(line);
                HashMap params2 = XssFilter.filterAllParameters(params1);
                ProtocolRequest request = new ProtocolRequest(params2);
                list.add(request);
            }
            line = buf.readLine();
        }
        return list;
    }
}