// $Id: DaoSourceTracker.java,v 1.1 2006-08-25 16:47:40 cerami Exp $
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

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object to the Source Tracker Table.
 *
 * @author Ethan Cerami.
 */
public class DaoSourceTracker {

    /**
     * Adds a new link between the specified source record and the
     * specified cPath generated record.
     *
     * @param sourceRecordId         cPath ID of Source record.
     * @param cPathGeneratedRecordId cPath ID of cPath generated record.
     * @return ID of new source tracker record.
     * @throws DaoException Error connecting to database.
     */
    public synchronized long addRecord(long sourceRecordId,
            long cPathGeneratedRecordId) throws DaoException {
        long sourceTrackerId;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO source_tracker "
                            + "(`ID_OF_CPATH_GENERATED_RECORD`, "
                            + "`ID_OF_SOURCE_RECORD`)"
                            + " VALUES (?,?)");
            pstmt.setLong(1, cPathGeneratedRecordId);
            pstmt.setLong(2, sourceRecordId);
            pstmt.executeUpdate();

            //  Get New Source Tracker ID
            pstmt = con.prepareStatement
                    ("select MAX(SOURCE_TRACKER_ID) from source_tracker ");
            rs = pstmt.executeQuery();
            rs.next();
            sourceTrackerId = rs.getLong(1);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
        return sourceTrackerId;
    }

    /**
     * Gets a list of all source records associated with the specified
     * cPath generated record.
     *
     * @param cPathGeneratedRecordId cPath ID of cPath generated record.
     * @return ArrayList of cPath Record Objects.
     * @throws DaoException Error connecting to database.
     */
    public ArrayList getSourceRecords(long cPathGeneratedRecordId)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList sourceRecordList = new ArrayList();
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from source_tracker where "
                            + "ID_OF_CPATH_GENERATED_RECORD = ?");
            pstmt.setLong(1, cPathGeneratedRecordId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                long sourceRecordId = rs.getLong("ID_OF_SOURCE_RECORD");
                DaoCPath daoCPath = DaoCPath.getInstance();
                CPathRecord sourceRecord = daoCPath.getRecordById
                        (sourceRecordId);
                sourceRecordList.add(sourceRecord);
            }
            return sourceRecordList;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}