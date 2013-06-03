// $Id
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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
package org.mskcc.pathdb.bb.sql.dao;

import org.mskcc.pathdb.bb.model.BBPathwayRecord;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Data Access Object to the bb_pathway table.
 *
 * @author Benjamin Gross
 */
public class DaoBBPathway {

    /**
     * Adds a new BBPathwayRecord to the db.
     *
     * @param bbPathwayRecord BBPathwayRecord
     * @throws DaoException
     */
    public void addRecord(BBPathwayRecord bbPathwayRecord) throws DaoException {

		// check args
		if (bbPathwayRecord == null) throw new DaoException("bbPathwayRecord is null.");

        //  Make sure record does not already exist.
        BBPathwayRecord record = getBBPathway(bbPathwayRecord.getPathwayID());
        if (record != null) throw new DaoException("Record already exists.");

		// query vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		// perform query
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO bb_pathway "
                            + "(`external_pathway_id`, "
                            + "`pathway_name`,"
                            + "`source`,"
                            + "`url`)"
                            + " VALUES (?,?,?,?)");
            pstmt.setString(1, bbPathwayRecord.getPathwayID());
            pstmt.setString(2, bbPathwayRecord.getPathwayName());
            pstmt.setString(3, bbPathwayRecord.getSource());
            pstmt.setString(4, bbPathwayRecord.getURL());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all bbPathwayRecords.
     *
     * @return ArrayList<BBPathwayRecord>
     * @throws DaoException
     */
    public ArrayList<BBPathwayRecord> getAllBBPathway() throws DaoException {

		// some used vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		ArrayList<BBPathwayRecord> pathwayRecords = new ArrayList<BBPathwayRecord>();
		// do the query
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("select * from bb_pathway order by pathway_name");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String pathwayID = rs.getString(1);
                String pathwayName = rs.getString(2);
                String source = rs.getString(3);
                String url = rs.getString(4);
				pathwayRecords.add(new BBPathwayRecord(pathwayID, pathwayName, source, url));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

		// outta here
		return pathwayRecords;
    }

    /**
     * Gets the specified bbPathwayRecord by ID.
     *
     * @param bbPathwayRecordID String
     * @return BBPathwayRecord 
     * @throws DaoException
     */
    public BBPathwayRecord getBBPathway(String bbPathwayRecordID) throws DaoException {

		// some used vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		// do the query
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from bb_pathway where external_pathway_id = ?");
            pstmt.setString(1, bbPathwayRecordID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String pathwayID = rs.getString(1);
                String pathwayName = rs.getString(2);
                String source = rs.getString(3);
                String url = rs.getString(4);
                return new BBPathwayRecord(pathwayID, pathwayName, source, url);
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
