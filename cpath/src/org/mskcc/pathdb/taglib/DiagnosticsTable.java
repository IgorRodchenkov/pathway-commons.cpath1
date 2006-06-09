// $Id: DiagnosticsTable.java,v 1.23 2006-06-09 19:22:03 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.taglib;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.lucene.LuceneReader;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.cache.EhCache;
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
        PropertyManager pManager = PropertyManager.getInstance();
        append("<TABLE><TR><TD>Using Database Host:  "
                + pManager.getProperty(PropertyManager.DB_LOCATION));
        append("<P>Using Database Name:  "
                + pManager.getProperty(CPathConstants.PROPERTY_MYSQL_DATABASE));
        append("</TD></TR></TABLE>");
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
        runFourthBatch();
    }

    private void runFirstBatch() {
        DiagnosticTestResults test = new DiagnosticTestResults
                ("Testing access to Table:  cpath");
        try {
            DaoCPath cpath = DaoCPath.getInstance();
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
        try {
            DaoExternalLink externalLink = DaoExternalLink.getInstance();
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
            internalLink.getTargets(12345);
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

        test = new DiagnosticTestResults("Testing access to Table:  "
                + "id_generator");
        DaoIdGenerator idGenerator = new DaoIdGenerator();
        try {
            idGenerator.getNextId();
        } catch (DaoException e) {
            test.setException(e);
        }
        testList.add(test);
    }

    private void runThirdBatch() {
        LuceneReader indexer = new LuceneReader();
        try {
            DiagnosticTestResults test = new DiagnosticTestResults
                    ("Testing access to Lucene Full Text Index<BR>"
                            + "Lucene Directory:  "
                            + LuceneConfig.getLuceneDirectory());
            try {
                indexer.executeQuery("dna");
            } catch (QueryException e) {
                test.setException(e);
            }
            testList.add(test);
        } finally {
            //  Make sure to always close the IndexReader.
            indexer.close();
        }
    }

    private void runFourthBatch() {
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
        DiagnosticTestResults test = new DiagnosticTestResults
                ("Testing access to cache disk store");
        try {
            Element e = new Element("web_diagnostics", "value1");
            cache.put(e);
            cache.flush();
        } catch (Throwable t) {
            test.setException(t);
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
            Throwable e = test.getException();
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
    private Throwable e;

    public DiagnosticTestResults(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Throwable getException() {
        return e;
    }

    public void setException(Throwable e) {
        this.e = e;
    }
}
