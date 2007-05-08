package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.schemas.biopax.summary.*;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.taglib.ReferenceUtil;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    /**
     * Attribute Key:  Interaction Parents HashMap.
     */
    public static String KEY_INTERACTION_PARENTS_SUMMARY_MAP = "INTERACTION_PARENTS_SUMMARY_MAP";

    /**
     * Attribute Key:  PMID Map.
     */
    public static String KEY_PMID_MAP = "KEY_PMID_MAP";

    /**
     * Command Parameter.
     */
    public static String COMMAND_PARAMETER = "command";

    /**
     * Tyoe Parameter.
     */
    public static String TYPE_PARAMETER = "type";

    /**
     * Start Index Parameter.
     */
    public static String START_INDEX_PARAMETER = "startIndex";

    /**
     * Max Records Parameter.
     */
    public static String MAX_RECORDS_PARAMETER = "maxRecords";

    /**
     * Total Num Records Parameter.
     */
    public static String TOTAL_NUM_RECORDS_PARAMETER = "totalNumRecords";

    /**
     * Executes Action.
     *
     * @param mapping  ActionMapping Object.
     * @param form     ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Action Forward Object.
     * @throws Exception All Errors.
     */
    public ActionForward subExecute (ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, XDebug xdebug)
            throws Exception {
        DaoCPath dao = DaoCPath.getInstance();

        //  required parameters
        String id = request.getParameter(ShowBioPaxRecord2.ID_PARAMETER);
        String command = request.getParameter(COMMAND_PARAMETER);
        String type = request.getParameter(TYPE_PARAMETER);

        //  optional parameters
        String startIndex = request.getParameter(START_INDEX_PARAMETER);
        String maxRecords = request.getParameter(MAX_RECORDS_PARAMETER);
        String totalNumRecords = request.getParameter(TOTAL_NUM_RECORDS_PARAMETER);

        //  Basic Input Validation of Required Paramters
        if (id == null) {
            throw new IllegalArgumentException("id parameter must be specified.");
        }
        if (command == null) {
            throw new IllegalArgumentException("command parameter must be specified.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type parameter must be specified.");
        }
        if (!command.equals(BioPaxParentChild.GET_CHILDREN)
                && !command.equals(BioPaxParentChild.GET_PARENTS)) {
            throw new IllegalArgumentException("command parameter not recognized --> "
                    + command);
        }

        //  Parse the startIndex parameter
        int start = 0;
        if (startIndex != null) {
            try {
                start = Integer.parseInt(startIndex);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException
                        ("startIndex parameter must be an integer value.");
            }
        }

        //  Parse the max number of records parameter
        int max = MAX_RECORDS;
        if (maxRecords != null) {
            try {
                max = Integer.parseInt(maxRecords);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException
                        ("maxRecords parameter must be an integer value.");
            }
        }

        //  Parse the total number of records parameter
        if (totalNumRecords != null) {
            try {
                int total = Integer.parseInt(totalNumRecords);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException
                        ("totalNumRecords parameter must be an integer value.");
            }
        }

        //  Look up CPath Record
        CPathRecord record = null;
        xdebug.logMsg(this, "Using cPath ID:  " + id);
        try {
            record = dao.getRecordById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id parameter must be an integer value.");
        }
        if (record == null) {
            throw new IllegalArgumentException("record id " + id
                    + " does not exist in database.");
        }
        xdebug.logMsg(this, "cPath Record Name:  " + record.getName());

        //  Determine Current Filter Settings
        GlobalFilterSettings filterSettings = this.getCurrentFilterSettings(request, xdebug);
        int taxId = getTaxonomyIdFilter(filterSettings, xdebug);
        long snapshotIds[] = getSnapshotFilter(filterSettings, xdebug);

        ArrayList records = null;
        ArrayList<BioPaxRecordSummary> bpSummaryList = null;
        HashMap interactionSummaryMap = new HashMap();

        //  Get parent or child elements.
        DaoInternalLink daoLinker = new DaoInternalLink();
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

        //  Get BioPax Summaries;  Get Interaction Summaries.
        HashMap <Long, ArrayList>interactionParentMap = null;
        if (records != null) {
            interactionParentMap = getInteractionParents(records, xdebug);
            interactionSummaryMap = getInteractionSummaryMap(records, xdebug);
            getParentInteractionSummaries(interactionParentMap, interactionSummaryMap,
                    xdebug);
            bpSummaryList = getBioPaxSummaries(records, xdebug);
        }

        //  Get PubMed References
        ReferenceUtil refUtil = new ReferenceUtil();
        HashMap refMap = refUtil.getReferenceMap(bpSummaryList, xdebug);

        //  Store data in attributes
        request.setAttribute(KEY_PMID_MAP, refMap);
        request.setAttribute(KEY_INTERACTION_SUMMARY_MAP, interactionSummaryMap);
        request.setAttribute(KEY_BP_SUMMARY_LIST, bpSummaryList);

        if (interactionParentMap != null) {
            request.setAttribute(KEY_INTERACTION_PARENTS_SUMMARY_MAP, interactionParentMap);
        }

        //  Forward to JSP for rendering
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private void getParentInteractionSummaries (HashMap interactionParentMap,
            HashMap interactionSummaryMap, XDebug xdebug)
            throws DaoException, EntitySummaryException {
        Iterator iterator = interactionParentMap.keySet().iterator();
        while (iterator.hasNext()) {
            Long interactionId = (Long) iterator.next();
            ArrayList parentRecords = (ArrayList) interactionParentMap.get(interactionId);
            interactionSummaryMap.putAll(getInteractionSummaryMap(parentRecords, xdebug));
        }
    }

    /**
     * Gets EntitySummary Objects (Applies to Interaction Records only)
     */
    private HashMap<Long, EntitySummary> getInteractionSummaryMap
            (ArrayList records, XDebug xdebug)
            throws DaoException, EntitySummaryException {
        HashMap<Long, EntitySummary> map = new HashMap<Long, EntitySummary>();
        for (int i = 0; i < records.size(); i++) {
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

    /**
     * Gets BioPAX Summary Objects.
     */
    private ArrayList<BioPaxRecordSummary> getBioPaxSummaries (ArrayList records, XDebug xdebug)
            throws BioPaxRecordSummaryException, DaoException {
        DaoCPath daoCPath = DaoCPath.getInstance();
        ArrayList bpSummaryList = new ArrayList();
        for (int i = 0; i < records.size(); i++) {
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

    /**
     * Gets all Children.
     */
    private ArrayList getChildren (XDebug xdebug, CPathRecord record, DaoInternalLink daoLinker,
            String id, int taxId, long[] snapshotIds, String type, int start, int max)
            throws DaoException {
        if (type != null) {
            xdebug.logMsg(this, "Getting children.  Restricting results to records of type:  "
                    + type);
            xdebug.logMsg(this, "Start Index is set to:  " + start);
            xdebug.logMsg(this, "Max Records is set to:  " + max);
            ArrayList records;
            if (record.getSpecificType().equalsIgnoreCase("complex")) {
                records = daoLinker.getChildren(Long.parseLong(id), start, max, xdebug);
            } else {
                records = daoLinker.getChildren(Long.parseLong(id), taxId, snapshotIds,
                        type, start, max, xdebug);
            }
            if (records.size() == 0) {
                xdebug.logMsg(this, "No children found");
            }
            for (int j = 0; j < records.size(); j++) {
                CPathRecord childRecord = (CPathRecord) records.get(j);
                xdebug.logMsg(this, "[cPathID:  " + childRecord.getId() + "]  "
                        + childRecord.getSpecificType() + ":  " + childRecord.getName());
            }
            return records;
        }
        return null;
    }

    /**
     * Gets all Parents.
     */
    private ArrayList getParents (XDebug xdebug, DaoInternalLink daoLinker, String id, int taxId,
            long[] snapshotIds, String type, int start, int max)
            throws DaoException {
        if (type != null) {
            xdebug.logMsg(this, "Getting parents.  Restricting results to records of type:  "
                    + type);
            xdebug.logMsg(this, "Start Index is set to:  " + start);
            xdebug.logMsg(this, "Max Records is set to:  " + max);
            ArrayList records = daoLinker.getParents(Long.parseLong(id), taxId, snapshotIds,
                    type, start, max, xdebug);
            if (records.size() == 0) {
                xdebug.logMsg(this, "No parents found");
            }
            for (int j = 0; j < records.size(); j++) {
                CPathRecord childRecord = (CPathRecord) records.get(j);
                xdebug.logMsg(this, "[cPathID:  " + childRecord.getId() + "]  "
                        + childRecord.getSpecificType() + ":  " + childRecord.getName());
            }
            return records;
        }
        return null;
    }

    /**
     * Gets all Pathway Roots.
     */
    private ArrayList<BioPaxRecordSummary> getPathwayRoots (XDebug xdebug, String id,
            Set snapshotIdSet, Set organismIdSet, int start, int max) throws DaoException,
            BioPaxRecordSummaryException {
        xdebug.logMsg(this, "Getting Pathway Root Elements");
        DaoInternalFamily dao = new DaoInternalFamily();
        LinkedHashSet<BioPaxRecordSummary> summarySet = new LinkedHashSet<BioPaxRecordSummary>();
        int count = dao.getAncestorSummaries(Long.parseLong(id), CPathRecordType.PATHWAY,
                summarySet, snapshotIdSet, organismIdSet, start, max);

        xdebug.logMsg(this, "Total number of pathway root elements:  " + count);
        ArrayList<BioPaxRecordSummary> rootList = new ArrayList<BioPaxRecordSummary>();
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

    /**
     * Gets all Physical Entity Leaves.
     */
    private ArrayList<BioPaxRecordSummary> getPeLeaves
            (XDebug xdebug, String id, int start, int max)
            throws DaoException, BioPaxRecordSummaryException {
        xdebug.logMsg(this, "Getting Physical Entity Leaf Elements");
        DaoInternalFamily dao = new DaoInternalFamily();
        LinkedHashSet<BioPaxRecordSummary> summarySet = new LinkedHashSet<BioPaxRecordSummary>();
        int count = dao.getDescendentSummaries(Long.parseLong(id), CPathRecordType.PHYSICAL_ENTITY,
                summarySet, start, max);
        xdebug.logMsg(this, "Total number of physical entity leaf elements:  " + count);

        Iterator iterator = summarySet.iterator();

        ArrayList<BioPaxRecordSummary> peList = new ArrayList<BioPaxRecordSummary>();
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

    /**
     * Gets parent interactions, e.g. controllers.
     */
    private HashMap <Long, ArrayList> getInteractionParents(ArrayList records, XDebug xdebug)
        throws DaoException {
        HashMap <Long, ArrayList> interactionParentMap = new HashMap<Long, ArrayList>();

        for (int i = 0; i < records.size(); i++) {
            CPathRecord record = (CPathRecord) records.get(i);
            if (record.getType().equals(CPathRecordType.INTERACTION)) {
                ArrayList parentInteractions = getInteractionParents(record.getId(), xdebug);
                interactionParentMap.put(record.getId(), parentInteractions);
            }
        }
        return interactionParentMap;
    }

    /**
     * Gets parent interactions, e.g. controllers.
     */
    private ArrayList getInteractionParents (long cpathId, XDebug xdebug) throws DaoException {
        ArrayList parentRecords = new ArrayList();

        //  First, get parents of this interaction
        DaoInternalLink daoInternalLink = new DaoInternalLink();
        DaoCPath daoCPath = DaoCPath.getInstance();
        ArrayList sources = daoInternalLink.getSources(cpathId);

        xdebug.logMsg(this, "Getting parents of interaction:  " + cpathId);
        for (int i = 0; i < sources.size(); i++) {
            InternalLinkRecord internalLinkRecord = (InternalLinkRecord) sources.get(i);
            CPathRecord parentRecord = daoCPath.getRecordById(internalLinkRecord.getSourceId());
            if (parentRecord.getType() == CPathRecordType.INTERACTION) {
                parentRecords.add(parentRecord);
            }
        }
        return parentRecords;
    }
}