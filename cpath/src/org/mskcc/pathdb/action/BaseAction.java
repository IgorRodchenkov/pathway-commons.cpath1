package org.mskcc.pathdb.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.xdebug.SnoopHttp;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Base Struts Action Class.
 *
 * @author Ethan Cerami
 */
public abstract class BaseAction extends Action {

    /**
     * Executes Action.
     * @param mapping Struts ActionMapping Object.
     * @param form Struts ActionForm Object.
     * @param request Http Servlet Request.
     * @param response Http Servlet Response.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        XDebug xdebug = new XDebug();
        xdebug.startTimer();
        SnoopHttp snoop = new SnoopHttp(xdebug,
                getServlet().getServletContext());
        snoop.process(request, response);
        request.setAttribute("xdebug", xdebug);
        ActionForward forward =
                subExecute(mapping, form, request, response, xdebug);
        xdebug.stopTimer();
        return forward;
    }

    /**
     * Sets User Display Message.
     * @param request Http Servlet Request.
     * @param msg User Message.
     */
    public void setUserMessage(HttpServletRequest request,
            String msg) {
        request.setAttribute("userMsg", msg);
    }

    /**
     * Executes SubAction.
     * Must be implemented by Subclass.
     * @param mapping Struts ActionMapping Object.
     * @param form Struts ActionForm Object.
     * @param request Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public abstract ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception;
}
