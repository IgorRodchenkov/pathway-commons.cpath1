package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.LogRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.util.LogUtil;

import java.util.ArrayList;

/**
 * Tests the LogUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestLogUtil extends TestCase {
    private static final String STACK_TRACE = "Stack Trace would go here:"
            + "'test1' \"test2\"";
    private static final String WEB_URL = "webservice.do";
    private static final String REMOTE_HOST = "cbio.mkscc.org";
    private static final String REMOTE_IP = "192.192.55.22";

    /**
     * Tests Log Util Class.
     *
     * @throws DaoException Data Access Error
     */
    public void testUtil() throws DaoException {
        DaoLog dao = new DaoLog();

        // Start with Clean Slate
        dao.deleteAllLogRecords();

        //  Log a Sample Message
        Exception exception = new Exception(STACK_TRACE);
        LogUtil.logException(exception, WEB_URL, REMOTE_HOST, REMOTE_IP);

        //  Verify the Record we just added
        ArrayList records = dao.getLogRecords();
        LogRecord record = (LogRecord) records.get(0);
        assertTrue(record.getDate() != null);
        assertEquals(LogRecord.PRIORITY_ERROR, record.getPriority());
        assertEquals(STACK_TRACE, record.getMessage());
        assertTrue(record.getStackTrace().startsWith
                ("java.lang.Exception: Stack Trace would "));
        assertEquals(WEB_URL, record.getWebUrl());
        assertEquals(REMOTE_HOST, record.getRemoteHost());
        assertEquals(REMOTE_IP, record.getRemoteIp());
    }
}
