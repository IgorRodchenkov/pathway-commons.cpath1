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
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoBackgroundReferences;
import org.mskcc.pathdb.sql.references.BackgroundReferenceService;
import org.mskcc.pathdb.sql.references.ParseBackgroundReferencesTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the ID Mapping Service Class.
 *
 * @author Ethan Cerami.
 */
public class TestBackgroundReferenceService extends TestCase {

    /**
     * Tests the ID Mapping Service Class.
     *
     * @throws Exception All Exceptions.
     */
    public void testIdMappingService() throws Exception {
        //  First, store some ID Mappings to the Database
        File file = new File("testData/id_map.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask(file, false);
        task.parseAndStoreToDb();

        //  Now, query the ID Mapping Service for Equivalent Refs.
        BackgroundReferenceService idService = new BackgroundReferenceService();

        //  Let's assume we start out with three references
        //  The first and third are already stored in the database.
        //  The second is not stored in the database.
        ExternalReference xrefs[] = new ExternalReference[3];
        xrefs[0] = new ExternalReference("SwissProt", "AAH08943");
        xrefs[1] = new ExternalReference("LocusLink", "ABCDE");
        xrefs[2] = new ExternalReference("RefSeq", "NP_060241");

        //  Now, get a complete list of Equivalent References
        ArrayList hitList = idService.getEquivalenceList(xrefs);

        //  There should be a total of 3 hits
        assertEquals(3, hitList.size());

        //  Verify the Database List
        boolean got1 = false;
        boolean got2 = false;
        boolean got3 = false;
        for (int i = 0; i < hitList.size(); i++) {
            ExternalReference xref = (ExternalReference) hitList.get(i);
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [UNIGENE], "
                    + "ID:  [Hs.77646]")) {
                got1 = true;
            }
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [UNIPROT], "
                    + "ID:  [Q727A4]")) {
                got2 = true;
            }
            if (xref.toString().equals
                    ("External Reference  -->  Database:  [AFFYMETRIX], "
                    + "ID:  [1552275_3p_s_at]")) {
                got3 = true;
            }
        }
        assertTrue(got1);
        assertTrue(got2);
        assertTrue(got3);


        //  Delete all records, so that we can rerun this unit test again
        DaoBackgroundReferences dao = new DaoBackgroundReferences();
        dao.deleteAllRecords();
    }
}