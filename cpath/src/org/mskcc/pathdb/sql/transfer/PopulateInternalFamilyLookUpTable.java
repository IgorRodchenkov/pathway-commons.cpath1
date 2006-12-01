package org.mskcc.pathdb.sql.transfer;

import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

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
            "select `CPATH_ID`, `TYPE`, `XML_TYPE`, `XML_CONTENT` from cpath WHERE TYPE = ? LIMIT ?,1";
    private static final String GET_RECORD_BY_ID =
            "select `CPATH_ID`, `TYPE`, `XML_TYPE`, `XML_CONTENT` from cpath WHERE CPATH_ID = ?";
    private ProgressMonitor pMonitor;
    private HashMap cache = new HashMap();
    private HashSet visitedSet;

    /**
     * Constructor.
     *
     * @param pMonitor ProgressMonitor Object.
     */
    public PopulateInternalFamilyLookUpTable (ProgressMonitor pMonitor) {
        this.pMonitor = pMonitor;
    }

    /**
     * Executes task.
     *
     * @throws DaoException Data access error.
	 * @throws BioPaxRecordSummaryException.
     */
    public void execute () throws DaoException, BioPaxRecordSummaryException {
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoInternalFamily daoFamily = new DaoInternalFamily();

        //  Start fresh:  Delete all existing internal family records
        daoFamily.deleteAllRecords();

        //  Determine # pathways
        int numPathways = daoCPath.getNumEntities(CPathRecordType.PATHWAY);
        pMonitor.setMaxValue(numPathways);

        int option = 1;

        //  Index each pathway
        for (int i = 0; i < numPathways; i++) {
            CPathRecord record = getRecordIdAtOffset(CPathRecordType.PATHWAY, i);
            BioPaxRecordSummary pathwayRecordSummary =
                BioPaxRecordUtil.createBioPaxRecordSummary(record);
			if (pathwayRecordSummary.getName() == null ||
				pathwayRecordSummary.getName().length() == 0) {
				if (CPathConstants.CPATH_DO_ASSERT) {
					assert (record.getType() == CPathRecordType.INTERACTION) :
					"*** PopulateInternalFamilyLookupTable: Pathway or Physical Entity Record has no name ***";
				}
				pathwayRecordSummary.setName(CPathRecord.NA_STRING);
			}
            if (option == 0) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
                ArrayList idList = getAllDescendents(record.getId());
                Iterator iterator = idList.iterator();

                while (iterator.hasNext()) {
                    Long descendentId = (Long) iterator.next();
                    CPathRecord descendentRecord = getRecordById(descendentId);
					BioPaxRecordSummary descendentRecordSummary =
						BioPaxRecordUtil.createBioPaxRecordSummary(descendentRecord);
					if (descendentRecordSummary.getName() == null ||
						descendentRecordSummary.getName().length() == 0) {
						if (CPathConstants.CPATH_DO_ASSERT) {
							assert (descendentRecord.getType() == CPathRecordType.INTERACTION) :
							"*** PopulateInternalFamilyLookupTable: Pathway or Physical Entity Record has no name ***";
						}
						descendentRecordSummary.setName(CPathRecord.NA_STRING);
					}
                    //  Only index descendents, which are of type:  physical entity
                    if (descendentRecord.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                        daoFamily.addRecord(record.getId(), pathwayRecordSummary.getName(),
											record.getType(), descendentRecord.getId(),
											descendentRecordSummary.getName(), descendentRecord.getType());
                    }
                }

            } else {
                visitedSet = new HashSet();
                populateInternalFamilyTable(record.getId());
            }
        }
    }

    /**
     * Gets records at offset.  Optimized SQL (does not retrieve XML content).
     */
    private CPathRecord getRecordIdAtOffset (CPathRecordType recordType, int offset)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = getPreparedStatement1(con);
            pstmt.setString(1, recordType.toString());
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractRecord(rs);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
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
            pstmt = getPreparedStatement2(con);
            pstmt.setLong(1, cPathId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractRecord(rs);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
        return null;
    }

    private CPathRecord extractRecord (ResultSet rs) throws SQLException {
        CPathRecord record = new CPathRecord();
        record.setId(rs.getLong(1));
        record.setType(CPathRecordType.getType(rs.getString(2)));
        record.setXmlType(XmlRecordType.getType(rs.getString(3)));
        record.setXmlContent(rs.getString(4));
        return record;
    }

    private PreparedStatement getPreparedStatement1 (Connection con) throws SQLException {
        return con.prepareStatement(GET_RECORD_ITERATOR);
    }

    private PreparedStatement getPreparedStatement2 (Connection con) throws SQLException {
        return con.prepareStatement(GET_RECORD_BY_ID);
    }

    /**
     * Gets all descendents of the specified cPath record.
     * <P>This is a potentially very slow query.
     *
     * @param cpathId CPath Record ID.
     * @return arraylist of descendent Ids.
     * @throws DaoException Database Access Error.
     */
    private ArrayList getAllDescendents (long cpathId) throws DaoException {
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

    private long[] populateInternalFamilyTable (long cpathId)
            throws DaoException, BioPaxRecordSummaryException {

        //  Prevent infinite loop on, e.g. circular pathways
        if (visitedSet.contains(cpathId)) {
            return new long[0];
        } else {
            visitedSet.add(cpathId);
        }

        //  First, check to see if we have already visited this record
        long descendentIds[] = (long[]) cache.get(cpathId);
        if (descendentIds != null) {
            //  If we have been here before, use data from family table and immediately return
            return descendentIds;
        } else {

            //  Otherwise, recursively walk through children list
            HashSet descendentList = new HashSet();
            DaoInternalLink internalLinker = new DaoInternalLink();
            ArrayList childrenList = internalLinker.getTargets(cpathId);

            //  Recurse through all children
            if (childrenList != null) {
                for (int i = 0; i < childrenList.size(); i++) {
                    InternalLinkRecord link = (InternalLinkRecord) childrenList.get(i);
                    descendentList.add(link.getTargetId());
                    long childDescendents[] = populateInternalFamilyTable(link.getTargetId());
                    for (int j = 0; j < childDescendents.length; j++) {
                        descendentList.add(childDescendents[j]);
                    }
                }
            }

            //  Store pathway descendents to family table
            CPathRecord parentRecord = getRecordById(cpathId);
            if (parentRecord.getType().equals(CPathRecordType.PATHWAY)) {
                storeFamilyMembership(descendentList, parentRecord);
            }

            int index = 0;
            descendentIds = new long[descendentList.size()];
            Iterator iterator = descendentList.iterator();
            while (iterator.hasNext()) {
                Long descendentId = (Long) iterator.next();
                descendentIds[index] = descendentId;
                index++;
            }

            //  Store descendents to cache
            cache.put(cpathId, descendentIds);
            return descendentIds;
        }
    }

    private void storeFamilyMembership (HashSet descendentList, CPathRecord parentRecord)
            throws DaoException, BioPaxRecordSummaryException {
		BioPaxRecordSummary parentRecordSummary =
			BioPaxRecordUtil.createBioPaxRecordSummary(parentRecord);
		if (parentRecordSummary.getName() == null ||
			parentRecordSummary.getName().length() == 0) {
			if (CPathConstants.CPATH_DO_ASSERT) {
				assert (parentRecord.getType() == CPathRecordType.INTERACTION) :
				"*** PopulateInternalFamilyLookupTable: Pathway or Physical Entity Record has no name ***";
			}
			parentRecordSummary.setName(CPathRecord.NA_STRING);
		}
        Iterator iterator = descendentList.iterator();
        pMonitor.incrementCurValue();
        ConsoleUtil.showProgress(pMonitor);
        DaoInternalFamily daoFamily = new DaoInternalFamily();
        ArrayList recordTypes = new ArrayList();
        ArrayList recordNames = new ArrayList();
        ArrayList recordIds = new ArrayList();
        while (iterator.hasNext()) {
            Long descendentId = (Long) iterator.next();
            CPathRecord descendentRecord = getRecordById(descendentId);
            BioPaxRecordSummary descendentRecordSummary =
                BioPaxRecordUtil.createBioPaxRecordSummary(descendentRecord);
			if (descendentRecordSummary.getName() == null ||
				descendentRecordSummary.getName().length() == 0) {
				if (CPathConstants.CPATH_DO_ASSERT) {
					assert (descendentRecord.getType() == CPathRecordType.INTERACTION) :
					"*** PopulateInternalFamilyLookupTable: Pathway or Physical Entity Record has no name ***";
				}
				descendentRecordSummary.setName(CPathRecord.NA_STRING);
			}
            recordIds.add(descendentRecord.getId());
			recordNames.add(descendentRecordSummary.getName());
            recordTypes.add(descendentRecord.getType().toString());
        }

        daoFamily.addRecords(parentRecord.getId(),
							 parentRecordSummary.getName(),
							 parentRecord.getType(),
							 recordIds, recordNames, recordTypes);

    }
}