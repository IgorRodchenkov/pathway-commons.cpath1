package org.mskcc.pathdb.test.lucene;

import junit.framework.TestCase;
import org.mskcc.pathdb.lucene.LuceneAutoFilter;
import org.mskcc.pathdb.lucene.LuceneConfig;

import java.util.ArrayList;

/**
 * Tests the LuceneAutoFilter.
 *
 * @author Ethan Cerami.
 */
public class TestLuceneAutoFilter extends TestCase {

    /**
     * Tests the Lucene Auto Filter class.
     */
    public void testLuceneAutoFilter() {
        ArrayList list = new ArrayList();
        list.add("Reactome");
        list.add("KEGG");
        list.add("INTACT");
        String q = LuceneAutoFilter.addFiltersToQuery("p53", LuceneConfig.FIELD_DATA_SOURCE,
                list);
        assertEquals ("p53 AND (data_source:\"Reactome\" OR data_source:\"KEGG\" " +
                "OR data_source:\"INTACT\")",
                q);
    }

    /**
     * Gets Test Name.
     * @return Test name.
     */
    public String getName() {
        return "Testing Lucene Auto Filter to auto-filter results, "
                + "based on user session settings";
    }
}
