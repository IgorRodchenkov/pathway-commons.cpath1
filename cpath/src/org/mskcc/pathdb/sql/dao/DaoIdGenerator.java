package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object to the ID Generator table.
 *
 * @author Ethan Cerami
 *
*/
public class DaoIdGenerator {
    /**
     * cPath Local ID Prefix.
     */
    public static final String CPATH_LOCAL_ID_PREFIX = "cpath_local_";

    /**
     * Gets Next Avalable ID.
     * @return  Next Available ID.
     * @throws DaoException Error Accessing Database.
     */
    public synchronized String getNextId() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            int id = 0;
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT NEXT_ID FROM id_generator");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
                saveNextId(con, pstmt, id);
            } else {
                saveFirstId (con, pstmt, id);
            }
            return CPATH_LOCAL_ID_PREFIX + id;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Resets the ID Generator.  Use with Extreme Caution!
     */
    public void resetIdGenerator() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement ("truncate id_generator");
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
     * Saves the Next ID back to MySQL.
     */
    private void saveNextId(Connection con,
            PreparedStatement pstmt, int nextId) throws SQLException {
        pstmt = con.prepareStatement
            ("UPDATE id_generator SET `NEXT_ID` = ?");
        pstmt.setInt(1, ++nextId);
        pstmt.executeUpdate();
    }

    /**
     * Saves the First ID to MySQL.
     */
    private void saveFirstId(Connection con,
            PreparedStatement pstmt, int nextId) throws SQLException {
        pstmt = con.prepareStatement
            ("INSERT into id_generator (`NEXT_ID`) VALUES (?)");
        pstmt.setInt(1, ++nextId);
        pstmt.executeUpdate();
    }
}