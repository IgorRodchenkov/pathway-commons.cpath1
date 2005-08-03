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
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
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
        int counter = 0;
        for (int i = 0; i < targetIds.length; i++) {
            boolean flag = this.addRecord(sourceId, targetIds[i]);
            if (flag) {
                counter++;
            }
        }
        return counter;
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
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all Target Links from the specified source cPath ID.
     *
     * @param sourceId CPath ID of Source.
     * @return ArrayList of InternalLinkRecords.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getTargets(long sourceId) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM internal_link WHERE SOURCE_ID = ?"
                    + " ORDER BY INTERNAL_LINK_ID");
            pstmt.setLong(1, sourceId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                long targetId = rs.getLong("TARGET_ID");
                long internalLinkId = rs.getLong("INTERNAL_LINK_ID");
                InternalLinkRecord link = new InternalLinkRecord
                        (sourceId, targetId);
                link.setId(internalLinkId);
                records.add(link);
            }
            return records;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets All Source Links that point to the specified cPath ID.
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
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
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
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}