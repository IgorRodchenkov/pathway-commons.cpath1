package org.mskcc.pathdb.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.mskcc.pathdb.sql.transfer.ImportException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Write to the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneWriter {
    /**
     * Index Writer Object (for indexing new records in Lucene).
     */
    private IndexWriter writer;

    private String dir = LuceneConfig.getLuceneDirectory();
    private Analyzer analyzer = LuceneConfig.getLuceneAnalyzer();

    /**
     * Constructor.
     *
     * @param resetFlag If Set to True, Existing Index is deleted!
     */
    public LuceneWriter(boolean resetFlag) throws IOException {
        writer = new IndexWriter(dir, analyzer, resetFlag);
        writer.mergeFactor = 1000;
        //writer.close();
    }

    /**
     * Adds New Record to the Lucene Index.
     *
     * @param item ItemToIndex.
     * @throws ImportException Error Adding New Record to Lucene.
     */
    public void addRecord(ItemToIndex item)
            throws ImportException {
        try {
            //writer = new IndexWriter(dir, analyzer, false);
            //  Index all Fields in ItemToIndex
            Document document = new Document();
            int numFields = item.getNumFields();
            for (int i = 0; i < numFields; i++) {
                Field field = item.getField(i);
                document.add(field);
            }
            //  Add New Document to Lucene
            writer.addDocument(document);
            //writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Got an Error:  " + e.toString());
            optimize();
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

    public void commit() throws IOException {
        writer.close();
    }

    /**
     * Optimizes Lucene Index Files.
     */
    public void optimize() {
        try {
            System.out.println("Attempting to Optimize the Indexes");
            writer.optimize();
        } catch (IOException e) {
            System.out.println("Optimization Failed:  " + e.toString());
        }
    }
}