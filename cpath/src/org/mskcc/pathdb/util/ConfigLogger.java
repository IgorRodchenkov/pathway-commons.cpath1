package org.mskcc.pathdb.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Configures the JDK 1.4 Logging Feature.
 *
 * @author Ethan Cerami
 */
public class ConfigLogger {

    /**
     * Configures the JDK 1.4 Logger.
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