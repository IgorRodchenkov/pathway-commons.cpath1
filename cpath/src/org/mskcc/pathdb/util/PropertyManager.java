package org.mskcc.pathdb.util;

/**
 * Singleton Property  Manager.
 *
 * @author Ethan Cerami
 */
public class PropertyManager {
    /**
     * DB Host Name.
     */
    private String dbHost;

    /**
     * Grid User Name.
     */
    private String dbUser;

    /**
     * Grid Password.
     */
    private String dbPassword;

    /**
     * Default Location for Log Config File.
     */
    private String logConfigFile = "config/config-JDBC.properties";

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
    public String getDbHost() {
        return dbHost;
    }

    /**
     * Sets GRID Host Name.
     * @param dbHost GRID Host Name.
     */
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    /**
     * Gets GRID User Name.
     * @return GRID User Name.
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Sets GRID User Name.
     * @param dbUser GRID User Name.
     */
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    /**
     * Gets GRID Password.
     * @return GRID Password.
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Sets GRID Password.
     * @param dbPassword GRID Password.
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    /**
     * Gets Log File Location.
     * @return Log File Location.
     */
    public String getLogConfigFile() {
        return logConfigFile;
    }

    /**
     * Sets Log File Location.
     * @param logFile Log File Location.
     */
    public void setLogConfigFile(String logFile) {
        this.logConfigFile = logFile;
    }
}