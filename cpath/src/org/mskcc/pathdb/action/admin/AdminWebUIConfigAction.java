// $Id: AdminWebUIConfigAction.java,v 1.8 2006-02-27 15:02:16 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

import org.mskcc.pathdb.form.WebUIBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.servlet.CPathUIConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action for Web UI Configuration Page.
 *
 * @author Benjamin Gross
 */
public class AdminWebUIConfigAction extends AdminBaseAction {

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
    public ActionForward adminExecute(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      XDebug xdebug) throws Exception {

        // only process if we are authorized
        if (CPathUIConfig.getAdminModeActive() ==
                CPathUIConfig.ADMIN_MODE_DEACTIVE) {
            return mapping.findForward(BaseAction.FORWARD_UNAUTHORIZED);
        }

        // populate webUIBean if necessary
        if (form != null) {
            populateWebUIBean(form);
        }

        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    /**
     * Must Be Implemented By Subclass.
     *
     * @param form Struts ActionForm Object.
     * @throws Exception All Exceptions.
     */
    private void populateWebUIBean(ActionForm form) throws Exception {

        // cast form
        WebUIBean webUIBean = (WebUIBean) form;

        // only retrieve form data if the form is empty
        if (webUIBean.getApplicationName() == null) {
            WebUIBean record = CPathUIConfig.getWebUIBean();
            webUIBean.setApplicationName(record.getApplicationName());
            webUIBean.setHomePageHeader(record.getHomePageHeader());
            webUIBean.setHomePageTagLine(record.getHomePageTagLine());
            webUIBean.setHomePageRightColumnContent(record.getHomePageRightColumnContent());
            webUIBean.setDisplayBrowseByPathwayTab(record.getDisplayBrowseByPathwayTab());
            webUIBean.setDisplayBrowseByOrganismTab(record.getDisplayBrowseByOrganismTab());
            webUIBean.setFAQPageContent(record.getFAQPageContent());
            webUIBean.setAboutPageContent(record.getAboutPageContent());
            webUIBean.setMaintenanceTagLine(record.getMaintenanceTagLine());
        }
    }
}
