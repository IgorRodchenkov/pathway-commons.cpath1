// $Id: ExportInteractionsToText.java,v 1.8 2006-06-09 19:22:03 cerami Exp $
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

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.util.tool.ConsoleUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Exports All Interactions to a Very Simple Tab Delimited Text File.
 *
 * @author Ethan Cerami.
 */
public class ExportInteractionsToText extends Task {

    private BufferedWriter fileWriter;
    private static final int BLOCK_SIZE = 100;

    /**
     * Constructor.
     *
     * @param consoleMode Running in Console Mode.
     */
    public ExportInteractionsToText(boolean consoleMode) {
        super("Export Interactions to Simple Text File Format",
                consoleMode);
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Exporting interaction records "
                + "to simple text format.");
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
     * Execute Export Task.
     *
     * @throws DaoException      Data Access Error.
     * @throws ImportException   Import Error.
     * @throws IOException       File IO Error.
     * @throws AssemblyException XML Assembly Error.
     * @throws QueryException    Data Query Error.
     */
    public void executeTask() throws DaoException, ImportException,
            IOException, AssemblyException, QueryException {
        fileWriter = new BufferedWriter
                (new FileWriter("interactions.txt"));
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Exporting all cPath Interactions");
        DaoCPath cpath = DaoCPath.getInstance();
        int numInteractions = cpath.getNumEntities(CPathRecordType.INTERACTION);
        pMonitor.setCurrentMessage("Total Number of Interactions:  "
                + numInteractions);
        pMonitor.setMaxValue(numInteractions);

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            for (int i = 0; i <= numInteractions; i += BLOCK_SIZE) {
                con = JdbcUtil.getCPathConnection();
                int end = i + BLOCK_SIZE;
                System.out.println("<getting batch of interactions:  "
                        + i + " - " + end + ">");
                pstmt = con.prepareStatement
                        ("select * from cpath WHERE TYPE = ?  order by "
                                + "CPATH_ID LIMIT " + i + ", " + BLOCK_SIZE);
                pstmt.setString(1, CPathRecordType.INTERACTION.toString());
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    exportRecord(cpath, rs);
                }
                fileWriter.flush();
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            fileWriter.close();
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Indexes One cPathRecord in Lucene.
     *
     * @param cpath cPathRecord.
     * @param rs    ResultSet
     * @throws SQLException Database Error
     * @throws DaoException Database Error
     */
    private void exportRecord(DaoCPath cpath, ResultSet rs)
            throws SQLException, DaoException, IOException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.incrementCurValue();
        CPathRecord interaction = cpath.extractRecord(rs);
        ConsoleUtil.showProgress(pMonitor);

        long primaryId = interaction.getId();
        DaoInternalLink internalLinker = new DaoInternalLink();
        ArrayList interactors = internalLinker.getTargetsWithLookUp
                (primaryId);
        for (int i = 0; i < interactors.size(); i++) {
            CPathRecord interactor = (CPathRecord) interactors.get(i);
            DaoExternalLink externalLinker = DaoExternalLink.getInstance();
            ArrayList refs = externalLinker.getRecordsByCPathId
                    (interactor.getId());
            String interactorId = null;
            for (int j = 0; j < refs.size(); j++) {
                ExternalLinkRecord externalLink = (ExternalLinkRecord)
                        refs.get(j);
                ExternalDatabaseRecord db = externalLink.getExternalDatabase();
                if (db.getMasterTerm().equals("UNIPROT")) {
                    interactorId = externalLink.getLinkedToId();
                }
            }
            if (interactorId == null) {
                interactorId = interactor.getName();
            }
            fileWriter.write(interactorId + "\t");
        }
        fileWriter.newLine();
    }
}
