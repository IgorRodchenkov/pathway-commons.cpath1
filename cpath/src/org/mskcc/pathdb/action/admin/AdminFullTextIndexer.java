package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.tool.LoadFullText;
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
     * @param mapping Struts ActionMapping Object.
     * @param form Struts ActionForm Object.
     * @param request Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        xdebug.logMsg(this, "Running Full Text Indexer");

        LoadFullText indexer = new LoadFullText(false);
        indexer.indexAllPhysicalEntities();
        this.setUserMessage(request, "All Records have been indexed");
        return mapping.findForward("success");
    }
}
