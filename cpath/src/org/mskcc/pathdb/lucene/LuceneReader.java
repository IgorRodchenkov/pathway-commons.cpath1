package org.mskcc.pathdb.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.mskcc.pathdb.sql.query.QueryException;

import java.io.IOException;

/**
 * Reads from the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneReader {
    /**
     * Index Search Object (for querying Lucene).
     */
    private IndexSearcher reader;

    /**
     * Constructor.
     *
     * @throws IOException Error Reading Lucene Index Files.
     */
    public LuceneReader() throws IOException {
        String dir = LuceneConfig.getLuceneDirectory();
        reader = new IndexSearcher(dir);
    }

    /**
     * Closes the Index Searcher.
     *
     * @throws IOException Input Output Exception.
     */
    protected void finalize() throws Throwable {
        if (reader != null) {
            reader.close();
        }
    }

    /**
     * Executes Search Query
     *
     * @param term Search Term.
     * @return Lucene Hits Object
     * @throws QueryException Error Processing Query. I
     */
    public Hits executeQuery(String term) throws QueryException {
        try {
            Analyzer analyzer = LuceneConfig.getLuceneAnalyzer();
            Query query = QueryParser.parse(term, LuceneConfig.FIELD_ALL,
                    analyzer);
            Hits hits = reader.search(query);
            return hits;
        } catch (IOException e) {
            e.printStackTrace();
            throw new QueryException("IOException:  " + e.getMessage(), e);
        } catch (ParseException e) {
            throw new QueryException("ParseException:  " + e.getMessage(), e);
        }
    }
}