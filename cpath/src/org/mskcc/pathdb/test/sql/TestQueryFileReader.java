package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.query.QueryFileReader;

import java.util.ArrayList;

/**
 * Tests the QueryFileReader Class.
 *
 * @author Ethan Cerami
 */
public class TestQueryFileReader extends TestCase {

    /**
     * Tests QueryFileReader.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        QueryFileReader reader = new QueryFileReader();
        ArrayList list = reader.getProtocolRequests
                ("testData/precompute_junit.txt");
        assertEquals(3, list.size());
        ProtocolRequest request = (ProtocolRequest) list.get(0);
        assertEquals("get_by_keyword", request.getCommand());
        assertEquals("1.0", request.getVersion());
        assertEquals(null, request.getQuery());
        assertEquals("psi", request.getFormat());
        assertEquals("webservice.do?version=1.0&cmd=get_by_keyword&q=&"
                + "format=psi&startIndex=0&organism=562&maxHits=10",
                request.getUri());
    }
}