package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Administrative Action for purging all Records in Xml Cache.
 *
 * @author Ethan Cerami
 */
public class AdminPurgeXmlCache extends AdminBaseAction {

    /**
     * Executes Action.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward adminExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        DaoXmlCache dao = new DaoXmlCache(xdebug);
        dao.deleteAllRecords();
        this.setUserMessage(request, "All Records in XML Cache have "
                + "been purged.");
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
