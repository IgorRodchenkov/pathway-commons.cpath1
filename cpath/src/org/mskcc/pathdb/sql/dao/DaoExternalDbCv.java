package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object to the External Database CV Table.
 *
 * @author Ethan Cerami
 */
public class DaoExternalDbCv {

    /**
     * Adds New ExternalDatabaseRecord CV Term.
     * @param dbId External Database ID.
     * @param term Term.
     * @return true indicates success.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean addRecord(int dbId, String term) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO EXTERNAL_DB_CV (`EXTERNAL_DB_ID`,`CV_TERM`)"
                    + " VALUES (?,?)");
            pstmt.setInt(1, dbId);
            pstmt.setString(2, term);
            int rows = pstmt.executeUpdate();
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
     * Normalizes External Reference to FIXED CV_TERM.
     * @param dbTerm External Database CV Term
     * @return Fixed CV Term.
     * @throws DaoException Data Access Exception.
     */
    public String getFixedCvTerm(String dbTerm)
            throws DaoException {
        ExternalDatabaseRecord exDb = getExternalDbByTerm(dbTerm);
        if (exDb == null) {
            throw new DaoException
                    ("No matching database found for: " + dbTerm);
        }
        return exDb.getFixedCvTerm();
    }

    /**
     * Gets Database by Term.
     * @param term Term to search.
     * @return ExternalDatabaseRecord Object.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getExternalDbByTerm(String term)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_DB_CV WHERE CV_TERM = ?");
            pstmt.setString(1, term);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("EXTERNAL_DB_ID");
                DaoExternalDb daoDb = new DaoExternalDb();
                ExternalDatabaseRecord dbRecord = daoDb.getRecordById(id);
                return dbRecord;
            }
            return null;
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
     * Gets all Terms Associated with the specified Database.
     * @param dbId External Database Id
     * @return ArrayList of Terms.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getTermsByDbId(int dbId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_DB_CV WHERE EXTERNAL_DB_ID = ? "
                    + "ORDER BY CV_TERM");
            pstmt.setInt(1, dbId);
            rs = pstmt.executeQuery();
            ArrayList terms = new ArrayList();
            while (rs.next()) {
                String term = rs.getString("CV_TERM");
                terms.add(term);
            }
            return terms;
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
     * Gets Term Associated with the specified Db Cv Id.
     * @param cvId External Database CV ID.
     * @return String term.
     * @throws DaoException Error Retrieving Data.
     */
    public String getTermByDbCvId(int cvId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_DB_CV WHERE CV_ID = ?");
            pstmt.setInt(1, cvId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                return rs.getString("CV_TERM");
            }
            return null;
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
     * Deletes All Terms Associated with Specified Database ID.
     * @param dbId Database ID.
     * @return true indicates success.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteTermsByDbId(int dbId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM EXTERNAL_DB_CV WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, dbId);
            int rows = pstmt.executeUpdate();
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
}