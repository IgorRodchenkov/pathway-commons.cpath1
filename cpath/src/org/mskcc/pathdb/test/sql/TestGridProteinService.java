package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.ExternalReference;
import org.mskcc.pathdb.model.GoBundle;
import org.mskcc.pathdb.model.GoTerm;
import org.mskcc.pathdb.model.Protein;
import org.mskcc.pathdb.sql.EmptySetException;
import org.mskcc.pathdb.sql.GridProteinService;
import org.mskcc.pathdb.test.TestConstants;

/**
 * Tests the GRID Protein Service.
 *
 * @author Ethan Cerami
 */
public class TestGridProteinService extends TestCase {
    /**
     * Sample GRID Local ID.
     */
    private static final String SAMPLE_LOCAL_ID = "3878";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_GENE_NAME = "NOC3";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_ID = "3878";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_DESCRIPTION =
            "NucleOlar Complex 2; involved in the nuclear export of "
            + "pre-ribosomes";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_PROCESS_ID_0 = "0006270";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_PROCESS_ID_1 = "0006364";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_PROCESS_NAME_0 =
            "DNA replication initiation";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_PROCESS_NAME_1 =
            "rRNA processing";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_COMPONENT_ID_0 = "0005634";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_COMPONENT_ID_1 = "0005730";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_COMPONENT_NAME_0 = "nucleus";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_COMPONENT_NAME_1 = "nucleolus";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_FUNCTION_ID_0 = "NONE";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_FUNCTION_NAME_0 = "NONE";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_SPECIAL_ID_0 = "0006260";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_SPECIAL_ID_8 = "0016043";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_SPECIAL_NAME_0 = "DNA replication";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_SPECIAL_NAME_8 =
            "cell organization and biogenesis";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_EXTERNAL_REF_ID_0 = "1360288";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_EXTERNAL_REF_NAME_0 = "Entrez GI";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_EXTERNAL_REF_ID_9 = "Z73174.1";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID.)
     */
    private static final String EXPECTED_EXTERNAL_REF_NAME_9 =
            "GenBank DNA Version";

    /**
     * Tests against live GRID Service.
     * @throws Exception Indicates Error.
     */
    public void testGridService() throws Exception {
        GridProteinService service = new GridProteinService
                (TestConstants.DB_HOST, TestConstants.USER,
                        TestConstants.PASSWORD);

        org.mskcc.pathdb.model.Protein protein =
                service.getProteinByOrf(TestConstants.SAMPLE_ORF_1);
        validateNameIdDescription(protein);
        validateGoTerms(protein);
        validateExternalReferences(protein);

        protein = service.getProteinByLocalId(SAMPLE_LOCAL_ID);
        validateNameIdDescription(protein);
    }

    /**
     * Tests the Grid XML Service.
     * @throws Exception All Exceptions.
     */
    public void testGridXmlService() throws Exception {
        GridProteinService service = new GridProteinService
                (TestConstants.DB_HOST, TestConstants.USER,
                        TestConstants.PASSWORD);

        String xml = service.getProteinXmlByOrf(TestConstants.SAMPLE_ORF_1);
        int index = xml.indexOf("<orf_name>YLR002C</orf_name>");
        assertTrue("Testing XML Contents", index > 5);
    }

    /**
     * Tests for Invalid ID.
     * @throws Exception All Exceptions.
     */
    public void testInvalidID() throws Exception {
        GridProteinService service = new GridProteinService
                (TestConstants.DB_HOST, TestConstants.USER,
                        TestConstants.PASSWORD);
        try {
            Protein protein = service.getProteinByOrf("ecerami");
            fail("EmptySetException should have been thrown.");
        } catch (EmptySetException e) {
            assertTrue(e instanceof EmptySetException);
        }
    }


    /**
     * Validates Name, ID and Description.
     * @param protein Protein object.
     */
    private void validateNameIdDescription(Protein protein) {
        //  Validate Gene Name
        String geneNames[] = protein.getGeneNames();

        assertEquals(EXPECTED_GENE_NAME, geneNames[0]);

        //  Validate Local ID
        String id = protein.getLocalId();
        assertEquals(EXPECTED_ID, id);

        //  Validate Description
        String description = protein.getDescription();
        assertEquals(EXPECTED_DESCRIPTION, description);
    }

    /**
     * Validates all GO Term Categories.
     * @param protein Protein object.
     */
    private void validateGoTerms(Protein protein) {
        GoBundle bundle = protein.getGoBundle();

        //  Validate GO Process Terms.
        GoTerm processes[] = bundle.getGoProcesses();
        assertEquals(EXPECTED_PROCESS_ID_0, processes[0].getID());
        assertEquals(EXPECTED_PROCESS_NAME_0, processes[0].getName());
        assertEquals(EXPECTED_PROCESS_ID_1, processes[1].getID());
        assertEquals(EXPECTED_PROCESS_NAME_1, processes[1].getName());

        //  Validate GO Component Terms.
        GoTerm components[] = bundle.getGoComponents();
        assertEquals(EXPECTED_COMPONENT_ID_0, components[0].getID());
        assertEquals(EXPECTED_COMPONENT_NAME_0, components[0].getName());
        assertEquals(EXPECTED_COMPONENT_ID_1, components[1].getID());
        assertEquals(EXPECTED_COMPONENT_NAME_1, components[1].getName());

        //  Validate GO Function Terms.
        GoTerm functions[] = bundle.getGoFunctions();
        assertEquals(EXPECTED_FUNCTION_ID_0, functions[0].getID());
        assertEquals(EXPECTED_FUNCTION_NAME_0, functions[0].getName());

        //  Validate GO Service Terms.
        GoTerm special[] = bundle.getGoSpecial();
        assertEquals(EXPECTED_SPECIAL_ID_0, special[0].getID());
        assertEquals(EXPECTED_SPECIAL_NAME_0, special[0].getName());
        assertEquals(EXPECTED_SPECIAL_ID_8, special[8].getID());
        assertEquals(EXPECTED_SPECIAL_NAME_8, special[8].getName());
    }

    /**
     * Validates External References.
     * @param protein Protein object.
     */
    private void validateExternalReferences(Protein protein) {
        ExternalReference refs[] = protein.getExternalRefs();
        assertEquals(EXPECTED_EXTERNAL_REF_ID_0, refs[0].getId());
        assertEquals(EXPECTED_EXTERNAL_REF_NAME_0, refs[0].getDatabase());
        assertEquals(EXPECTED_EXTERNAL_REF_ID_9, refs[9].getId());
        assertEquals(EXPECTED_EXTERNAL_REF_NAME_9, refs[9].getDatabase());
    }
}