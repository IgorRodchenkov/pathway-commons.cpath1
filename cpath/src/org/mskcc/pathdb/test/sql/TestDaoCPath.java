package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.DaoCPath;
import org.mskcc.pathdb.sql.DaoExternalLink;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Tests the DaoCPath Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoCPath extends TestCase {
    private static final String NAME = "PROTEIN_A";
    private static final String DESCRIPTION = "Protein A is just great!";
    private static final String XML = "<proteinInteractor id=\"YCR038C\">\n"
            + "<names>\n"
            + "<shortLabel>YCR038C</shortLabel>\n"
            + "<fullName>GTP/GDP exchange factor for Rsr1 protein</fullName>\n"
            + "</names>\n"
            + "</proteinInteractor>\n";
    private static final int YEAST_NCBI_ID = 4932;
    private static final String DB_NAME_0 = "Swiss-Prot";
    private static final String DB_NAME_1 = "PIR";
    private static final String DB_ID_0 = "P25300";
    private static final String DB_ID_1 = "BWBYD5";

    /**
     * Tests Dao Access.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        DaoCPath dao = new DaoCPath();

        ExternalReference refs[] = new ExternalReference[2];
        refs[0] = new ExternalReference(DB_NAME_0, DB_ID_0);
        refs[1] = new ExternalReference(DB_NAME_1, DB_ID_1);

        //  Test addRecord()
        long cpathId = dao.addRecord(NAME, DESCRIPTION, YEAST_NCBI_ID,
                CPathRecordType.PHYSICAL_ENTITY, XML, refs);
        assertTrue(cpathId > 0);

        //  Test getRecordById()
        CPathRecord record = dao.getRecordById(cpathId);
        validateRecord(record);

        //  Test getRecordByName()
        record = dao.getRecordByName(NAME);
        validateRecord(record);

        //  Test getAllRecords()
        ArrayList records = dao.getAllRecords();
        assertTrue(records.size() > 0);

        //  Test deleteRecordById()
        boolean success = dao.deleteRecordById(cpathId);
        assertTrue(success);

        //  Verify that record has been deleted
        record = dao.getRecordById(cpathId);
        assertTrue(record == null);

        //  Verify that all external links have been deleted
        DaoExternalLink linker = new DaoExternalLink();
        ArrayList links = linker.getRecordsByCPathId(cpathId);
        assertTrue(links.size() == 0);
    }

    private void validateRecord(CPathRecord record) throws SQLException,
            ClassNotFoundException {
        assertEquals(NAME, record.getName());
        assertEquals(DESCRIPTION, record.getDescription());
        assertEquals(XML, record.getXmlContent());
        assertEquals(YEAST_NCBI_ID, record.getNcbiTaxonomyId());
        assertEquals(CPathRecordType.PHYSICAL_ENTITY.toString(),
                record.getType().toString());

        DaoExternalLink dao = new DaoExternalLink();
        ArrayList links = dao.getRecordsByCPathId(record.getId());
        ExternalLinkRecord link = (ExternalLinkRecord) links.get(0);
        assertEquals(DB_NAME_0, link.getExternalDatabase().getName());
        assertEquals(DB_ID_0, link.getLinkedToId());

        link = (ExternalLinkRecord) links.get(1);
        assertEquals(DB_NAME_1, link.getExternalDatabase().getName());
        assertEquals(DB_ID_1, link.getLinkedToId());
    }
}
