// $Id: DaoInternalLink.java,v 1.27 2008-03-28 02:26:42 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.TypeCount;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.action.BioPaxParentChild;

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
     *
     * @param sourceId cPath ID of Source.
     * @param targetId cPath ID of Target.
     * @return True if Internal Link was stored successfully.
     * @throws DaoException Error Connecting to Database.
     */
    public boolean addRecord(long sourceId, long targetId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO internal_link (`SOURCE_ID`,`TARGET_ID`)"
                            + " VALUES (?,?)");
            pstmt.setLong(1, sourceId);
            pstmt.setLong(2, targetId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Creates Internal Links between A and all B's.
     *
     * @param sourceId  cPath ID of Source.
     * @param targetIds Array of CPath IDs for Targets.
     * @return Number of New Internal Links Stored.
     * @throws DaoException Error Retrieving Data.
     */
    public int addRecords(long sourceId, long targetIds[]) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            //  Use batch mode for faster performance
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO internal_link (`SOURCE_ID`,`TARGET_ID`)"
                            + " VALUES (?,?)");
            for (int i = 0; i < targetIds.length; i++) {
                pstmt.setLong(1, sourceId);
                pstmt.setLong(2, targetIds[i]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            return targetIds.length;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all Targets Links from the specified source cPath ID.
     *
     * @param sourceId CPath ID.
     * @return ArrayList of CPath Records.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getTargetsWithLookUp(long sourceId)
            throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            ArrayList links = getTargets(sourceId);
            for (int i = 0; i < links.size(); i++) {
                DaoCPath dao = DaoCPath.getInstance();
                InternalLinkRecord link = (InternalLinkRecord) links.get(i);
                long targetId = link.getTargetId();
                CPathRecord record = dao.getRecordById(targetId);
                records.add(record);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all descendents of the specified cPath record.
     * <P>This is a potentially very slow query.
     *
     * @param cpathId CPath Record ID.
     * @return arraylist of descendent Ids.
     * @throws DaoException Database Access Error.
     */
    public ArrayList getAllDescendents(long cpathId)
            throws DaoException {
        ArrayList masterList = new ArrayList();
        traverseNode(cpathId, masterList);
        return masterList;
    }

    /**
     * Gets all Target Links from the specified source cPath ID.  For example, get all children
     * of a pathway element.
     *
     *
     * @param sourceId CPath ID of Source.
     * @return ArrayList of InternalLinkRecords.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getTargets(long sourceId) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT INTERNAL_LINK_ID, TARGET_ID FROM internal_link WHERE SOURCE_ID = ?");
            pstmt.setLong(1, sourceId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                long targetId = rs.getLong("TARGET_ID");
                long internalLinkId = rs.getLong("INTERNAL_LINK_ID");
                InternalLinkRecord link = new InternalLinkRecord (sourceId, targetId);
                link.setId(internalLinkId);
                records.add(link);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all children;  filters by taxonomy ID, external db source, and specific type.
     * Uses cursor to limit result set.  Used primarily to build paginated web pages.
     * @param cPathId cPath ID
     * @param ncbiTaxonomyId Organism filter. Set to -1 if there is no organism filter.
     * @param externalDbSnapshots Data source filter.
     * @param specificType specific type filter.
     * @param startIndex startIndex.
     * @param numRecords numRecords to retrieve.
     * @param xdebug XDebug object.
     * @return ArrayList of CPathRecord Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getChildren (long cPathId, int ncbiTaxonomyId, long externalDbSnapshots[],
        String specificType, int startIndex, int numRecords, XDebug xdebug) throws DaoException {
        //SELECT cpath.CPATH_ID, cpath.TYPE, cpath.SPECIFIC_TYPE FROM cpath, internal_link
        //WHERE cpath.NCBI_TAX_ID = 9606
        //AND (EXTERNAL_DB_SNAPSHOT_ID =1 OR EXTERNAL_DB_SNAPSHOT_ID=2)
        //AND cpath.SPECIFIC_TYPE='biochemicalReaction'
        //AND cpath.CPATH_ID = internal_link.TARGET_ID
        //AND internal_link.SOURCE_ID = 1
        //ORDER BY cpath.SPECIFIC_TYPE
        //LIMIT 0,20;
        ArrayList records = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();

            //  Set up SQL
            StringBuffer buf = new StringBuffer(
                "SELECT cpath.CPATH_ID, cpath.NAME, cpath.TYPE, cpath.SPECIFIC_TYPE"
                + " FROM cpath, internal_link\n");
            if (ncbiTaxonomyId >0) {
                buf.append ("WHERE cpath.NCBI_TAX_ID = " + ncbiTaxonomyId + "\n");
                buf.append ("AND (");
            } else {
                buf.append ("WHERE (");
            }
            for (int i=0; i<externalDbSnapshots.length; i++) {
                buf.append ("EXTERNAL_DB_SNAPSHOT_ID = " + externalDbSnapshots[i]);
                if (i < externalDbSnapshots.length - 1) {
                    buf.append (" OR ");
                }
            }
            buf.append (")\n");
            buf.append ("AND cpath.SPECIFIC_TYPE = '" + specificType + "'\n");
            buf.append ("AND cpath.CPATH_ID = internal_link.TARGET_ID\n");
            buf.append ("AND internal_link.SOURCE_ID = " + cPathId +"\n");
            buf.append ("ORDER BY cpath.CPATH_ID\n");
            buf.append ("LIMIT "+ startIndex + "," + numRecords);

            //  Create Prepared Statement
            pstmt = con.prepareStatement (buf.toString());
            xdebug.logMsg(this, "Using SQL Statement:  " + buf.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                long childId = rs.getLong("cpath.CPATH_ID");
                String childName = rs.getString("cpath.NAME");
                String childType = rs.getString("cpath.TYPE");
                String childSpecificType = rs.getString("cpath.SPECIFIC_TYPE");
                CPathRecord record = new CPathRecord();
                record.setId(childId);
                record.setName(childName);
                record.setType(CPathRecordType.getType(childType));
                record.setSpecType(childSpecificType);
                records.add(record);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all children
     * Uses cursor to limit result set.  Used primarily to build paginated web pages.
     * @param cPathId cPath ID
     * @param startIndex startIndex.
     * @param numRecords numRecords to retrieve.
     * @param xdebug XDebug object.
     * @return ArrayList of CPathRecord Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getChildren (long cPathId, int startIndex,
            int numRecords, XDebug xdebug) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();

            //  Set up SQL
            StringBuffer buf = new StringBuffer(
                "SELECT cpath.CPATH_ID, cpath.NAME, cpath.TYPE, cpath.SPECIFIC_TYPE"
                + " FROM cpath, internal_link\n");
            buf.append ("WHERE cpath.CPATH_ID = internal_link.TARGET_ID\n");
            buf.append ("AND internal_link.SOURCE_ID = " + cPathId +"\n");
            buf.append ("ORDER BY cpath.CPATH_ID\n");
            buf.append ("LIMIT "+ startIndex + "," + numRecords);

            //  Create Prepared Statement
            pstmt = con.prepareStatement (buf.toString());
            xdebug.logMsg(this, "Using SQL Statement:  " + buf.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                long childId = rs.getLong("cpath.CPATH_ID");
                String childName = rs.getString("cpath.NAME");
                String childType = rs.getString("cpath.TYPE");
                String childSpecificType = rs.getString("cpath.SPECIFIC_TYPE");
                CPathRecord record = new CPathRecord();
                record.setId(childId);
                record.setName(childName);
                record.setType(CPathRecordType.getType(childType));
                record.setSpecType(childSpecificType);
                records.add(record);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all parents;  filters by taxonomy ID, external db source, and specific type.
     * Uses cursor to limit result set.  Used primarily to build paginated web pages.
     * @param cPathId cPath ID
     * @param ncbiTaxonomyId Organism filter. Set to -1 if there is no organism filter.
     * @param externalDbSnapshots Data source filter.
     * @param specificType specific type filter.
     * @param startIndex startIndex.
     * @param numRecords numRecords to retrieve.
     * @param xdebug XDebug object.
     * @return ArrayList of CPathRecord Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getParents (long cPathId, int ncbiTaxonomyId, long externalDbSnapshots[],
        String specificType, int startIndex, int numRecords, XDebug xdebug) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();

            //  Set up SQL
            StringBuffer buf = new StringBuffer(
                "SELECT cpath.CPATH_ID, cpath.NAME, cpath.TYPE, cpath.SPECIFIC_TYPE"
                + " FROM cpath, internal_link\n");
            if (ncbiTaxonomyId >0) {
                buf.append ("WHERE cpath.NCBI_TAX_ID = " + ncbiTaxonomyId + "\n");
                buf.append ("AND (");
            } else {
                buf.append ("WHERE (");
            }
            for (int i=0; i<externalDbSnapshots.length; i++) {
                buf.append ("EXTERNAL_DB_SNAPSHOT_ID = " + externalDbSnapshots[i]);
                if (i < externalDbSnapshots.length - 1) {
                    buf.append (" OR ");
                }
            }
            buf.append (")\n");
            buf.append ("AND cpath.SPECIFIC_TYPE = '" + specificType + "'\n");
            buf.append ("AND cpath.CPATH_ID = internal_link.SOURCE_ID\n");
            buf.append ("AND internal_link.TARGET_ID = " + cPathId +"\n");
            buf.append ("ORDER BY cpath.CPATH_ID\n");
            buf.append ("LIMIT "+ startIndex + "," + numRecords);

            //  Create Prepared Statement
            pstmt = con.prepareStatement (buf.toString());
            xdebug.logMsg(this, "Using SQL Statement:  " + buf.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                long childId = rs.getLong("cpath.CPATH_ID");
                String childName = rs.getString("cpath.NAME");
                String childType = rs.getString("cpath.TYPE");
                String childSpecificType = rs.getString("cpath.SPECIFIC_TYPE");
                CPathRecord record = new CPathRecord();
                record.setId(childId);
                record.setName(childName);
                record.setType(CPathRecordType.getType(childType));
                record.setSpecType(childSpecificType);
                records.add(record);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets specific types (with counts) for all children elements.
     * @param cPathId   cPath ID.
     * @param ncbiTaxonomyId Organism filter. Set to -1 if there is no organism filter.
     * @param externalDbSnapshots data source filter.  Set to null if there is no data source filter
     * @param xdebug  XDebug.
     * @return ArrayList of TypeCount Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getChildrenTypes (long cPathId, int ncbiTaxonomyId, long externalDbSnapshots[],
        XDebug xdebug) throws DaoException {
        //SELECT cpath.SPECIFIC_TYPE, count(*) FROM cpath, internal_link
        //WHERE cpath.NCBI_TAX_ID = -9999
        //AND (EXTERNAL_DB_SNAPSHOT_ID = 1)
        //AND cpath.CPATH_ID = internal_link.TARGET_ID
        //AND internal_link.SOURCE_ID = 5
        //GROUP BY cpath.SPECIFIC_TYPE
        //ORDER BY cpath.SPECIFIC_TYPE
        ArrayList records = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();

            //  Set up SQL
            StringBuffer buf = new StringBuffer(
                "SELECT cpath.SPECIFIC_TYPE, count(*) as COUNT FROM cpath, internal_link\n");

            if (ncbiTaxonomyId >0) {
                buf.append ("WHERE cpath.NCBI_TAX_ID = " + ncbiTaxonomyId + "\n");
                buf.append ("AND (");
            } else {
                buf.append ("WHERE (");
            }
            for (int i=0; i<externalDbSnapshots.length; i++) {
                buf.append ("EXTERNAL_DB_SNAPSHOT_ID = " + externalDbSnapshots[i]);
                if (i < externalDbSnapshots.length - 1) {
                    buf.append (" OR ");
                }
            }
            buf.append (")\n");
            buf.append ("AND cpath.CPATH_ID = internal_link.TARGET_ID\n");
            buf.append ("AND internal_link.SOURCE_ID = " + cPathId +"\n");
            buf.append ("GROUP BY cpath.SPECIFIC_TYPE\n");
            buf.append ("ORDER BY cpath.SPECIFIC_TYPE\n");

            //  Create Prepared Statement
            pstmt = con.prepareStatement (buf.toString());
            xdebug.logMsg(this, "Using SQL Statement:  " + buf.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String childSpecificType = rs.getString("cpath.SPECIFIC_TYPE");
                int childCount = rs.getInt("COUNT");
                TypeCount typeCount = new TypeCount(BioPaxParentChild.GET_CHILDREN);
                typeCount.setType(childSpecificType);
                typeCount.setCount(childCount);
                records.add(typeCount);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets specific types (with counts) for all children elements.
     * @param cPathId   cPath ID.
     * @param xdebug  XDebug.
     * @return ArrayList of TypeCount Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getChildrenTypes (long cPathId, XDebug xdebug) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();

            //  Set up SQL
            StringBuffer buf = new StringBuffer(
                "SELECT cpath.SPECIFIC_TYPE, count(*) as COUNT FROM cpath, internal_link\n");
            buf.append ("WHERE cpath.CPATH_ID = internal_link.TARGET_ID\n");
            buf.append ("AND internal_link.SOURCE_ID = " + cPathId +"\n");
            buf.append ("GROUP BY cpath.SPECIFIC_TYPE\n");
            buf.append ("ORDER BY cpath.SPECIFIC_TYPE\n");

            //  Create Prepared Statement
            pstmt = con.prepareStatement (buf.toString());
			if (xdebug != null) {
				xdebug.logMsg(this, "Using SQL Statement:  " + buf.toString());
			}

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String childSpecificType = rs.getString("cpath.SPECIFIC_TYPE");
                int childCount = rs.getInt("COUNT");
                TypeCount typeCount = new TypeCount(BioPaxParentChild.GET_CHILDREN);
                typeCount.setType(childSpecificType);
                typeCount.setCount(childCount);
                records.add(typeCount);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets specific types (with counts) for all parent elements.
     * @param cPathId   cPath ID.
     * @param ncbiTaxonomyId Organism filter. Set to -1 if there is no organism filter.
     * @param externalDbSnapshots data source filter.
     * @param xdebug  XDebug.
     * @return ArrayList of TypeCount Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getParentTypes (long cPathId, int ncbiTaxonomyId, long externalDbSnapshots[],
        XDebug xdebug) throws DaoException {
		return getParentTypes(cPathId, ncbiTaxonomyId, externalDbSnapshots, null, xdebug);
	}

    /**
     * Gets specific types (with counts) for all parent elements.
     * @param cPathId   cPath ID.
     * @param ncbiTaxonomyId Organism filter. Set to -1 if there is no organism filter.
     * @param externalDbSnapshots data source filter.
     * @param parentSpecificType parent - specific type filter.
     * @param xdebug  XDebug.
     * @return ArrayList of TypeCount Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getParentTypes (long cPathId, int ncbiTaxonomyId, long externalDbSnapshots[],
									 String parentSpecificType, XDebug xdebug) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();

            //  Set up SQL
            StringBuffer buf = new StringBuffer(
                "SELECT cpath.SPECIFIC_TYPE, count(*) as COUNT FROM cpath, internal_link\n");

            if (ncbiTaxonomyId >0) {
                buf.append ("WHERE cpath.NCBI_TAX_ID = " + ncbiTaxonomyId + "\n");
                buf.append ("AND (");
            } else {
                buf.append ("WHERE (");
            }
            for (int i=0; i<externalDbSnapshots.length; i++) {
                buf.append ("EXTERNAL_DB_SNAPSHOT_ID = " + externalDbSnapshots[i]);
                if (i < externalDbSnapshots.length - 1) {
                    buf.append (" OR ");
                }
            }
            buf.append (")\n");
            buf.append ("AND cpath.CPATH_ID = internal_link.SOURCE_ID\n");
            buf.append ("AND internal_link.TARGET_ID = " + cPathId +"\n");
			if (parentSpecificType != null && parentSpecificType.length() > 0) {
				buf.append ("AND cpath.SPECIFIC_TYPE = '" + parentSpecificType + "'\n");
			}
            buf.append ("GROUP BY cpath.SPECIFIC_TYPE\n");
            buf.append ("ORDER BY cpath.SPECIFIC_TYPE\n");

            //  Create Prepared Statement
            pstmt = con.prepareStatement (buf.toString());
            xdebug.logMsg(this, "Using SQL Statement:  " + buf.toString());
            System.out.println(buf.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String childSpecificType = rs.getString("cpath.SPECIFIC_TYPE");
                int childCount = rs.getInt("COUNT");
                TypeCount typeCount = new TypeCount(BioPaxParentChild.GET_PARENTS);
                typeCount.setType(childSpecificType);
                typeCount.setCount(childCount);
                records.add(typeCount);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets All Source Links that point to the specified cPath ID.  For example, get all parents
     * of a physical entity element.
     *
     * @param targetId CPath ID.
     * @return ArrayList of InternalLinkRecords.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getSources(long targetId) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM internal_link WHERE TARGET_ID = ?"
                            + " ORDER BY INTERNAL_LINK_ID");
            pstmt.setLong(1, targetId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                long sourceId = rs.getLong("SOURCE_ID");
                long internalLinkId = rs.getLong("INTERNAL_LINK_ID");
                InternalLinkRecord link = new InternalLinkRecord
                        (sourceId, targetId);
                link.setId(internalLinkId);
                records.add(link);
            }
            return records;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes all Internal Links associated with the specified cpathId.
     *
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
            ArrayList links = getTargets(cpathId);
            for (int i = 0; i < links.size(); i++) {
                InternalLinkRecord link = (InternalLinkRecord) links.get(i);
                pstmt = con.prepareStatement
                        ("DELETE FROM internal_link WHERE "
                                + "INTERNAL_LINK_ID = ?");
                pstmt.setLong(1, link.getId());
                counter += pstmt.executeUpdate();
            }
            return counter;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    private void traverseNode(long sourceId, ArrayList masterList)
            throws DaoException {
        ArrayList childrenList = getTargets(sourceId);
        if (childrenList != null && childrenList.size() > 0) {
            for (int i = 0; i < childrenList.size(); i++) {
                InternalLinkRecord link = (InternalLinkRecord)
                        childrenList.get(i);
                Long childId = new Long(link.getTargetId());
                if (!masterList.contains(childId)) {
                    masterList.add(childId);
                    traverseNode(link.getTargetId(), masterList);
                }
            }
        }
    }
}
