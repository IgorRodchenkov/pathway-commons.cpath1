package org.mskcc.pathdb.sql;

import org.mskcc.pathdb.model.ExternalDatabase;
import org.mskcc.pathdb.model.ExternalLink;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the External Link Table.
 *
 * @author Ethan Cerami
 */
public class DaoExternalLink {

    /**
     * Adds New External Link Record.
     * @param link ExternalLink Object.
     * @return true if saved successfully.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean addRecord(ExternalLink link)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
            if (!recordExists(link)) {
                pstmt = con.prepareStatement
                        ("INSERT INTO external_link (`CPATH_ID`, "
                        + "`EXTERNAL_DB_ID`, `LINKED_TO_ID`, `CREATE_TIME`)"
                        + " VALUES (?,?,?,?)");
                pstmt.setInt(1, link.getCpathId());
                pstmt.setInt(2, link.getExternalDbId());
                pstmt.setString(3, link.getLinkedToId());
                java.util.Date date = new java.util.Date();
                Timestamp timeStamp = new Timestamp(date.getTime());
                pstmt.setTimestamp(4, timeStamp);
                int rows = pstmt.executeUpdate();
                return (rows > 0) ? true : false;
            } else {
                return false;
            }
        } finally {
            JdbcUtil.freeConnection(con, pstmt, null);
        }
    }

    /**
     * Gets Record by specified External Link ID.
     * @param externalLinkId External Link ID.
     * @return External Link Object.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public ExternalLink getRecordById(int externalLinkId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_LINK WHERE EXTERNAL_LINK_ID = ?");
            pstmt.setInt(1, externalLinkId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createBean(rs);
            } else {
                return null;
            }
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    /**
     * Gets All External Link Records Associated with specified CPath ID.
     * @param cpathId CPath ID.
     * @return ArrayList of External Link Objects.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public ArrayList getRecordsByCPathId(int cpathId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        ArrayList links = new ArrayList();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_LINK WHERE CPATH_ID = ?");
            pstmt.setInt(1, cpathId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ExternalLink link = createBean(rs);
                links.add(link);
            }
            return links;
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    /**
     * Gets Record by specified External Link ID.
     * @param externalLinkId External Link ID.
     * @return true if deletion is successful.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean deleteRecordById(int externalLinkId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("DELETE FROM EXTERNAL_LINK WHERE EXTERNAL_LINK_ID = ?");
            pstmt.setInt(1, externalLinkId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    /**
     * Determines if the specified record already exists.
     * @param link ExternalLink Object.
     * @return true if record already exists in database.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean recordExists(ExternalLink link)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_LINK WHERE CPATH_ID = ? "
                    + " AND EXTERNAL_DB_ID = ? AND LINKED_TO_ID = ?");
            pstmt.setInt(1, link.getCpathId());
            pstmt.setInt(2, link.getExternalDbId());
            pstmt.setString(3, link.getLinkedToId());
            ResultSet rs = pstmt.executeQuery();
            return (rs.next()) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    private ExternalLink createBean(ResultSet rs) throws SQLException,
            ClassNotFoundException {
        ExternalLink link = new ExternalLink();
        link.setId(rs.getInt("EXTERNAL_LINK_ID"));
        link.setCpathId(rs.getInt("CPATH_ID"));
        link.setExternalDbId(rs.getInt("EXTERNAL_DB_ID"));
        link.setLinkedToId(rs.getString("LINKED_TO_ID"));
        link.setCreateTime(rs.getTimestamp("CREATE_TIME"));
        link.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));

        //  Get Associated External Database Record
        DaoExternalDb table = new DaoExternalDb();
        ExternalDatabase db = table.getRecordById(link.getExternalDbId());
        link.setExternalDatabase(db);
        return link;
    }
}