package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

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
                    ("INSERT INTO EXTERNAL_DB (`NAME`,`URL`,"
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
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }


    /**
     * Retrieves the External Database Record with the specified ID.
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
                    ("SELECT * FROM EXTERNAL_DB WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            return extractRecord(rs);
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Retrieves the External Database Record with the specified name.
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
                    ("SELECT * FROM EXTERNAL_DB WHERE NAME = ?");
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            return extractRecord(rs);
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Retrieves the External Database Record with the matching pattern.
     * @param term String Term
     * @return External Database Record.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getRecordByTerm(String term)
            throws DaoException {
        DaoExternalDbCv dao = new DaoExternalDbCv();
        return dao.getExternalDbByTerm(term);
    }

    /**
     * Gets a Complete List of All External Database Records.
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
                    ("SELECT * FROM EXTERNAL_DB");
            rs = pstmt.executeQuery();
            ArrayList records = new ArrayList();
            while (rs.next()) {
                ExternalDatabaseRecord db = this.createBean(rs);
                records.add(db);
            }
            return records;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Delete External Database Record with the Specified ID.
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
                    ("DELETE FROM EXTERNAL_DB WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            //  Delete all associated terms.
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dao.deleteTermsByDbId(id);
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Update External Database Record with the Specified ID.
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
                    ("UPDATE EXTERNAL_DB SET `NAME` = ?, "
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
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
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

        DaoExternalDbCv dao = new DaoExternalDbCv();
        ArrayList terms = dao.getTermsByDbId(record.getId());
        record.setCvTerms(terms);
        return record;
    }
}