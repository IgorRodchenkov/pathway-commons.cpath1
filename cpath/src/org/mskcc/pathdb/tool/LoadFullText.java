package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.task.IndexLuceneTask;

/**
 * Command Line Tool to Fully Initialize/Load the Lucene Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LoadFullText {

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