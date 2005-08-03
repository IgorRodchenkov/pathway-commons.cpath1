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
package org.mskcc.pathdb.test.sql.references;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.BackgroundReference;
import org.mskcc.pathdb.model.BackgroundReferencePair;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.dao.DaoBackgroundReferences;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.references.ParseBackgroundReferencesTask;
import org.mskcc.pathdb.sql.transfer.ImportException;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the Background Reference Parser.
 *
 * @author Ethan Cerami.
 */
public class TestParseBackgroundReferenceTask extends TestCase {

    /**
     * Tests the Unification Parser, without saving anything to the database.
     *
     * @throws Exception All Exceptions.
     */
    public void testUnificationParserWithOutSave() throws Exception {
        File file = new File("testData/references/unification_refs.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        ArrayList list = task.parseAndGenerateList();

        //  Validate that this data is of type:  PROTEIN_UNIFICATION
        ReferenceType refType = task.getReferenceType();
        assertEquals(ReferenceType.PROTEIN_UNIFICATION, refType);

        //  Validate the first five background references
        validateBackgroundReferenceRecord(0, list, "UNIPROT",
                "UNIPROT_1234", "PIR", "PIR_1234");
        validateBackgroundReferenceRecord(1, list, "PIR",
                "PIR_1234", "PIR", "PIR_4321");
        validateBackgroundReferenceRecord(2, list, "PIR", "PIR_4321",
                "HUGE", "HUGE_1234");
        validateBackgroundReferenceRecord(3, list, "UNIPROT", "UNIPROT_XYZ",
                "PIR", "PIR_XYZ");
        validateBackgroundReferenceRecord(4, list, "PIR", "PIR_XYZ",
                "HUGE", "HUGE_XYZ");
    }

    /**
     * Tests the LinkOut Parser, without saving anything to the database.
     *
     * @throws Exception All Exceptions.
     */
    public void testLinkOutParserWithoutSave() throws Exception {
        File file = new File("testData/references/link_out_refs.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        ArrayList list = task.parseAndGenerateList();

        //  Validate the first five background references
        validateBackgroundReferenceRecord(0, list, "ENTREZ_GENE",
                "5595", "AFFYMETRIX", "1000_at");
        validateBackgroundReferenceRecord(1, list, "UNIPROT",
                "P27361", "AFFYMETRIX", "1000_at");
        validateBackgroundReferenceRecord(2, list, "UNIPROT",
                "Q8NHX0", "AFFYMETRIX", "1000_at");
        validateBackgroundReferenceRecord(3, list, "UNIPROT",
                "Q8NHX1", "AFFYMETRIX", "1000_at");
        validateBackgroundReferenceRecord(4, list, "UNIPROT",
                "Q7Z3H5", "AFFYMETRIX", "1000_at");
    }

    /**
     * Tests the Parser, with saving to the database.
     *
     * @throws Exception All Exceptions.
     */
    public void testUnificationParserWithSave() throws Exception {
        // Start with a clean slate
        DaoBackgroundReferences dao = new DaoBackgroundReferences();
        dao.deleteAllRecords();

        File file = new File("testData/references/unification_refs.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        int numRecordsSaved = task.parseAndStoreToDb();

        //  Validate that we saved data as PROTEIN_UNIFICATION.
        assertEquals(ReferenceType.PROTEIN_UNIFICATION,
                task.getReferenceType());

        //  Validate Number of Records Saved
        assertEquals(6, numRecordsSaved);

        //  Now try saving again;  verify 0 records saved
        numRecordsSaved = task.parseAndStoreToDb();
        assertEquals(0, numRecordsSaved);

        //  Now try getting all equivalent Identifiers
        BackgroundReference xref = new BackgroundReference(1, "UNIPROT_1234");
        ArrayList equivalenceList = dao.getEquivalenceList(xref);
        assertEquals(4, equivalenceList.size());
        boolean got[] = new boolean[4];

        for (int i = 0; i < equivalenceList.size(); i++) {
            BackgroundReference match = (BackgroundReference)
                    equivalenceList.get(i);
            if (match.toString().equals("PIR: PIR_1234")) {
                got[0] = true;
            } else if (match.toString().equals("HUGE: HUGE_1234")) {
                got[1] = true;
            } else if (match.toString().equals("HUGE: HUGE_4321")) {
                got[2] = true;
            } else if (match.toString().equals("PIR:  PIR_4321")) {
                got[3] = true;
            }
        }
        assertTrue(got[0]);
        assertTrue(got[1]);
        assertTrue(got[2]);

        //  Delete all records, so that we can rerun this unit test again
        dao.deleteAllRecords();
    }


    /**
     * Tests the LinkOut Parser, and saving everything to the database.
     *
     * @throws Exception All Exceptions.
     */
    public void testLinkOutParserWithSave() throws Exception {
        // Start with a clean slate
        DaoBackgroundReferences dao = new DaoBackgroundReferences();
        dao.deleteAllRecords();

        File file = new File("testData/references/link_out_refs.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        int numRecordsSaved = task.parseAndStoreToDb();

        //  Validate that we saved data as LINK_OUT.
        assertEquals(ReferenceType.LINK_OUT, task.getReferenceType());

        //  Validate Number of Records Saved
        assertEquals(45, numRecordsSaved);

        //  Now try saving again;  verify 0 records saved
        numRecordsSaved = task.parseAndStoreToDb();
        assertEquals(0, numRecordsSaved);

        //  Now try getting all linkouts for a specific protein.
        BackgroundReference xref = new BackgroundReference(1, "Q8NHX0");
        ArrayList linkOutList = dao.getLinkOutList(xref);
        BackgroundReference linkOut0 = (BackgroundReference) linkOutList.get(0);
        BackgroundReference linkOut1 = (BackgroundReference) linkOutList.get(1);

        //  Verify that UniProt: Q8NHX0 points to two Affymetrix IDs. 
        assertEquals(2, linkOutList.size());
        assertEquals("AFFYMETRIX: 1000_at", linkOut0.toString());
        assertEquals("AFFYMETRIX: 1008_f_at", linkOut1.toString());

        //  Delete all records, so that we can rerun this unit test again
        dao.deleteAllRecords();
    }

    /**
     * Tests the Import Parser with a Series of Invalid Data Files.
     *
     * @throws Exception All Exceptions.
     */
    public void testParserWithInvalidDataFiles() throws Exception {
        //  Test:  This file is invalid because it contains a non-existent
        //  database called 'SANDER.'
        File file = new File("testData/references/refs_invalid1.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        try {
            task.parseAndGenerateList();
            fail("ImportException should have been thrown.  Data file "
                    + " contains and invalid database, named 'SANDER'.");
        } catch (ImportException e) {
            //  Validate the actual exception message.
            assertTrue(e.getMessage().indexOf
                    ("Unable to import background references data file.  "
                    + "Database:  SANDER is not known to cPath.") >= 0);
        }

        //  Test:  This file is invalid because it is not tab-delimited.
        file = new File("testData/references/refs_invalid2.txt");
        task = new ParseBackgroundReferencesTask(file, false);
        try {
            task.parseAndGenerateList();
            fail("ImportException should have been thrown.  Data file "
                    + " is not tab delimited.");
        } catch (ImportException e) {
            assertTrue(e.getMessage().indexOf
                    ("Unable to import background reference file.  "
                    + "File must be tab delimited.  Check the file and "
                    + "try again.") >= 0);
        }

        //  Test:  This file is invalid because it contains only one column
        //  of data.
        file = new File("testData/references/refs_invalid3.txt");
        task = new ParseBackgroundReferencesTask(file, false);
        try {
            task.parseAndGenerateList();
            fail("ImportException should have been thrown.  Data file "
                    + " contains only one column of data.");
        } catch (ImportException e) {
            assertTrue(e.getMessage().indexOf
                    ("Unable to import background reference file.  "
                    + "File must contain at least two columns of data.") >= 0);
        }

        //  Test:  This file is invalid because it is a LINK_OUT file,
        //  but the first column is not of type:  LINK_OUT.
        file = new File("testData/references/refs_invalid4.txt");
        task = new ParseBackgroundReferencesTask(file, false);
        try {
            task.parseAndGenerateList();
            fail("ImportException should have been thrown.  Data file "
                    + " contains more than one column of "
                    + ReferenceType.LINK_OUT + " data.");
        } catch (ImportException e) {
            assertTrue(e.getMessage().indexOf
                    ("Unable to import background reference file of LINK_OUT "
                    + "data.  First column must be of type:  "
                    + ReferenceType.LINK_OUT) >= 0);
        }
    }

    private void validateBackgroundReferenceRecord(int index, ArrayList idList,
            String expectedDb1, String expectedId1, String expectedDb2,
            String expectedId2) throws DaoException {
        BackgroundReferencePair id = (BackgroundReferencePair)
                idList.get(index);
        DaoExternalDb dao = new DaoExternalDb();
        int db1 = id.getDbId1();
        ExternalDatabaseRecord dbRecord1 = dao.getRecordById(db1);
        int db2 = id.getDbId2();
        ExternalDatabaseRecord dbRecord2 = dao.getRecordById(db2);
        assertEquals(expectedDb1, dbRecord1.getMasterTerm());
        assertEquals(expectedId1, id.getLinkedToId1());
        assertEquals(expectedDb2, dbRecord2.getMasterTerm());
        assertEquals(expectedId2, id.getLinkedToId2());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can properly parse background reference/id files";
    }
}
