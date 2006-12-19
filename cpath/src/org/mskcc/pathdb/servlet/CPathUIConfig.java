// $Id: CPathUIConfig.java,v 1.10 2006-12-19 19:17:20 cerami Exp $
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
     * Flag to show Data Sources.
     */
    private static boolean showDataSourceDetails;

    /**
     * Inactive Flag
     */
    public static final int INACTIVE = 0;

    /**
     * Active Flag
     */
    public static final int ACTIVE = 1;

    /**
     * Admin Mode Active.
     */
    private static int adminModeActive;

    /**
     * Reference to WebUIBean.
     */
    private static WebUIBean webUIBean;

    /**
     * Web Skin.
     */
    private static String webSkin;

    /**
     * Sets the Web Mode.
     *
     * @param mode WEB_MODE_BIOPAX or WEB_MODE_PSI_MI.
     */
    public static void setWebMode(int mode) {
        if (mode != WEB_MODE_BIOPAX && mode != WEB_MODE_PSI_MI) {
            throw new IllegalArgumentException("mode must be set to:  "
                    + "WEB_MODE_BIOPAX or WEB_MODE_PSI_MI.");
        }
        webMode = mode;
    }

    /**
     * Gets the Web Mode.
     *
     * @return WEB_MODE_BIOPAX or WEB_MODE_PSI_MI.
     */
    public static int getWebMode() {
        return webMode;
    }

    /**
     * Sets Admin Mode Active.
     *
     * @param activeMode ACTIVE or INACTIVE.
     */
    public static void setAdminModeActive(int activeMode) {
        if (activeMode != ACTIVE && activeMode != INACTIVE) {
            throw new IllegalArgumentException("activeMode must be set to:  "
                    + "ACTIVE or INACTIVE.");
        }
        adminModeActive = activeMode;
    }

    /**
     * Gets the Admin Mode Active.
     *
     * @return ACTIVE or INACTIVE.
     */
    public static int getAdminModeActive() {
        return adminModeActive;
    }

    /**
     * Sets the Show Datasource Details Flag.
     *
     * @param flag true or false
     */
    public static void setShowDataSourceDetails (boolean flag) {
        showDataSourceDetails = flag;
    }

    /**
     * Show Data Souce Details?
     * @return true or false.
     */
    public static boolean getShowDataSourceDetails() {
        return showDataSourceDetails;
    }

    /**
     * Sets the WebUIBean reference.
     *
     * @param bean WebUIBean.
     */
    public static void setWebUIBean(WebUIBean bean) {
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

    /**
     * Gets Web Skin Value.
     * @return web skin value.
     */
    public static String getWebSkin () {
        return webSkin;
    }

    /**
     * Sets Web Skin Value.
     * @param webSkin web skin value.
     */
    public static void setWebSkin (String webSkin) {
        CPathUIConfig.webSkin = webSkin;
    }

    public static String getPath (String fileName) {
        return "../skins/" + webSkin + "/" + fileName;
    }
}