package org.mskcc.pathdb.sql;

import org.mskcc.pathdb.model.ExternalDatabase;

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
     * Adds New ExternalDatabase CV Term.
     * @param dbId External Database ID.
     * @param term Term.
     * @return true indicates success.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public boolean addRecord(int dbId, String term)
            throws SQLException, ClassNotFoundException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement
                    ("INSERT INTO EXTERNAL_DB_CV (`EXTERNAL_DB_ID`,`CV_TERM`)"
                    + " VALUES (?,?)");
            pstmt.setInt(1, dbId);
            pstmt.setString(2, term);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con, pstmt, null);
        }
    }

    /**
     * Gets Database by Term.
     * @param term Term to search.
     * @return ExternalDatabase Object.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public ExternalDatabase getExternalDbByTerm(String term)
            throws SQLException,
            ClassNotFoundException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_DB_CV WHERE CV_TERM = ?");
            pstmt.setString(1, term);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("EXTERNAL_DB_ID");
                DaoExternalDb daoDb = new DaoExternalDb();
                return daoDb.getRecordById(id);
            }
            return null;
        } finally {
            JdbcUtil.freeConnection(con, pstmt, null);
        }
    }

    /**
     * Gets all Terms Associated with the specified Database.
     * @param dbId External Database Id
     * @return ArrayList of Terms.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public ArrayList getTermsByDbId(int dbId)
            throws SQLException,
            ClassNotFoundException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_DB_CV WHERE EXTERNAL_DB_ID = ? "
                    + "ORDER BY CV_TERM");
            pstmt.setInt(1, dbId);
            ResultSet rs = pstmt.executeQuery();
            ArrayList terms = new ArrayList();
            while (rs.next()) {
                String term = rs.getString("CV_TERM");
                terms.add(term);
            }
            return terms;
        } finally {
            JdbcUtil.freeConnection(con, pstmt, null);
        }
    }

    /**
     * Deletes All Terms Associated with Specified Database ID.
     * @param dbId Database ID.
     * @return true indicates success.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public boolean deleteTermsByDbId(int dbId)
            throws SQLException, ClassNotFoundException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement
                    ("DELETE FROM EXTERNAL_DB_CV WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, dbId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con, pstmt, null);
        }
    }
}