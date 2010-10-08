package org.mskcc.pathdb.sql.transfer;

import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;

import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;

import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.tool.ConsoleUtil;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.*;

/**
 * Populates the number of pathways and number of interaction stats stored
 * in the external_db_snapshot table.
 *
 * @author Benjamin Gross.
 */
public class PopulateDatabaseSnapshotStats {

    public static enum STATS {

        // command types
    	PATHWAYS("pathways"),
        INTERACTIONS("interactions"),
		PHYSICAL_ENTITIES("physical-entities");

        // string ref for readable name
        private String stats;
        
        // contructor
        STATS(String stats) { this.stats = stats; }

        // method to get enum readable name
        public String toString() { return stats; }
    }

	private static final String GET_NUM_PATHWAYS = "select count(*) from cpath where type = \"pathways\" and external_db_snapshot_id = ?";
	private static final String GET_NUM_INTERACTIONS = "select count(*) from cpath where type = \"interaction\" and external_db_snapshot_id = ?";
	private static final String GET_NUM_PHYSICAL_ENTITIES = "select count(*) from cpath where type = \"physical_entity\" and external_db_snapshot_id = ?";

    private ProgressMonitor pMonitor;

    /**
     * Constructor.
     *
     * @param pMonitor ProgressMonitor Object.
     */
    public PopulateDatabaseSnapshotStats(ProgressMonitor pMonitor) {
        this.pMonitor = pMonitor;
    }

    /**
     * Executes task.
     *
     * @throws DaoException Data access error.
     */
    public void execute () throws DaoException {

		DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();

        //  Determine # snapshot records to process
		ArrayList<ExternalDatabaseSnapshotRecord> snapshotRecords = 
			(ArrayList<ExternalDatabaseSnapshotRecord>)daoSnapshot.getAllDatabaseSnapshots();
        pMonitor.setMaxValue(snapshotRecords.size());

		// iterate over all snapshot id records
		for (ExternalDatabaseSnapshotRecord snapshotRecord : snapshotRecords) {

			// update progress monitor
			pMonitor.incrementCurValue();
			ConsoleUtil.showProgress(pMonitor);

			// get pathway count
			snapshotRecord.setNumPathways(getStats(snapshotRecord.getId(), STATS.PATHWAYS));

			// get interaction count
			snapshotRecord.setNumInteractions(getStats(snapshotRecord.getId(), STATS.INTERACTIONS));

			// get interaction count
			snapshotRecord.setNumPhysicalEntities(getStats(snapshotRecord.getId(), STATS.PHYSICAL_ENTITIES));

			// update in table
			daoSnapshot.updatePathwayInteractionPEStats(snapshotRecord);
        }
    }

    /**
     * Gets total count of pathways, interactions, or PEs introduced into pc by the given snapshot.
     */
    private long getStats(long snapshotID, STATS stats) throws DaoException {

        ResultSet rs = null;
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = JdbcUtil.getCPathConnection();
			switch (stats) {
			case PATHWAYS:
				pstmt = con.prepareStatement(GET_NUM_PATHWAYS);
				break;
			case INTERACTIONS:
				pstmt = con.prepareStatement(GET_NUM_INTERACTIONS);
				break;
			case PHYSICAL_ENTITIES:
				pstmt = con.prepareStatement(GET_NUM_PHYSICAL_ENTITIES);
				break;
			}
            pstmt.setLong(1, snapshotID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
		catch (SQLException e) {
            throw new DaoException(e);
        }
		finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

        return 0;
    }
}
