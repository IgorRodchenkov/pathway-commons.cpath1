package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shows Data Source Details.
 *
 * @author Ethan Cerami
 */
public class ShowDataSourceDetails extends BaseAction {

    /**
     * Executes DB Lookup.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response,
                                    XDebug xdebug) throws Exception {
        String snapshotIdStr = request.getParameter("snapshot_id");
        if (snapshotIdStr == null) {
            throw new IllegalArgumentException ("parameter snapshot_id must be specified.");
        }
        long snapshotId = -1;
        try {
            snapshotId = Long.parseLong(snapshotIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException ("parameter snapshot_id must be an integer value.");
        }

        if (snapshotId < 0) {
            throw new IllegalArgumentException ("parameter snapshot_id must be > 0.");
        }
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ExternalDatabaseSnapshotRecord snapshotRecord = dao.getDatabaseSnapshot(snapshotId);
        if (snapshotRecord == null) {
            throw new IllegalArgumentException ("record id " + snapshotId
                + " does not exist in database.");
        }

        request.setAttribute("SNAPSHOT_RECORD", snapshotRecord);
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

}
