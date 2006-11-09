// $Id: IndexLuceneTask.java,v 1.50 2006-11-09 18:38:00 cerami Exp $
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
package org.mskcc.pathdb.task;

import net.sf.ehcache.CacheException;
import org.apache.lucene.store.Directory;
import org.mskcc.pathdb.lucene.LuceneWriter;
import org.mskcc.pathdb.lucene.OrganismStats;
import org.mskcc.pathdb.lucene.RecordIndexerManager;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.sql.transfer.PopulateInternalFamilyLookUpTable;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Runs the Lucene Text Indexer on all Records in CPath.
 *
 * @author Ethan Cerami.
 */
public class IndexLuceneTask extends Task {
    private static final int BLOCK_SIZE = 100;
    private XDebug xdebug;

    /**
     * Constructor.
     *
     * @param consoleMode Running in Console Mode.
     * @param xdebug      XDebug Object.
     */
    public IndexLuceneTask(boolean consoleMode, XDebug xdebug) {
        super("Run Full Text Indexer", consoleMode);
        this.xdebug = xdebug;
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Running Full Text Indexer");
    }

    /**
     * Runs the Task.
     */
    public void run() {
        try {
            executeTask();
        } catch (Exception e) {
            setThrowable(e);
        }
    }

    /**
     * Execute Index Task.
     *
     * @throws DaoException      Data Access Error.
     * @throws ImportException   Import Error.
     * @throws IOException       File IO Error.
     * @throws AssemblyException XML Assembly Error.
     * @throws QueryException    Data Query Error.
     * @throws CacheException    Cache Error.
     */
    public void executeTask() throws DaoException, ImportException,
            IOException, AssemblyException, QueryException, CacheException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Clearing XML Cache");
        DaoXmlCache dao = new DaoXmlCache(xdebug);
        dao.deleteAllRecords();
        XDebug xdebug = new XDebug();
        xdebug.startTimer();
        indexAllInteractions();

        OrganismStats orgStats = new OrganismStats();
        pMonitor.setCurrentMessage("Indexing Complete");
        xdebug.stopTimer();
    }

    /**
     * Run Full Text Indexing on all Interaction Records.
     *
     * @throws DaoException      Data Access Exception.
     * @throws IOException       Input/Output Exception.
     * @throws ImportException   Import Exception.
     * @throws AssemblyException Assembly Exception.
     */
    public void indexAllInteractions() throws DaoException, IOException,
            ImportException, AssemblyException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        DaoCPath cpath = DaoCPath.getInstance();
        int numPathways = cpath.getNumEntities(CPathRecordType.PATHWAY);
        int numInteractions = cpath
                .getNumEntities(CPathRecordType.INTERACTION);
        int numPhysicalEntities = cpath.getNumEntities
                (CPathRecordType.PHYSICAL_ENTITY);
        int totalNumEntities = numPathways + numInteractions
                + numPhysicalEntities;

        pMonitor.setCurrentMessage("Indexing all cPath Records");
        pMonitor.setCurrentMessage("Total Number of Pathways:  "
                + numPathways);
        pMonitor.setCurrentMessage("Total Number of Interactions:  "
                + numInteractions);
        pMonitor.setCurrentMessage("Total Number of Physical Entities:  "
                + numPhysicalEntities);
        pMonitor.setMaxValue(totalNumEntities);

        //Divide the indexing job up into sections.
        int iterationSize = 20000; //chose 20000 because there are 4 threads and
        //each thread can have a max of 5000 in the queue.
        long maxIterId = cpath.getMaxCpathID();


        for (int id = 0; id <= maxIterId; id = id + iterationSize + 1) {

            //First time through initialize the index writer to create directories
            //and delete any existing indexes.
            //subsequent indexes just get added to the initial index file.
            LuceneWriter indexWriter;
            if (id == 0) {
                indexWriter = new LuceneWriter(true);
            } else {
                indexWriter = new LuceneWriter(false);
            }
            // setup the threaded indexing
            RecordIndexerManager.setIndexerCount(4); // configure the number
            // of threads
            RecordIndexerManager indexManager = new RecordIndexerManager();
            indexManager.init(pMonitor, true);

            // fire off the indexing threads
            indexManager.startIndexing();

            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            CPathRecord record = null;
            // keep track of how many records we have indexed
            long indexedRecordCount = 0;
            long returnLong = -1;

            try {
                long startId = id;
                long endId = id + BLOCK_SIZE;
                long maxId = id + iterationSize;


                for (; startId <= maxId; startId = endId + 1,
                        endId = startId + BLOCK_SIZE) {

                    con = JdbcUtil.getCPathConnection();

                    pstmt = con.prepareStatement
                            ("select * from cpath WHERE "
                                    + " CPATH_ID BETWEEN " + startId + " and " + endId
                                    + " order by CPATH_ID ");
                    rs = pstmt.executeQuery();

                    try {
                        while (rs.next()) {
                            record = cpath.extractRecord(rs);
                            indexManager.pushRecord(record);
                            indexedRecordCount++;
                        }
                    } catch (Exception e1) {
                        throw new DaoException(e1);
                    }
                    JdbcUtil.closeAll(con, pstmt, rs);
                }

                // wind down the separate indexer threads
                indexManager.signalFinish();
                indexManager.waitForThreads();
                indexManager.closeAll();

                if (indexedRecordCount > 0) {
                    // tell the indexers to wind down
                    pMonitor.setCurrentMessage("\nOptimizing Indexes");
                    // merge the indexes, this also optimises
                    Directory[] dirs = indexManager.getLuceneDirs();
                    indexWriter.addIndexes(dirs);
                    indexWriter.closeWriter();

                    if (record != null) {
                        returnLong = record.getId();
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new DaoException(e);
            } catch (SQLException e) {
                throw new DaoException(e);
            } finally {
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        }
    }
}
