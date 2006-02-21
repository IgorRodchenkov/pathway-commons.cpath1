// $Id: TestLogUtil.java,v 1.8 2006-02-21 22:51:11 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.LogRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.util.log.LogUtil;

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

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the System Log Utility Class";
    }
}
