package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
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
            XDebug xdebug = new XDebug();
            xdebug.startTimer();
            LoadFullText indexer = new LoadFullText();
            if (args.length > 0) {
                indexer.indexText(args[0]);
            } else {
                indexer.index();
            }
            xdebug.stopTimer();
            System.out.println("\nTotal Time for Indexing:  " +
                    xdebug.getTimeElapsed() + " ms");
        } catch (Exception e) {
            System.out.println("\n!!!!  Indexing aborted due to error!");
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

    public void indexText (String fileName) throws IOException,
            ImportException {
        System.out.println("Indexing File:  " + fileName);
        long numRecords = 0;
        LuceneIndexer lucene = new LuceneIndexer();
        lucene.initIndex();
        lucene.initIndexWriter();
        FileReader fileReader = new FileReader (fileName);
        BufferedReader bufferedReader = new BufferedReader (fileReader);
        String line = bufferedReader.readLine();
        StringBuffer record = new StringBuffer();
        while (line != null) {
            if (line.startsWith("ID")) {
                record = new StringBuffer (line+"\n");
                line = bufferedReader.readLine();
                while (line != null && ! line.startsWith("ID")) {
                    record.append(line+"\n");
                    line = bufferedReader.readLine();
                }
                numRecords++;
                if (numRecords % 100 == 0) {
                    System.out.println("\nNumber Indexed so far:  "
                            + numRecords);
                }
                indexRecord (lucene, record.toString());
            }
        }
        lucene.closeIndexWriter();
        System.out.println("Total Number of Records Indexed:  "
                + numRecords);
    }

    private void indexRecord (LuceneIndexer lucene, String record)
            throws ImportException {
        System.out.print(".");
        lucene.addRecord(record);
    }
}