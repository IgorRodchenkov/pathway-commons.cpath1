package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.task.IndexLuceneTask;

/**
 * Command Line Tool to Run the Lucene Text Indexer on all Records in CPath.
 *
 * @author Ethan Cerami
 */
public class RunIndexer {

    /**
     * Main Method.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        IndexLuceneTask indexer = new IndexLuceneTask(true);
        indexer.start();
    }
}