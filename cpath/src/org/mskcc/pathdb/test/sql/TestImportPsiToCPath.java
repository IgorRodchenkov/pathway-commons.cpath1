package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.schemas.psi.Entry;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.InteractionList;
import org.mskcc.dataservices.schemas.psi.InteractorList;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.query.*;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.tool.LoadFullText;
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
    private XDebug xdebug = new XDebug();

    /**
     * Tests Import.
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true, false);
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

        //  Run Full Text Indexer
        LoadFullText batch = new LoadFullText(false);
        batch.indexAllPhysicalEntities();

        validateQueries();

        //  Try Saving Again
        //  Validate that no new interactors are saved.
        //  Validate that new interactions clobbered old interactions.
        //  Only one interaction in psi_sample_mixed.xml has an external ref.
        //  Hence, only one interaction gets clobbered.
        summary = importer.addRecord(xml, true, false);
        assertEquals(0, summary.getNumInteractorsSaved());
        assertEquals(6, summary.getNumInteractionsSaved());
        assertEquals(1, summary.getNumInteractionsClobbered());
    }

    /**
     * Validates Data with Multiple Queries.
     * @throws Exception All Exceptions.
     */
    private void validateQueries() throws Exception {
        validateGetByName();
        validateGetById();
        validateInteractionSource();
        validateGetByTaxonomyId();
        validateGetByPmid();
        validateGetByDbSource();
        validateGetByKeyword();
    }

    /**
     * Verifies that GetInteractionsByInteractorName Works.
     */
    private void validateGetByName() throws QueryException,
            EmptySetException, MarshalException, ValidationException {
        PsiInteractionQuery query =
                new GetInteractionsByInteractorName("YCR038C");
        query.execute(xdebug);
        EntrySet entrySet = query.getEntrySet();
        validateInteractionSet(entrySet);
    }

    /**
     * Verifies that GetInteractionsByInteractorID Works.
     */
    private void validateGetById() throws QueryException,
            EmptySetException, MarshalException, ValidationException,
            DaoException {
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordByName("YCR038C");
        PsiInteractionQuery query =
                new GetInteractionsByInteractorId(record.getId());
        query.execute(xdebug);
        EntrySet entrySet = query.getEntrySet();
        validateInteractionSet(entrySet);
    }

    /**
     * Verifies that GetInteractionsByInteractorTaxonomyId Works.
     */
    private void validateGetByTaxonomyId() throws QueryException {
        int taxId = 4932;
        PsiInteractionQuery query =
                new GetInteractionsByInteractorTaxonomyId(taxId, 25);
        query.execute(xdebug);
        EntrySet entrySet = query.getEntrySet();
        assertEquals(5, entrySet.getEntry(0).getInteractorList().
                getProteinInteractorCount());
        assertEquals(4, entrySet.getEntry(0).getInteractionList()
                .getInteractionCount());
        String xml = query.getXml();
        int index = xml.indexOf(Integer.toString(taxId));
        assertTrue(index > 0);
    }

    /**
     * Verifies that GetInteractionsByInteractionPmid Works.
     */
    private void validateGetByPmid() throws QueryException,
            EmptySetException {
        String pmid = "12345678";
        PsiInteractionQuery query = new GetInteractionsByInteractionPmid
                (pmid, 25);
        query.execute(xdebug);
        EntrySet entrySet = query.getEntrySet();
        assertEquals(2, entrySet.getEntry(0).getInteractorList().
                getProteinInteractorCount());
        assertEquals(1, entrySet.getEntry(0).getInteractionList()
                .getInteractionCount());
        String xml = query.getXml();
        int index = xml.indexOf(pmid);
        assertTrue(index > 0);
    }

    /**
     * Verifies that GetInteractionsByIntractionDbSource Works.
     */
    private void validateGetByDbSource() throws QueryException,
            EmptySetException {
        String db = "DIP";
        PsiInteractionQuery query =
                new GetInteractionsByInteractionDbSource(db, 25);
        query.execute(xdebug);
        String xml = query.getXml();
        EntrySet entrySet = query.getEntrySet();
        assertEquals(2, entrySet.getEntry(0).getInteractorList().
                getProteinInteractorCount());
        assertEquals(1, entrySet.getEntry(0).getInteractionList()
                .getInteractionCount());
    }

    /**
     * Verifies that GetInteractionsByKeyword Works.
     */
    private void validateGetByKeyword() throws QueryException,
            EmptySetException {
        String term = "Xenopus";
        PsiInteractionQuery query =
                new GetInteractionsByInteractorKeyword(term, 25);
        query.execute(xdebug);
        String xml = query.getXml();
        EntrySet entrySet = query.getEntrySet();
        int index = xml.indexOf(term);
        assertTrue(index > 0);
        assertEquals(2, entrySet.getEntry(0).getInteractorList().
                getProteinInteractorCount());
        assertEquals(1, entrySet.getEntry(0).getInteractionList()
                .getInteractionCount());
    }

    private void validateInteractionSet(EntrySet entrySet)
            throws MarshalException, ValidationException {
        StringWriter writer = new StringWriter();
        entrySet.marshal(writer);
        Entry entry = entrySet.getEntry(0);
        InteractorList interactorList = entry.getInteractorList();
        assertEquals(5, interactorList.getProteinInteractorCount());
        InteractionList interactionList = entry.getInteractionList();
        assertEquals(4, interactionList.getInteractionCount());
        assertTrue(entrySet.isValid());
    }

    /**
     * Validates the Interaction Source was saved to the ExternalLinks
     * table.
     */
    private void validateInteractionSource() throws DaoException {
        //  Do a look up based on External Reference
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference("DIP", "58E");
        ArrayList records = linker.lookUpByExternalRef(ref);
        CPathRecord record = (CPathRecord) records.get(0);

        //  Find the IDs for Known Interactors
        DaoCPath cpath = new DaoCPath();
        CPathRecord interactor1 = cpath.getRecordByName("YCR038C");
        CPathRecord interactor2 = cpath.getRecordByName("YAL036C");

        //  Verify that interaction record references known interactors.
        DaoInternalLink internalLinker = new DaoInternalLink();
        ArrayList links = internalLinker.getInternalLinks(record.getId());
        InternalLinkRecord link1 = (InternalLinkRecord) links.get(0);
        InternalLinkRecord link2 = (InternalLinkRecord) links.get(1);
        assertEquals(interactor1.getId(), link1.getCpathIdB());
        assertEquals(interactor2.getId(), link2.getCpathIdB());
    }
}