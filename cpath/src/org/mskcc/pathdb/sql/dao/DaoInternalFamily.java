// $Id: DaoInternalFamily.java,v 1.6 2006-11-17 19:25:10 cerami Exp $
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

import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.model.CPathRecordType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Data Access Object to the internal family table.
 *
 * @author Ethan Cerami.
 */
public class DaoInternalFamily {
    private static final String INSERT_SQL =
        "INSERT INTO internal_family (`ANCESTOR_ID`, `ANCESTOR_TYPE`, "
        + "`DESCENDENT_ID`, `DESCENDENT_TYPE`) VALUES (?,?,?,?)";

    /**
     * Adds a new record.
     * @param ancestorId        ID of Ancestor record.
     * @param ancestorType      Record type of ancestor.
     * @param descendentId      ID of Descendent record.
     * @param descendentType    Record type of Descendent.
     * @throws DaoException     Database access error.
     */
    public void addRecord (long ancestorId, CPathRecordType ancestorType, long descendentId,
            CPathRecordType descendentType) throws DaoException {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement (INSERT_SQL);
            pstmt.setLong(1, ancestorId);
            pstmt.setString(2, ancestorType.toString());
            pstmt.setLong(3, descendentId);
            pstmt.setString(4, descendentType.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
    public lon
     * Gets all descendents of this ancestor.
     * @param ancestorId        ID of ancestor.
     * @return array of all descendent IDs.
     * @throws DaoException     Database access error.
     */
    public long[] getDescendentIds (long ancestorId)
        throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from internal_family where "
                            + "ANCESTOR_ID = ?");
            pstmt.setLong(1, ancestorId);
            return getDescendentIds(pstmt, rs, list);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all descendents of this ancestor, which are of type:
     * CPathRecordType.
     * @param ancestorId        ID of ancestor.
     * @param descendentType              CPathRecord Type of descendent.
     * @return array of all descendent IDs.
     * @throws DaoException     Database access error.
     */
    public long[] getDescendentIds (long ancestorId, CPathRecordType
            descendentType) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from internal_family where "
                            + "ANCESTOR_ID = ? AND DESCENDENT_TYPE = ?");
            pstmt.setLong(1, ancestorId);
            pstmt.setString(2, descendentType.toString());
            return getDescendentIds(pstmt, rs, list);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all ancestors of this record, which are of type:
     * CPathRecordType.
     * @param cPathId        ID of record.
     * @param ancestorType   CPathRecord Type of ancestor.
     * @return array of all ancestor IDs.
     * @throws DaoException     Database access error.
     */
    public long[] getAncestorIds (long cPathId, CPathRecordType ancestorType) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from internal_family where "
                            + "DESCENDENT_ID = ? AND ANCESTOR_TYPE = ?");
            pstmt.setLong(1, cPathId);
            pstmt.setString(2, ancestorType.toString());
            return getAncestorIds(pstmt, rs, list);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes all existing rrecords.
     *
     * @throws DaoException Error Accessing Database.
     */
    public void deleteAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("TRUNCATE TABLE internal_family");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    private long[] getDescendentIds(PreparedStatement pstmt, ResultSet rs, ArrayList list)
            throws SQLException {
        rs = pstmt.executeQuery();
        while (rs.next()) {
            long descendentId = rs.getLong("DESCENDENT_ID");
            list.add(new Long(descendentId));
        }
        long ids[] = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Long idLong = (Long) list.get(i);
            ids[i] = idLong.longValue();
        }
        return ids;
    }

    private long[] getAncestorIds(PreparedStatement pstmt, ResultSet rs, ArrayList list)
            throws SQLException {
        rs = pstmt.executeQuery();
        while (rs.next()) {
            long descendentId = rs.getLong("ANCESTOR_ID");
            list.add(new Long(descendentId));
        }
        long ids[] = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Long idLong = (Long) list.get(i);
            ids[i] = idLong.longValue();
        }
        return ids;
    }

}
