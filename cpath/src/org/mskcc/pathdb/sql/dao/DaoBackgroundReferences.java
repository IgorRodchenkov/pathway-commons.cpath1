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

import org.mskcc.pathdb.model.BackgroundReference;
import org.mskcc.pathdb.model.BackgroundReferencePair;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Data Access Object to the Background References table.
 *
 * @author Ethan Cerami
 */
public class DaoBackgroundReferences {
    protected String tableName = "background_reference";

    /**
     * Adds New Background Reference Pair Record.
     * <P>
     * This method ensures that duplicate records are not stored to the
     * database.  Check the return value to determine if record was saved
     * successfully.  A true value indicates success.  A false value indicates
     * that the record already exists and was not saved, or an error occurred.
     *
     * @param pair           BackgroundReference Object.
     * @param validateRecord Validates DB1 and DB2 to ensure that these
     *                       actually exist in the database.  When set to true,
     *                       full validation check is run.  When set to false,
     *                       no validation check is run.  If code has already
     *                       validated DB1 and DB2 prior to this call, setting
     *                       this parameter to false results in much faster
     *                       execution.
     * @return true if saved successfully.
     * @throws DaoException Error Saving Data.
     */
    public boolean addRecord(BackgroundReferencePair pair,
            boolean validateRecord) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //  Validate the Incoming Database Ids
        if (validateRecord) {
            DaoExternalDb dao = new DaoExternalDb();
            ExternalDatabaseRecord dbRecord1 = dao.getRecordById
                    (pair.getDbId1());
            if (dbRecord1 == null) {
                throw new IllegalArgumentException
                        ("External Database, DB1:  "
                        + pair.getDbId1() + " does not exist in database.");
            }
            ExternalDatabaseRecord dbRecord2 = dao.getRecordById
                    (pair.getDbId2());
            if (dbRecord2 == null) {
                throw new IllegalArgumentException("External Database, DB2:  "
                        + pair.getDbId2() + " does not exist in database.");
            }

            //  Validate the Reference Type
            //  If this is an PROTEIN_UNIFICATION Record, both databases
            //  must be of type PROTEIN_UNIFICATION.
            if (pair.getReferenceType().equals
                    (ReferenceType.PROTEIN_UNIFICATION)) {
                if (!(dbRecord1.getDbType().equals
                        (ReferenceType.PROTEIN_UNIFICATION)
                        && dbRecord2.getDbType().equals
                        (ReferenceType.PROTEIN_UNIFICATION))) {
                    throw new IllegalArgumentException("This is a "
                            + "PROTEIN_UNIFICATION record.  "
                            + "However, one of the specified "
                            + "databases is not of type:  PROTEIN_UNIFICATION."
                            + "  You have specified database 1:  "
                            + dbRecord1.getDbType()
                            + ", database 2:  " + dbRecord2.getDbType());
                }
            }

            //  If this is a LINK_OUT Record, first database must be of
            //  type:  PROTEIN_UNIFICATION, and second must be of type:
            //  LINK_OUT.
            if (pair.getReferenceType().equals(ReferenceType.LINK_OUT)) {
                if (!(dbRecord1.getDbType().equals
                        (ReferenceType.PROTEIN_UNIFICATION)
                        && dbRecord2.getDbType().equals
                        (ReferenceType.LINK_OUT))) {
                    throw new IllegalArgumentException("This is a "
                            + "LINK_OUT record.  The first database must"
                            + "be of type:  PROTEIN_UNIFICATION, and the "
                            + "second must be of type:  LINK_OUT."
                            + "  You have specified database 1:  "
                            + dbRecord1.getDbType()
                            + ", database 2:  " + dbRecord2.getDbType());
                }
            }
        }

        //  Validate the Incoming Ids
        String id1 = pair.getLinkedToId1();
        String id2 = pair.getLinkedToId2();
        if (id1 == null || id1.trim().length() == 0) {
            throw new IllegalArgumentException("ID1 is null or empty");
        }
        if (id2 == null || id2.trim().length() == 0) {
            throw new IllegalArgumentException("ID2 is null or empty");
        }

