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
        StringBuffer userMsg = new StringBuffer();

        //  Check for missing snapshot IDs.
        String idStrs[] = request.getParameterValues("SNAPSHOT_ID");
        if (idStrs == null || idStrs.length ==0) {
            xdebug.logMsg(this, "Error:  No data sources selected.");
            userMsg.append("Please select at least one data source.");
            settings.setSnapshotsSelected(null);
        }

        //  Check for missing organism IDs.
        String organismIdStrs[] = request.getParameterValues("ORGANISM_TAXONOMY_ID");
        if (organismIdStrs == null || organismIdStrs.length ==0) {
            xdebug.logMsg(this, "Error:  No organisms selected.");
            if  (userMsg.toString().length() > 0) {
                userMsg.append("<P>&gt; ");
            }
            userMsg.append("Please select at least one organism.");
            settings.setOrganismSelected(null);
        }

        //  Send Error Message to User.
        if (userMsg.toString().length() > 0) {
            request.setAttribute("userMsg", userMsg.toString());
            return mapping.findForward(BaseAction.FORWARD_FAILURE);
        }

        //  Store selected data sources in session object.
        ArrayList idList = new ArrayList();
        for (int i=0; i < idStrs.length; i++) {
            Long id = new Long (Long.parseLong(idStrs[i]));
            idList.add(id);
        }
        settings.setSnapshotsSelected(idList);

        //  Store selected organisms in session object.
        idList = new ArrayList();
        for (int i=0; i < organismIdStrs.length; i++) {
            Integer id = new Integer (Integer.parseInt(organismIdStrs[i]));
            idList.add(id);
        }
        settings.setOrganismSelected(idList);

        //  Redirect user back to where they originally came from.
        String userMessage = "Global Filters Updated.";
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