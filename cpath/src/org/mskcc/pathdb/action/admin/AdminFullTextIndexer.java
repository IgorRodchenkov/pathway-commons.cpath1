package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.task.IndexLuceneTask;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Administrative Action for Running the Full Text Indexer.
 *
 * @author Ethan Cerami.
 */
public class AdminFullTextIndexer extends AdminBaseAction {

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
        xdebug.logMsg(this, "Running Full Text Indexer");

        IndexLuceneTask task = new IndexLuceneTask(false, xdebug);
        task.start();
        this.setUserMessage(request, "Index task is now running.");

        //  Set Auto-Update Flag
        request.setAttribute(BaseAction.PAGE_AUTO_UPDATE,
                BaseAction.YES);

        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
