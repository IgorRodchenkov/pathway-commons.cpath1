package org.mskcc.pathdb.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.xdebug.SnoopHttp;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base Struts Action Class.
 * All Client Access is funnelled through this code.
 *
 * @author Ethan Cerami
 */
public abstract class BaseAction extends Action {

    /**
     * Struts Forward:  FAILURE.
     */
    public static final String FORWARD_FAILURE = "failure";

    /**
     * Struts Forward:  SUCCESS.
     */
    public static final String FORWARD_SUCCESS = "success";

    /**
     * Struts Forward:  UNAUTHORIZED ACCESS.
     */
    public static final String FORWARD_UNAUTHORIZED = "unauthorized";

    /**
     * Struts Forward:  HELP.
     */
    public static final String FORWARD_HELP = "help";

    /**
     * Struts Forward:  FULL TEXT SEARCH
     */
    public static final String FORWARD_FULL_TEXT_SEARCH = "fullTextSearch";

    /**
     * Page Attribute:  XDEBUG Object.
     */
    public static final String ATTRIBUTE_XDEBUG = "xdebug";

    /**
     * Page Attribute:  EXCEPTION Object.
     */
    public static final String ATTRIBUTE_EXCEPTION = "exception";

    /**
     * Page Attribute:  USER MESSAGE Object.
     */
    public static final String ATTRIBUTE_USER_MSG = "userMsg";

    /**
     * Page Attribute:  PROTOCOL REQUEST Object.
     */
    public static final String ATTRIBUTE_PROTOCOL_REQUEST = "protocol_request";

    /**
     * Page Attribute:  INTERACTIONS ArrayList.
     */
    public static final String ATTRIBUTE_INTERACTIONS = "interactions";

    /**
     * Page Attribtue:  TITLE of HTML Page.
     */
    public static final String ATTRIBUTE_TITLE = "title";

    /**
     * Global Property:  Admin User Name.
     */
    public static final String PROPERTY_ADMIN_USER = "admin_user";

    /**
     * Global Property:  Admin Password.
     */
    public static final String PROPERTY_ADMIN_PASSWORD = "admin_password";

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
        try {
            XDebug xdebug = new XDebug();
            xdebug.startTimer();
            SnoopHttp snoop = new SnoopHttp(xdebug,
                    getServlet().getServletContext());
            snoop.process(request, response);
            request.setAttribute(ATTRIBUTE_XDEBUG, xdebug);
            xdebug.logMsg(this, "Running cPath Base Action");
            boolean authorized = isUserAuthorized
                    (mapping, request, response, xdebug);
            if (authorized) {
                ActionForward forward =
                        subExecute(mapping, form, request, response, xdebug);
                xdebug.stopTimer();
                return forward;
            } else {
                return mapping.findForward(FORWARD_UNAUTHORIZED);
            }
        } catch (Exception e) {
            request.setAttribute(ATTRIBUTE_EXCEPTION, e);
            return mapping.findForward(BaseAction.FORWARD_FAILURE);
        }
    }

    /**
     * Sets User Display Message.
     * @param request Http Servlet Request.
     * @param msg User Message.
     */
    public void setUserMessage(HttpServletRequest request,
            String msg) {
        request.setAttribute(ATTRIBUTE_USER_MSG, msg);
    }

    /**
     * Is the User Authorized to access this Action Class?
     * May be overriden by sub-classes.
     * @param mapping Struts ActionMapping Object.
     * @param request Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug XDebug Object.
     */
    protected boolean isUserAuthorized(ActionMapping mapping,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws IOException {
        xdebug.logMsg(this, "Page is not protected.  "
                + "User is authorized");
        return true;
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