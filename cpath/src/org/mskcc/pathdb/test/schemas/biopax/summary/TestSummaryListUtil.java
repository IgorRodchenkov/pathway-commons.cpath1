package org.mskcc.pathdb.test.schemas.biopax.summary;

import org.mskcc.pathdb.schemas.biopax.summary.SummaryListUtil;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummary;

import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * Tests the SummaryListUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestSummaryListUtil extends TestCase {

    /**
     * Tests the SummaryListUtil Class.
     * @throws Exception All Exceptions
     */
    public void testSummaryList() throws Exception {
        SummaryListUtil util = new SummaryListUtil(108);
        ArrayList list = util.getSummaryList();
        EntitySummary summary = (EntitySummary) list.get(0);
        String currentType = summary.getSpecificType();
        for (int i=0; i<list.size(); i++) {
            summary = (EntitySummary) list.get(i);

            //  Verify that list is sorted by specific type, in ascending order.
            int compare = summary.getSpecificType().compareTo(currentType);
            assertTrue (compare >= 0);
            currentType = summary.getSpecificType();
        }
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can retrieve a list of Interaction Summary objects";
    }
}