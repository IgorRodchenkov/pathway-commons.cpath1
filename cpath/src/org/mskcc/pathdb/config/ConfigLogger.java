package org.mskcc.pathdb.config;

/**
 * Configures the Apache Commons Logging.
 *
 * @author Ethan Cerami
 */
public class ConfigLogger {

    /**
     * Configures the Apache Commons Logger Logger.
     */
    public static void configureLogger() {
        //  Use the Apache Simple Log
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.SimpleLog");

        //  Set Logging Level:  Must be one of ("trace", "debug", "info",
        //  "warn", "error", or "fatal"). If not specified, defaults to "info".
        System.setProperty("org.apache.commons.logging.simplelog.defaultlog",
                "debug");
    }
}