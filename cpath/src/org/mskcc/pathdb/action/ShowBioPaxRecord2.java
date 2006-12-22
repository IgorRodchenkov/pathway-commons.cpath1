package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoReference;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.TypeCount;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.Reference;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.util.CPathConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

public class ShowBioPaxRecord2 extends BaseAction {

    public ActionForward subExecute (ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, XDebug xdebug)
            throws Exception {
        DaoCPath dao = DaoCPath.getInstance();
        String id = request.getParameter("id");
        CPathRecord record = null;
		BioPaxRecordSummary bpSummary = null;
        if (id != null) {
            xdebug.logMsg(this, "Using cPath ID:  " + id);
            record = dao.getRecordById(Long.parseLong(id));
            xdebug.logMsg(this, "cPath Record Name:  " + record.getName());
            bpSummary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            request.setAttribute("BP_SUMMARY", bpSummary);
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
        ArrayList typeList = new ArrayList();
        boolean getChildren = false;
        boolean getParents = false;
        boolean getPathwayRoots = false;
        boolean getPeLeaves = false;
        boolean isComplex = false;

        //  Get different elements, depending on type
        if (record != null) {
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
				request.setAttribute("EXTERNAL_LINKS", getExternalLinks(bpSummary));
			}
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

	/**
	 * Creates external link list(s) and adds them to request object.
	 *
	 * @param bpSummary BioPaxRecordSummary
	 * @return HashMap<String,Reference>
	 * @throws DaoException
	 */
	private HashMap<String,Reference> getExternalLinks(BioPaxRecordSummary bpSummary) 
		throws DaoException {

		// hashset to return
		HashMap<String,Reference> externalLinkSet = new HashMap<String,Reference>();

		// iterate over ExternalLinkRecord from bpSummary
		DaoReference daoReference = new DaoReference();
		List<ExternalLinkRecord> externalLinkRecords = bpSummary.getExternalLinks();
		for (ExternalLinkRecord externalLinkRecord : externalLinkRecords) {

			// get the linked to id
			String linkedToId = externalLinkRecord.getLinkedToId();

			// get the reference object
			Reference reference = daoReference.getRecord(linkedToId);
			if (CPathConstants.CPATH_DO_ASSERT) {
				assert (reference != null) :
				"ShowBioPaxRecord2.setExternalLinks(), reference object is null";
			}
			if (reference == null) continue;

			// add reference string to proper list
			externalLinkSet.put(linkedToId, reference);
		}

		// outta here
		return externalLinkSet;
	}
}
