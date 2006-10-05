// $Id: TestDaoSourceTracker.java,v 1.2 2006-10-05 20:09:32 cerami Exp $
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
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoSourceTracker;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.dataservices.bio.ExternalReference;
import java.util.ArrayList;

/**
 * Tests the DaoSourceTracker Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoSourceTracker extends TestCase {
    private static final String NAME_1 = "PROTEIN_A_SOURCE";
    private static final String DESCRIPTION_1 = "Protein A is just great!";
    private static final String XML_1 = "<xml>test</xml>";

    private static final String NAME_2 = "PROTEIN_A_CPATH_GENERATED";
    private static final String DESCRIPTION_2 = "Protein A is just great!"
            + " generated";
    private static final String XML_2 = "<xml>test</xml>";

    private static final int YEAST_NCBI_ID = 4932;
    private String testName;

    /**
     * Tests the SourceTracker DAO.
     * @throws Exception All Errors.
     */
    public void testAccess() throws Exception {
        testName = "Test Add, Get, Delete Methods";
        DaoCPath dao = DaoCPath.getInstance();

        ExternalReference refs[] = new ExternalReference[0];

        //  Add Sample Record #1:  Source Record
        long cpathId1 = dao.addRecord(NAME_1, DESCRIPTION_1, YEAST_NCBI_ID,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.BIO_PAX, XML_1, refs, -1 , false);
        assertTrue(cpathId1 > 0);

        //  Add Sample Record #2:  cPath Generated Record
        long cpathId2 = dao.addRecord(NAME_2, DESCRIPTION_2, YEAST_NCBI_ID,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.BIO_PAX, XML_2, refs, -1, false);
        assertTrue(cpathId2 > 0);

        //  Add link between records #1 and #2
        DaoSourceTracker daoSourceTracker = new DaoSourceTracker();
        long sourceTrackerRecordId =
                daoSourceTracker.addRecord(cpathId1, cpathId2);
        assertTrue (sourceTrackerRecordId > 0);

        //  Try adding a link between record #1 and a non-existent record.
        //  This should be caught by MySQL as it violates a foreign key
        //  constraint.
        try {
            daoSourceTracker.addRecord(cpathId1, 9999122);
            fail("DaoException should have been thrown.  This is an illegal "
                + " operation.");
        } catch (DaoException e) {
            assertEquals("Cannot add or update a child row: a foreign key "
                + "constraint fails",
                e.getMessage());
        }

        //  Verify that source records can be retrieved
        ArrayList list = daoSourceTracker.getSourceRecords(cpathId2);
        assertEquals (1, list.size());
        CPathRecord record = (CPathRecord) list.get(0);
        assertEquals ("PROTEIN_A_SOURCE", record.getName());

        //  Now, delete cpath generated record;  MySQL should
        //  auto-delete all linked source tracker records.
        dao.deleteRecordById(cpathId2);

        //  Verify that source tracker records have indeed been deleted.
        list = daoSourceTracker.getSourceRecords(cpathId2);
        assertEquals (0, list.size());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the MySQL Source Tracker Data Access Object (DAO):  "
                + testName;
    }
}