package org.mskcc.pathdb.tool;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;

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
            XDebug xdebug = new XDebug();
            xdebug.startTimer();
            StringBuffer terms = new StringBuffer();
            for (int i = 0; i < args.length; i++) {
                terms.append(args[i] + " ");
            }
            System.out.println("Using search terms:  " + terms.toString());
            queryFullText(terms.toString());
            xdebug.stopTimer();
            System.out.println("Total Time for Query:  "
                    + xdebug.getTimeElapsed() + " ms");
        } else {
            System.out.println("Command line usage:  admin.pl ft_query"
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
            Hits hits = lucene.executeQuery(terms);
            displayHits(hits);
        } catch (QueryException e) {
            System.out.println("\n!!!!  An Error Has Occurred!");
            System.out.println("-->  " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n!!!!  An Error Has Occurred!");
            System.out.println("-->  " + e.getMessage());
        }
    }

    /**
     * Display all Results.
     */
    private static void displayHits(Hits hits) throws IOException {
        if (hits.length() == 0) {
            System.out.println("No matching records found.");
        } else {
            System.out.println("Showing Top Hit Only:");
            Document doc = hits.doc(0);
            Field cpathId = doc.getField(LuceneIndexer.FIELD_CPATH_ID);
            if (cpathId != null) {
                System.out.println("cPath ID:  " + cpathId.stringValue());
            }
            Field xml = doc.getField(LuceneIndexer.FIELD_ALL);
            if (xml != null) {
                System.out.println("Data record:  \n" + xml.stringValue());
            }
        }
    }
}
