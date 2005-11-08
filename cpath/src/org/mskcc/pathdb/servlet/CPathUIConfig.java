package org.mskcc.pathdb.servlet;

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

    private static int webMode;

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

}
