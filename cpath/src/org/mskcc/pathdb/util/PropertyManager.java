package org.mskcc.pathdb.util;

/**
 * Singleton Property  Manager.
 *
 * @author Ethan Cerami
 */
public class PropertyManager {
    /**
     * Grid Host Name.
     */
    private String gridHost;

    /**
     * Grid User Name.
     */
    private String gridUser;

    /**
     * Grid Password.
     */
    private String gridPassword;

    /**
     * Default Location for Log File.
     */
    private String logFile = "log/ds_console.log";

    /**
     * Single Instance of Property Manager.
     */
    private static PropertyManager manager;

    /**
     * Private Constructor.
     */
    private PropertyManager() {
    }

    /**
     * Get Instance Method.
     * @return Property Manager Instance.
     */
    public static PropertyManager getInstance() {
        if (manager == null) {
            manager = new PropertyManager();
        }
        return manager;
    }

    /**
     * Gets GRID Host Name.
     * @return GRID Host Name.
     */
    public String getGridHost() {
        return gridHost;
    }

    /**
     * Sets GRID Host Name.
     * @param gridHost GRID Host Name.
     */
    public void setGridHost(String gridHost) {
        this.gridHost = gridHost;
    }

    /**
     * Gets GRID User Name.
     * @return GRID User Name.
     */
    public String getGridUser() {
        return gridUser;
    }

    /**
     * Sets GRID User Name.
     * @param gridUser GRID User Name.
     */
    public void setGridUser(String gridUser) {
        this.gridUser = gridUser;
    }

    /**
     * Gets GRID Password.
     * @return GRID Password.
     */
    public String getGridPassword() {
        return gridPassword;
    }

    /**
     * Sets GRID Password.
     * @param gridPassword GRID Password.
     */
    public void setGridPassword(String gridPassword) {
        this.gridPassword = gridPassword;
    }

    /**
     * Gets Log File Location.
     * @return Log File Location.
     */
    public String getLogFile() {
        return logFile;
    }

    /**
     * Sets Log File Location.
     * @param logFile Log File Location.
     */
    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }
}