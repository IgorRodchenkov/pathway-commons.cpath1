package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.ImportException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Command Line Tool to Fully Initialize/Load the Lucene Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LoadFullText {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        try {
            LoadFullText indexer = new LoadFullText();
            indexer.index();
        } catch (Exception e) {
            System.out.println("\n!!!!  Transfer aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        }
    }

    /**
     * Run Full Text Indexing.
     * @throws DaoException Data Accession Exception.
     * @throws IOException Input Output Exception.
     * @throws ImportException Import Exception.
     */
    public void index() throws DaoException, IOException, ImportException {
        System.out.println("Loading Full Text Indexer");
        System.out.print("Processing all cPath records:  ");
        DaoCPath dao = new DaoCPath();
        LuceneIndexer lucene = new LuceneIndexer();
        lucene.initIndex();
        ArrayList records = dao.getAllRecords();
        for (int i = 0; i < records.size(); i++) {
            System.out.print(".");
            CPathRecord record = (CPathRecord) records.get(i);
            if (record.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                lucene.addRecord(record.getXmlContent(), record.getId());
            }
        }
        System.out.println("\nIndexing complete");
    }
}