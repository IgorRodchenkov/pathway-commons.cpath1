package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Command Line Tool to Fully Initialize/Load the Lucene Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LoadFullText {
    private static boolean verbose;

    /**
     * Constructor.
     * @param verbose Verbose Flag.
     */
    public LoadFullText(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        try {
            XDebug xdebug = new XDebug();
            xdebug.startTimer();
            LoadFullText indexer = new LoadFullText(true);
            if (args.length > 0) {
                indexer.indexText(args[0]);
            } else {
                indexer.indexAllPhysicalEntities();
            }
            xdebug.stopTimer();
            outputMsg("\nTotal Time for Indexing:  "
                    + xdebug.getTimeElapsed() + " ms");
        } catch (Exception e) {
            System.out.println("\n!!!!  Indexing aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        }
    }

    /**
     * Run Full Text Indexing on all Physical Entities.
     * @throws DaoException Data Accession Exception.
     * @throws IOException Input Output Exception.
     * @throws ImportException Import Exception.
     */
    public void indexAllPhysicalEntities() throws DaoException, IOException,
            ImportException {
        outputMsg("Loading Full Text Indexer");
        outputMsg("Processing all cPath records:  ");
        DaoCPath dao = new DaoCPath();
        LuceneIndexer lucene = new LuceneIndexer();
        lucene.initIndex();
        ArrayList records = dao.getAllRecords();
        for (int i = 0; i < records.size(); i++) {
            outputMsg(".");
            CPathRecord record = (CPathRecord) records.get(i);
            if (record.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                lucene.addRecord(record.getName(), record.getDescription(),
                        record.getXmlContent(), record.getId());
            }
        }
        outputMsg("\nIndexing complete");
    }

    /**
     * Indexes Text.
     * @param fileName File Name.
     * @throws IOException Input Output Exception.
     * @throws ImportException Error performing Import.
     */
    public void indexText(String fileName) throws IOException,
            ImportException {
        outputMsg("Indexing File:  " + fileName);
        long numRecords = 0;
        LuceneIndexer lucene = new LuceneIndexer();
        lucene.initIndex();
        lucene.initIndexWriter();
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        StringBuffer record = new StringBuffer();
        while (line != null) {
            if (line.startsWith("ID")) {
                record = new StringBuffer(line + "\n");
                line = bufferedReader.readLine();
                while (line != null && !line.startsWith("ID")) {
                    record.append(line + "\n");
                    line = bufferedReader.readLine();
                }
                numRecords++;
                if (numRecords % 100 == 0) {
                    outputMsg ("\nNumber Indexed so far:  "
                                + numRecords);
                }
                indexRecord(lucene, record.toString());
            }
        }
        lucene.closeIndexWriter();
        outputMsg ("Total Number of Records Indexed:  "
                    + numRecords);
    }

    private void indexRecord(LuceneIndexer lucene, String record)
            throws ImportException {
        outputMsg(".");
        lucene.addRecord(record);
    }

    /**
     * Conditionally Output Message to System.out.
     * @param msg User Message.
     */
    public static void outputMsg(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }
}