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
import org.mskcc.pathdb.model.IdMapRecord;
import org.mskcc.pathdb.model.CPathXRef;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.LinkedList;

import sun.misc.Queue;

/**
 * Data Access Object to the Id Map Table.
 *
 * @author Ethan Cerami
 */
public class DaoIdMap {

    /**
     * Adds New ID Map Record.
     * <P>
     * This method ensures that duplicate records are not stored to the
     * database.  Check the return value to determine if record was saved
     * successfully.  A true value indicates success.  A false value indicates
     * that the record already exists and was not saved, or an error occurred.
     *
     * @param record IdMapRecord Object.
     * @param validateRecord Validates DB1 and DB2 to ensure that these
     *        actually exist in the database.  When set to true, full validation
     *        check is run.  When set to false, no validation check is run.
     *        If code has already validaed DB1 and DB2 prior to this call,
     *        setting this parameter to false results in much faster execution.
     * @return true if saved successfully.
     * @throws DaoException Error Saving Data.
     */
    public boolean addRecord(IdMapRecord record, boolean validateRecord)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //  Validate the Incoming Database Ids
        if (validateRecord) {
            DaoExternalDb dao = new DaoExternalDb();
            ExternalDatabaseRecord dbRecord1 = dao.getRecordById
                    (record.getDb1());
            if (dbRecord1 == null) {
                throw new IllegalArgumentException
                        ("External Database, DB1:  "
                        + record.getDb1() + " does not exist in database.");
            }
            ExternalDatabaseRecord dbRecord2 = dao.getRecordById
                    (record.getDb1());
            if (dbRecord2 == null) {
                throw new IllegalArgumentException("External Database, DB2:  "
                        + record.getDb2() + " does not exist in database.");
            }
        }

        //  Validate the Incoming Ids
        String id1 = record.getId1();
        String id2 = record.getId2();
        if (id1 == null || id1.trim().length() ==  0) {
            throw new IllegalArgumentException ("ID1 is null or empty");
        }
        if (id2 == null || id2.trim().length() == 0) {
            throw new IllegalArgumentException ("ID2 is null or empty");
        }

        //  Validate that the record does not already exist in the database
        IdMapRecord dbRecord = getRecord(record);
        if (dbRecord == null) {
            try {
                //  Note that we store a hash code to enable very fast lookups.
                con = JdbcUtil.getCPathConnection();
                pstmt = con.prepareStatement
                        ("INSERT INTO id_map (`DB_1`, `ID_1`, "
                        + "`DB_2`, `ID_2`, `HASH_CODE`) VALUES (?,?,?,?,?)");
                pstmt.setInt(1, record.getDb1());
                pstmt.setString(2, record.getId1().trim());
                pstmt.setInt(3, record.getDb2());
                pstmt.setString(4, record.getId2().trim());
                pstmt.setInt(5, record.hashCode());
                int rows = pstmt.executeUpdate();
                return (rows > 0) ? true : false;
            } catch (ClassNotFoundException e) {
                throw new DaoException(e);
            } catch (SQLException e) {
                throw new DaoException(e);
            } finally {
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        } else {
            return false;
        }
    }

