package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.service.WriteInteractionsToGrid;
import org.mskcc.pathdb.sql.dao.DaoInteractor;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;

/**
 * Tests the DaoInteractor Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoInteractor extends TestCase {

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
        DaoInteractor daoInteractor = new DaoInteractor();
        HashMap map = daoInteractor.getLocalInteractorIds
                (interactors);
        String localId1 = (String) map.get(interactor1.getName());
        assertEquals("1662", localId1);

        //  This one does not exist in the database.
        //  An Exception should be thrown.
        Interactor interactor2 = new Interactor();
        interactor2.setName("JUNIT_123");
        interactors.add(interactor2);
        try {
            map = daoInteractor.getLocalInteractorIds(interactors);
            fail("DataServiceException should have been thrown");
        } catch (DataServiceException e) {
            String msg = e.getMessage();
        }
    }

    /**
     * Tests the getAllInteractors() method.
     * @throws Exception All Exceptions.
     */
    public void testGetAllInteractors(DaoInteractor daoInteractor)
            throws Exception {
        ArrayList list = daoInteractor.getAllInteractors();
        Interactor interactor = (Interactor) list.get(0);
        assertTrue(list.size() > 0);
    }
}