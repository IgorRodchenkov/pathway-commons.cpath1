package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.DaoCPath;
import org.mskcc.pathdb.sql.DaoInternalLink;

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
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        createSampleCPathRecords();
        DaoInternalLink linker = new DaoInternalLink();

        // Add New Link between A and B.
        boolean flag = linker.addRecord(cPathIdA, cPathIdB);
        assertTrue (flag);

        // Test Get Internal Links
        ArrayList links = linker.getInternalLinks(cPathIdA);
        assertEquals (1, links.size());
        InternalLinkRecord link = (InternalLinkRecord) links.get(0);
        assertEquals (cPathIdA, link.getCpathIdA());
        assertEquals (cPathIdB, link.getCpathIdB());

        // Test Get Internal Links with Lookup
        ArrayList records = linker.getInternalLinksWithLookup(cPathIdA);
        assertEquals (1, records.size());
        CPathRecord record = (CPathRecord) records.get(0);
        assertEquals (PROTEIN_B, record.getName());

        // Test Delete
        int count = linker.deleteRecordsByCPathId(cPathIdA);
        assertEquals (1, count);
        links = linker.getInternalLinks(cPathIdA);
        assertEquals (0, links.size());

        //  Add Multiple Links, Test, then Delete.
        long ids[] = new long[2];
        ids[0] = cPathIdB;
        ids[1] = cPathIdC;
        count = linker.addRecords(cPathIdA, ids);
        assertEquals (2, count);
        links = linker.getInternalLinks(cPathIdA);
        assertEquals (2, links.size());

        //  Delete A, and verify that links are automatically purged.
        DaoCPath dao = new DaoCPath();
        flag = dao.deleteRecordById(cPathIdA);
        assertTrue (flag);
        links = linker.getInternalLinks(cPathIdA);
        assertEquals (0, links.size());

        dao.deleteRecordById(cPathIdB);
        dao.deleteRecordById(cPathIdC);
    }

    private void createSampleCPathRecords () throws Exception {
        DaoCPath dao = new DaoCPath();
        cPathIdA = dao.addRecord(PROTEIN_A, "Protein A Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, "protein a xml here");
        cPathIdB = dao.addRecord(PROTEIN_B, "Protein B Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, "protein B xml here");
        cPathIdC = dao.addRecord(PROTEIN_C, "Protein C Blah", 101,
                CPathRecordType.PHYSICAL_ENTITY, "protein C xml here");
    }
}