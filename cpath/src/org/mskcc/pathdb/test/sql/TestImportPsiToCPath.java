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
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.InteractionQuery;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.StringWriter;
import java.util.ArrayList;

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
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);
        XDebug xdebug = new XDebug();
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true, false);
        assertEquals(7, summary.getNumInteractorsProcessed());
        assertEquals(0, summary.getNumInteractorsFound());
        assertEquals(7, summary.getNumInteractorsSaved());
        assertEquals(6, summary.getNumInteractionsSaved());

        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference("PIR", "BWBYD5");
        CPathRecord record = linker.lookUpByExternalRefs(refs);
        assertEquals(4932, record.getNcbiTaxonomyId());
        assertEquals("GTP/GDP exchange factor for Rsr1 protein",
                record.getDescription());

        validateData();

        // Try Saving Again
        summary = importer.addRecord(xml, true, false);
        assertEquals(0, summary.getNumInteractorsSaved());
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
        assertEquals(5, interactorList.getProteinInteractorCount());
        InteractionList interactionList = entry.getInteractionList();
        assertEquals(4, interactionList.getInteractionCount());
        assertTrue(entrySet.isValid());

        validateInteractionSource();

    }

    /**
     * Validates the Interaction Source was saved to the ExternalLinks
     * table.
     */
    private void validateInteractionSource() throws DaoException {
        //  Do a look up based on External Reference
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference ("DIP", "58E");
        CPathRecord record = linker.lookUpByExternalRef(ref);

        //  Find the IDs for Known Interactors
        DaoCPath cpath = new DaoCPath();
        CPathRecord interactor1 = cpath.getRecordByName("YCR038C");
        CPathRecord interactor2 = cpath.getRecordByName("YAL036C");

        //  Verify that interaction record references known interactors.
        DaoInternalLink internalLinker = new DaoInternalLink();
        ArrayList links = internalLinker.getInternalLinks(record.getId());
        InternalLinkRecord link1 = (InternalLinkRecord) links.get(0);
        InternalLinkRecord link2 = (InternalLinkRecord) links.get(1);
        assertEquals (interactor1.getId(), link1.getCpathIdB());
        assertEquals (interactor2.getId(), link2.getCpathIdB());
    }
}