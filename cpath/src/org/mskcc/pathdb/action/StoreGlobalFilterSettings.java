package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.GlobalFilterSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Stores Global Filter Settings
 *
 * @author Ethan Cerami.
 */
public class StoreGlobalFilterSettings extends BaseAction {

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
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        HttpSession session = request.getSession();
        GlobalFilterSettings settings = (GlobalFilterSettings)
                session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
        if (settings == null) {
            settings = new GlobalFilterSettings();
            session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, settings);
        }
        String idStrs[] = request.getParameterValues("SNAPSHOT_ID");
        if (idStrs == null || idStrs.length ==0) {
            request.setAttribute("userMsg", "Please select at least one data source.");
            settings.setSnapshotsSelected(null);
            return mapping.findForward(BaseAction.FORWARD_FAILURE);
        } else {
            ArrayList idList = new ArrayList();
            for (int i=0; i < idStrs.length; i++) {
                Long id = new Long (Long.parseLong(idStrs[i]));
                idList.add(id);
            }
            settings.setSnapshotsSelected(idList);
            String userMessage = "Global Filters Set";
            request.setAttribute("userMsg", userMessage);
            String referalUrl = (String) session.getAttribute("Referer");
            if (referalUrl != null) {
                if (referalUrl.indexOf("?") > -1) {
                    response.sendRedirect(referalUrl + "&userMsg="+ userMessage +"");
                } else {
                    response.sendRedirect(referalUrl + "?userMsg="+ userMessage +"");
                }
                return null;
            } else {
                return mapping.findForward(BaseAction.FORWARD_SUCCESS);
            }
        }
    }
}