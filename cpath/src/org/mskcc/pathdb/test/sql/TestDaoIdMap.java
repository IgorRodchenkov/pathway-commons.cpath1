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
import org.mskcc.pathdb.model.IdentityRecord;
import org.mskcc.pathdb.model.CPathXRef;
import org.mskcc.pathdb.sql.dao.DaoIdentity;

import java.util.ArrayList;

/**
 * Tests the DaoIdMap class.
 *
 * @author Ethan Cerami
 */
public class TestDaoIdMap extends TestCase {

    /**
     * Tests Data Access:  Create, Get, Delete.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        DaoIdentity dao = new DaoIdentity();

        //  First, try adding a sample record with invalid DB Ids.
        //  This should trigger an exception.
        IdentityRecord record = new IdentityRecord(100, "ABCD", 200, "XYZ");
        try {
            dao.addRecord(record, true);
            fail("Illegal Argument Exception should have been thrown.  "
                    + " DB1 and DB2 are not stored in the database.");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
        }

        //  Now, try adding a sample record with an empty Id.
        //  This should trigger an exception.
        record = new IdentityRecord(1, "ABCD", 2, "");
        try {
            dao.addRecord(record, true);
            fail("Illegal Argument Exception should have been thrown.  "
                    + " ID2 is null");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
        }

        //  Now, try adding a sample record, based on external databases
        //  defined in reset.sql
        record = new IdentityRecord(1, "ABCD", 2, "XYZ");
        boolean success = dao.addRecord(record, true);
        assertTrue(success);

        //  Verify that the record 1:ABCD <--> 2:XYZ now exists within
        //  the database
        IdentityRecord record2 = dao.getRecord(record);
        assertTrue(record2 != null);
        assertEquals(1, record2.getDb1());
        assertEquals(2, record2.getDb2());
        assertEquals("ABCD", record2.getId1());
        assertEquals("XYZ", record2.getId2());

        //  Verify that the record 2:XYZ <--> 1:ABCD generates the same hit.
        record2 = dao.getRecord(new IdentityRecord(2, "XYZ", 1, "ABCD"));
        assertTrue(record2 != null);
        assertEquals(1, record2.getDb1());
        assertEquals(2, record2.getDb2());
        assertEquals("ABCD", record2.getId1());
        assertEquals("XYZ", record2.getId2());

        //  Now Delete it
        success = dao.deleteRecordById(record2.getPrimaryId());
        assertTrue(success);

        //  Verify that record is indeed deleted.
        record2 = dao.getRecord(record);
        assertTrue(record2 == null);
    }

    public void testEntrezGene () throws Exception {
        DaoIdentity dao = new DaoIdentity();
        CPathXRef ref = new CPathXRef (3, "NP_005894");
        ArrayList list = dao.getEquivalenceList(ref);
    }
}