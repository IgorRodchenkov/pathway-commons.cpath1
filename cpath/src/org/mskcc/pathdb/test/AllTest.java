package org.mskcc.pathdb.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.controller.ControllerSuite;
import org.mskcc.pathdb.test.format.FormatSuite;
import org.mskcc.pathdb.test.sql.SqlSuite;
import org.mskcc.pathdb.test.web.WebSuite;
import org.mskcc.pathdb.test.logger.LoggerSuite;
import org.mskcc.pathdb.logger.ConfigLogger;
import org.mskcc.pathdb.util.PropertyManager;

/**
 * Runs all Unit Tests.
 *
 * @author Ethan Cerami
 */
public class AllTest extends TestCase {

    /**
     * The suite method kicks off all of the tests.
     *
     * @return junit.framework.Test
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(LoggerSuite.suite());
        suite.addTest(SqlSuite.suite());
        suite.addTest(FormatSuite.suite());
        suite.addTest(ControllerSuite.suite());
        suite.addTest(WebSuite.suite());
        suite.setName("PathDB Tests");
        return suite;
    }

    /**
     * Run the all tests method.
     *
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        initProperties();
        ConfigLogger.configureLogger();
        if (args.length > 0 && args[0] != null && args[0].equals("-ui")) {
            String newargs[] = {"org.mskcc.pathdb.test.AllTest",
                                "-noloading"};
            junit.swingui.TestRunner.main(newargs);
        } else {
            junit.textui.TestRunner.run(suite());
        }
    }

    /**
     * Initialize all required properties.
     */
    private static void initProperties() {
        PropertyManager propertyManager = PropertyManager.getInstance();
        propertyManager.setLogConfigFile("config/config-JDBC.properties");
        propertyManager.setDbHost("localhost");
        propertyManager.setDbUser("cbio");
        propertyManager.setDbPassword("");
    }
}