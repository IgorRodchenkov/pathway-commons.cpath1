package org.mskcc.pathdb.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.transfer.ImportException;

import java.io.IOException;

/**
 * Provides access to the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneIndexer {
    /**
     * Default Lucene Field for Storing All Terms.
     */
    public static final String FIELD_ALL = "all";

    /**
     * Lucene Field for Storing Intractor CPath ID.
     */
    public static final String FIELD_INTERACTOR_ID = "interactor_id";

    /**
     * Lucene Field for Storing Interaction CPath ID.
     */
    public static final String FIELD_INTERACTION_ID = "interaction_id";

    /**
     * Text Index Directory.
     */
    public static final String INDEX_DIR_PREFIX = "textIndex";

    /**
     * Lucene Directory Property
     */
    public static final String PROPERTY_LUCENE_DIR = "lucene.dir";

    private IndexWriter indexWriter = null;
    private IndexSearcher indexSearcher = null;

    /**
     * Initializes Index with Fresh Database.
     *
     * @throws IOException InputOutput Exception.
     */
    public void initIndex() throws IOException {
        String dir = this.getDirectory();
        Analyzer analyzer = this.getAnalyzer();
        IndexWriter writer = new IndexWriter(dir, analyzer, true);
        writer.close();
    }

    /**
     * Adds New Record to Full Text Indexer.
     *
     * @param item ItemToIndex.
     * @throws ImportException Error Importing Record to Full Text Engine.
     */
    public void addRecord(ItemToIndex item)
            throws ImportException {
        try {
            String dir = this.getDirectory();
            Analyzer analyzer = this.getAnalyzer();
            IndexWriter writer = new IndexWriter(dir, analyzer, false);

            Document document = new Document();
            int numFields = item.getNumFields();
            for (int i = 0; i < numFields; i++) {
                Field field = item.getField(i);
                document.add(field);
            }
            writer.addDocument(document);
            writer.close();
        } catch (IOException e) {
            throw new ImportException("IOException:  " + e.getMessage());
        }
    }

    /**
     * Initializes Index Writer.
     *
     * @throws IOException Input Output Exception.
     */
    public void initIndexWriter() throws IOException {
        String dir = this.getDirectory();
        Analyzer analyzer = this.getAnalyzer();
        indexWriter = new IndexWriter(dir, analyzer, false);
    }

    /**
     * Closes the Index Writer.
     *
     * @throws IOException Input Output Exception.
     */
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    /**
     * Closes the Index Searcher.
     *
     * @throws IOException Input Output Exception.
     */
    public void closeIndexSearcher() throws IOException {
        if (indexSearcher != null) {
            indexSearcher.close();
        }
    }

    /**
     * Executes Query
     *
     * @param term Search Term.
     * @return Lucene Hits Object
     * @throws QueryException Error Processing Query. I
     */
    public Hits executeQuery(String term) throws QueryException {
        indexSearcher = null;
        try {
            String dir = this.getDirectory();
            indexSearcher = new IndexSearcher(dir);
            Analyzer analyzer = this.getAnalyzer();
            Query query = QueryParser.parse(term, FIELD_ALL, analyzer);
            Hits hits = indexSearcher.search(query);
            return hits;
        } catch (IOException e) {
            e.printStackTrace();
            throw new QueryException("IOException:  " + e.getMessage(), e);
        } catch (ParseException e) {
            throw new QueryException("ParseException:  " + e.getMessage(), e);
        }
    }

    /**
     * Gets Directory for Full Text Indexer.
     *
     * @return Directory Location.
     */
    public String getDirectory() {
        PropertyManager manager = PropertyManager.getInstance();
        String dir = manager.getProperty(PROPERTY_LUCENE_DIR);
        //  dir should only be null when run from the command line.
        if (dir == null) {
            String cPathHome = System.getProperty("CPATH_HOME");
            dir = cPathHome + "/build/WEB-INF/" + INDEX_DIR_PREFIX;
        }
        return dir;
    }

    /**
     * Gets Analyzer.
     * Index and Query must use the same Analyzer.
     *
     * @return Analyzer Object.
     */
    private Analyzer getAnalyzer() {
        return new StandardAnalyzer();
    }
}