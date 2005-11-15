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
