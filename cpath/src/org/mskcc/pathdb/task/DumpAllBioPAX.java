// $Id: DumpAllBioPAX.java,v 1.1 2008-07-31 16:31:30 grossben Exp $
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

import org.mskcc.pathdb.tool.Admin;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Dump xml content for all cpath records into 
 *
 * @author Ethan Cerami.
 */
public class DumpAllBioPAX extends Task {

    private static final int BLOCK_SIZE = 1000;
    private XDebug xdebug;
	private String dumpFileName;

    /**
     * Constructor.
     *
     * @param consoleMode Running in Console Mode.
     * @param xdebug      XDebug Object.
     */
    public DumpAllBioPAX(boolean consoleMode, XDebug xdebug) {
        super("Dump all BioPAX", consoleMode);
        this.xdebug = xdebug;
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Dumping all BioPAX");

		String cpathHome = System.getProperty(Admin.CPATH_HOME);
		String separator = System.getProperty("file.separator");
		dumpFileName = (cpathHome + separator + "full-biopax-dump.xml");
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
     * Execute Dump BioPAX Task.
     *
     * @throws DaoException      Data Access Error.
     * @throws IOException       File IO Error.
     */
    public void executeTask() throws DaoException, IOException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        XDebug xdebug = new XDebug();
        xdebug.startTimer();
        dumpAllBioPAX();
        pMonitor.setCurrentMessage("Dump Complete");
        xdebug.stopTimer();
    }

    /**
     * Dumps biopax content from all cpath record.
     *
     * @throws DaoException      Data Access Exception.
     * @throws IOException       Input/Output Exception.
     */
    public void dumpAllBioPAX() throws IOException, DaoException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        DaoCPath cpath = DaoCPath.getInstance();
        int numPathways = cpath.getNumEntities(CPathRecordType.PATHWAY);
        int numInteractions = cpath
                .getNumEntities(CPathRecordType.INTERACTION);
        int numPhysicalEntities = cpath.getNumEntities
                (CPathRecordType.PHYSICAL_ENTITY);
        int totalNumEntities = numPathways + numInteractions
                + numPhysicalEntities;

        pMonitor.setCurrentMessage("Dumping BioPAX content of all cPath Records");
        pMonitor.setCurrentMessage("Total Number of Pathways:  "
                + numPathways);
        pMonitor.setCurrentMessage("Total Number of Interactions:  "
                + numInteractions);
        pMonitor.setCurrentMessage("Total Number of Physical Entities:  "
                + numPhysicalEntities);
        pMonitor.setMaxValue(totalNumEntities);

		// create dump file
		java.io.File dumpFile = new java.io.File(dumpFileName);
		java.io.FileOutputStream outStream = new java.io.FileOutputStream(dumpFile);
		java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(outStream));

		pMonitor.setCurrentMessage("Dumping BioPAX to file: " + dumpFile.getCanonicalPath());

		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CPathRecord record = null;

        long maxIterId = cpath.getMaxCpathID();
        for (int id = 0; id <= maxIterId; id = id + BLOCK_SIZE + 1) {
            System.out.println("Starting batch, starting at cPath id= " + id);

			// setup start/end id to fetch
			long startId = id;
			long endId = id + BLOCK_SIZE;
			if (endId > maxIterId) endId = maxIterId;

			try {

				con = JdbcUtil.getCPathConnection();
				pstmt = con.prepareStatement("select * from cpath WHERE " + " CPATH_ID BETWEEN " + startId + " and " + endId + " order by CPATH_ID ");
				rs = pstmt.executeQuery();

				while (rs.next()) {
					record = cpath.extractRecord(rs);
					writer.write(record.getXmlContent());
					writer.newLine();
				}
			} catch (Exception e1) {
				throw new DaoException(e1);
			}
			JdbcUtil.closeAll(con, pstmt, rs);
		}

		// close write
		writer.flush();
		writer.close();
	}
}