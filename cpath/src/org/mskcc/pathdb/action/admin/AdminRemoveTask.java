package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.task.GlobalTaskList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminRemoveTask extends AdminBaseAction {
    /**
     * Index Parameter of Task to Remove.
     */
    public static final String INDEX_PARAMETER = "index";

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
        removeTask(request, xdebug);
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private void removeTask(HttpServletRequest request, XDebug xdebug) {
        String index = request.getParameter(INDEX_PARAMETER);
        try {
            int i = Integer.parseInt(index);
            xdebug.logMsg(this, "Removing Task:  " + i);
            GlobalTaskList globalTaskList = GlobalTaskList.getInstance();
            globalTaskList.removeTask(i);
            setUserMessage(request, "Task Removed.");
        } catch (NumberFormatException e) {
            setUserMessage(request, "Invalid Index Number.");
        }
    }

}
