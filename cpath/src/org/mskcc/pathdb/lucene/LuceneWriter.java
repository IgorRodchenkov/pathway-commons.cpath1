package org.mskcc.pathdb.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.mskcc.pathdb.sql.transfer.ImportException;

import java.io.IOException;

/**
 * Write to the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneWriter {
    private String dir = LuceneConfig.getLuceneDirectory();
    private Analyzer analyzer = LuceneConfig.getLuceneAnalyzer();

    /**
     * Constructor.
     *
     * @param resetFlag If Set to True, Existing Index is deleted!
     */
    public LuceneWriter(boolean resetFlag) throws IOException {
        if (resetFlag) {
            IndexWriter writer = new IndexWriter(dir, analyzer, resetFlag);
            writer.close();
        }
    }

    /**
     * Adds New Record to the Lucene Index.
     *
     * @param item ItemToIndex.
     * @throws ImportException Error Adding New Record to Lucene.
     */
    public void addRecord(ItemToIndex item)
            throws ImportException {
        IndexWriter writer = null;
        try {
            //  Create Index Writer
            writer = new IndexWriter(dir, analyzer, false);

            //  Set CompoundFile to True
            //  From Lucene Javadoc:  When on, multiple files for each segment
            //  are merged into a single file once the segment creation is
            //  finished.  Setting to true is one of the recommended solutions
            //  for resolving the "too many open files" error which occurs
            //  on Linux.
            writer.setUseCompoundFile(true);

            //  Index all Fields in ItemToIndex
            Document document = new Document();
            int numFields = item.getNumFields();
            for (int i = 0; i < numFields; i++) {
                Field field = item.getField(i);
                document.add(field);
            }
            //  Add New Document to Lucene
            writer.addDocument(document);
            writer.close();
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

//    /**
//     * Optimizes Lucene Index Files.
//     */
//    public void optimize() throws IOException {
//        IndexWriter writer = new IndexWriter(dir, analyzer, false);
//        System.out.println("Optimizing Lucene Indexes...");
//        writer.optimize();
//    }
}