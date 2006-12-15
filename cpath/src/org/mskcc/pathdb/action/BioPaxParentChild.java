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
     * Get Physical Entity Leaves.
     */
    public static String GET_PE_LEAVES = "peLeaf";

    public ActionForward subExecute (ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, XDebug xdebug)
            throws Exception {
        DaoCPath dao = DaoCPath.getInstance();
        String id = request.getParameter("id");
        String command = request.getParameter("command");
        String type = request.getParameter("type");
        String startIndex = request.getParameter("startIndex");
        String maxRecords = request.getParameter("maxRecords");
        CPathRecord record;
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
        ArrayList summaryList = null;
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
                summaryList = getPathwayRoots(xdebug, id, filterSettings.getSnapshotIdSet(),
                        filterSettings.getOrganismTaxonomyIdSet(), start, max);
            } else {
                records = getParents(xdebug, daoLinker, id, taxId, snapshotIds, type,
                    start, max);
            }
        } else {
            if (type != null && type.equals(GET_PE_LEAVES)) {
                summaryList = getPeLeaves(xdebug, id, start, max);
            } else {
                records = getChildren(xdebug, daoLinker, id, taxId, snapshotIds, type,
                    start, max);
            }
        }

        //  Get entity summaries
        if (records != null && summaryList == null) {
            summaryList = getEntitySummaries(records, xdebug);
        }

        request.setAttribute("SUMMARY_LIST", summaryList);
        request.setAttribute("START", start);
        request.setAttribute("MAX", max);
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private ArrayList getEntitySummaries (ArrayList records, XDebug xdebug)
            throws DaoException, EntitySummaryException {
        ArrayList summaryList;
        summaryList = new ArrayList();
        for (int i=0; i<records.size(); i++) {
            CPathRecord record0 = (CPathRecord) records.get(i);
            EntitySummaryParser parser = new EntitySummaryParser(record0.getId());
            EntitySummary summary = parser.getEntitySummary();
            summaryList.add(summary);
            xdebug.logMsg(this, "Got summary for:  [cPath ID: " + summary.getRecordID()
                + "] --> " + summary.getName());
        }
        return summaryList;
    }

    private ArrayList getChildren (XDebug xdebug, DaoInternalLink daoLinker, String id, int taxId,
            long[] snapshotIds, String type, int start, int max)
            throws DaoException {
        xdebug.logMsg(this, "Determing types of all child elements");
        ArrayList childTypes = daoLinker.getChildrenTypes(Long.parseLong(id),
                taxId, snapshotIds, xdebug);

        if (childTypes.size() ==0) {
                xdebug.logMsg(this, "No child types found");
        }
        for (int i=0; i<childTypes.size(); i++) {
            TypeCount typeCount = (TypeCount) childTypes.get(i);
            xdebug.logMsg(this, "Specific type:  " + typeCount.getType()
                + " -->  " + typeCount.getCount() + " records");
        }

        if (type != null) {
            xdebug.logMsg(this, "Getting children.  Restricting results to records of type:  "
                    + type);
            xdebug.logMsg (this, "Start Index is set to:  " + start);
            xdebug.logMsg (this, "Max Records is set to:  " + max);
            ArrayList records = daoLinker.getChildren(Long.parseLong(id), taxId, snapshotIds,
                    type, start, max, xdebug);
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
        xdebug.logMsg(this, "Determing types of all parent elements");
        ArrayList parentTypes = daoLinker.getParentTypes(Long.parseLong(id),
                taxId, snapshotIds, xdebug);


        if (parentTypes.size() ==0) {
            xdebug.logMsg (this, "No parent types found");
        }
        for (int i=0; i<parentTypes.size(); i++) {
            TypeCount typeCount = (TypeCount) parentTypes.get(i);
            xdebug.logMsg(this, "Specific type:  " + typeCount.getType()
                + " -->  " + typeCount.getCount() + " records");
        }

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

    private ArrayList getPathwayRoots(XDebug xdebug, String id, Set snapshotIdSet,
            Set organismIdSet, int start, int max) throws DaoException {
        xdebug.logMsg(this, "Getting Pathway Root Elements");
        DaoInternalFamily dao = new DaoInternalFamily();
        LinkedHashSet<BioPaxRecordSummary> summarySet = new LinkedHashSet<BioPaxRecordSummary>();
        int count = dao.getAncestorSummaries(Long.parseLong(id), CPathRecordType.PATHWAY, summarySet,
                snapshotIdSet, organismIdSet, start, max);

        xdebug.logMsg(this, "Total number of pathway root elements:  " + count);
        ArrayList list = new ArrayList();
        list.addAll(summarySet);
        return list;
    }

    private ArrayList getPeLeaves (XDebug xdebug, String id, int start, int max)
            throws DaoException, BioPaxRecordSummaryException {
        xdebug.logMsg(this, "Getting Physical Entity Leaf Elements");
        DaoInternalFamily dao = new DaoInternalFamily();
        LinkedHashSet<BioPaxRecordSummary> summarySet = new LinkedHashSet<BioPaxRecordSummary>();
        int count = dao.getDescendentSummaries(Long.parseLong(id), CPathRecordType.PHYSICAL_ENTITY,
                summarySet, start, max);
        xdebug.logMsg(this, "Total number of physical entity leaf elements:  " + count);

        Iterator iterator = summarySet.iterator();

        ArrayList peList = new ArrayList();
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
