package org.mskcc.pathdb.test.logger;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.mskcc.pathdb.logger.AdminLogger;
import org.mskcc.pathdb.logger.ConfigLogger;

import java.util.ArrayList;

/**
 * Tests the Logger Admin/Viewer.
 *
 * @author Ethan Cerami
 */
public class TestAdminLogger extends TestCase {
    private static Logger log = Logger.getLogger(ConfigLogger.class.getName());

    /**
     * Tests the Logger Admin / Viewer.
     * @throws Exception All Exceptions.
     */
    public void testAdminLogger() throws Exception {

        //  First, delete all exiting log messages.
        AdminLogger admin = new AdminLogger();
        admin.deleteAllLogRecords();

        //  Add a sample log Message.
        ConfigLogger.configureLogger();
        log.fatal("Testing 1-2-3");

        // Verify it goes in.
        ArrayList records = admin.getLogRecords();
        assertEquals(1, records.size());

        //  Delete all, and verify they are all gone.
        admin.deleteAllLogRecords();
        records = admin.getLogRecords();
        assertEquals(0, records.size());
    }
}