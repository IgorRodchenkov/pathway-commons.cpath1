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
package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.GlobalCache;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the External Database Table.
 *
 * @author Ethan Cerami.
 */
public class DaoExternalDb {

    /**
     * Adds Specified External Database Record to the CPath Database.
     *
     * @param db External Database Record.
     * @return true if record was successfully added.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean addRecord(ExternalDatabaseRecord db) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO external_db (`NAME`,`URL`,"
                    + "`DESC`,`CREATE_TIME`) VALUES (?,?,?,?)");
            pstmt.setString(1, db.getName());
            pstmt.setString(2, db.getUrl());
            pstmt.setString(3, db.getDescription());
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());
            pstmt.setTimestamp(4, timeStamp);
            int rows = pstmt.executeUpdate();

            // Save CV Terms.
            ArrayList terms = db.getCvTerms();
            DaoExternalDbCv dao = new DaoExternalDbCv();
            db = getRecordByName(db.getName());
            if (terms != null) {
                for (int i = 0; i < terms.size(); i++) {
                    dao.addRecord(db.getId(), (String) terms.get(i));
                }
            }
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }


    /**
     * Retrieves the External Database Record with the specified ID.
     *
     * @param id External Database ID.
     * @return External Database Record.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getRecordById(int id) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            return extractRecord(rs);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Retrieves the External Database Record with the specified name.
     *
     * @param name External Database Name
     * @return External Database Record.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getRecordByName(String name)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db WHERE NAME = ?");
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            return extractRecord(rs);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Retrieves the External Database Record with the matching pattern.
     *
     * @param term String Term
     * @return External Database Record.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getRecordByTerm(String term)
            throws DaoException {
        // First Check Global Cache
        GlobalCache cache = GlobalCache.getInstance();
        ExternalDatabaseRecord dbRecord = (ExternalDatabaseRecord)
                cache.get(term);

        //  If not in cache, get from Database
        if (dbRecord == null) {
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dbRecord = dao.getExternalDbByTerm(term);
            if (dbRecord != null) {
                cache.put(term, dbRecord);
            }
        }
        return dbRecord;
    }

    /**
     * Gets a Complete List of All External Database Records.
     *
     * @return ArrayList of External Database object.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db");
            rs = pstmt.executeQuery();
            ArrayList records = new ArrayList();
            while (rs.next()) {
                ExternalDatabaseRecord db = this.createBean(rs);
                records.add(db);
            }
            return records;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Delete External Database Record with the Specified ID.
     *
     * @param id External Database ID.
     * @return true indicates record was successfully deleted.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(int id) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM external_db WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            //  Delete all associated terms.
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dao.deleteTermsByDbId(id);
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Update External Database Record with the Specified ID.
     *
     * @param db External Database Record.
     * @return true indicates record was successfully update.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean updateRecord(ExternalDatabaseRecord db) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE external_db SET `NAME` = ?, "
                    + "`DESC` = ?, `URL` = ?, `UPDATE_TIME` = ? "
                    + "WHERE `EXTERNAL_DB_ID` = ?");
            pstmt.setString(1, db.getName());
            pstmt.setString(2, db.getDescription());
            pstmt.setString(3, db.getUrl());
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());
            pstmt.setTimestamp(4, timeStamp);
            pstmt.setInt(5, db.getId());
            int rows = pstmt.executeUpdate();

            //  Delete all associated terms.
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dao.deleteTermsByDbId(db.getId());

            // Save CV Terms.
            ArrayList terms = db.getCvTerms();
            db = getRecordByName(db.getName());
            for (int i = 0; i < terms.size(); i++) {
                dao.addRecord(db.getId(), (String) terms.get(i));
            }
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Extracts Database Record into Java Bean.
     */
    private ExternalDatabaseRecord extractRecord(ResultSet rs)
            throws SQLException, DaoException {
        if (rs.next()) {
            return createBean(rs);
        } else {
            return null;
        }
    }

    /**
     * Creates External Database Java Bean.
     */
    private ExternalDatabaseRecord createBean(ResultSet rs)
            throws SQLException, DaoException {
        ExternalDatabaseRecord record = new ExternalDatabaseRecord();
        record.setId(rs.getInt("EXTERNAL_DB_ID"));
        record.setName(rs.getString("NAME"));
        record.setUrl(rs.getString("URL"));
        record.setDescription(rs.getString("DESC"));
        record.setCreateTime(rs.getTimestamp("CREATE_TIME"));
        record.setUpdateTime(rs.getTime("UPDATE_TIME"));
        int cvId = rs.getInt("FIXED_CV_TERM");

        DaoExternalDbCv dao = new DaoExternalDbCv();
        ArrayList terms = dao.getTermsByDbId(record.getId());
        record.setCvTerms(terms);
        String term = dao.getTermByDbCvId(cvId);
        record.setFixedCvTerm(term);
        return record;
    }
}