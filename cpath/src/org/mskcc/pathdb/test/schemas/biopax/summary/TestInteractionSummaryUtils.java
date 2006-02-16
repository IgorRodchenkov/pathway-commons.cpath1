package org.mskcc.pathdb.test.schemas.biopax.summary;

import junit.framework.TestCase;
import org.mskcc.pathdb.schemas.biopax.summary.*;

/**
 * Tests the InteractionSummaryUtils Class.
 *
 * @author Ethan Cerami
 *
 */
public class TestInteractionSummaryUtils extends TestCase {
    private String testName;

    /**
     * Validate the summary of a Conversion Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testConversionInteractionSummary() throws Exception {
        testName = "Test Conversion Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(10);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);

        // Verify Left Side is present
        assertTrue (summary.indexOf("3-phosphoglycerate")>0);

        //  Verify Right Side is present
        assertTrue (summary.indexOf("2-phosphoglycerate")>0);

        // Verify Some Synonyms
        assertTrue (summary.indexOf("3-phospho-glyceric acid")>0);
    }

    /**
     * Validate the summary of a Conversion Interaction (Phosphorylation).
     *
     * @throws Exception All Exceptions.
     */
    public void testPhosphorylationInteractionSummary() throws Exception {
        testName = "Test Phosphorylation Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(172);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
        assertTrue (summary.indexOf("SMAD3 (Phosphorylated)")>0);
    }

    /**
     * Validate the summary of a Control Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testControlInteractionSummary() throws Exception {
        testName = "Test Control Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(159);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
        assertTrue (summary.indexOf("activates [") > 0);
    }

    /**
     * Validate the summary of a Transport Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testTransportInteractionSummary() throws Exception {
        testName = "Test Transport Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(123);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);

        //  Validate cellular component on left
        assertTrue (summary.indexOf("(in cytoplasm)") >0);

        //  Validate cellular component on right
        assertTrue (summary.indexOf("(in nucleus)") >0);
    }

    /**
     * Returns Name of Unit Test.
     * @return Name of Unit Test.
     */
    public String getName() {
        return "Test the Creation of Interaction Summaries:  " + testName;
    }
}