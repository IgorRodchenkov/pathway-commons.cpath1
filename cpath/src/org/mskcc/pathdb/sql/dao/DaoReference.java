// $Id: DaoReference.java,v 1.4 2006-12-22 18:02:45 grossb Exp $
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
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object to the reference Table.
 *
 * @author Benjamin Gross
 */
public class DaoReference {

	// authors delimiter
	private final static String AUTHORS_DELIMITER = "12345";

	/**
	 * Given a Reference object, adds it to reference table
	 *
	 * @param ref Reference
	 * @throws DaoException
	 */
	public void addReference(Reference ref) throws DaoException {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement ("INSERT INTO reference (" +
										  "`REFERENCE_ID`, `YEAR`, `TITLE`, " +
										  "`AUTHORS`, `SOURCE`, `EXTERNAL_DB_ID`) " +
										  "VALUES (?,?,?,?,?,?)");
			// id
			pstmt.setString(1, ref.getId());
			// year
			pstmt.setString(2, ref.getYear());
			// title
			pstmt.setString(3, ref.getTitle());
			// authors
			String authors = "";
			for (String author : ref.getAuthors()) {
				authors += (author + AUTHORS_DELIMITER);
			}
			pstmt.setString(4, authors);
			// source
			pstmt.setString(5, ref.getSource());
			// database id
			DaoExternalDb daoExternalDb = new DaoExternalDb();
			ExternalDatabaseRecord dbRecord = daoExternalDb.getRecordByName(ref.getDatabase());
			pstmt.setInt(6, dbRecord.getId());
			// execute query
			pstmt.executeUpdate();			  
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
	}

    /**
     * Given an id, returns a Reference object.
     *
	 * @param referenceId String
	 * @param databaseId int
     * @return Reference
     * @throws DaoException
     */
    public Reference getRecord(String referenceId, int databaseId) throws DaoException {

        // init some local vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Reference ref = null;

        try {
            // perform the query
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("select * from reference where " +
										 "REFERENCE_ID = ? AND EXTERNAL_DB_ID = ?");
            pstmt.setString(1, referenceId);
			pstmt.setInt(2, databaseId);
            rs = pstmt.executeQuery();

            // set the form object
            if (rs.next()) {
				// create new object
                ref = new Reference();
				// id
				ref.setId(rs.getString(1));
				// year
				ref.setYear(rs.getString(2));
				// title
				ref.setTitle(rs.getString(3));
				// authors
				String authors = rs.getString(4);
				ref.setAuthors(authors.split(AUTHORS_DELIMITER));
				// source
				ref.setSource(rs.getString(5));
				// database name
				DaoExternalDb daoExternalDb = new DaoExternalDb();
				ExternalDatabaseRecord dbRecord = daoExternalDb.getRecordById(rs.getInt(6));
				ref.setDatabase(dbRecord.getName());
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

        // outta here
        return ref;
    }

    /**
     * Deletes Record specified by Reference ID.
     *
     * @param referenceId String
     * @return true if deletion is successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(String referenceId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("delete from reference where REFERENCE_ID = ?");
            pstmt.setString(1, referenceId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
