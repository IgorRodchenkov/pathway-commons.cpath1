package org.mskcc.pathdb.test.indexer;


import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.util.XmlStripper;

import java.io.IOException;

/**
 * Tests the StoreXmlToIndexer and the QueryIndexer Classes.
 *
 * @author Ethan Cerami
 */
public class TestIndexer extends TestCase {
    private static final String JUNIT_NAME = "JUNIT NAME";
    private static final String JUNIT_DESCRIPTION = "JUNIT DESCRIPTION";
    private static final long ID = 1234;

    /**
     * Tests the Full Text Indexer.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);

        LuceneIndexer lucene = new LuceneIndexer();
        lucene.initIndex();
        lucene.addRecord(JUNIT_NAME, JUNIT_DESCRIPTION, xml, ID);

        //  Test with a bunch of terms.
        query("exchange");
        query("factor");
        query("YAL036C");
        query("GTP GDP");
        query("Q07418");
        query("SwissProt");
        query("swissPROT");
    }

    private void query(String term) throws QueryException,
            IOException {
        LuceneIndexer lucene = new LuceneIndexer();
        Hits hits = lucene.executeQuery(term);
        assertEquals(1, hits.length());
        Document doc = hits.doc(0);

        //  Validate that we get back name, description and cpath id.
        Field name = doc.getField(LuceneIndexer.FIELD_NAME);
        Field description = doc.getField(LuceneIndexer.FIELD_DESCRIPTION);
        Field id = doc.getField(LuceneIndexer.FIELD_CPATH_ID);
        assertEquals(JUNIT_NAME, name.stringValue());
        assertEquals(JUNIT_DESCRIPTION, description.stringValue());
        assertEquals(Long.toString(ID), id.stringValue());
    }

    /**
     * Tests the XML Stripper Utility.
     * @throws Exception All Exceptions.
     */
    public void testStripper() throws Exception {
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);

        //  Test the XML Stripper.
        XmlStripper stripper = new XmlStripper();
        String text = stripper.stripTags(xml);
        int index = text.indexOf("classical two hybrid");
        assertTrue(index > 0);
    }
}