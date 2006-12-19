// $Id: BaseAction.java,v 1.28 2006-12-19 19:17:20 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
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
     * URL Parameter for Referrer.
     */
    public static final String REFERER = "referer";

    /**
     * URL Parameter for HOME.
     */
    public static final String FORWARD_HOME = "HOME";

    /**
     * URL Parameter for BROWSE.
     */
    public static final String FORWARD_BROWSE = "BROWSE";

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
     * Page Attribute:  XML Assembly.
     */
    public static final String ATTRIBUTE_XML_ASSEMBLY = "xml_assembly";

    /**
     * Page Attribute:  CPath IDs.
     */
    public static final String ATTRIBUTE_CPATH_IDS = "cpath_ids";

    /**
     * Page Attribute:  Total Number of Search Hits.
     */
    public static final String ATTRIBUTE_TOTAL_NUM_HITS = "total_num_hits";

    /**
     * Page Attribute:  Text Fragments.
     */
    public static final String ATTRIBUTE_TEXT_FRAGMENTS = "text_fragments";

    /**
     * Page Attribute:  INTERACTOR Set.
     */
    public static final String ATTRIBUTE_INTERACTOR_SET = "interactor_set";

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
     * Global Property:  Admin Page Active.
     */
    public static final String PROPERTY_ADMIN_MODE_ACTIVE = "admin_mode_active";

    /**
     * Admin page
     */
    public static final String PAGE_IS_ADMIN = "admin_page";

    /**
     * Search Results Page Flag.
     */
    public static final String PAGE_IS_SEARCH_RESULT = "search_result_page";

    /**
     * Page should be automatically updated.
     */
    public static final String PAGE_AUTO_UPDATE = "auto_update";

    /**
     * Page Attribute:  Servlet Name (before forwarding within struts).
     */
    public static final String ATTRIBUTE_SERVLET_NAME = "servlet_name";

    /**
     * Page Attribute:  Request URL (before forwarding within struts);
     */
    public static final String ATTRIBUTE_URL_BEFORE_FORWARDING = "request_url";

    /**
     * Page Attribute:  Page Style.
     */
    public static final String ATTRIBUTE_STYLE = "style";

    /**
     * Page Attribute:  Page Print Style.
     */
    public static final String ATTRIBUTE_STYLE_PRINT = "print";

    /**
     * URL Request.  Test the Error Page
     */
    public static final String PARAMETER_TEST_ERROR_PAGE = "testError";

    /**
     * Yes Value
     */
    public static final String YES = "yes";

    /**
     * Executes Action.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ActionForward forward = null;
        XDebug xdebug = null;
        try {
            xdebug = new XDebug();
            xdebug.startTimer();
            xdebug.logMsg(this, "===========================");
            xdebug.logMsg(this, "Request:  " + request.getRequestURI());
            SnoopHttp snoop = new SnoopHttp(xdebug,
                    getServlet().getServletContext());
            snoop.process(request, response);
            request.setAttribute(ATTRIBUTE_XDEBUG, xdebug);
            request.setAttribute(ATTRIBUTE_SERVLET_NAME,
                    request.getServletPath());
            if (request.getRequestURL() != null
                    && request.getQueryString() != null) {
                request.setAttribute(ATTRIBUTE_URL_BEFORE_FORWARDING,
                        request.getRequestURL().toString() + "?"
                                + request.getQueryString());
            }
            xdebug.logMsg(this, "Running cPath Base Action");
            boolean authorized = isUserAuthorized
                    (mapping, request, response, xdebug);
            if (authorized) {
                forward = subExecute(mapping, form, request, response, xdebug);
            } else {
                forward = mapping.findForward(FORWARD_UNAUTHORIZED);
            }
        } catch (Exception e) {
            request.setAttribute(ATTRIBUTE_EXCEPTION, e);
            forward = mapping.findForward(BaseAction.FORWARD_FAILURE);
        }
        if (forward != null) {
            xdebug.logMsg(this, "Forwarding to Struts:  " + forward.getName());
            xdebug.logMsg(this, "Forwarding to Path:  " + forward.getPath());
        }
        return forward;
    }

    /**
     * Sets User Display Message.
     *
     * @param request Http Servlet Request.
     * @param msg     User Message.
     */
    public void setUserMessage(HttpServletRequest request,
            String msg) {
        request.setAttribute(ATTRIBUTE_USER_MSG, msg);
    }

    /**
     * Determine if User is Authorized to access this action.
     * May be overriden by sub-classes.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
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
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public abstract ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception;
}
