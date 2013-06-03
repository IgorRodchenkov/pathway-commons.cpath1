// $Id: TestDaoInternalLink.java,v 1.21 2008-04-17 13:31:51 cerami Exp $
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
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.xdebug.XDebug;

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
    private String testName;

    /**
     * Tests Data Access.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        testName = "Test basic access methods";
        createSampleCPathRecords();
        DaoInternalLink linker = new DaoInternalLink();

        // Add New Link between A and B.
        boolean flag = linker.addRecord(cPathIdA, cPathIdB);
        assertTrue(flag);

        // Test Get Internal Links
        // Get all things that cPath A points to.
        ArrayList links = linker.getTargets(cPathIdA);
        assertEquals(1, links.size());
        InternalLinkRecord link = (InternalLinkRecord) links.get(0);
        assertEquals(cPathIdA, link.getSourceId());
        assertEquals(cPathIdB, link.getTargetId());

        // Get all things that point to cPath B.
        links = linker.getSources(cPathIdB);
        assertEquals(1, links.size());
        link = (InternalLinkRecord) links.get(0);
        assertEquals(cPathIdA, link.getSourceId());
        assertEquals(cPathIdB, link.getTargetId());

        // Test Get Internal Links with Lookup
        ArrayList records = linker.getTargetsWithLookUp(cPathIdA);
        assertEquals(1, records.size());
        CPathRecord record = (CPathRecord) records.get(0);
        assertEquals(PROTEIN_B, record.getName());

        // Test Delete
        int count = linker.deleteRecordsByCPathId(cPathIdA);
        assertEquals(1, count);
        links = linker.getTargets(cPathIdA);
        assertEquals(0, links.size());

        //  Add Multiple Links, Test, then Delete.
        long ids[] = new long[2];
        ids[0] = cPathIdB;
        ids[1] = cPathIdC;
        count = linker.addRecords(cPathIdA, ids);
        assertEquals(2, count);
        links = linker.getTargets(cPathIdA);
        assertEquals(2, links.size());

        //  Delete A, and verify that links are automatically purged.
        DaoCPath dao = DaoCPath.getInstance();
        flag = dao.deleteRecordById(cPathIdA);
        assertTrue(flag);
        links = linker.getTargets(cPathIdA);
        assertEquals(0, links.size());

        dao.deleteRecordById(cPathIdB);
        dao.deleteRecordById(cPathIdC);
    }

    /**
     * Test the get descendents method.
     * @throws DaoException Database access error.
     */
    public void testGetDescendents() throws DaoException {
        testName = "Test get descendents functionality";
        DaoCPath dao = DaoCPath.getInstance();
        long id1 = dao.addRecord(PROTEIN_A, "Protein A Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein a xml here", -1, false);
        long id2 = dao.addRecord(PROTEIN_B, "Protein B Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein B xml here", -1, false);
        long id3 = dao.addRecord(PROTEIN_C, "Protein C Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein C xml here", -1, false);

        DaoInternalLink linker = new DaoInternalLink();

        // Add New Link 1 --> 2.
        linker.addRecord(id1, id2);

        // Then, link 2 --> 3
        linker.addRecord(id2, id3);

        //  Should get back two descendents
        ArrayList list = linker.getAllDescendents(id1);
        assertEquals (2, list.size());

        boolean flag2 = false, flag3 = false;
        for (int i = 0; i < list.size(); i++) {
            Long descendentId =  (Long) list.get(i);
            if (descendentId.longValue() == id2) {
                flag2 = true;
            } else if (descendentId.longValue() == id3) {
                flag3 = true;
            }
        }
        assertTrue (flag2);
        assertTrue (flag3);
    }

    /**
     * Test getChildren Functionality.
     * @throws DaoException Database Access Error.
     */
    public void testGetChildren() throws DaoException {
        testName = "Test get children functionality";
        DaoInternalLink dao = new DaoInternalLink();
        long externalDbSnapshots[] = new long[1];
        externalDbSnapshots[0] = 1;
        ArrayList children = dao.getChildren(5, -9999, externalDbSnapshots, "biochemicalReaction",
                0, 100, new XDebug());
        assertEquals (9, children.size());
        for (int i=0; i<children.size(); i++) {
            CPathRecord record = (CPathRecord) children.get(i);
            assertEquals (CPathRecordType.INTERACTION, record.getType());
            assertEquals ("biochemicalReaction", record.getSpecificType());
        }
        CPathRecord record0 = (CPathRecord) children.get(0);
        CPathRecord record1 = (CPathRecord) children.get(1);
        CPathRecord record2 = (CPathRecord) children.get(2);
        assertEquals (8, record0.getId());
        assertEquals ("6PFRUCTPHOS-RXN", record0.getName());
        assertEquals (10, record1.getId());
        assertEquals ("3PGAREARR-RXN", record1.getName());
        assertEquals (13, record2.getId());
        assertEquals ("2-phosphoglycerate dehydration", record2.getName());
     }

    /**
     * Test getChildrenTypes Functionality.
     * @throws DaoException Database Access Error.
     */
    public void testGetChildrenTypes() throws DaoException {
        testName = "Test get children types functionality";
        DaoInternalLink dao = new DaoInternalLink();
        long externalDbSnapshots[] = new long[1];
        externalDbSnapshots[0] = 1;
        ArrayList typeList = dao.getChildrenTypes(5, -9999, externalDbSnapshots, new XDebug());
        assertEquals (3, typeList.size());

        TypeCount type0 = (TypeCount) typeList.get(0);
        TypeCount type1 = (TypeCount) typeList.get(1);
        TypeCount type2 = (TypeCount) typeList.get(2);
        assertEquals ("biochemicalReaction", type0.getType());
        assertEquals (9, type0.getCount());
        assertEquals ("catalysis", type1.getType());
        assertEquals (14, type1.getCount());
        assertEquals ("modulation", type2.getType());
        assertEquals (24, type2.getCount());
    }

    private void createSampleCPathRecords() throws DaoException {
        DaoCPath dao = DaoCPath.getInstance();
        cPathIdA = dao.addRecord(PROTEIN_A, "Protein A Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein a xml here", -1, false);
        cPathIdB = dao.addRecord(PROTEIN_B, "Protein B Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein B xml here", -1, false);
        cPathIdC = dao.addRecord(PROTEIN_C, "Protein C Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, "protein C xml here", -1, false);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the MySQL InternalLink Data Access Object (DAO):  "
            + testName;
    }
}
