package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.task.GlobalTaskList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action for Main cPath Admin Home Page.
 *
 * @author Ethan Cerami
 */
public class AdminHomeAction extends AdminBaseAction {

    /**
     * Executes Home Page.
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
        GlobalTaskList list = GlobalTaskList.getInstance();
        if (list.oneOrMoreTasksAreActive()) {
            request.setAttribute(BaseAction.PAGE_AUTO_UPDATE,
                BaseAction.YES);
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
