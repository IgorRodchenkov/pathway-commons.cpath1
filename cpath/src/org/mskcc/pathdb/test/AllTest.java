package org.mskcc.pathdb.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.logger.ConfigLogger;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.test.controller.ControllerSuite;
import org.mskcc.pathdb.test.indexer.IndexerSuite;
import org.mskcc.pathdb.test.logger.LoggerSuite;
import org.mskcc.pathdb.test.service.ServiceSuite;
import org.mskcc.pathdb.test.sql.SqlSuite;
import org.mskcc.pathdb.test.util.UtilSuite;
import org.mskcc.pathdb.test.web.WebSuite;
import org.mskcc.pathdb.test.xmlrpc.XmlRpcSuite;

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
        suite.addTest(ControllerSuite.suite());
        suite.addTest(LoggerSuite.suite());
        suite.addTest(UtilSuite.suite());
        suite.addTest(IndexerSuite.suite());
        suite.addTest(SqlSuite.suite());
        suite.addTest(WebSuite.suite());
        suite.addTest(ServiceSuite.suite());
        suite.addTest(XmlRpcSuite.suite());
        suite.setName("PathDB Tests");
        return suite;
    }

    /**
     * Run the all tests method.
     *
     * @param args java.lang.String[]
     * @throws DataServiceException Error Connecting to Data Service.
     */
    public static void main(String[] args) throws DataServiceException {
        PropertyManager manager = PropertyManager.getInstance();
        manager.setProperty(PropertyManager.CPATH_READ_LOCATION,
                "http://localhost:8080/ds/dataservice");
        RegisterCPathServices.registerServices();
        ConfigLogger.configureLogger();
        if (args.length > 0 && args[0] != null && args[0].equals("-ui")) {
            String newargs[] = {"org.mskcc.pathdb.test.AllTest",
                                "-noloading"};
            junit.swingui.TestRunner.main(newargs);
        } else {
            junit.textui.TestRunner.run(suite());
        }
    }

}