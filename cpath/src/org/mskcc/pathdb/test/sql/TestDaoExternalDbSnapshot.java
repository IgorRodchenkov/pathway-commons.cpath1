// $Id: TestDaoExternalDbSnapshot.java,v 1.3 2006-11-17 19:47:16 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

/**
 * Tests the DaoExternalDbSnapshot Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoExternalDbSnapshot extends TestCase {
    private String testName;

    /**
     * Tests the ExternalDbSnapshot DAO.
     * @throws Exception All Errors.
     */
    public void testAccess() throws Exception {
        //  Start with clean slate
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        dao.deleteAllRecords();

        //  Try adding two new snapshot records, based on an existing
        // external db record
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date date = format.parse("12/31/2004");
        long id = dao.addRecord(1, date, "Version 1.0");
        assertTrue (id > 0);

        date = format.parse("6/24/2006");
        id = dao.addRecord(1, date, "Version 2.0");
        assertTrue (id > 0);

        //  Try retrieving the snapshot by snapshot ID
        ExternalDatabaseSnapshotRecord record = dao.getDatabaseSnapshot(id);
        verifySnapshotRecord2(record);

        //  Try retrieving the snapshot by Database ID and date
        record = dao.getDatabaseSnapshot(1,  date);
        verifySnapshotRecord2(record);

        //  Try retrieving all snapshots by ID
        ArrayList list = dao.getDatabaseSnapshot(1);
        assertEquals (2, list.size());
        verifySnapshotRecord1 ((ExternalDatabaseSnapshotRecord) list.get(0));
        verifySnapshotRecord2 ((ExternalDatabaseSnapshotRecord) list.get(1));

        //  Get all snapshots
        list = dao.getAllDatabaseSnapshots();
        assertEquals (2, list.size());
        verifySnapshotRecord1 ((ExternalDatabaseSnapshotRecord) list.get(0));
        verifySnapshotRecord2 ((ExternalDatabaseSnapshotRecord) list.get(1));
    }

    private void verifySnapshotRecord1(ExternalDatabaseSnapshotRecord record) {
        assertEquals ("UniProt", record.getExternalDatabase().getName());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(record.getSnapshotDate().getTime());
        assertEquals (11, calendar.get(Calendar.MONTH));
        assertEquals (31, calendar.get(Calendar.DATE));
        assertEquals (2004, calendar.get(Calendar.YEAR));
        assertEquals ("Version 1.0", record.getSnapshotVersion());
    }

    private void verifySnapshotRecord2(ExternalDatabaseSnapshotRecord record) {
        assertEquals ("UniProt", record.getExternalDatabase().getName());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(record.getSnapshotDate().getTime());
        assertEquals (5, calendar.get(Calendar.MONTH));
        assertEquals (24, calendar.get(Calendar.DATE));
        assertEquals (2006, calendar.get(Calendar.YEAR));
        assertEquals ("Version 2.0", record.getSnapshotVersion());
    }


    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the MySQL External Database Snapshot Data Access "
             + "Object (DAO):  " + testName;
    }
}
