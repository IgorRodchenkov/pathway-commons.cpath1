package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.controller.ProtocolRequest;
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
        assertEquals(4, list.size());
        ProtocolRequest request = (ProtocolRequest) list.get(0);
        assertEquals("get_by_interactor_tax_id", request.getCommand());
        assertEquals("1.0", request.getVersion());
        assertEquals("9606", request.getQuery());
        assertEquals("psi", request.getFormat());
        assertEquals("webservice.do?version=1.0&cmd="
                + "get_by_interactor_tax_id&q=9606&format=psi&startIndex=0"
                + "&organism=&maxHits=unbounded", request.getUri());
    }
}