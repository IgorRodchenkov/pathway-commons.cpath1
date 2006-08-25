// $Id: DaoExternalDbSnapshot.java,v 1.1 2006-08-25 16:47:40 cerami Exp $
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
package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Data Access Object to the External Database Snapshot table.
 *
 * @author Ethan Cerami.
 */
public class DaoExternalDbSnapshot {

    /**
     * Deletes all existing snapshot records.
     *
     * @throws DaoException Error Accessing Database.
     */
    public void deleteAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("TRUNCATE TABLE external_db_snapshot");
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Adds a new snapshot record.
     *
     * @param externalDbId    External database identifier.
     * @param snapshotDate    Date of snapshot.
     * @param snapshotVersion Version / release number of snapshot.
     * @return ID of new external database snapshot record.
     * @throws DaoException Error connecting to the databse.
     */
    public synchronized long addRecord(int externalDbId,
            Date snapshotDate, String snapshotVersion) throws DaoException {
        long externalDbSnapshotId;
        if (snapshotVersion == null) {
            snapshotVersion = "N/A";
        }

        //  Make sure record does not already exist.
        ExternalDatabaseSnapshotRecord record =
                getDatabaseSnapshot(externalDbId, snapshotDate);
        if (record != null) {
            throw new DaoException("Snapshot record with this date"
                    + "already exists.");
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO external_db_snapshot "
                            + "(`EXTERNAL_DB_ID`, "
                            + "`SNAPSHOT_DATE`,"
                            + "`SNAPSHOT_VERSION`)"
                            + " VALUES (?,?,?)");
            pstmt.setLong(1, externalDbId);
            java.sql.Date date = new java.sql.Date(snapshotDate.getTime());
            pstmt.setDate(2, date);
            pstmt.setString(3, snapshotVersion);
            pstmt.executeUpdate();

            //  Get New External DB Snapshot ID
            pstmt = con.prepareStatement
                    ("select MAX(EXTERNAL_DB_SNAPSHOT_ID) "
                            + "from external_db_snapshot");
            rs = pstmt.executeQuery();
            rs.next();
            externalDbSnapshotId = rs.getLong(1);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
        return externalDbSnapshotId;
    }

    /**
     * Gets the specified snapshot record.
     *
     * @param externalDbId External database ID.
     * @param snapshotDate Snapshot date.
     * @return ExternalDatabaseSnapshotRecord
     * @throws DaoException Error connecting to database.
     */
    public ExternalDatabaseSnapshotRecord getDatabaseSnapshot(int externalDbId,
            Date snapshotDate) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from external_db_snapshot where "
                            + "EXTERNAL_DB_ID = ? AND "
                            + "SNAPSHOT_DATE = ?");
            pstmt.setLong(1, externalDbId);
            java.sql.Date date = new java.sql.Date(snapshotDate.getTime());
            pstmt.setDate(2, date);
            ArrayList snapshotList = getMultipleSnapshots(pstmt);
            if (snapshotList.size() == 1) {
                return (ExternalDatabaseSnapshotRecord) snapshotList.get(0);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all snapshots for the specified external database.
     *
     * @param externalDbId External database ID.
     * @return ArrayList of ExternalDatabaseSnapshotRecord records.
     * @throws DaoException Error connecting to database.
     */
    public ArrayList getDatabaseSnapshot(int externalDbId)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from external_db_snapshot where "
                            + "EXTERNAL_DB_ID = ? "
                            + "ORDER BY EXTERNAL_DB_SNAPSHOT_ID");
            pstmt.setLong(1, externalDbId);
            return getMultipleSnapshots(pstmt);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all snapshots in the database.
     *
     * @return ArrayList of ExternalDatabaseSnapshotRecord records.
     * @throws DaoException Error connecting to database.
     */
    public ArrayList getAllDatabaseSnapshots() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from external_db_snapshot "
                            + "ORDER BY EXTERNAL_DB_SNAPSHOT_ID");
            return getMultipleSnapshots(pstmt);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets multiple snapshot records.
     */
    private ArrayList getMultipleSnapshots(PreparedStatement pstmt)
            throws DaoException {
        ArrayList snapshotList = new ArrayList();
        try {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int externalDbId = rs.getInt("EXTERNAL_DB_ID");
                String snapshotVersion = rs.getString("SNAPSHOT_VERSION");
                java.sql.Date date = rs.getDate("SNAPSHOT_DATE");
                DaoExternalDb daoExternalDb = new DaoExternalDb();
                ExternalDatabaseRecord dbRecord =
                        daoExternalDb.getRecordById(externalDbId);
                ExternalDatabaseSnapshotRecord snapshotRecord =
                        new ExternalDatabaseSnapshotRecord
                                (dbRecord, new Date(date.getTime()), snapshotVersion);
                snapshotList.add(snapshotRecord);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return snapshotList;
    }
}