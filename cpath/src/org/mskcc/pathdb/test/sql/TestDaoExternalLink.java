// $Id: TestDaoExternalLink.java,v 1.22 2006-02-21 22:51:11 grossb Exp $
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
package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.ExternalDatabaseNotFoundException;

import java.util.ArrayList;

/**
 * Tests the DaoExternalLink class.
 *
 * @author Ethan Cerami
 */
public class TestDaoExternalLink extends TestCase {
    private static final String DB_NAME = "PIR";
    private static final String DB_ID = "BVECGL";
    private String testName;

    /**
     * Tests Access to the ExternalLinkRecord Table.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        testName = "Test Add, Get, Delete Methods";
        DaoExternalLink db = DaoExternalLink.getInstance();
        addSampleRecord();

        // Test getRecordsByCPathId() Method.
        ArrayList list = db.getRecordsByCPathId(1);
        assertEquals(1, list.size());
        ExternalLinkRecord link = (ExternalLinkRecord) list.get(0);
        ExternalDatabaseRecord externalDb = link.getExternalDatabase();
        assertEquals("PIR", externalDb.getName());

        //  Test getRecordsById() Method
        externalDb = null;
        link = db.getRecordById(link.getId());
        externalDb = link.getExternalDatabase();
        assertEquals("PIR", externalDb.getName());
        String webLink = link.getWebLink();
        assertEquals("http://pir.georgetown.edu/cgi-bin/nbrfget?uid=BVECGL",
                webLink);

        //  Test LookupByExternalRefs
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference(DB_NAME, DB_ID);
        ArrayList records = db.lookUpByExternalRefs(refs);
        CPathRecord record = (CPathRecord) records.get(0);
        assertEquals("JUNIT_ENTITY", record.getName());

        //  Test deleteRecordById() Method.
        boolean success = db.deleteRecordById(link.getId());
        assertTrue(success);

        boolean exists = db.recordExists(link);
        assertTrue(!exists);
    }

    /**
     * Tests Empty ID Issue, related to bug #0000508.
     *
     * @throws Exception All Exceptions.
     */
    public void testEmptyIds() throws Exception {
        testName = "Test Empty ID Issue";
        DaoExternalDb dbTable = new DaoExternalDb();
        ExternalDatabaseRecord externalDb = dbTable.getRecordByTerm(DB_NAME);
        DaoExternalLink db = DaoExternalLink.getInstance();
        ExternalLinkRecord link = new ExternalLinkRecord();
        link.setCpathId(1);
        link.setExternalDatabase(externalDb);
        link.setLinkedToId("");

        boolean success = false;
        try {
            success = db.addRecord(link, false);
            fail("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertEquals(false, success);
        }

        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference("", "");
        try {
            db.validateExternalReferences(refs);
            fail("ExternalDatabaseNotFoundException should have been thrown.");
        } catch (ExternalDatabaseNotFoundException e) {
            assertEquals(false, success);
        }
    }

    private void addSampleRecord() throws DaoException {
        DaoExternalDb dbTable = new DaoExternalDb();
        ExternalDatabaseRecord externalDb = dbTable.getRecordByTerm(DB_NAME);
        DaoExternalLink db = DaoExternalLink.getInstance();
        ExternalLinkRecord link = new ExternalLinkRecord();
        link.setCpathId(1);
        link.setExternalDatabase(externalDb);
        link.setLinkedToId(DB_ID);

        //  First, verify that record does not exist.
        boolean exists = db.recordExists(link);
        assertTrue(!exists);

        //  Second, add record.
        boolean success = db.addRecord(link, false);
        assertTrue(success);

        //  Third, verify that record now exists.
        exists = db.recordExists(link);
        assertTrue(exists);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the MySQL ExternalLink Data Access Object (DAO):  "
                + testName;
    }
}
