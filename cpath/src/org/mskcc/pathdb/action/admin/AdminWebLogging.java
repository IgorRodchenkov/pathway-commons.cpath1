/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Administrative Action for Activating/Deactivating Web Logging
 * and Diagnostics.
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
    public ActionForward adminExecute(ActionMapping mapping,
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
