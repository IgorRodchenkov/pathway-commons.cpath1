/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.lucene.IndexFactory;
import org.mskcc.pathdb.lucene.ItemToIndex;
import org.mskcc.pathdb.lucene.LuceneWriter;
import org.mskcc.pathdb.lucene.OrganismStats;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.util.ConsoleUtil;
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
    private XDebug xdebug;

    /**
     * Constructor.
     *
     * @param verbose Verbose flag.
     * @param xdebug  XDebug Object.
     */
    public IndexLuceneTask(boolean verbose, XDebug xdebug) {
        super("Run Full Text Indexer");
        this.setVerbose(verbose);
        pMonitor = new ProgressMonitor();
        pMonitor.setCurrentMessage("Running");
    }

    /**
     * Runs the Task.
     */
    public void run() {
        try {
            outputMsg("Clearing XML Cache");
            pMonitor.setCurrentMessage("Indexing Records");
            DaoXmlCache dao = new DaoXmlCache(xdebug);
            dao.deleteAllRecords();
            XDebug xdebug = new XDebug();
            xdebug.startTimer();
            indexAllInteractions();

            OrganismStats orgStats = new OrganismStats();
            orgStats.resetStats();
            xdebug.stopTimer();

            pMonitor.setCurrentMessage("Indexing Complete -->  Number of "
                    + "Entities Indexed:  " + pMonitor.getCurValue());
            if (verbose) {
                System.out.println("Total Time Elapsed:  "
                        + xdebug.getTimeElapsed());
            }
        } catch (Exception e) {
            setException(e);
            System.err.println("**** ERROR:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the Progress Monitor.
     *
     * @return Progress Monitor object.
     */
    public ProgressMonitor getProgressMonitor() {
        return pMonitor;
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
        outputMsg("Indexing all cPath Interactions");
        DaoCPath cpath = new DaoCPath();
        pMonitor.setMaxValue(cpath.getNumEntities(CPathRecordType.INTERACTION));

        LuceneWriter indexWriter = new LuceneWriter(true);
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from cpath WHERE TYPE = ?  order by CPATH_ID ");
            pstmt.setString(1, CPathRecordType.INTERACTION.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                indexRecord(cpath, rs, indexWriter);
            }
            outputMsg("\nOptimizing Indexes");
            indexWriter.optimize();
            indexWriter.closeWriter();
            outputMsg("\nIndexing Complete");
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Indexes One cPathRecord in Lucene.
     *
     * @param cpath       cPathRecord.
     * @param rs          ResultSet
     * @param indexWriter Index Writer.
     * @throws SQLException      Database Error
     * @throws IOException       I/O Error
     * @throws ImportException   Import Error
     * @throws AssemblyException Assembly Error
     */
    private void indexRecord(DaoCPath cpath, ResultSet rs,
            LuceneWriter indexWriter)
            throws SQLException, IOException, ImportException,
            AssemblyException {
        pMonitor.incrementCurValue();
        CPathRecord record = cpath.extractRecord(rs);
        ConsoleUtil.showProgress(verbose, pMonitor);

        //  Create XML Assembly Object of Specified Interaction Record
        XmlAssembly xmlAssembly = XmlAssemblyFactory.createXmlAssembly
                (record, 1, new XDebug());

        //  Determine which fields to index
        ItemToIndex item = IndexFactory.createItemToIndex
                (record.getId(), xmlAssembly);

        //  Then, index all fields in Lucene
        indexWriter.addRecord(item);
    }
}