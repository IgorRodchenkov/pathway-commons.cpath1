package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.lucene.OrganismStats;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.util.GlobalCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Administrative Action for Reseting the Organism Stats.
 *
 * @author Ethan Cerami.
 */
public class AdminResetOrganismStats extends AdminBaseAction {

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
        OrganismStats orgStats = new OrganismStats();
        orgStats.resetStats();

        GlobalCache cache = GlobalCache.getInstance();
        cache.resetCache();
        
        this.setUserMessage(request, "Organism Stats have been reset.");
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
