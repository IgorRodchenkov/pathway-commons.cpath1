package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.lucene.CPathResult;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.query.QueryException;

import java.util.ArrayList;

/**
 * Command Line Tool for Querying Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class QueryFullText {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            StringBuffer terms = new StringBuffer();
            for (int i = 0; i < args.length; i++) {
                terms.append(args[i] + " ");
            }
            System.out.println("Using search terms:  " + terms.toString());
            queryFullText(terms.toString());
        } else {
            System.out.println("Command line usage:  queryText.sh"
                    + " search_terms");
        }
    }

    /**
     * Perform Query.
     * @param terms Search Terms.
     */
    private static void queryFullText(String terms) {
        try {
            LuceneIndexer lucene = new LuceneIndexer();
            ArrayList records = lucene.executeQueryWithLookUp(terms);
            displayRecords(records);
        } catch (QueryException e) {
            System.out.println("\n!!!!  An Error Has Occurred!");
            System.out.println("-->  " + e.getMessage());
        }
    }

    /**
     * Display all Results.
     * @param records ArrayList of CPathRecord Objects.
     */
    private static void displayRecords(ArrayList records) {
        if (records.size() == 0) {
            System.out.println("No matching records found.");
        }
        for (int i = 0; i < records.size(); i++) {
            CPathResult result = (CPathResult) records.get(i);
            CPathRecord record = result.getRecord();
            System.out.print(i + ". [" + result.getScore() * 100.0 + "%] ");
            System.out.println(record.getName());
            System.out.println("... Description:  " + record.getDescription());
        }
    }
}
