package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Toggles the Search Options on/off.
 *
 * @author Ethan Cerami
 */
public class ToggleSearchOptions extends BaseAction {
    /**
     * SearchOptionsFlag Named Stored in Session.
     */
    public static final String SESSION_SEARCH_OPTIONS_FLAG
            = "search_options_flag";

    /**
     * Toggles Search Options on/off.
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
        HttpSession session = request.getSession();
        Boolean searchOptionsFlag = (Boolean)
                session.getAttribute(SESSION_SEARCH_OPTIONS_FLAG);
        if (searchOptionsFlag == null) {
            searchOptionsFlag = new Boolean (false);
        }
        xdebug.logMsg(this, "Search Options Flag was:  " + searchOptionsFlag);
        searchOptionsFlag = new Boolean (!searchOptionsFlag.booleanValue());
        session.setAttribute(SESSION_SEARCH_OPTIONS_FLAG, searchOptionsFlag);
        xdebug.logMsg(this, "Search Options Flag is:  " + searchOptionsFlag);
        if (searchOptionsFlag.booleanValue()) {
            this.setUserMessage(request, "Now Showing Field Specific Filter.");
        } else {
            this.setUserMessage(request, "Now Hiding Field Specific Filter.");
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
