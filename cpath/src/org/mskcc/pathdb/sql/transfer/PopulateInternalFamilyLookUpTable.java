package org.mskcc.pathdb.sql.transfer;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populates the Internal Family Look up table for all pathways
 * in the database.
 *
 * @author Ethan Cerami.
 */
public class PopulateInternalFamilyLookUpTable {
    private static final String GET_RECORD_ITERATOR =
        "select `CPATH_ID`, `TYPE` from cpath WHERE TYPE = ? LIMIT ?,1";
    private static final String GET_RECORD_BY_ID =
        "select `CPATH_ID`, `TYPE` from cpath WHERE CPATH_ID = ?";

    private PreparedStatement pstmt1;
    private PreparedStatement pstmt2;
    private ProgressMonitor pMonitor;

    /**
     * Constructor.
     * @param pMonitor  ProgressMonitor Object.
     */
    public PopulateInternalFamilyLookUpTable (ProgressMonitor pMonitor) {
        this.pMonitor = pMonitor;
    }

    /**
     * Executes task.
     * @throws DaoException Data access error.
     */
    public void execute() throws DaoException {
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoInternalFamily daoFamily = new DaoInternalFamily();

        //  Start fresh:  Delete all existing internal family records
        daoFamily.deleteAllRecords();

        //  Determine # pathways
        int numPathways = daoCPath.getNumEntities(CPathRecordType.PATHWAY);
        pMonitor.setMaxValue(numPathways);

        //  Index each pathway
        for (int i = 0; i < numPathways; i++) {
            pMonitor.incrementCurValue();
            ConsoleUtil.showProgress(pMonitor);
            CPathRecord record = getRecordIdAtOffset(CPathRecordType.PATHWAY, i);
            ArrayList idList = getAllDescendents (record.getId());
            Iterator iterator = idList.iterator();

            while (iterator.hasNext()) {
                Long descendentId = (Long) iterator.next();
                CPathRecord descendentRecord = getRecordById(descendentId);

                //  Only index physical entities
                if (descendentRecord.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                    daoFamily.addRecord(record.getId(), record.getType(),
                        descendentRecord.getId(), descendentRecord.getType());
                }
            }
        }
    }

    /**
     * Gets records at offset.  Optimized SQL (does not retrieve XML content).
     */
    private CPathRecord getRecordIdAtOffset(CPathRecordType recordType, int offset)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = getPreparedStatement1 (con);
            pstmt.setString(1, recordType.toString());
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractRecord(rs);
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(rs);
        }
        return null;
    }

    /**
     * Gets record by ID.  Optimized SQL (does not retrieve XML content).
     */
    private CPathRecord getRecordById (long cPathId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = getPreparedStatement2 (con);
            pstmt.setLong(1, cPathId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractRecord(rs);
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(rs);
        }
        return null;
    }

    private CPathRecord extractRecord(ResultSet rs) throws SQLException {
        CPathRecord record = new CPathRecord();
        record.setId(rs.getLong("CPATH_ID"));
        record.setType(CPathRecordType.getType(rs.getString("TYPE")));
        return record;
    }

    protected void localCloseAll(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
        }
    }

    private PreparedStatement getPreparedStatement1(Connection con) throws SQLException {
        //  Re-use prepared statement
        if (pstmt1 == null) {
            pstmt1 = con.prepareStatement (GET_RECORD_ITERATOR);
        }
        return pstmt1;
    }

    private PreparedStatement getPreparedStatement2(Connection con) throws SQLException {
        //  Re-use prepared statement
        if (pstmt2 == null) {
            pstmt2 = con.prepareStatement (GET_RECORD_BY_ID);
        }
        return pstmt2;
    }

    /**
     * Gets all descendents of the specified cPath record.
     * <P>This is a potentially very slow query.
     *
     * @param cpathId CPath Record ID.
     * @return arraylist of descendent Ids.
     * @throws DaoException Database Access Error.
     */
    private ArrayList getAllDescendents(long cpathId) throws DaoException {
        ArrayList masterList = new ArrayList();
        Stack stack = new Stack();
        stack.push(cpathId);

        while (stack.size() > 0) {
            cpathId = (Long) stack.pop();
            DaoInternalLink internalLinker = new DaoInternalLink();
            ArrayList childrenList = internalLinker.getTargets(cpathId);
            if (childrenList != null) {
                for (int i = 0; i < childrenList.size(); i++) {
                    InternalLinkRecord link = (InternalLinkRecord) childrenList.get(i);
                    if (!masterList.contains(link.getTargetId())) {
                        masterList.add(link.getTargetId());
                        stack.push(link.getTargetId());
                    }
                }
            }
        }
        return masterList;
    }
}
