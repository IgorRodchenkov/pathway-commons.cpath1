package org.mskcc.pathdb.test.lucene;

import junit.framework.TestCase;
import org.mskcc.pathdb.lucene.LuceneUtil;

public class TestLuceneUtil extends TestCase {

    public void testLuceneUtil() {
        String query = LuceneUtil.cleanQuery("p-53 +helicase");
        assertEquals ("\"p-53\" +helicase", query);

        query = LuceneUtil.cleanQuery("dna repair");
        assertEquals ("dna repair", query);

        query = LuceneUtil.cleanQuery("experiment_type:\"dna repair\"");
        assertEquals ("experiment_type:\"dna repair\"", query);
    }
}
