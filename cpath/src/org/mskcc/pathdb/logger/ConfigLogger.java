package org.mskcc.pathdb.logger;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.mskcc.dataservices.util.PropertyManager;

/**
 * Configures the Log4J Logging Feature.
 *
 * @author Ethan Cerami
 */
public class ConfigLogger {
    private static Logger log = Logger.getLogger(ConfigLogger.class.getName());

    /**
     * Configures the Log 4J Logger.
     */
    public static void configureLogger() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            PropertyManager propertyManager = PropertyManager.getInstance();
            String logFile = propertyManager.getProperty
                    (PropertyManager.PROPERTY_LOG_CONFIG_FILE);
            PropertyConfigurator.configure(logFile);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}