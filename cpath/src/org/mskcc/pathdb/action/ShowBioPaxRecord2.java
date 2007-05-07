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

/**
 * Action to show one specific BioPAX Record.
 *
 * @author Ethan Cerami
 */
public class ShowBioPaxRecord2 extends BaseAction {
    /**
     * BioPAX Summary Attribute name.
     */
    public static String BP_SUMMARY = "BP_SUMMARY";

    /**
     * Entity Summary Attribute name.
     */
    public static String ENTITY_SUMMARY = "ENTITY_SUMMARY";

    /**
     * External Links Attribute name.
     */
    public static String EXTERNAL_LINKS = "EXTERNAL_LINKS";

    /**
     * Types List Attributes name;  used to create the tabs on the JSP.
     */
    public static String TYPES_LIST = "TYPES_LIST";

    /**
     * Out of Scope Error.
     */
    public static String OUT_OF_SCOPE = "out_of_scope";

    /**
     * Incoming ID Parameter.
     */
    public static String ID_PARAMETER = "id";

    /**
     * Executes Action.
     * @param mapping       ActionMapping Object.
     * @param form          ActionForm Object.
     * @param request       Http Servlet Request.
     * @param response      Http Servlet Response.
     * @param xdebug        XDebug Object.
     * @return              Action Forward Object.
     * @throws Exception    All Errors.
     */
    public ActionForward subExecute (ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, XDebug xdebug)
            throws Exception {
        DaoCPath dao = DaoCPath.getInstance();

        //  Get the "id" parameter (required)
        String id = request.getParameter(ID_PARAMETER);
        CPathRecord record = null;
        BioPaxRecordSummary bpSummary = null;

        //  Basic "id" parameter validation
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

            //  Get BioPAX record summary, and pass along to JSP
            bpSummary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            request.setAttribute(BP_SUMMARY, bpSummary);

            //  If this is an interaction record, get its entity summary.
            //  The entity summary provides a short summary of the interaction.
            //  For example A + B --> C
            if (record.getType() == CPathRecordType.INTERACTION) {
                EntitySummaryParser parser = new EntitySummaryParser(Long.parseLong(id));
                EntitySummary entitySummary = parser.getEntitySummary();
                request.setAttribute(ENTITY_SUMMARY, entitySummary);
            }
        }

        //  Get user's current filter settings
        GlobalFilterSettings filterSettings = getCurrentFilterSettings(request, xdebug);
        int taxId = getTaxonomyIdFilter(filterSettings, xdebug);
        long snapshotIds[] = getSnapshotFilter(filterSettings, xdebug);
        Set snapshotIdSet = filterSettings.getSnapshotIdSet();

        //  If the requested record is not in the user's set of selected data sources,
        //  we have an "out of scope" error.
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
            //  If a pathway, get direct children and physical entity "leaves"
            getChildren = true;
            getPeLeaves = true;
        } else if (record.getType() == CPathRecordType.INTERACTION) {
            //  If an interaction, get pathway "roots" and physical entity "leaves"
            getPeLeaves = true;
            getPathwayRoots = true;
        } else {
            //  If a physical entity, get direct parents, and pathway "roots"
            xdebug.logMsg (this, "Record specific type:  " + record.getSpecificType());
            getParents = true;
            getPathwayRoots = true;
            if (record.getSpecificType().toLowerCase().equals("complex")) {
                //  If a complex, this is a special case;  also got children.
                isComplex = true;
                getChildren = true;
            }
        }

        DaoInternalLink daoLinker = new DaoInternalLink();

        // set external links
        if (bpSummary != null) {
            ReferenceUtil refUtil = new ReferenceUtil();
            ArrayList bpSummaryList = new ArrayList ();
            bpSummaryList.add(bpSummary);
            request.setAttribute(EXTERNAL_LINKS, refUtil.getReferenceMap(bpSummaryList, xdebug));
        }

        //  Get count information, required to create the tabs for e.g. children, parents,
        //  pathway roots, etc.

        //  get children count
        if (getChildren) {
            xdebug.logMsg (this, "Getting all children");
            ArrayList childList = getChildTypes(xdebug, daoLinker, id, taxId, snapshotIds,
                    isComplex);
            typeList.addAll(childList);
        }

        //  get parents count
        if (getParents) {
            xdebug.logMsg (this, "Getting all parents");
            ArrayList parentList = getParentTypes(xdebug, daoLinker, id, taxId, snapshotIds);
            typeList.addAll(parentList);
        }

        //  get pathway roots count
        if (getPathwayRoots) {
            xdebug.logMsg (this, "Getting all pathway roots");
            TypeCount typeCount = getPathwayRoots(id, filterSettings, xdebug);
            typeList.add(typeCount);
        }

        //  get physical entity leaf count
        if (getPeLeaves) {
            xdebug.logMsg (this, "Getting all physical entity leaves");
            TypeCount typeCount = getPeLeaves(id, xdebug);
            typeList.add(typeCount);
        }


        xdebug.logMsg(this, "Total number of tabs:  " + typeList.size());
        request.setAttribute(TYPES_LIST, typeList);

        //  Forward to JSP page for HTML creation.
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    /**
     * Gets number of physical entity leaves.
     */
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

    /**
     * Gets number of pathway roots.
     */
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

    /**
     * Gets type/number of children.
     */
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

    /**
     * Gets type/number of parents.
     */
    private ArrayList getParentTypes (XDebug xdebug, DaoInternalLink daoLinker, String id,
            int taxId, long[] snapshotIds)
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

    /**
     *  Removes control interactions, since these are now shown as the
     *  parents of biochemical reactions.
     */
    private void removeControlInteractions (ArrayList typeList, XDebug xdebug) {
        int controlIndex = -1;
        for (int i=0; i<typeList.size(); i++) {
            TypeCount typeCount = (TypeCount) typeList.get(i);
            xdebug.logMsg(this, "Specific type:  " + typeCount.getType()
                + " -->  " + typeCount.getCount() + " records");
            if (typeCount.getType().equals(BioPaxConstants.CONTROL)
                    || typeCount.getType().equals(BioPaxConstants.CATALYSIS)) {
                controlIndex = i;
            }
        }
       if (controlIndex > 0) {
            typeList.remove(controlIndex);
            xdebug.logMsg(this, "Removing control interactions");
        }
    }
}
