// $Id: CPathUIConfig.java,v 1.6 2006-02-21 23:13:16 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.servlet;

// imports
import org.mskcc.pathdb.form.WebUIBean;

/**
 * Encapsulates Configuration options for the cPath Web UI.
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class CPathUIConfig {
    /**
     * Flag for BioPAX Web Mode.
     */
    public static final int WEB_MODE_BIOPAX = 0;

    /**
     * Flag for PSI-MI Web Mode.
     */
    public static final int WEB_MODE_PSI_MI = 1;

    /**
     * PSI-MI String.
     */
    public static final String PSI_MI = "psi_mi";

    /**
     * BioPAX String.
     */
    public static final String BIOPAX = "biopax";

	/**
	 * WebMode.
	 */
    private static int webMode;

    /**
     * Flag for AdminMode Deactive.
     */
    public static final int ADMIN_MODE_DEACTIVE = 0;

    /**
     * Flag for AdminMode Active.
     */
    public static final int ADMIN_MODE_ACTIVE = 1;

	/**
	 * Admin Mode Active.
	 */
    private static int adminModeActive;

    /**
     * Reference to WebUIBean.
     */
    private static WebUIBean webUIBean;

    /**
     * Sets the Web Mode.
     * @param mode WEB_MODE_BIOPAX or WEB_MODE_PSI_MI.
     */
    public static void setWebMode (int mode) {
        if (mode != WEB_MODE_BIOPAX && mode != WEB_MODE_PSI_MI) {
            throw new IllegalArgumentException ("mode must be set to:  "
                + "WEB_MODE_BIOPAX or WEB_MODE_PSI_MI.");
        }
        webMode = mode;
    }

    /**
     * Gets the Web Mode.
     * @return WEB_MODE_BIOPAX or WEB_MODE_PSI_MI.
     */
    public static int getWebMode() {
        return webMode;
    }

	/**
	 * Sets Admin Mode Active.
	 *
	 * @param adminModeActive ADMIN_MODE_ACTIVE or ADMIN_MODE_DEACTIVE.
	 */
    public static void setAdminModeActive (int activeMode) {
        if (activeMode != ADMIN_MODE_ACTIVE && activeMode != ADMIN_MODE_DEACTIVE) {
            throw new IllegalArgumentException ("activeMode must be set to:  "
                + "ADMIN_MODE_ACTIVE or ADMIN_MODE_DEACTIVE.");
        }
        adminModeActive = activeMode;
    }
	
    /**
     * Gets the Admin Mode Active.
     * @return ADMIN_MODE_ACTIVE or ADMIN_MODE_DEACTIVE.
     */
    public static int getAdminModeActive() {
        return adminModeActive;
    }

    /**
     * Sets the WebUIBean reference.
	 *
     * @param webUIBean.
     */
    public static void setWebUIBean (WebUIBean bean) {
        webUIBean = bean;
    }

    /**
     * Gets the WebUIBean ref.
	 *
     * @return webUIBean.
     */
    public static WebUIBean getWebUIBean() {
        return webUIBean;
    }
}
