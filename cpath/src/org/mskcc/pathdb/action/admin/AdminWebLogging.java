package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.action.BaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Activates/Deactivates Web Logging and Diagnostics.
 *
 * @author Ethan Cerami
 */
public class AdminWebLogging extends AdminBaseAction {
    /**
     * Session Parameter:  cPath Web Logging.
     */
    public static final String WEB_LOGGING = "debug";

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
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        HttpSession session = request.getSession();
        String xdebugFlag = (String) session.getAttribute(WEB_LOGGING);
        if (xdebugFlag != null) {
            session.removeAttribute(WEB_LOGGING);
            this.setUserMessage(request, "Web Diagnostics Deactivated");
        } else {
            session.setAttribute(WEB_LOGGING, "on");
            this.setUserMessage(request, "Web Diagnostics Activated");
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
