package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
     * @throws DaoException Error Connecting to Database.
     */
    public boolean addRecord(long cpathIdA, long cpathIdB) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO INTERNAL_LINK (`CPATH_ID_A`,`CPATH_ID_B`)"
                    + " VALUES (?,?)");
            pstmt.setLong(1, cpathIdA);
            pstmt.setLong(2, cpathIdB);
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
     * Creates Internal Links between A and all B's.
     * @param cpathIdA cPath ID of Entity A.
     * @param cpathIdsB Array of CPath IDs for Entity B.
     * @return Number of New Internal Links Stored.
     * @throws DaoException Error Retrieving Data.
     */
    public int addRecords(long cpathIdA, long cpathIdsB[]) throws DaoException {
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
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getInternalLinksWithLookup(long cpathId)
            throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();

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
     * Gets all Internal Links for Specified cPath ID.
     * @param cpathId CPath ID.
     * @return ArrayList of InternalLinkRecords.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getInternalLinks(long cpathId) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM INTERNAL_LINK WHERE CPATH_ID_A = ?"
                    + " OR CPATH_ID_B = ? ORDER BY INTERNAL_LINK_ID");
            pstmt.setLong(1, cpathId);
            pstmt.setLong(2, cpathId);
            rs = pstmt.executeQuery();
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
     * Deletes all Internal Links associated with the specified cpathId.
     * @param cpathId cPath ID of record to delete.
     * @return returns number of internal links deleted.
     * @throws DaoException Error Retrieving Data.
     */
    public int deleteRecordsByCPathId(long cpathId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            int counter = 0;
            ArrayList links = getInternalLinks(cpathId);
            for (int i = 0; i < links.size(); i++) {
                InternalLinkRecord link = (InternalLinkRecord) links.get(i);
                pstmt = con.prepareStatement
                        ("DELETE FROM INTERNAL_LINK WHERE "
                        + "INTERNAL_LINK_ID = ?");
                pstmt.setLong(1, link.getId());
                counter += pstmt.executeUpdate();
            }
            return counter;
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