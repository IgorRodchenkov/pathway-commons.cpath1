package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;

import java.util.ArrayList;

import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.model.Interaction;
import org.mskcc.pathdb.test.TestConstants;

/**
 * Tests the GRID Interaction Service.
 *
 * @author Ethan Cerami
 */
public class TestGridInteractionService extends TestCase {

    /**
     * Tests against live GRID Service.
     * @throws Exception Indicates Error.
     */
    public void testGridService() throws Exception {
            GridInteractionService service = new GridInteractionService
                (TestConstants.DB_HOST, TestConstants.USER,
                TestConstants.PASSWORD);

            ArrayList interactions =
                    service.getInteractions(TestConstants.SAMPLE_ORF_1);
            //  Uncomment the line below to view all interaction data.
            //  printAllInteractions(interactions);
            validateData (interactions, 0, "YER006W", "YLR002C",
                    "Affinity Precipitation", "AB", "11583615");
            validateData (interactions, 6, "YIL035C", "YLR002C",
                    "Affinity Precipitation", "AB", "11805837");
    }

    /**
     * Prints all Interaction Data.
     * @param interactions ArrayList of Interaction objects.
     */
    private void printAllInteractions(ArrayList interactions) {
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            System.out.println(interaction.getNodeA().getOrfName());
            System.out.println(interaction.getNodeB().getOrfName());
            System.out.println(interaction.getExperimentalSystem());
            System.out.println(interaction.getDirection());
            String[] pids = interaction.getPubMedIds();
            if (pids.length > 0) {
                System.out.println(pids[0]);
            }
            System.out.println("----------");
        }
    }

    /**
     * Validates Interaction Data.
     * @param interactions ArrayList of Interactions.
     * @param index Index value into ArrayList.
     * @param geneA ORF Name for GeneA.
     * @param geneB ORF Name for GeneB.
     * @param expSystem Experimental System.
     * @param direction Direction of Interaction.
     * @param pid PubMed Id.
     */
    private void validateData (ArrayList interactions, int index, String geneA,
            String geneB, String expSystem, String direction, String pid) {
        Interaction interaction = (Interaction) interactions.get(index);
        assertEquals (geneA, interaction.getNodeA().getOrfName());
        assertEquals (geneB, interaction.getNodeB().getOrfName());
        assertEquals (expSystem, interaction.getExperimentalSystem());
        assertEquals (direction, interaction.getDirection());
        String[] pids = interaction.getPubMedIds();
        assertEquals (pid, pids[0]);
    }
}