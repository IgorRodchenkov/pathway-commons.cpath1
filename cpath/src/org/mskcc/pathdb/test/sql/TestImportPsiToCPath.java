package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.Entry;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.InteractionList;
import org.mskcc.dataservices.schemas.psi.InteractorList;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.query.InteractionQuery;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.File;
import java.io.StringWriter;

/**
 * Tests the ImportPsiToCPath, the InteractionQuery
 * and the PsiBuilder Classes.
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
        ImportPsiToCPath importer = new ImportPsiToCPath();
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

    /**
     * Validates Data with InteractionQuery.
     * @throws Exception All Exceptions.
     */
    private void validateData() throws Exception {
        InteractionQuery query = new InteractionQuery("YCR038C");
        EntrySet entrySet = query.getEntrySet();
        StringWriter writer = new StringWriter();
        entrySet.marshal(writer);
        Entry entry = entrySet.getEntry(0);
        InteractorList interactorList = entry.getInteractorList();
        assertEquals (5, interactorList.getProteinInteractorCount());
        InteractionList interactionList = entry.getInteractionList();
        assertEquals (4, interactionList.getInteractionCount());
        assertTrue (entrySet.isValid());
        // System.out.println(writer.toString());
    }
}