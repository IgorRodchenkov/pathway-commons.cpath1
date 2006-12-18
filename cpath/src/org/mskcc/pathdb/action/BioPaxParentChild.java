package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.schemas.biopax.summary.*;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Shows BioPAX Parent Child Data.
 *
 * @author Ethan Cerami.
 */
public class BioPaxParentChild extends BaseAction {
    /**
     * Default Max Number of Records in each view.
     */
    public static int MAX_RECORDS = 10;

    /**
     * Get Children.
     */
    public static String GET_CHILDREN = "getChildren";

    /**
     * Get Parents
     */
    public static String GET_PARENTS = "getParents";

    /**
     * Get Pathway Roots.
     */
    public static String GET_PATHWAY_ROOTS = "pathwayRoot";

    /**
     * Get Subunits.
     */
    public static String GET_SUB_UNITS = "subUnits";

    /**
     * Get Physical Entity Leaves.
     */
    public static String GET_PE_LEAVES = "peLeaf";

    /**
     * Attribute Key:  BP Summary List.
     */
    public static String KEY_BP_SUMMARY_LIST = "BP_SUMMARY_LIST";

    /**
     * Attribute Key:  Interaction Summary Map.
     */
    public static String KEY_INTERACTION_SUMMARY_MAP = "INTERACTION_SUMARY_MAP";

