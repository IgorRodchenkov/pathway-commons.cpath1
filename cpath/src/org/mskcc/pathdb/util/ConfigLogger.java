package org.mskcc.pathdb.util;

import org.mskcc.pathdb.util.PropertyManager;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

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
        try {
            PropertyManager manager = PropertyManager.getInstance();
            FileHandler fh = new FileHandler(manager.getLogFile());
            fh.setFormatter(new HtmlFormatter());
            Logger.getLogger("").addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}