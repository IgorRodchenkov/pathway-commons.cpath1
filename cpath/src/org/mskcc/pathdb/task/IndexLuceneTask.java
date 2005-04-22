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
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.xdebug.XDebug;
import org.jdom.JDOMException;

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
     */
    public void executeTask() throws DaoException, ImportException,
            IOException, AssemblyException, QueryException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Clearing XML Cache");
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
        pMonitor.setCurrentMessage("Total Time Elapsed:  "
                + xdebug.getTimeElapsed());
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
        pMonitor.setCurrentMessage("Indexing all cPath Pathways/Interactions");
        DaoCPath cpath = new DaoCPath();
        int numPathways = cpath.getNumEntities(CPathRecordType.PATHWAY);
        int numInteractions = cpath.getNumEntities(CPathRecordType.INTERACTION);
        pMonitor.setCurrentMessage("Total Number of Pathways:  "
                + numPathways);
        pMonitor.setCurrentMessage("Total Number of Interactions:  "
                + numInteractions);
        pMonitor.setMaxValue(numPathways + numInteractions);

        LuceneWriter indexWriter = new LuceneWriter(true);
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            for (int i = 0; i <= numInteractions; i += BLOCK_SIZE) {
                con = JdbcUtil.getCPathConnection();
                int end = i + BLOCK_SIZE;
                System.out.println("<getting batch of entities:  "
                        + i + " - " + end + ">");
                pstmt = con.prepareStatement
                        ("select * from cpath WHERE TYPE = ? "
                        + "OR TYPE = ? order by "
                        + "CPATH_ID LIMIT " + i + ", " + BLOCK_SIZE);
                pstmt.setString(1, CPathRecordType.INTERACTION.toString());
                pstmt.setString(2, CPathRecordType.PATHWAY.toString());
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    indexRecord(cpath, rs, indexWriter);
                }
                JdbcUtil.closeAll(con, pstmt, rs);
            }
            pMonitor.setCurrentMessage("\nOptimizing Indexes");
            indexWriter.optimize();
            indexWriter.closeWriter();
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (JDOMException e) {
            throw new DaoException (e);
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
            AssemblyException, JDOMException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.incrementCurValue();
        CPathRecord record = cpath.extractRecord(rs);
        ConsoleUtil.showProgress(pMonitor);

        //  Create XML Assembly Object of Specified Record
        XmlAssembly xmlAssembly = XmlAssemblyFactory.createXmlAssembly
                (record.getId(), 1, new XDebug());

        //  Determine which fields to index
        ItemToIndex item = IndexFactory.createItemToIndex
                (record.getId(), xmlAssembly);

        //  Then, index all fields in Lucene
        indexWriter.addRecord(item);
    }
}