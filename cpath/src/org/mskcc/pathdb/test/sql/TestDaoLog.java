/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.LogRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;

import java.util.ArrayList;
import java.util.Date;

/**
 * Tests the DaoLog Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoLog extends TestCase {
    private static final String MESSAGE = "Validation Error";
    private static final String STACK_TRACE = "Stack Trace would go here:"
            + "'test1' \"test2\"";
    private static final String WEB_URL = "webservice.do";
    private static final String REMOTE_HOST = "cbio.mkscc.org";
    private static final String REMOTE_IP = "192.192.55.22";

    /**
     * Tests Data Access.
     *
     * @throws DaoException Data Access Error.
     */
    public void testAccess() throws DaoException {
        DaoLog dao = new DaoLog();

        //  Delete All Records (Starts with clean slate)
        dao.deleteAllLogRecords();

        //  Add Sample Record
        LogRecord record = new LogRecord();
        Date now = new Date();
        record.setDate(now);
        record.setPriority(LogRecord.PRIORITY_ERROR);
        record.setMessage(MESSAGE);
        record.setStackTrace(STACK_TRACE);
        record.setWebUrl(WEB_URL);
        record.setRemoteHost(REMOTE_HOST);
        record.setRemoteIp(REMOTE_IP);
        dao.addRecord(record);

        //  Verify the Record we just added
        ArrayList records = dao.getLogRecords();
        record = (LogRecord) records.get(0);
        assertTrue(record.getDate() != null);
        assertEquals(LogRecord.PRIORITY_ERROR, record.getPriority());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(STACK_TRACE, record.getStackTrace());
        assertEquals(WEB_URL, record.getWebUrl());
        assertEquals(REMOTE_HOST, record.getRemoteHost());
        assertEquals(REMOTE_IP, record.getRemoteIp());

        //  Delete all records, and verify deletion works.
        dao.deleteAllLogRecords();
        records = dao.getLogRecords();
        assertEquals(0, records.size());

        //  Now Add a Whole bunch of records, and verify that we do
        //  not exceed MAX_NUM_RECORDS
        for (int i = 0; i < DaoLog.MAX_NUM_RECORDS * 2; i++) {
            dao.addRecord(record);
        }
        records = dao.getLogRecords();
        assertTrue(records.size() <= DaoLog.MAX_NUM_RECORDS);
    }
}