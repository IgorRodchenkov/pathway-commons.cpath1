package org.mskcc.pathdb.test.indexer;


import junit.framework.TestCase;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.util.XmlStripper;

import java.util.ArrayList;

/**
 * Tests the StoreXmlToIndexer and the QueryIndexer Classes.
 *
 * @author Ethan Cerami
 */
public class TestIndexer extends TestCase {
    private static final String JUNIT_NAME = "JUNIT NAME";

    /**
     * Tests the Full Text Indexer.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);

        DaoCPath dao = new DaoCPath();
        long cpathId = dao.addRecord(JUNIT_NAME, "JUNIT_DESCRIPTION", 1234,
                CPathRecordType.INTERACTION, xml);

        LuceneIndexer lucene = new LuceneIndexer();
        lucene.addRecord(xml, cpathId);

        //  Test with a bunch of terms.
        query("exchange", cpathId);
        query("factor", cpathId);
        query("YAL036C", cpathId);
        query("GTP GDP", cpathId);
        query("Q07418", cpathId);
        query("SwissProt", cpathId);
        query("swissPROT", cpathId);
    }

    private void query(String term, long cpathId) throws QueryException {
        LuceneIndexer lucene = new LuceneIndexer();
        ArrayList records = lucene.executeQueryWithLookUp(term);
        assertEquals(1, records.size());
        CPathRecord record = (CPathRecord) records.get(0);
        assertEquals(JUNIT_NAME, record.getName());
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