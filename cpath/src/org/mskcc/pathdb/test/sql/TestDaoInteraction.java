package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.dao.DaoInteraction;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.util.ArrayList;
import java.sql.Connection;

/**
 * Tests the DaoInteraction Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoInteraction extends TestCase {

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
        interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME,
                "Affinity Precipitation");
        interaction.addAttribute(InteractionVocab.PUB_MED_ID,
                "11583615");

        //  This one does exist.
        PropertyManager manager = PropertyManager.getInstance();
        String location = manager.getProperty(PropertyManager.DB_LOCATION);
        DaoInteraction daoInteraction = new DaoInteraction();
        boolean exists = daoInteraction.interactionExists(interaction,
                location);
        assertTrue(exists);

        //  This is the same as above, except interactors have been swapped.
        interactors = new ArrayList();
        interactors.add(interactor2);
        interactors.add(interactor1);
        interaction.setInteractors(interactors);

        exists = daoInteraction.interactionExists(interaction, location);
        assertTrue(exists);

        //  This one does not exist
        interactors = new ArrayList();
        interactors.add(interactor1);
        interactor2 = new Interactor();
        interactor2.setName("JUNIT_123");
        interactors.add(interactor2);
        interaction.setInteractors(interactors);
        exists = daoInteraction.interactionExists(interaction, location);
        assertTrue(!exists);
    }

    /**
     * Tests Getting of Interaction IDs.
     * @throws Exception All Exceptions.
     */
    public void testGetInteractionId(Connection con) throws Exception {
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

        interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME,
                "Affinity Precipitation");
        interaction.addAttribute(InteractionVocab.PUB_MED_ID,
                "11583615");

        PropertyManager manager = PropertyManager.getInstance();
        String location = manager.getProperty(PropertyManager.DB_LOCATION);
        DaoInteraction daoInteraction = new DaoInteraction();
        int id = daoInteraction.getInteractionId(interaction, location);
        assertEquals(2, id);
    }
}