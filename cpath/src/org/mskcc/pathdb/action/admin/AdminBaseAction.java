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

import org.mskcc.pathdb.form.WebUIForm;
import org.mskcc.pathdb.sql.dao.DaoWebUI;
import org.mskcc.pathdb.sql.dao.DaoException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.xdebug.XDebug;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base Class for for Administrative Actions.
 *
 * @author Ethan Cerami
 */
public abstract class AdminBaseAction extends BaseAction {

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
        request.setAttribute(BaseAction.PAGE_IS_ADMIN, "YES");
        checkErrorPage(request, xdebug);

		// populate webUIForm if necessary
		if (form != null){
			populateWebUIForm(form, xdebug);
		}

        return adminExecute(mapping, form, request, response, xdebug);
    }

    /**
     * Must Be Implemented By Subclass.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    protected abstract ActionForward adminExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception;

    protected boolean isUserAuthorized(ActionMapping mapping,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws IOException {
        xdebug.logMsg(this, "Page is protected.  Available to Admin User only");
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            askForPassword(response);
            return false;
        } else {
            return validateUserPassword(authorization, xdebug, response);
        }
    }

    private boolean validateUserPassword(String authorization, XDebug xdebug,
            HttpServletResponse response) throws IOException {
        PropertyManager pManager = PropertyManager.getInstance();
        String adminUser = pManager.getProperty(BaseAction.PROPERTY_ADMIN_USER);
        String adminPassword = pManager.getProperty
                (BaseAction.PROPERTY_ADMIN_PASSWORD);
        String userInfo = authorization.substring(6).trim();
        BASE64Decoder decoder = new BASE64Decoder();
        String nameAndPassword =
                new String(decoder.decodeBuffer(userInfo));
        int index = nameAndPassword.indexOf(":");
        String user = nameAndPassword.substring(0, index);
        String password = nameAndPassword.substring(index + 1);
        xdebug.logMsg(this, "User Name:  " + user);
        xdebug.logMsg(this, "Password:  " + password);
        if (user.equals(adminUser) && password.equals(adminPassword)) {
            xdebug.logMsg(this, "User/Password Correct");
            return true;
        } else {
            askForPassword(response);
            return false;
        }
    }

    private void askForPassword(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate",
                "BASIC realm=\"cPath Admin\"");
    }

    /**
     * This method generates a sample Error Page.  It is useful for testing
     * purposes only.
     */
    private void checkErrorPage(HttpServletRequest request, XDebug xdebug)
            throws Exception {
        String testError = request.getParameter(PARAMETER_TEST_ERROR_PAGE);
        if (testError != null) {
            xdebug.logMsg(this, "Throwing an Exception to Test Error Page");
            throw new Exception("This is a test of the Error"
                    + " page functionality");
        }
    }

    /**
     * Must Be Implemented By Subclass.
     *
     * @param form     Struts ActionForm Object.
     * @param xdebug   XDebug Object.

     * @throws Exception All Exceptions.
     */
	private void populateWebUIForm(ActionForm form, XDebug xdebug) throws Exception {

		// cast form
		WebUIForm webUIForm = (WebUIForm) form;

		// only retrieve form data if the form is empty
		if (webUIForm.getLogo() == null){

			// create dao object
			DaoWebUI dbWebUI = new DaoWebUI();
			WebUIForm record = dbWebUI.getRecord();

			// set fields
			webUIForm.setLogo(record.getLogo());
			webUIForm.setHomePageTitle(record.getHomePageTitle());
			webUIForm.setHomePageTagLine(record.getHomePageTagLine());
            webUIForm.setHomePageRightColumnContent(record.getHomePageRightColumnContent());
            webUIForm.setDisplayBrowseByPathwayTab(record.getDisplayBrowseByPathwayTab());
            webUIForm.setDisplayBrowseByOrganismTab(record.getDisplayBrowseByOrganismTab());
            webUIForm.setFAQPageContent(record.getFAQPageContent());
            webUIForm.setAboutPageContent(record.getAboutPageContent());
            webUIForm.setHomePageMaintenanceTagLine(record.getHomePageMaintenanceTagLine());
		}
	}
}
