package org.mskcc.pathdb.test.service;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.services.WriteInteractions;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.DaoInteraction;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.CPathConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Tests the WriteInteractionsToGrid Class.
 *
 * @author Ethan Cerami
 */
public class TestWriteInteractionsToGrid extends TestCase {
    private static final String EXP_SYSTEM = "Two Hybrid - JUnit";
    private static final String OWNER = "JUnit";
    private static final String PMID = "10688190";

    /**
     * Tests Saving of New Interactions to GRID.
     * @throws Exception All Exceptions.
     */
    public void testWriteInteractions() throws Exception {
        RegisterCPathServices.registerServices();

        //  Clears old JUnit Interactions (just in case).
        this.deleteJUnitInteractions();

        DataServiceFactory factory = DataServiceFactory.getInstance();
        WriteInteractions service = (WriteInteractions)
                factory.getService(CPathConstants.WRITE_INTERACTIONS_TO_GRID);
        ArrayList interactors = new ArrayList();
        Interactor interactor1 = new Interactor();
        interactor1.setName("YDL065C");
        interactors.add(interactor1);
        Interactor interactor2 = new Interactor();
        interactor2.setName("YDR532C");
        interactors.add(interactor2);

        Interaction interaction = new Interaction();
        interaction.setInteractors(interactors);
        interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME,
                EXP_SYSTEM);
        interaction.addAttribute(InteractionVocab.OWNER, OWNER);

        String pmids[] = new String[1];
        pmids[0] = PMID;
        interaction.addAttribute(InteractionVocab.PUB_MED_ID, pmids);

        ArrayList interactions = new ArrayList();
        interactions.add(interaction);

        int numSaved = service.writeInteractions(interactions);
        assertEquals(1, numSaved);

        PropertyManager manager = PropertyManager.getInstance();
        String location = manager.getProperty(PropertyManager.DB_LOCATION);
        int id = DaoInteraction.getInteractionId(interaction, location);
        validateInteraction(id);
        this.deleteJUnitInteractions();
    }

    /**
     * Validates that the new Interaction is in fact in the database.
     */
    private void validateInteraction(int id) throws ClassNotFoundException,
            SQLException {
        Connection con = JdbcUtil.getGridConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("SELECT * FROM interactions WHERE interaction_id = ?");
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        String expSystem = rs.getString("experimental_system");
        String owner = rs.getString("owner");
        String pmid = rs.getString("pubmed_id");
        String direction = rs.getString("direction");
        assertEquals(EXP_SYSTEM, expSystem);
        assertEquals(OWNER, owner);
        assertEquals(";10688190;", pmid);
        assertEquals("AB", direction);
    }

    /**
     * Delete all JUnit Interactors.
     * @throws Exception All Exceptions.
     */
    public void deleteJUnitInteractions() throws Exception {
        Connection con = JdbcUtil.getGridConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("DELETE FROM interactions WHERE OWNER = ?");
        pstmt.setString(1, "JUNIT");
        int rows = pstmt.executeUpdate();
    }
}