    /**
     * Gets ID Map Record specified by Primary ID.
     *
     * @param idMapId ID Map Primary ID.
     * @return IdMap Record
     * @throws DaoException Error Retrieving Data.
     */
    public IdMapRecord getRecordById(int idMapId)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM id_map WHERE ID_MAP_ID = ?");
            pstmt.setInt(1, idMapId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return createBean(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes Record specified by Primary ID.
     *
     * @param idMapId ID Map Primary ID.
     * @return true if deletion is successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(int idMapId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM id_map WHERE ID_MAP_ID = ?");
            pstmt.setInt(1, idMapId);
            int rows = pstmt.executeUpdate();
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
     * Gets the Record Specified by idRecord.
     * <P>
     * All ID Mappings are undirected.  Therefore the following are considered
     * equivalent:
     * <P>Affymetrix:155_s_at -- SwissProt: Q7272
     * <BR>SwissProt: Q7272 -- Affymetrix:155_s_at
     * <P>
     * Given an IdMapRecord, this method will therefore check for both
     * equivalent possibilities.  Either match will return a hit, and the
     * Data Access Object ensures that both options will never exist
     * simultaneously in the database.
     *
     * @param idRecord IdMapRecord Object.
     * @return IdMapRecord, if the record exists;  otherwise, null.
     * @throws DaoException Error Retrieving Data.
     */
    public IdMapRecord getRecord(IdMapRecord idRecord) throws DaoException {
        return getRecord(idRecord.hashCode());
    }

    /**
     * Deletes all IdMap Records.  Use with extreme caution!
     *
     * @return true indicates success.
     * @throws DaoException Error Deleting Records.
     */
    public boolean deleteAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("TRUNCATE table id_map;");
            int rows = pstmt.executeUpdate();
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
     * Given a XRef DB:ID pair, finds all equivalent XRef DB:ID pairs.
     *
     * Uses a bread-first search algorithm to determine complete set of
     * equivalent IDs.
     * <P>
     * Implementation note:  JUnit Test for this method is in
     * TestIdMappingsParser.java, not TestDaoIdMap.java.
     *
     *
     * @param xref  XRef Object
     * @return      ArrayList of XRef Objects.
     * @throws DaoException Error Connecting to Database.
     */
    public ArrayList getEquivalenceList (CPathXRef xref)
        throws DaoException {
        //  Represents List of Nodes to Visit
        LinkedList openQueue = new LinkedList();

        //  Represents List of Nodes Already Visited (prevents loops).
        ArrayList closedList = new ArrayList();

        //  Enqueue the First Xref
        openQueue.add(xref);

        //  While there are still nodes to visit
        while (openQueue.size() > 0) {

            //  Get the Next Item in the Queue
            CPathXRef current = (CPathXRef) openQueue.removeFirst();

            //  Get all Immediate Neighbors
            ArrayList neighbors = getImmediateNeighbors(current);

            //  Iterate through all neighbors;  only enqueue new nodes
            for (int i=0; i<neighbors.size(); i++) {
                CPathXRef neighbor = (CPathXRef) neighbors.get(i);
                if (! closedList.contains(neighbor)
                    && ! openQueue.contains(neighbor)) {
                    openQueue.add(neighbor);
                }
            }

            //  Add this node to the visited/closed list
            closedList.add(current);
        }
        //  Remove the Original XRef
        closedList.remove(xref);

        return closedList;
    }

    /**
     * Gets All Immediate Neighbors of the Specified DB:ID pair.
     *
     * @param xref  XRef Object.
     * @return      ArrayList of XRef Objects.
     * @throws DaoException Error Connecting to Database.
     */
    private ArrayList getImmediateNeighbors (CPathXRef xref)
            throws DaoException {
        ArrayList neighborList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM id_map WHERE (DB_1 = ? AND ID_1 = ?) "
                    + "OR (DB_2 = ? AND ID_2 = ?)");
            pstmt.setInt(1, xref.getDbId());
            pstmt.setString(2, xref.getLinkedToId());
            pstmt.setInt(3, xref.getDbId());
            pstmt.setString(4, xref.getLinkedToId());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                IdMapRecord idRecord = createBean(rs);
//                System.out.println("Running query:  "+ xref.toString()
//                    + " Gets:  " + idRecord.toString());
                CPathXRef neighbor = null;
                if (idRecord.getDb1() == xref.getDbId()
                        && idRecord.getId1().equals(xref.getLinkedToId())) {
                    neighbor = new CPathXRef (idRecord.getDb2(),
                            idRecord.getId2());
                } else {
                    neighbor = new CPathXRef(idRecord.getDb1(),
                            idRecord.getId1());
                }
                neighborList.add(neighbor);
            }
            return neighborList;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Record which the specified HashCode.  Using the hash code
     * enables very quick lookups.
     *
     * @param hashCode Integer HashCode.
     * @return IdMapRecord, if the record exists;  otherwise, null.
     * @throws DaoException Error Retrieving Data.
     */
    private IdMapRecord getRecord(int hashCode) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM id_map WHERE HASH_CODE = ?");
            pstmt.setInt(1, hashCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return createBean(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

    }

    /**
     * Creates an IdMapRecord Bean.
     */
    private IdMapRecord createBean(ResultSet rs) throws SQLException {
        IdMapRecord record = new IdMapRecord();
        record.setPrimaryId(rs.getInt("ID_MAP_ID"));
        record.setDb1(rs.getInt("DB_1"));
        record.setId1(rs.getString("ID_1"));
        record.setDb2(rs.getInt("DB_2"));
        record.setId2(rs.getString("ID_2"));
        return record;
    }
}