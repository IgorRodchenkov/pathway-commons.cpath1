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
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummaryParser;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummary;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.taglib.ReferenceUtil;

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
        CPathRecord record = null;
        BioPaxRecordSummary bpSummary = null;
        if (id == null) {
            throw new IllegalArgumentException ("id parameter must be specified.");
        } else {
            xdebug.logMsg(this, "Using cPath ID:  " + id);
            try {
                record = dao.getRecordById(Long.parseLong(id));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException ("id parameter must be an integer value.");
            }
            if (record == null) {
                throw new IllegalArgumentException ("record id " + id
                    + " does not exist in database.");
            }
            xdebug.logMsg(this, "cPath Record Name:  " + record.getName());
            bpSummary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            request.setAttribute("BP_SUMMARY", bpSummary);

            if (record.getType() == CPathRecordType.INTERACTION) {
                EntitySummaryParser parser = new EntitySummaryParser(Long.parseLong(id));
                EntitySummary entitySummary = parser.getEntitySummary();
                request.setAttribute("ENTITY_SUMMARY", entitySummary);
            }
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

        Set snapshotIdSet = filterSettings.getSnapshotIdSet();
        xdebug.logMsg(this, "Snapshot ID is:  "  + record.getSnapshotId());
        if (record.getSnapshotId() > 0 &&
                !snapshotIdSet.contains(record.getSnapshotId())) {
            xdebug.logMsg(this, "Record is out of scope");
            return mapping.findForward("out_of_scope");
        }

        //  Get parent or child types.
        ArrayList typeList = new ArrayList();
        boolean getChildren = false;
        boolean getParents = false;
        boolean getPathwayRoots = false;
        boolean getPeLeaves = false;
        boolean isComplex = false;

        //  Get different elements, depending on type
        xdebug.logMsg (this, "Record type:  " + record.getType());
        if (record.getType() == CPathRecordType.PATHWAY) {
            getChildren = true;
            getPeLeaves = true;
        } else if (record.getType() == CPathRecordType.INTERACTION) {
            getPeLeaves = true;
            getPathwayRoots = true;
        } else {
            xdebug.logMsg (this, "Record specific type:  " + record.getSpecificType());
            getParents = true;
            getPathwayRoots = true;
            if (record.getSpecificType().toLowerCase().equals("complex")) {
                isComplex = true;
                getChildren = true;
            }
        }
        // set external links
        if (bpSummary != null) {
            ReferenceUtil refUtil = new ReferenceUtil();
            ArrayList bpSummaryList = new ArrayList ();
            bpSummaryList.add(bpSummary);
            request.setAttribute("EXTERNAL_LINKS", refUtil.getReferenceMap(bpSummaryList, xdebug));
        }

        if (getChildren) {
            xdebug.logMsg (this, "Getting all children");
            ArrayList childList = getChildTypes(xdebug, daoLinker, id, taxId, snapshotIds,
                    isComplex);
            typeList.addAll(childList);
        }
        if (getParents) {
            xdebug.logMsg (this, "Getting all parents");
            ArrayList parentList = getParentTypes(xdebug, daoLinker, id, taxId, snapshotIds);
            typeList.addAll(parentList);
        }
        if (getPathwayRoots) {
            xdebug.logMsg (this, "Getting all pathway roots");
            TypeCount typeCount = getPathwayRoots(id, filterSettings, xdebug);
            typeList.add(typeCount);
        }
        if (getPeLeaves) {
            xdebug.logMsg (this, "Getting all physical entity leaves");
            TypeCount typeCount = getPeLeaves(id, xdebug);
            typeList.add(typeCount);
        }
        xdebug.logMsg(this, "Total number of tabs:  " + typeList.size());
        request.setAttribute("TYPES_LIST", typeList);
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private TypeCount getPeLeaves (String id, XDebug xdebug) throws DaoException {
        DaoInternalFamily daoFamily = new DaoInternalFamily();
        int count = daoFamily.getDescendentIdCount(Long.parseLong(id),
                CPathRecordType.PHYSICAL_ENTITY);
        TypeCount typeCount = new TypeCount(BioPaxParentChild.GET_CHILDREN);
        xdebug.logMsg(this, "Number of Molecule Leaves:  " + count);
        typeCount.setType(BioPaxParentChild.GET_PE_LEAVES);
        typeCount.setCount(count);
        return typeCount;
    }

    private TypeCount getPathwayRoots (String id, GlobalFilterSettings filterSettings,
            XDebug xdebug) throws DaoException {
        DaoInternalFamily daoFamily = new DaoInternalFamily();
        int count = daoFamily.getAncestorIdCount(Long.parseLong(id), CPathRecordType.PATHWAY,
                filterSettings.getSnapshotIdSet(), filterSettings.getOrganismTaxonomyIdSet());
        TypeCount typeCount = new TypeCount(BioPaxParentChild.GET_PARENTS);
        xdebug.logMsg(this, "Number of Pathway Roots:  " + count);
        typeCount.setType(BioPaxParentChild.GET_PATHWAY_ROOTS);
        typeCount.setCount(count);
        return typeCount;
    }

    private ArrayList getChildTypes (XDebug xdebug, DaoInternalLink daoLinker, String id, int taxId,
            long[] snapshotIds, boolean isComplex) throws DaoException {
        xdebug.logMsg(this, "Determing types of all child elements");
        ArrayList childTypes;
        if (isComplex) {
            childTypes = daoLinker.getChildrenTypes(Long.parseLong(id), xdebug);
            int counter = 0;
            //  Merge all into one category.
            for (int i=0; i<childTypes.size(); i++) {
                TypeCount typeCount = (TypeCount) childTypes.get(i);
                counter+= typeCount.getCount();
            }
            TypeCount typeCount = new TypeCount(BioPaxParentChild.GET_CHILDREN);
            typeCount.setCount(counter);
            typeCount.setType(BioPaxParentChild.GET_SUB_UNITS);
            childTypes = new ArrayList();
            childTypes.add(typeCount);
        } else {
            childTypes = daoLinker.getChildrenTypes(Long.parseLong(id),
                taxId, snapshotIds, xdebug);
        }

        if (childTypes.size() ==0) {
                xdebug.logMsg(this, "No child types found");
        }
        for (int i=0; i<childTypes.size(); i++) {
            TypeCount typeCount = (TypeCount) childTypes.get(i);
            xdebug.logMsg(this, "Specific type:  " + typeCount.getType()
                + " -->  " + typeCount.getCount() + " records");
        }
        //  remove control interactions, since these are now shown as the
        //  parents of biochemical reactions.
        removeControlInteractions (childTypes, xdebug);
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
        //  remove control interactions, since these are now shown as the
        //  parents of biochemical reactions.
        removeControlInteractions (parentTypes, xdebug);
        return parentTypes;
    }

    private void removeControlInteractions (ArrayList typeList, XDebug xdebug) {
        int controlIndex = -1;
        for (int i=0; i<typeList.size(); i++) {
            TypeCount typeCount = (TypeCount) typeList.get(i);
            xdebug.logMsg(this, "Specific type:  " + typeCount.getType()
                + " -->  " + typeCount.getCount() + " records");
            if (typeCount.getType().equals(BioPaxConstants.CONTROL)) {
                controlIndex = i;
            }
        }
       if (controlIndex > 0) {
            typeList.remove(controlIndex);
            xdebug.logMsg(this, "Removing control interactions");
        }
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
