package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying cPath Diagnostics.
 *
 * @author Ethan Cerami
 */
public class DiagnosticsTable extends HtmlTable {
    private ArrayList testList;

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        String headers[] = {"Result", "DiagnosticTestResults", "Error"};
        createHeader("cPath Diagnostics");
        startTable();
        createTableHeaders(headers);
        testList = new ArrayList();
        runTests();
        outputTests();
        endTable();
    }

    private void runTests() throws IOException {
        runFirstBatch();
        runSecondBatch();
        runThirdBatch();
    }

    private void runFirstBatch() {
        DiagnosticTestResults test = new DiagnosticTestResults
                ("Testing access to Table:  cpath");
        DaoCPath cpath = new DaoCPath();
        try {
            cpath.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new DiagnosticTestResults
                ("Testing access to Table:  external_db");
        DaoExternalDb externalDb = new DaoExternalDb();
        try {
            externalDb.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new DiagnosticTestResults
                ("Testing access to Table:  external_db_cv");
        DaoExternalDbCv externalDbCv = new DaoExternalDbCv();
        try {
            externalDbCv.getTermByDbCvId(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new DiagnosticTestResults
                ("Testing access to Table:  external_link");
        DaoExternalLink externalLink = new DaoExternalLink();
        try {
            externalLink.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);
    }

    private void runSecondBatch() {
        DiagnosticTestResults test = new DiagnosticTestResults
                ("Testing access to Table:  import");
        DaoImport daoImport = new DaoImport();
        try {
            daoImport.getRecordById(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new DiagnosticTestResults
                ("Testing access to Table:  internal_link");
        DaoInternalLink internalLink = new DaoInternalLink();
        try {
            internalLink.getInternalLinks(12345);
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new DiagnosticTestResults("Testing access to Table:  log");
        DaoLog logger = new DaoLog();
        try {
            logger.getLogRecords();
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new DiagnosticTestResults("Testing access to Table:  xml_cache");
        DaoXmlCache cache = new DaoXmlCache(new XDebug());
        try {
            cache.getXmlAssemblyByKey("12345");
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);

        test = new DiagnosticTestResults("Testing access to Table:  organism");
        DaoOrganism organism = new DaoOrganism();
        try {
            organism.getAllOrganisms();
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);
    }

    private void runThirdBatch() throws IOException {
        LuceneIndexer indexer = new LuceneIndexer();
        DiagnosticTestResults test = new DiagnosticTestResults
                ("Testing access to Lucene Full Text Index<BR>"
                +"Lucene Directory:  " + indexer.getDirectory());
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
        for (int i = 0; i < testList.size(); i++) {
            this.startRow(i);
            DiagnosticTestResults test = (DiagnosticTestResults)
                    testList.get(i);
            Exception e = test.getException();
            if (e == null) {
                outputDataField("<IMG SRC='jsp/images/icon_success_sml.gif'>");
            } else {
                outputDataField("<IMG SRC='jsp/images/icon_error_sml.gif'>");
            }
            outputDataField(test.getName());
            if (e != null) {
                outputDataField(e.getMessage());
            } else {
                outputDataField("");
            }
            this.endRow();
        }
    }
}

/**
 * Encapsulates DiagnosticTestResults Results.
 *
 * @author Ethan Cerami.
 */
class DiagnosticTestResults {
    private String name;
    private Exception e;

    public DiagnosticTestResults(String name) {
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