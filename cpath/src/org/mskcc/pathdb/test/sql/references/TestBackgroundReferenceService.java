// $Id: TestBackgroundReferenceService.java,v 1.9 2006-02-22 22:47:51 grossb Exp $
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
package org.mskcc.pathdb.test.sql.references;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoBackgroundReferences;
import org.mskcc.pathdb.sql.references.BackgroundReferenceService;
import org.mskcc.pathdb.sql.references.ParseBackgroundReferencesTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the Background Reference Service Class.
 *
 * @author Ethan Cerami.
 */
public class TestBackgroundReferenceService extends TestCase {
    private String testName;

    /**
     * Tests the Unification Look up Service.
     *
     * @throws Exception All Exceptions.
     */
    public void testUnificationService() throws Exception {
        testName = "Test Unification Service";
        //  Delete all records, so that we start with a clean slate.
        DaoBackgroundReferences dao = new DaoBackgroundReferences();
        dao.deleteAllRecords();

        //  Store some background references to the Database
        File file = new File("testData/references/unification_refs.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        task.parseAndStoreToDb();

        //  Now, query the Background Reference Service for Unification Refs.
        BackgroundReferenceService refService =
                new BackgroundReferenceService();

        //  Let's assume we start out with two references
        ExternalReference xrefs[] = new ExternalReference[2];
        xrefs[0] = new ExternalReference("UNIPROT", "UNIPROT_1234");
        xrefs[1] = new ExternalReference("PIR", "SANDER_123");

        //  Now, get a complete list of Equivalent References
        ArrayList unifiedList = refService.getUnificationReferences(xrefs);

        //  There should now be a total of 6 equivalent references.
        assertEquals(6, unifiedList.size());

        //  Verify the Database List
        boolean got1 = false;
        boolean got2 = false;
        boolean got3 = false;
        boolean got4 = false;
        boolean got5 = false;
        boolean got6 = false;
        for (int i = 0; i < unifiedList.size(); i++) {
            ExternalReference xref = (ExternalReference) unifiedList.get(i);
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [UNIPROT], ID:  "
                    + "[UNIPROT_1234]")) {
                got1 = true;
            }
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [HUGE], ID:  "
                    + "[HUGE_4321]")) {
                got2 = true;
            }
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [PIR], ID:  "
                    + "[PIR_4321]")) {
                got3 = true;
            }
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [PIR], ID:  "
                    + "[SANDER_123]")) {
                got4 = true;
            }
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [PIR], ID:  "
                    + "[PIR_1234]")) {
                got5 = true;
            }
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [HUGE], ID:  "
                    + "[HUGE_1234]")) {
                got6 = true;
            }
        }
        assertTrue(got1);
        assertTrue(got2);
        assertTrue(got3);
        assertTrue(got4);
        assertTrue(got5);
        assertTrue(got6);

        //  Delete all records, so that we can rerun this unit test again
        dao.deleteAllRecords();
    }

    /**
     * Tests the LinkOut Service.
     *
     * @throws Exception All Exceptions.
     */
    public void testLinkOutService() throws Exception {
        testName = "Test Link Out Service";
        //  Delete all records, so that we start with a clean slate.
        DaoBackgroundReferences dao = new DaoBackgroundReferences();
        dao.deleteAllRecords();

        //  Store some background references to the Database
        File file = new File("testData/references/link_out_refs.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        task.parseAndStoreToDb();

        //  Now, query the Background Reference Service for LinkOut Refs.
        BackgroundReferenceService refService =
                new BackgroundReferenceService();

        ExternalReference xrefs[] = new ExternalReference[1];
        xrefs[0] = new ExternalReference("UNIPROT", "Q8NHX0");
        ArrayList linkOutList = refService.getLinkOutReferences(xrefs);

        //  There should be two Affymetrix LinkOuts
        assertEquals(2, linkOutList.size());
        ExternalReference xref0 = (ExternalReference) linkOutList.get(0);
        ExternalReference xref1 = (ExternalReference) linkOutList.get(1);

        assertEquals("External Reference  -->  Database:  [AFFYMETRIX], "
                + "ID:  [1008_f_at]", xref0.toString());
        assertEquals("External Reference  -->  Database:  [AFFYMETRIX], "
                + "ID:  [1000_at]", xref1.toString());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the Background Reference / ID Service:  " + testName;
    }
}
