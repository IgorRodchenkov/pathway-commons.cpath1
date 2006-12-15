package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.TypeCount;
import org.mskcc.pathdb.model.CPathRecordType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

public class ShowBioPaxRecord2 extends BaseAction {

    public ActionForward subExecute (ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, XDebug xdebug)
            throws Exception {
        DaoCPath dao = DaoCPath.getInstance();
        String id = request.getParameter("id");
        String command = request.getParameter("command");
        CPathRecord record = null;
        if (id != null) {
            xdebug.logMsg(this, "Using cPath ID:  " + id);
            record = dao.getRecordById(Long.parseLong(id));
            xdebug.logMsg(this, "cPath Record Name:  " + record.getName());
            request.setAttribute("NAME", record.getName());
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

        //  Get parent or child types.
        ArrayList typeList;
        if (command != null && command.equals("getParents")) {
            typeList = getParentTypes(xdebug, daoLinker, id, taxId, snapshotIds);
            DaoInternalFamily daoFamily = new DaoInternalFamily();
            int count = daoFamily.getAncestorIdCount(Long.parseLong(id), CPathRecordType.PATHWAY,
                    filterSettings.getSnapshotIdSet(), filterSettings.getOrganismTaxonomyIdSet());
            TypeCount typeCount = new TypeCount();
            xdebug.logMsg(this, "Number of Pathway Roots:  " + count);
            typeCount.setType(BioPaxParentChild.GET_PATHWAY_ROOTS);
            typeCount.setCount(count);
            typeList.add(typeCount);
        } else {
            typeList = getChildTypes(xdebug, daoLinker, id, taxId, snapshotIds);
            DaoInternalFamily daoFamily = new DaoInternalFamily();
            int count = daoFamily.getDescendentIdCount(Long.parseLong(id),
                    CPathRecordType.PHYSICAL_ENTITY);
            TypeCount typeCount = new TypeCount();
            xdebug.logMsg(this, "Number of Molecule Leaves:  " + count);
            typeCount.setType(BioPaxParentChild.GET_PE_LEAVES);
            typeCount.setCount(count);
            typeList.add(typeCount);
        }
        request.setAttribute("TYPES_LIST", typeList);
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private ArrayList getChildTypes (XDebug xdebug, DaoInternalLink daoLinker, String id, int taxId,
            long[] snapshotIds) throws DaoException {
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
        return childTypes;
    }

    private ArrayList getParentTypes (XDebug xdebug, DaoInternalLink daoLinker, String id, int taxId,
            long[] snapshotIds)
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
        return parentTypes;
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

    /**
     * Determine Data Source Filter.
     */
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
}
