package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.util.ArrayList;

/**
 * Tests the ImportPsiToCPath class.
 *
 * @author Ethan Cerami
 */
public class TestImportPsiToCPath extends TestCase {

    /**
     * Tests Import.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        ProgressMonitor pMonitor = new ProgressMonitor();
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true, false,
                pMonitor);
        assertEquals(7, summary.getNumInteractorsProcessed());
        assertEquals(0, summary.getNumInteractorsFound());
        assertEquals(7, summary.getNumInteractorsSaved());
        assertEquals(6, summary.getNumInteractionsSaved());
        assertEquals(0, summary.getNumInteractionsClobbered());

        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference("PIR", "BWBYD5");
        ArrayList records = linker.lookUpByExternalRefs(refs);
        CPathRecord record = (CPathRecord) records.get(0);
        assertEquals(4932, record.getNcbiTaxonomyId());
        assertEquals("GTP/GDP exchange factor for Rsr1 protein",
                record.getDescription());

        //  Try Saving Again
        //  Validate that no new interactors are saved.
        //  Validate that new interactions clobbered old interactions.
        //  Only one interaction in psi_sample_mixed.xml has an external ref.
        //  Hence, only one interaction gets clobbered.
        summary = importer.addRecord(xml, true, false, pMonitor);
        assertEquals(0, summary.getNumInteractorsSaved());
        assertEquals(6, summary.getNumInteractionsSaved());
        assertEquals(1, summary.getNumInteractionsClobbered());

        //  Retrieve Interaction, DIP:  58E, and verify that all three
        //  interactors were saved.
        ExternalReference ref = new ExternalReference("DIP", "58E");
        records = linker.lookUpByExternalRef(ref);
        record = (CPathRecord) records.get(0);
        long interactionId = record.getId();
        DaoInternalLink internalLinker = new DaoInternalLink();
        records = internalLinker.getInternalLinksWithLookup(interactionId);
        assertEquals(3, records.size());
    }
}