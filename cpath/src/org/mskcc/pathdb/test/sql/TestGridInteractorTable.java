package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.service.WriteInteractionsToGrid;
import org.mskcc.pathdb.sql.GridInteractorTable;

import java.util.ArrayList;

/**
 * Tests the GridInteractorTable Class.
 *
 * @author Ethan Cerami
 */
public class TestGridInteractorTable extends TestCase {

    /**
     * Tests Getting of Local IDs.
     * @throws Exception All Exceptions.
     */
    public void testGetLocalIds() throws Exception {
        RegisterCPathServices.registerServices();
        WriteInteractionsToGrid service = new WriteInteractionsToGrid();

        ArrayList interactors = new ArrayList();

        //  This one exists in the database.
        Interactor interactor1 = new Interactor();
        interactor1.setName("YER006W");
        interactors.add(interactor1);

        //  This one does not exist in the database.
        Interactor interactor2 = new Interactor();
        interactor2.setName("JUNIT_123");
        interactors.add(interactor2);

        GridInteractorTable.getLocalInteractorIds(interactors);
        String localId1 = (String) interactor1.getAttribute
                (InteractorVocab.LOCAL_ID);
        String localId2 = (String) interactor2.getAttribute
                (InteractorVocab.LOCAL_ID);
        assertEquals("1662", localId1);
        assertEquals("N/A", localId2);
    }
}