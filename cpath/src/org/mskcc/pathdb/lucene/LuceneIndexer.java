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
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.util.XmlStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides access to the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneIndexer {
    /**
     * XML Field Name.
     */
    public static final String FIELD_NAME_XML = "xml";

    /**
     * CPath Field Name.
     */
    public static final String FIELD_NAME_CPATH_ID = "cpath_id";

    private static IndexWriter indexWriter = null;

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
     * @param xml XML String.
     * @param cpathId CPath ID.
     * @throws ImportException Error Importing Record to Full Text Engine.
     */
    public void addRecord(String xml, long cpathId)
            throws ImportException {
        try {
            String dir = this.getDirectory();
            Analyzer analyzer = this.getAnalyzer();
            IndexWriter writer = new IndexWriter(dir, analyzer, false);
            XmlStripper stripper = new XmlStripper();
            String terms = stripper.stripTags(xml);
            Document document = new Document();
            document.add(Field.Text(FIELD_NAME_XML, terms));
            document.add(Field.Keyword(FIELD_NAME_CPATH_ID,
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
        indexWriter.close();
    }

    /**
     * Adds New Record to Full Text Indexer.
     * @param text Text To Index
     * @throws ImportException Error Importing Record to Full Text Engine.
     */
    public void addRecord(String text) throws ImportException {
        try {
            Document document = new Document();
            document.add(Field.Text(FIELD_NAME_XML, text));
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
        IndexSearcher searcher = null;
        try {
            String dir = this.getDirectory();
            searcher = new IndexSearcher(dir);
            Analyzer analyzer = this.getAnalyzer();
            Query query = QueryParser.parse(term, FIELD_NAME_XML, analyzer);
            Hits hits = searcher.search(query);
            return hits;
        } catch (IOException e) {
            throw new QueryException("IOException:  " + e.getMessage(), e);
        } catch (ParseException e) {
            throw new QueryException("ParseException:  " + e.getMessage(), e);
        }
    }

    /**
     * Executes Query
     * @param term Search Term.
     * @return Lucene Hits Object
     * @throws QueryException Error Processing Query. I
     */
    private Hits executeQuery(IndexSearcher searcher, String term)
            throws QueryException {
        try {
            Analyzer analyzer = this.getAnalyzer();
            Query query = QueryParser.parse(term, FIELD_NAME_XML, analyzer);
            Hits hits = searcher.search(query);
            return hits;
        } catch (IOException e) {
            throw new QueryException("IOException:  " + e.getMessage(), e);
        } catch (ParseException e) {
            throw new QueryException("ParseException:  " + e.getMessage(), e);
        }
    }

    /**
     * Executes Query with CPath LookUp.
     * @param term Search Term.
     * @return ArrayList of CPathResult Records.
     * @throws QueryException Error Processing Query. I
     */
    public ArrayList executeQueryWithLookUp(String term)
            throws QueryException {
        IndexSearcher searcher = null;
        ArrayList records = new ArrayList();
        try {
            String dir = this.getDirectory();
            searcher = new IndexSearcher(dir);
            Hits hits = this.executeQuery(searcher, term);
            for (int i = 0; i < hits.length(); i++) {
                Document doc = hits.doc(i);
                float score = hits.score(i);
                Field idField = doc.getField(LuceneIndexer.FIELD_NAME_CPATH_ID);
                addCPathRecord(idField, score, records);
            }
        } catch (IOException e) {
            throw new QueryException("IOException:  " + e.getMessage(), e);
        } catch (DaoException e) {
            throw new QueryException("DaoException:  " + e.getMessage(), e);
        } finally {
            try {
                if (searcher != null) {
                    searcher.close();
                }
            } catch (IOException e) {
                throw new QueryException("IOException:  " + e.getMessage(), e);
            }
        }
        return records;
    }

    /**
     * Adds CPath Record to ArrayList.
     */
    private void addCPathRecord(Field idField, float score, ArrayList records)
            throws DaoException {
        DaoCPath dao = new DaoCPath();
        if (idField != null) {
            String idStr = idField.stringValue();
            long cpathId = Integer.parseInt(idStr);
            CPathRecord record = dao.getRecordById(cpathId);
            if (record != null) {
                CPathResult result = new CPathResult();
                result.setRecord(record);
                result.setScore(score);
                records.add(result);
            }
        }
    }

    /**
     * Gets Directory for Full Text Indexer.
     * @return Directory Location.
     */
    private String getDirectory() {
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
