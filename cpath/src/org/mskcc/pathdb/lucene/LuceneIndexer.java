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
import org.jdom.JDOMException;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.util.XmlStripper;

import java.io.File;
import java.io.IOException;

/**
 * Provides access to the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneIndexer {
    /**
     * Lucene Field for Storing XML Content.
     */
    public static final String FIELD_XML = "xml";

    /**
     *  Lucene Field for Storing Entity Name.
     */
    public static final String FIELD_NAME = "name";

    /**
     *  Lucene Field for Storing Entity Description.
     */
    public static final String FIELD_DESCRIPTION = "description";

    /**
     * Lucene Field for Storing CPath ID.
     */
    public static final String FIELD_CPATH_ID = "cpath_id";

    private static IndexWriter indexWriter = null;
    private static IndexSearcher indexSearcher = null;

    /**
     * Initializes Index with Fresh Database.
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
     * @param name Entity Name.
     * @param description Entity Description.
     * @param xml XML String.
     * @param cpathId CPath ID.
     * @throws ImportException Error Importing Record to Full Text Engine.
     */
    public void addRecord(String name, String description, String xml,
            long cpathId) throws ImportException {
        try {
            String dir = this.getDirectory();
            Analyzer analyzer = this.getAnalyzer();
            IndexWriter writer = new IndexWriter(dir, analyzer, false);
            XmlStripper stripper = new XmlStripper();
            String terms = stripper.stripTags(xml);
            Document document = new Document();

            //  XML Terms are indexed, but not stored.
            document.add(Field.Text(FIELD_XML, terms));

            //  Name, Description and ID are stored, but not indexed.
            document.add(Field.UnIndexed(FIELD_NAME, name));
            document.add(Field.UnIndexed(FIELD_DESCRIPTION, description));
            document.add(Field.UnIndexed(FIELD_CPATH_ID,
                    Long.toString(cpathId)));

            writer.addDocument(document);
            writer.close();
        } catch (IOException e) {
            throw new ImportException("IOException:  " + e.getMessage());
        } catch (JDOMException e) {
            throw new ImportException("JDOMException:  " + e.getMessage());
        }
    }

    /**
     * Initializes Index Writer.
     * @throws IOException Input Output Exception.
     */
    public void initIndexWriter() throws IOException {
        String dir = this.getDirectory();
        Analyzer analyzer = this.getAnalyzer();
        indexWriter = new IndexWriter(dir, analyzer, false);
    }

    /**
     * Closes the Index Writer.
     * @throws IOException Input Output Exception.
     */
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    /**
     * Closes the Index Searcher.
     * @throws IOException Input Output Exception.
     */
    public void closeIndexSearcher() throws IOException {
        if (indexSearcher != null) {
            indexSearcher.close();
        }
    }

    /**
     * Adds New Record to Full Text Indexer.
     * @param text Text To Index
     * @throws ImportException Error Importing Record to Full Text Engine.
     */
    public void addRecord(String text) throws ImportException {
        try {
            Document document = new Document();
            document.add(Field.Text(FIELD_XML, text));
            indexWriter.addDocument(document);
        } catch (IOException e) {
            throw new ImportException("IOException:  " + e.getMessage());
        }
    }

    /**
     * Executes Query
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
            Query query = QueryParser.parse(term, FIELD_XML, analyzer);
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
     * @return Directory Location.
     */
    public String getDirectory() {
        String textIndexDir = "textIndex";
        File dir = new File(System.getProperty("user.dir"), textIndexDir);
        if (!dir.isDirectory()) {
            File parentDir = dir.getParentFile().getParentFile();
            dir = new File(parentDir, textIndexDir);
        }
        return dir.toString();
    }

    /**
     * Gets Analyzer.
     * Index and Query must use the same Analyzer.
     * @return Analyzer Object.
     */
    private Analyzer getAnalyzer() {
        return new StandardAnalyzer();
    }
}