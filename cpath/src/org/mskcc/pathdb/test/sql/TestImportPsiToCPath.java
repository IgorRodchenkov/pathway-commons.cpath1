package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.ImportPsiToCPath;
import org.mskcc.pathdb.sql.DaoExternalLink;
import org.mskcc.pathdb.sql.DaoCPath;
import org.mskcc.pathdb.sql.DaoInternalLink;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.model.CPathRecord;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Tests the ImportPsiToCPath Class.
 *
 * @author Ethan Cerami
 */
public class TestImportPsiToCPath extends TestCase {

    /**
     * Tests Import.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        ContentReader reader = new ContentReader();
        File file = new File("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContentFromFile(file);
        XDebug xdebug = new XDebug();
        ImportPsiToCPath importer = new ImportPsiToCPath(xdebug);
        ImportSummary summary = importer.addRecord(xml);
        assertEquals (7, summary.getNumInteractorsProcessed());
        assertEquals (0, summary.getNumInteractorsFound());
        assertEquals (7, summary.getNumInteractorsSaved());
        assertEquals (6, summary.getNumInteractionsSaved());

        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference("PIR", "BWBYD5");
        CPathRecord record = linker.lookUpByByExternalRefs(refs);
        assertEquals (4932, record.getNcbiTaxonomyId());
        assertEquals ("GTP/GDP exchange factor for Rsr1 protein",
                record.getDescription());

        validateData();

        // Try Saving Again
        summary = importer.addRecord(xml);
        assertEquals (0, summary.getNumInteractorsSaved());
    }

    private void validateData() throws ClassNotFoundException, SQLException {
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordByName("YCR038C");

        DaoInternalLink linker = new DaoInternalLink();
        ArrayList list = linker.getInternalLinksWithLookup(record.getId());
        assertEquals (4, list.size());
//      Useful for Debugging.
//        for (int i=0; i<list.size(); i++) {
//            record = (CPathRecord) list.get(i);
//            String xml = record.getXmlContent();
//            System.out.println(xml);
//            System.out.println("--------------------------");
//        }
    }
}