    public ActionForward subExecute (ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, XDebug xdebug)
            throws Exception {
        DaoCPath dao = DaoCPath.getInstance();
        String id = request.getParameter("id");
        String command = request.getParameter("command");
        String type = request.getParameter("type");
        String startIndex = request.getParameter("startIndex");
        String maxRecords = request.getParameter("maxRecords");
        CPathRecord record = null;
        if (id != null) {
            xdebug.logMsg(this, "Using cPath ID:  " + id);
            record = dao.getRecordById(Long.parseLong(id));
            xdebug.logMsg(this, "cPath Record Name:  " + record.getName());
        }

        DaoInternalLink daoLinker = new DaoInternalLink();

        //  Determine Filter Settings
        HttpSession session = request.getSession();
        GlobalFilterSettings filterSettings = (GlobalFilterSettings) session.getAttribute
                (GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
        if (filterSettings == null) {
            filterSettings = new GlobalFilterSettings();
            session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS,
                    filterSettings);
        }
        xdebug.logMsg(this, "Determining Global Filter Settings");

        int taxId = getTaxonomyIdFilter(filterSettings, xdebug);
        long snapshotIds[] = getSnapshotFilter(filterSettings, xdebug);

        ArrayList records = null;
        ArrayList <BioPaxRecordSummary> bpSummaryList = null;
        HashMap interactionSummaryMap = new HashMap();
        int start = 0;
        if (startIndex != null) {
            start = Integer.parseInt(startIndex);
        }
        int max = MAX_RECORDS;
        if (maxRecords != null) {
            max = Integer.parseInt(maxRecords);
        }

        //  Get parent or child elements.
        if (command != null && command.equals(GET_PARENTS)) {
            if (type != null && type.equals(GET_PATHWAY_ROOTS)) {
                bpSummaryList = getPathwayRoots(xdebug, id, filterSettings.getSnapshotIdSet(),
                        filterSettings.getOrganismTaxonomyIdSet(), start, max);
            } else {
                records = getParents(xdebug, daoLinker, id, taxId, snapshotIds, type,
                    start, max);
            }
        } else {
            if (type != null && type.equals(GET_PE_LEAVES)) {
                bpSummaryList = getPeLeaves(xdebug, id, start, max);
            } else {
                records = getChildren(xdebug, record, daoLinker, id, taxId, snapshotIds, type,
                    start, max);
            }
        }

        //  Get BioPax Summaries
        if (records != null) {
            interactionSummaryMap = getInteractionSummaryMap(records, xdebug);
            bpSummaryList = getBioPaxSummaries(records, xdebug);
        }

        request.setAttribute(KEY_INTERACTION_SUMMARY_MAP, interactionSummaryMap);
        request.setAttribute(KEY_BP_SUMMARY_LIST, bpSummaryList);
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private HashMap <Long, EntitySummary> getInteractionSummaryMap
            (ArrayList records, XDebug xdebug)
            throws DaoException, EntitySummaryException {
        HashMap <Long, EntitySummary> map = new HashMap<Long, EntitySummary>();
        for (int i=0; i<records.size(); i++) {
            CPathRecord record0 = (CPathRecord) records.get(i);
            if (record0.getType().equals(CPathRecordType.INTERACTION)) {
                EntitySummaryParser parser = new EntitySummaryParser(record0.getId());
                EntitySummary summary = parser.getEntitySummary();
                map.put(record0.getId(), summary);
                xdebug.logMsg(this, "Got summary for interaction:  [cPath ID: "
                    + summary.getRecordID()
                    + "] --> " + summary.getName());
            }
        }
        return map;
    }

    private ArrayList <BioPaxRecordSummary> getBioPaxSummaries (ArrayList records, XDebug xdebug)
            throws BioPaxRecordSummaryException, DaoException {
        DaoCPath daoCPath = DaoCPath.getInstance();
        ArrayList bpSummaryList = new ArrayList();
        for (int i=0; i<records.size(); i++) {
            CPathRecord record = (CPathRecord) records.get(i);
            CPathRecord recordFull = daoCPath.getRecordById(record.getId());
            BioPaxRecordSummary bpSummary =
                    BioPaxRecordUtil.createBioPaxRecordSummary(recordFull);
            bpSummaryList.add(bpSummary);
            xdebug.logMsg(this, "Got summary for:  [cPath ID: " + bpSummary.getRecordID()
                + "] --> " + bpSummary.getName());
        }
        return bpSummaryList;
    }

    private ArrayList getChildren (XDebug xdebug, CPathRecord record, DaoInternalLink daoLinker,
            String id, int taxId, long[] snapshotIds, String type, int start, int max)
            throws DaoException {
        if (type != null) {
            xdebug.logMsg(this, "Getting children.  Restricting results to records of type:  "
                    + type);
            xdebug.logMsg (this, "Start Index is set to:  " + start);
            xdebug.logMsg (this, "Max Records is set to:  " + max);
            ArrayList records;
            if (record.getSpecificType().equalsIgnoreCase("complex")) {
                records = daoLinker.getChildren(Long.parseLong(id), start, max, xdebug);
            } else {
                records = daoLinker.getChildren(Long.parseLong(id), taxId, snapshotIds,
                    type, start, max, xdebug);
            }
            if (records.size() ==0) {
                xdebug.logMsg(this, "No children found");
            }
            for (int j=0; j<records.size(); j++) {
                CPathRecord childRecord = (CPathRecord) records.get(j);
                xdebug.logMsg(this, "[cPathID:  " + childRecord.getId() + "]  "
                    + childRecord.getSpecificType() + ":  " + childRecord.getName());
            }
            return records;
        }
        return null;
    }

    private ArrayList getParents (XDebug xdebug, DaoInternalLink daoLinker, String id, int taxId,
            long[] snapshotIds, String type, int start, int max)
            throws DaoException {
        if (type != null) {
            xdebug.logMsg(this, "Getting parents.  Restricting results to records of type:  "
                + type);
            xdebug.logMsg (this, "Start Index is set to:  " + start);
            xdebug.logMsg (this, "Max Records is set to:  " + max);
            ArrayList records = daoLinker.getParents(Long.parseLong(id), taxId, snapshotIds,
                    type, start, max, xdebug);
            if (records.size() ==0) {
                xdebug.logMsg(this, "No parents found");
            }
            for (int j=0; j<records.size(); j++) {
                CPathRecord childRecord = (CPathRecord) records.get(j);
                xdebug.logMsg(this, "[cPathID:  " + childRecord.getId() + "]  "
                    + childRecord.getSpecificType() + ":  " + childRecord.getName());
            }
            return records;
        }
        return null;
    }

    /**
     * Determine Organism Filter.
     */
    private int getTaxonomyIdFilter (GlobalFilterSettings filterSettings, XDebug xdebug) {
        int taxId = -1;
        Set organismSet = filterSettings.getOrganismTaxonomyIdSet();
        Iterator organismIterator = organismSet.iterator();
        while (organismIterator.hasNext()) {
            Integer ncbiTaxonomyId = (Integer) organismIterator.next();
            if (ncbiTaxonomyId == GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE) {
                xdebug.logMsg (this, "Organism Filter set to:  ALL ORGANISMS");
            } else {
                xdebug.logMsg (this, "Organism Filter set to:  " + ncbiTaxonomyId);
                taxId = ncbiTaxonomyId;
            }
        }
        return taxId;
    }

    private long[] getSnapshotFilter (GlobalFilterSettings filterSettings, XDebug xdebug) {
        Set snapshotSet = filterSettings.getSnapshotIdSet();
        long snapshotIds [] = new long[snapshotSet.size()];
        Iterator snapshotIterator = snapshotSet.iterator();
        int index = 0;
        while (snapshotIterator.hasNext()) {
            Long snapshotId = (Long) snapshotIterator.next();
            xdebug.logMsg (this, "Snapshot Filter set to:  " + snapshotId);
            snapshotIds[index++] = snapshotId;
        }
        return snapshotIds;

    }

    private ArrayList <BioPaxRecordSummary> getPathwayRoots(XDebug xdebug, String id,
            Set snapshotIdSet, Set organismIdSet, int start, int max) throws DaoException,
            BioPaxRecordSummaryException {
        xdebug.logMsg(this, "Getting Pathway Root Elements");
        DaoInternalFamily dao = new DaoInternalFamily();
        LinkedHashSet<BioPaxRecordSummary> summarySet = new LinkedHashSet<BioPaxRecordSummary>();
        int count = dao.getAncestorSummaries(Long.parseLong(id), CPathRecordType.PATHWAY,
                summarySet, snapshotIdSet, organismIdSet, start, max);

        xdebug.logMsg(this, "Total number of pathway root elements:  " + count);
        ArrayList <BioPaxRecordSummary> rootList = new ArrayList <BioPaxRecordSummary>();
        DaoCPath daoCPath = DaoCPath.getInstance();
        Iterator iterator = summarySet.iterator();
        while (iterator.hasNext()) {
            BioPaxRecordSummary bpSummaryBrief = (BioPaxRecordSummary) iterator.next();
            CPathRecord record = daoCPath.getRecordById(bpSummaryBrief.getRecordID());
            xdebug.logMsg(this, "Getting Full Details for Record:  " + record.getName());
            BioPaxRecordSummary bpSummaryComplete =
                    BioPaxRecordUtil.createBioPaxRecordSummary(record);
            rootList.add(bpSummaryComplete);
        }
        return rootList;
    }

    private ArrayList <BioPaxRecordSummary> getPeLeaves
            (XDebug xdebug, String id, int start, int max)
            throws DaoException, BioPaxRecordSummaryException {
        xdebug.logMsg(this, "Getting Physical Entity Leaf Elements");
        DaoInternalFamily dao = new DaoInternalFamily();
        LinkedHashSet<BioPaxRecordSummary> summarySet = new LinkedHashSet<BioPaxRecordSummary>();
        int count = dao.getDescendentSummaries(Long.parseLong(id), CPathRecordType.PHYSICAL_ENTITY,
                summarySet, start, max);
        xdebug.logMsg(this, "Total number of physical entity leaf elements:  " + count);

        Iterator iterator = summarySet.iterator();

        ArrayList <BioPaxRecordSummary> peList = new ArrayList <BioPaxRecordSummary>();
        DaoCPath daoCPath = DaoCPath.getInstance();
        while (iterator.hasNext()) {
            BioPaxRecordSummary bpSummaryBrief = (BioPaxRecordSummary) iterator.next();
            CPathRecord record = daoCPath.getRecordById(bpSummaryBrief.getRecordID());
            xdebug.logMsg(this, "Getting Full Details for Record:  " + record.getName());
            BioPaxRecordSummary bpSummaryComplete =
                    BioPaxRecordUtil.createBioPaxRecordSummary(record);
            peList.add(bpSummaryComplete);
        }
        return peList;
    }
}
