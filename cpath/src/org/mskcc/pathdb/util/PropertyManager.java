package org.mskcc.pathdb.util;

/**
 * Package Comments
 *
 * @author cerami
 */
public class PropertyManager {
    private String gridHost;
    private String gridUser;
    private String gridPassword;
    private String logFile = "log/ds_console.log";

    private static PropertyManager manager;

    private PropertyManager () {
    }

    public static PropertyManager getInstance() {
        if (manager == null) {
            manager = new PropertyManager();
        }
        return manager;
    }

    public String getGridHost() {
        return gridHost;
    }

    public void setGridHost(String gridHost) {
        this.gridHost = gridHost;
    }

    public String getGridUser() {
        return gridUser;
    }

    public void setGridUser(String gridUser) {
        this.gridUser = gridUser;
    }

    public String getGridPassword() {
        return gridPassword;
    }

    public void setGridPassword(String gridPassword) {
        this.gridPassword = gridPassword;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }
}
