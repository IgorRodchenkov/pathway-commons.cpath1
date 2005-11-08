//
// $Id: WebUIForm.java,v 1.1 2005-11-08 17:57:01 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.form;

// imports
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import javax.servlet.http.HttpServletRequest;

/**
 * Struts ActionForm for updating/retrieving web ui elements.
 *
 * @author Benjamin Gross
 */
public class WebUIForm extends ActionForm {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	/**
	 * Logo (URL to Logo).
	 */
    private String logo;

	/**
	 * Home Page Title.
	 */
    private String homePageTitle;

    /**
     * Sets the logo.
     *
     * @param logo String.
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * Gets the logo.
     *
     * @return logo.
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Sets the Home Page Title.
     *
     * @param homePageTitle String.
     */
    public void setHomePageTitle(String homePageTitle) {
        this.homePageTitle = homePageTitle;
    }

    /**
     * Gets the Home Page Title.
     *
     * @return homePageTitle.
     */
    public String getHomePageTitle() {
        return homePageTitle;
    }

	/**
	 * Our implementation of validate.
	 *
	 * @param mapping ActionMapping reference.
	 * @param request HttpServletRequest.
	 * @return ActionErrors reference.
	 *
	 */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

		// create errors object to return
		ActionErrors errors = new ActionErrors();

		logger.info("INFO in validate()");
		logger.info("logo: " + logo);
		logger.info("homePageTile: " + homePageTitle);

		// logo
		if (logo == null || logo.equals("")) {
			logger.info("logo is null or empty");
			errors.add("logo", new ActionError("error.missing.logo"));
		}

		// homePageTitle
		if (homePageTitle == null || homePageTitle.equals("")) {
			logger.info("homePageTitle is null or empty");
			errors.add("homePageTitle", new ActionError("error.missing.homePageTitle"));
		}

		// outta here
		return errors;
	}
}
