// $Id: DaoNeighborhoodMap.java,v 1.1 2008-12-10 16:50:55 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

// imports
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.model.Reference;
import org.mskcc.pathdb.model.NeighborhoodMap;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

/**
 * Data Access Object to the reference Table.
 *
 * @author Benjamin Gross
 */
public class DaoNeighborhoodMap {

	/**
	 * Given a NeighborhoodMap object, adds it to neighborhood map table.
	 *
	 * @param neighborhoodMap NeighborhoodMap
	 * @throws DaoException
	 */
	public void addNeighborhoodMap(NeighborhoodMap neighborhoodMap) throws DaoException {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement ("INSERT INTO neighborhood_map " +
										  "(`CPATH_ID`, `NEIGHBORHOOD_MAP_SIZE`) " +
										  "VALUES (?,?)");
			// cpath id
			pstmt.setLong(1, neighborhoodMap.getCpathID());
			// size
			pstmt.setInt(2, neighborhoodMap.getMapSize());
			// execute query
			pstmt.executeUpdate();			  
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
	}

    /**
     * Given a cpath id, returns a NeighborhoodMap object.
     *
	 * @param cpathID long
     * @return NeighborhoodMap
     * @throws DaoException
     */
    public NeighborhoodMap getNeighborhoodMapRecord(long cpathID) throws DaoException {

        // init some local vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        NeighborhoodMap map = null;
		
        try {
            // perform the query
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("select * from neighborhood_map where " + 
										 "CPATH_ID = ?");
            pstmt.setLong(1, cpathID);
            rs = pstmt.executeQuery();

            // set the form object
            if (rs.next()) {
				// create new object
                map = new NeighborhoodMap();
				// cpath id
				map.setCpathID(rs.getLong(1));
				// map size
				map.setMapSize(rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

        // outta here
        return map;
    }

    /**
     * Deletes all existing records.
     *
     * @throws DaoException
     */
    public void deleteAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("TRUNCATE TABLE neighborhood_map");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
