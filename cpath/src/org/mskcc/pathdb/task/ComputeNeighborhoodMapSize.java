// $Id: ComputeNeighborhoodMapSize.java,v 1.1 2008-12-10 16:51:43 grossben Exp $
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
import org.mskcc.pathdb.model.NeighborhoodMap;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoNeighborhoodMap;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.action.web_api.NeighborhoodMapRetriever;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;

import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.io.IOException;

/**
 * For each protein in cpath table, compute neighborhood map size.
 *
 * @author Benjamin Gross
 */
public class ComputeNeighborhoodMapSize extends Task {

	// private members
    private XDebug xdebug;
    private static final int BLOCK_SIZE = 1000;

    /**
     * Constructor.
     *
     * @param consoleMode Running in Console Mode.
     * @param xdebug      XDebug Object.
     */
    public ComputeNeighborhoodMapSize(boolean consoleMode, XDebug xdebug) {
        super("ComputeNeighborhoodMapSize", consoleMode);
        this.xdebug = xdebug;
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Computing neighborhood map size for all proteins in cpath");
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
        computeNeighborhoodMapSizes();
        pMonitor.setCurrentMessage("Compute neighborhood map size complete");
        xdebug.stopTimer();
    }

    /**
     * Computes neighborhood map sizes for all proteins.
     *
     * @throws DaoException
     */
    public void computeNeighborhoodMapSizes() throws DaoException {

		// setup some required refs
        ProgressMonitor pMonitor = this.getProgressMonitor();
        DaoCPath cpath = DaoCPath.getInstance();
		DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
		DaoNeighborhoodMap daoMap = new DaoNeighborhoodMap();
		daoMap.deleteAllRecords();

		// precompute some arguments that will be passed to the neighborhood map routines
		ProtocolRequest protocolRequest =  new ProtocolRequest();
		protocolRequest.setInputIDType("CPATH_ID");
		ArrayList<ExternalDatabaseSnapshotRecord> snapshotRecords = daoSnapshot.getAllNetworkDatabaseSnapshots();
		String dataSources = "";
		for (ExternalDatabaseSnapshotRecord record : snapshotRecords) {
			dataSources += record.getExternalDatabase().getMasterTerm() + ",";
		}
		protocolRequest.setDataSource(dataSources);

		// interate over all records in cpath table and compute map sizes for all physical entities
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CPathRecord record = null;
        long maxIterId = cpath.getMaxCpathID();
		NeighborhoodMapRetriever retriever = new NeighborhoodMapRetriever();
		System.out.println("RecordID:RecordName:MapSize");
		System.out.println("---------------------------");
        for (int id = 0; id <= maxIterId; id = id + BLOCK_SIZE + 1) {
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
					// only process physical entities
					if (!record.getSpecificType().equalsIgnoreCase(BioPaxConstants.PROTEIN)) continue;
					// create map object
					NeighborhoodMap map = new NeighborhoodMap();
					map.setCpathID(record.getId());
					// get map size
					protocolRequest.setQuery(Long.toString(record.getId()));
					Integer mapSize = retriever.getNeighborhoodMapSize(xdebug, protocolRequest);
					map.setMapSize(mapSize);
					// add record to table
					daoMap.addNeighborhoodMap(map);
					System.out.println(record.getId() + "|" + record.getName() + "|" + mapSize);
				}
			} catch (Exception e1) {
				throw new DaoException(e1);
			}
			JdbcUtil.closeAll(con, pstmt, rs);
		}
	}
}