        //  Validate that the record does not already exist in the database
        BackgroundReferencePair dbPair = getRecord(pair);
        if (dbPair == null) {
            try {
                //  Note that we store a hash code to enable fast lookups.
                con = JdbcUtil.getCPathConnection();
                pstmt = con.prepareStatement
                        ("INSERT INTO " + tableName + " (`DB_1`, `ID_1`, "
                        + "`DB_2`, `ID_2`, `REFERENCE_TYPE`, `HASH_CODE`) "
                        + " VALUES (?,?,?,?,?,?)");
                pstmt.setInt(1, pair.getDbId1());
                pstmt.setString(2, pair.getLinkedToId1().trim());
                pstmt.setInt(3, pair.getDbId2());
                pstmt.setString(4, pair.getLinkedToId2().trim());
                pstmt.setString(5, pair.getReferenceType().toString());
                pstmt.setInt(6, pair.hashCode());
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
     * Gets Background Reference Pair Record specified by Primary ID.
     *
     * @param identityId Identity Primary ID.
     * @return BackgroundReference Record
     * @throws DaoException Error Retrieving Data.
     */
    public BackgroundReferencePair getRecordById(int identityId)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM " + tableName
                    + " WHERE BACKGROUND_REFERENCE_ID = ?");
            pstmt.setInt(1, identityId);
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
     * @param primaryId BackgroundReference Record Primary ID.
     * @return true if deletion is successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(int primaryId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM " + tableName
                    + " WHERE BACKGROUND_REFERENCE_ID = ?");
            pstmt.setInt(1, primaryId);
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
     * Gets the Record Specified by refRecord.
     * <P>
     * All Background References are undirected.  Therefore the following
     * are considered equivalent:
     * <P>Affymetrix:155_s_at -- SwissProt: Q7272
     * <BR>SwissProt: Q7272 -- Affymetrix:155_s_at
     * <P>
     * Given an Identity record, this method will therefore check for both
     * equivalent possibilities.  Either match will return a hit, and the
     * Data Access Object ensures that both options will never exist
     * simultaneously in the database.
     *
     * @param pair BackgroundReferenceRecord Object.
     * @return IdMapRecord, if the record exists;  otherwise, null.
     * @throws DaoException Error Retrieving Data.
     */
    public BackgroundReferencePair getRecord(BackgroundReferencePair
            pair) throws DaoException {
        return getRecord(pair.hashCode());
    }

    /**
     * Deletes all BackgroundReference Records.  Use with extreme caution!
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
                    ("TRUNCATE table " + tableName);
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
     * Finds all references which are equivalent to the specified Background
     * Reference object.
     * <p/>
     * Uses a bread-first search algorithm to determine complete set of
     * equivalent IDs.
     * <P>
     * Implementation note:  JUnit Test for this method is in
     * TestIdMappingsParser.java, not TestDaoIdMap.java.
     *
     * @param xref BackgroundReference Object.
     * @return ArrayList of BackgroundReference Objects.
     * @throws DaoException Error Connecting to Database.
     */
    public ArrayList getEquivalenceList(BackgroundReference xref)
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
            BackgroundReference current = (BackgroundReference) openQueue.removeFirst();

            //  Get all Immediate Neighbors
            ArrayList neighbors = getImmediateNeighbors(current);

            //  Iterate through all neighbors;  only enqueue new nodes
            for (int i = 0; i < neighbors.size(); i++) {
                BackgroundReference neighbor = (BackgroundReference) neighbors.get(i);
                if (!closedList.contains(neighbor)
                        && !openQueue.contains(neighbor)) {
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
     * Gets a complete list of link out references for the specified
     * BackgroundReference record.
     *
     * @param xref XRef Object
     * @return ArrayList of XRef Objects.
     * @throws DaoException Error Connecting to Database.
     */
    public ArrayList getLinkOutList(BackgroundReference xref)
            throws DaoException {
        ArrayList neighborList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM " + tableName + " WHERE "
                    + "(DB_1 = ? AND ID_1 = ? AND REFERENCE_TYPE = ?)");
            pstmt.setInt(1, xref.getDbId1());
            pstmt.setString(2, xref.getLinkedToId1());
            pstmt.setString(3, ReferenceType.LINK_OUT.toString());
            rs = pstmt.executeQuery();
            processResultSet(rs, xref, neighborList);
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
     * Gets All Immediate Neighbors of the Specified Background
     * Reference Object.
     *
     * @param xref XRef Object.
     * @return ArrayList of XRef Objects.
     * @throws DaoException Error Connecting to Database.
     */
    private ArrayList getImmediateNeighbors(BackgroundReference xref)
            throws DaoException {
        ArrayList neighborList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();

            //  Issue two separate queries instead of one logical OR query
            //  Why do we do this?  Because we have indexes on DB1:ID1,
            //  and DB2:ID2, but cannot create an index on all four values.
            //  By using the two queries, we get *much* faster performance.

            //  Issue and Process First Query
            pstmt = con.prepareStatement
                    ("SELECT * FROM " + tableName + " WHERE "
                    + "(DB_1 = ? AND ID_1 = ? AND REFERENCE_TYPE = ?)");
            pstmt.setInt(1, xref.getDbId1());
            pstmt.setString(2, xref.getLinkedToId1());
            pstmt.setString(3, ReferenceType.PROTEIN_UNIFICATION.toString());
            rs = pstmt.executeQuery();
            processResultSet(rs, xref, neighborList);

            //  Issue and Process Second Query
            pstmt = con.prepareStatement
                    ("SELECT * FROM " + tableName
                    + " WHERE (DB_2 = ? AND ID_2 = ? AND REFERENCE_TYPE = ?)");
            pstmt.setInt(1, xref.getDbId1());
            pstmt.setString(2, xref.getLinkedToId1());
            pstmt.setString(3, ReferenceType.PROTEIN_UNIFICATION.toString());
            rs = pstmt.executeQuery();
            processResultSet(rs, xref, neighborList);

            if (neighborList.size() > 100) {
                System.err.println("Warning!  Got "
                        + neighborList.size() + " hits");
                System.err.println("Quering for:  " + xref.toString());
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

    private void processResultSet(ResultSet rs, BackgroundReference xref,
            ArrayList neighborList) throws SQLException {
        while (rs.next()) {
            BackgroundReferencePair refPair = createBean(rs);
            BackgroundReference neighbor = null;
            if (refPair.getDbId1() == xref.getDbId1()
                    && refPair.getLinkedToId1().equals(xref.getLinkedToId1())) {
                neighbor = new BackgroundReference(refPair.getDbId2(),
                        refPair.getLinkedToId2());
            } else {
                neighbor = new BackgroundReference(refPair.getDbId1(),
                        refPair.getLinkedToId1());
            }
            neighborList.add(neighbor);
        }
    }

    /**
     * Gets Record which the specified HashCode.  Using the hash code
     * enables very quick lookups.
     *
     * @param hashCode Integer HashCode.
     * @return IdentityRecord, if the record exists;  otherwise, null.
     * @throws DaoException Error Retrieving Data.
     */
    private BackgroundReferencePair getRecord(int hashCode)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM " + tableName + " WHERE HASH_CODE = ?");
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
     * Creates an BackgroundReference Bean.
     */
    private BackgroundReferencePair createBean(ResultSet rs) throws SQLException {
        BackgroundReferencePair pair = new BackgroundReferencePair();
        pair.setPrimaryId(rs.getInt("BACKGROUND_REFERENCE_ID"));
        pair.setDbId1(rs.getInt("DB_1"));
        pair.setLinkedToId1(rs.getString("ID_1"));
        pair.setDbId2(rs.getInt("DB_2"));
        pair.setLinkedToId2(rs.getString("ID_2"));
        pair.setReferenceType(ReferenceType.getType
                (rs.getString("REFERENCE_TYPE")));
        return pair;
    }
}