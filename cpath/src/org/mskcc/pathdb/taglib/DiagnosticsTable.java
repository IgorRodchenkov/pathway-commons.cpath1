package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.lucene.LuceneIndexer;

import java.util.*;
import java.io.IOException;

/**
 * Custom JSP Tag for Displaying cPath Diagnostics.
 *
 * @author Ethan Cerami
 */
public class DiagnosticsTable extends HtmlTable {
    private ArrayList testList;


    /**
     * Executes JSP Custom Tag
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        String headers[] = {"Result", "Test", "Error"};
        createHeader("cPath Diagnostics");
        startTable();
        createTableHeaders(headers);
        testList = new ArrayList();
        runTests();
        outputTests();
        endTable();
    }

    private void runTests () throws IOException {
        runFirstBatch();
        runSecondBatch();
        runThirdBatch();
    }

    private void runFirstBatch() {
        Test test = new Test("Testing access to Table:  CPATH");
        DaoCPath cpath = new DaoCPath();
        try {
            cpath.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new Test("Testing access to Table:  EXTERNAL_DB");
        DaoExternalDb externalDb = new DaoExternalDb();
        try {
            externalDb.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new Test("Testing access to Table:  EXTERNAL_DB_CV");
        DaoExternalDbCv externalDbCv = new DaoExternalDbCv();
        try {
            externalDbCv.getTermByDbCvId(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new Test("Testing access to Table:  EXTERNAL_LINK");
        DaoExternalLink externalLink = new DaoExternalLink();
        try {
            externalLink.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);
    }

    private void runSecondBatch() {
        Test test = new Test("Testing access to Table:  IMPORT");
        DaoImport daoImport = new DaoImport();
        try {
            daoImport.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new Test ("Testing access to Table:  INTERNAL_LINK");
        DaoInternalLink internalLink = new DaoInternalLink();
        try {
            internalLink.getInternalLinks(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new Test ("Testing access to Table:  LOG");
        DaoLog logger = new DaoLog();
        try {
            logger.getLogRecords();
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new Test ("Testing access to Table:  XML_CACHE");
        DaoXmlCache cache = new DaoXmlCache ();
        try {
            cache.getXmlByKey("12345");
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);
    }

    private void  runThirdBatch () throws IOException {
        Test test = new Test ("Testing access to Lucene Full Text Index");
        LuceneIndexer indexer = new LuceneIndexer();
        try {
            indexer.executeQuery("dna");
        } catch (QueryException e) {
            test.setException(e);
        } finally {
            indexer.closeIndexSearcher();
        }
        testList.add(test);
    }

    /**
     * Output Diagnostic Tests.
     */
    private void outputTests() {
        for (int i=0; i<testList.size(); i++) {
            this.startRow(i);
            Test test = (Test) testList.get(i);
            Exception e = test.getException();
            if (e == null) {
                outputDataField("<IMG SRC='jsp/images/icon_success_sml.gif'>");
            } else {
                outputDataField("<IMG SRC='jsp/images/icon_error_sml.gif'>");
            }
            outputDataField(test.getName());
            if (e!=null) {
                outputDataField (e.getMessage());
            } else {
                outputDataField("");
            }
            this.endRow();
        }
    }
}

class Test {
    private String name;
    private Exception e;

    public Test (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exception getException() {
        return e;
    }

    public void setException(Exception e) {
        this.e = e;
    }
}