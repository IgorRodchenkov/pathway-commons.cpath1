package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.GridInteractionTable;
import org.mskcc.pathdb.sql.GridInteractorTable;

import java.util.ArrayList;

/**
 * Tests the GridInteractionTable Class.
 *
 * @author Ethan Cerami
 */
public class TestGridInteractionTable extends TestCase {

    /**
     * Tests that Interaction Exists.
     * @throws Exception All Exceptions.
     */
    public void testInteractionExists() throws Exception {
        RegisterCPathServices.registerServices();
        ArrayList interactors = new ArrayList();
        Interactor interactor1 = new Interactor();
        interactor1.setName("YER006W");
        interactors.add(interactor1);
        Interactor interactor2 = new Interactor();
        interactor2.setName("YPL211W");
        interactors.add(interactor2);

        Interaction interaction = new Interaction();
        interaction.setInteractors(interactors);

        //  This one does exist.
        boolean exists = GridInteractionTable.interactionExists(interaction);
        assertTrue(exists);

        //  This is the same as above, except interactors have been swapped.
        interactors = new ArrayList();
        interactors.add(interactor2);
        interactors.add(interactor1);
        interaction.setInteractors(interactors);

        exists = GridInteractionTable.interactionExists(interaction);
        assertTrue(exists);

        //  This one does not exist
        interactors = new ArrayList();
        interactors.add(interactor1);
        interactor2 = new Interactor();
        interactor2.setName("JUNIT_123");
        interactors.add(interactor2);
        interaction.setInteractors(interactors);
        GridInteractorTable.getLocalInteractorIds(interactors);
        exists = GridInteractionTable.interactionExists(interaction);
        assertTrue(!exists);
    }

    /**
     * Tests Getting of Interaction IDs.
     * @throws Exception All Exceptions.
     */
    public void testGetInteractionId() throws Exception {
        RegisterCPathServices.registerServices();
        ArrayList interactors = new ArrayList();
        Interactor interactor1 = new Interactor();
        interactor1.setName("YER006W");
        interactors.add(interactor1);
        Interactor interactor2 = new Interactor();
        interactor2.setName("YKL009W");
        interactors.add(interactor2);
        Interaction interaction = new Interaction();
        interaction.setInteractors(interactors);
        int id = GridInteractionTable.getInteractionId(interaction);
        assertEquals(2, id);
    }
}