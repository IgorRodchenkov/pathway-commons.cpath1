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
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.query.ExecuteQuery;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryResult;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.task.IndexLuceneTask;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.Md5Util;
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
        ProgressMonitor pMonitor = new ProgressMonitor();
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true, false, pMonitor);
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
        IndexLuceneTask task = new IndexLuceneTask(false);
        task.indexAllPhysicalEntities();

        validateQueries();

        //  Try Saving Again
        //  Validate that no new interactors are saved.
        //  Validate that new interactions clobbered old interactions.
        //  Only one interaction in psi_sample_mixed.xml has an external ref.
        //  Hence, only one interaction gets clobbered.
        summary = importer.addRecord(xml, true, false, pMonitor);
        assertEquals(0, summary.getNumInteractorsSaved());
        assertEquals(6, summary.getNumInteractionsSaved());
        assertEquals(1, summary.getNumInteractionsClobbered());

        validatePrecomputedQueries();
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
    private void validateGetByName() throws QueryException, MarshalException,
            ValidationException {
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery("YCR038C");
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME);
        ExecuteQuery query = new ExecuteQuery(xdebug);
        try {
            QueryResult result = query.executeQuery(request, true);
            EntrySet entrySet = result.getEntrySet();
            validateInteractionSet(entrySet);
        } catch (QueryException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies that GetInteractionsByInteractorID Works.
     */
    private void validateGetById() throws QueryException,
            MarshalException, ValidationException, DaoException {
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordByName("YCR038C");

        ProtocolRequest request = new ProtocolRequest();
        request.setQuery(Long.toString(record.getId()));
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID);
        ExecuteQuery query = new ExecuteQuery(xdebug);
        QueryResult result = query.executeQuery(request, true);
        EntrySet entrySet = result.getEntrySet();
        validateInteractionSet(entrySet);
    }

    /**
     * Verifies that GetInteractionsByInteractorTaxonomyId Works.
     */
    private void validateGetByTaxonomyId() throws QueryException {
        int taxId = 4932;
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery(Long.toString(taxId));
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_TAX_ID);
        ExecuteQuery query = new ExecuteQuery(xdebug);
        QueryResult result = query.executeQuery(request, true);
        EntrySet entrySet = result.getEntrySet();
        assertEquals(7, entrySet.getEntry(0).getInteractorList().
                getProteinInteractorCount());
        assertEquals(12, entrySet.getEntry(0).getInteractionList()
                .getInteractionCount());
        String xml = result.getXml();
        int index = xml.indexOf(Integer.toString(taxId));
        assertTrue(index > 0);
    }

    /**
     * Verifies that GetInteractionsByInteractionPmid Works.
     */
    private void validateGetByPmid() throws QueryException {
        String pmid = "12345678";
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery(pmid);
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTION_PMID);
        ExecuteQuery query = new ExecuteQuery(xdebug);
        QueryResult result = query.executeQuery(request, true);
        EntrySet entrySet = result.getEntrySet();
        assertEquals(2, entrySet.getEntry(0).getInteractorList().
                getProteinInteractorCount());
        assertEquals(1, entrySet.getEntry(0).getInteractionList()
                .getInteractionCount());
        String xml = result.getXml();
        int index = xml.indexOf(pmid);
        assertTrue(index > 0);
    }

    /**
     * Verifies that GetInteractionsByIntractionDbSource Works.
     */
    private void validateGetByDbSource() throws QueryException,
            EmptySetException {
        String db = "DIP";
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery(db);
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTION_DB);
        ExecuteQuery query = new ExecuteQuery(xdebug);
        QueryResult result = query.executeQuery(request, true);
        EntrySet entrySet = result.getEntrySet();
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

        ProtocolRequest request = new ProtocolRequest();
        request.setQuery(term);
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_KEYWORD);
        ExecuteQuery query = new ExecuteQuery(xdebug);
        QueryResult result = query.executeQuery(request, true);
        String xml = result.getXml();
        EntrySet entrySet = result.getEntrySet();
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

    private void validatePrecomputedQueries() throws Exception {
        //  Create a precomute query;  store to database cache.
        String taxId = "4932";
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery(taxId);
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_TAX_ID);
        request.setFormat(ProtocolConstants.FORMAT_PSI);
        request.setVersion(ProtocolConstants.CURRENT_VERSION);
        ExecuteQuery query = new ExecuteQuery(xdebug);
        query.executeAndStoreQuery(request);

        //  Verify Cached Contents exist in Database.
        String key = Md5Util.createMd5Hash(request.getUri());
        DaoXmlCache dao = new DaoXmlCache();
        String xml = dao.getXmlByKey(key);
        int index = xml.indexOf(taxId);
        assertTrue(index > 0);
        index = xml.indexOf("<fullName>GTP/GDP exchange factor for Rsr1");
        assertTrue(index > 0);
    }
}