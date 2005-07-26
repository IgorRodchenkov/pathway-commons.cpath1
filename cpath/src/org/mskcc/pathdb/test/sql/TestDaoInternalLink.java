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
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;

import java.util.ArrayList;

/**
 * Tests the DaoInternalLink Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoInternalLink extends TestCase {
    private static final String PROTEIN_A = "PROTEIN_A";
    private static final String PROTEIN_B = "PROTEIN_B";
    private static final String PROTEIN_C = "PROTEIN_C";
    private long cPathIdA;
    private long cPathIdB;
    private long cPathIdC;

    /**
     * Tests Data Access.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        createSampleCPathRecords();
        DaoInternalLink linker = new DaoInternalLink();

        // Add New Link between A and B.
        boolean flag = linker.addRecord(cPathIdA, cPathIdB);
        assertTrue(flag);

        // Test Get Internal Links
        ArrayList links = linker.getInternalLinks(cPathIdA);
        assertEquals(1, links.size());
        InternalLinkRecord link = (InternalLinkRecord) links.get(0);
        assertEquals(cPathIdA, link.getCpathIdA());
        assertEquals(cPathIdB, link.getCpathIdB());

        // Test Get Internal Links with Lookup
        ArrayList records = linker.getInternalLinksWithLookup(cPathIdA);
        assertEquals(1, records.size());
        CPathRecord record = (CPathRecord) records.get(0);
        assertEquals(PROTEIN_B, record.getName());

        // Test Delete
        int count = linker.deleteRecordsByCPathId(cPathIdA);
        assertEquals(1, count);
        links = linker.getInternalLinks(cPathIdA);
        assertEquals(0, links.size());

        //  Add Multiple Links, Test, then Delete.
        long ids[] = new long[2];
        ids[0] = cPathIdB;
        ids[1] = cPathIdC;
        count = linker.addRecords(cPathIdA, ids);
        assertEquals(2, count);
        links = linker.getInternalLinks(cPathIdA);
        assertEquals(2, links.size());

        //  Delete A, and verify that links are automatically purged.
        DaoCPath dao = DaoCPath.getInstance();
        flag = dao.deleteRecordById(cPathIdA);
        assertTrue(flag);
        links = linker.getInternalLinks(cPathIdA);
        assertEquals(0, links.size());

        dao.deleteRecordById(cPathIdB);
        dao.deleteRecordById(cPathIdC);
    }

    private void createSampleCPathRecords() throws Exception {
        DaoCPath dao = DaoCPath.getInstance();
        cPathIdA = dao.addRecord(PROTEIN_A, "Protein A Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein a xml here");
        cPathIdB = dao.addRecord(PROTEIN_B, "Protein B Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein B xml here");
        cPathIdC = dao.addRecord(PROTEIN_C, "Protein C Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein C xml here");
    }

    /**
     * Gets Name of Test.
     * @return Name of Test.
     */
    public String getName() {
        return "Test the MySQL InternalLink Data Access Object (DAO)";
    }
}