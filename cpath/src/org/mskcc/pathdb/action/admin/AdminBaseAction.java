package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.lucene.LuceneIndexer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Base Class for for Administrative Actions.
 *
 * @author Ethan Cerami
 */
public class AdminBaseAction extends BaseAction {

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
        LuceneIndexer indexer = new LuceneIndexer();
        String dir = indexer.getDirectory();
        xdebug.logMsg(this, "Lucene Index Directory:  " + dir);
        return mapping.findForward("success");
    }
}
