package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;

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
     * @param link ExternalLinkRecord Object.
     * @return true if saved successfully.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean addRecord(ExternalLinkRecord link)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
            if (!recordExists(link)) {
                pstmt = con.prepareStatement
                        ("INSERT INTO external_link (`CPATH_ID`, "
                        + "`EXTERNAL_DB_ID`, `LINKED_TO_ID`, `CREATE_TIME`)"
                        + " VALUES (?,?,?,?)");
                pstmt.setLong(1, link.getCpathId());
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
     * Adds All External References to Database.
     * @param cpathId cPath ID.
     * @param refs Array of External Reference Objects.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     * @throws ExternalDatabaseNotFoundException Database Not Found.
     */
    public void addMulipleRecords(long cpathId, ExternalReference refs[])
            throws ClassNotFoundException, SQLException,
            ExternalDatabaseNotFoundException {
        if (refs != null) {
            for (int i = 0; i < refs.length; i++) {
                String dbName = refs[i].getDatabase();
                String id = refs[i].getId();
                DaoExternalDb dao = new DaoExternalDb();
                ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbName);
                if (dbRecord != null) {
                    ExternalLinkRecord link = new ExternalLinkRecord();
                    link.setExternalDatabase(dbRecord);
                    link.setCpathId(cpathId);
                    link.setLinkedToId(id);
                    addRecord(link);
                } else {
                    throw new ExternalDatabaseNotFoundException
                            ("No matching database "
                            + "found for:  " + dbName + "[" + id + "]");
                }
            }
        }
    }

    /**
     * Looks Up the cPath Record that matches any of the specified External
     * References.
     * @param refs An Array of External References.  All these references
     * refer to the same interactor, as defined in different databases.
     * @return Matching cPath Record or Null.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public CPathRecord lookUpByByExternalRefs(ExternalReference refs[])
            throws SQLException, ClassNotFoundException {
        //  Iterate through all External References.
        if (refs != null) {
            for (int i = 0; i < refs.length; i++) {
                String dbName = refs[i].getDatabase();
                String linkedToId = refs[i].getId();

                // Find matching Database (if available).
                DaoExternalDb dao = new DaoExternalDb();
                ExternalDatabaseRecord externalDb = dao.getRecordByTerm(dbName);
                if (externalDb != null) {
                    //  Find Record that already uses this DbId and linkedToId.
                    ExternalLinkRecord link = this.getRecordByDbAndLinkedToId
                            (externalDb.getId(), linkedToId);
                    //  Retrieve the CPath Record for this match.
                    if (link != null) {
                        long cpathId = link.getCpathId();
                        DaoCPath cpathDao = new DaoCPath();
                        CPathRecord record = cpathDao.getRecordById(cpathId);
                        return record;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets Record by specified External Link ID.
     * @param externalLinkId External Link ID.
     * @return External Link Object.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public ExternalLinkRecord getRecordById(long externalLinkId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_LINK WHERE EXTERNAL_LINK_ID = ?");
            pstmt.setLong(1, externalLinkId);
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
     * Gets Record that matches specified ExternalDbId and LinkedToId.
     * @param externalDbId External Database ID.
     * @param linkedToId Linked To ID String.
     * @return External Link Object.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    private ExternalLinkRecord getRecordByDbAndLinkedToId(long externalDbId,
            String linkedToId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_LINK WHERE EXTERNAL_DB_ID = ? "
                    + "AND LINKED_TO_ID =?");
            pstmt.setLong(1, externalDbId);
            pstmt.setString(2, linkedToId);
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
    public ArrayList getRecordsByCPathId(long cpathId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        ArrayList links = new ArrayList();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_LINK WHERE CPATH_ID = ? "
                    + "ORDER BY EXTERNAL_LINK_ID");
            pstmt.setLong(1, cpathId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ExternalLinkRecord link = createBean(rs);
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
    public boolean deleteRecordById(long externalLinkId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("DELETE FROM EXTERNAL_LINK WHERE EXTERNAL_LINK_ID = ?");
            pstmt.setLong(1, externalLinkId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    /**
     * Determines if the specified record already exists.
     * @param link ExternalLinkRecord Object.
     * @return true if record already exists in database.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean recordExists(ExternalLinkRecord link)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM EXTERNAL_LINK WHERE CPATH_ID = ? "
                    + " AND EXTERNAL_DB_ID = ? AND LINKED_TO_ID = ?");
            pstmt.setLong(1, link.getCpathId());
            pstmt.setInt(2, link.getExternalDbId());
            pstmt.setString(3, link.getLinkedToId());
            ResultSet rs = pstmt.executeQuery();
            return (rs.next()) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    private ExternalLinkRecord createBean(ResultSet rs) throws SQLException,
            ClassNotFoundException {
        ExternalLinkRecord link = new ExternalLinkRecord();
        link.setId(rs.getInt("EXTERNAL_LINK_ID"));
        link.setCpathId(rs.getInt("CPATH_ID"));
        link.setExternalDbId(rs.getInt("EXTERNAL_DB_ID"));
        link.setLinkedToId(rs.getString("LINKED_TO_ID"));
        link.setCreateTime(rs.getTimestamp("CREATE_TIME"));
        link.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));

        //  Get Associated External Database Record
        DaoExternalDb table = new DaoExternalDb();
        ExternalDatabaseRecord db = table.getRecordById(link.getExternalDbId());
        link.setExternalDatabase(db);
        return link;
    }
}