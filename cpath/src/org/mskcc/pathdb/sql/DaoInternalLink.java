package org.mskcc.pathdb.sql;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the Internal Link Table.
 *
 * @author Ethan Cerami.
 */
public class DaoInternalLink {

    /**
     * Creates an Internal Link between A and B.
     * @param cpathIdA cPath ID of Entity A.
     * @param cpathIdB cPath ID of Entity B.
     * @return True if Internal Links was stored successfully.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public boolean addRecord(long cpathIdA, long cpathIdB)
            throws SQLException, ClassNotFoundException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement
                    ("INSERT INTO INTERNAL_LINK (`CPATH_ID_A`,`CPATH_ID_B`)"
                    + " VALUES (?,?)");
            pstmt.setLong(1, cpathIdA);
            pstmt.setLong(2, cpathIdB);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con, pstmt, null);
        }
    }

    /**
     * Creates Internal Links between A and all B's.
     * @param cpathIdA cPath ID of Entity A.
     * @param cpathIdsB Array of CPath IDs for Entity B.
     * @return Number of New Internal Links Stored.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public int addRecords(long cpathIdA, long cpathIdsB[])
            throws SQLException, ClassNotFoundException {
        int counter = 0;
        for (int i = 0; i < cpathIdsB.length; i++) {
            boolean flag = this.addRecord(cpathIdA, cpathIdsB[i]);
            if (flag) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Gets all Internal Links for Specified cPath ID.
     * @param cpathId CPath ID.
     * @return ArrayList of CPath Records.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public ArrayList getInternalLinksWithLookup(long cpathId)
            throws ClassNotFoundException, SQLException {
        ArrayList records = new ArrayList();
        ArrayList links = getInternalLinks(cpathId);
        for (int i = 0; i < links.size(); i++) {
            DaoCPath dao = new DaoCPath();
            InternalLinkRecord link = (InternalLinkRecord) links.get(i);
            long cpathIdA = link.getCpathIdA();
            long cpathIdB = link.getCpathIdB();
            long cpathNew = (cpathId == cpathIdA) ? cpathIdB : cpathIdA;
            CPathRecord record = dao.getRecordById(cpathNew);
            records.add(record);
        }
        return records;
    }

    /**
     * Gets all Internal Links for Specified cPath ID.
     * @param cpathId CPath ID.
     * @return ArrayList of InternalLinkRecords.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     */
    public ArrayList getInternalLinks(long cpathId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        ArrayList records = new ArrayList();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM INTERNAL_LINK WHERE CPATH_ID_A = ?"
                    + " OR CPATH_ID_B = ?");
            pstmt.setLong(1, cpathId);
            pstmt.setLong(2, cpathId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                long cpathA = rs.getLong("CPATH_ID_A");
                long cpathB = rs.getLong("CPATH_ID_B");
                long internalLinkId = rs.getLong("INTERNAL_LINK_ID");
                InternalLinkRecord link = new InternalLinkRecord
                        (cpathA, cpathB);
                link.setId(internalLinkId);
                records.add(link);
            }
            return records;
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }


    /**
     * Deletes all Internal Links associated with the specified cpathId.
     * @param cpathId cPath ID of record to delete.
     * @return returns number of internal links deleted.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public int deleteRecordsByCPathId(long cpathId)
            throws SQLException, ClassNotFoundException {
        int counter = 0;
        Connection con = JdbcUtil.getCPathConnection();
        try {
            ArrayList links = getInternalLinks(cpathId);
            for (int i = 0; i < links.size(); i++) {
                InternalLinkRecord link = (InternalLinkRecord) links.get(i);
                PreparedStatement pstmt = con.prepareStatement
                        ("DELETE FROM INTERNAL_LINK WHERE "
                        + "INTERNAL_LINK_ID = ?");
                pstmt.setLong(1, link.getId());
                counter += pstmt.executeUpdate();
            }
            return counter;
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }
}