package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.util.PsiUtil;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Tests the PsiUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestPsiUtil extends TestCase {

    /**
     * Tests the PsiUtil Class.
     * @throws Exception All Exceptions.
     */
    public void testNormalization() throws Exception {
        ContentReader reader = new ContentReader();
        File file = new File("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContentFromFile(file);
        PsiUtil normalizer = new PsiUtil();

        EntrySet entrySet = normalizer.getNormalizedDocument(xml);
        StringWriter writer = new StringWriter();
        //entrySet.marshal(writer);
        //System.out.println(writer.toString());

        Entry entry = entrySet.getEntry(0);
        validateAvailability(entry);
        validateExperiments(entry);
        validateInteractors(entry);
        validateInteractions(entry);
        validateInteractionUpdate(entry);
    }

    private void validateInteractionUpdate(Entry entry) throws Exception {
        PsiUtil util = new PsiUtil();
        HashMap idMap = new HashMap();
        idMap.put("YAL036C", new Long(1));
        idMap.put("YCR038C", new Long(2));
        idMap.put("YDL065C", new Long(3));
        idMap.put("YDR532C", new Long(4));
        idMap.put("YEL061C", new Long(5));
        idMap.put("YBR200W", new Long(6));
        idMap.put("YHR119W", new Long(7));

        InteractionList interactions = entry.getInteractionList();
        util.updateInteractions(interactions, idMap);
        StringWriter writer = new StringWriter();
        interactions.marshal(writer);
        String xml = writer.toString();
        assertTrue(xml.indexOf("<proteinInteractorRef ref=\"2\"/>") > 0);
    }

    /**
     * Verify that  No Global Availability Item exist,
     * and that all availability references have been replaced
     * with redundant copies.
     */
    private void validateAvailability(Entry entry) {
        AvailabilityList list = entry.getAvailabilityList();
        assertTrue(list == null);
        InteractionList interactionList = entry.getInteractionList();
        InteractionElementType interaction =
                interactionList.getInteraction(0);
        InteractionElementTypeChoice choice =
                interaction.getInteractionElementTypeChoice();
        AvailabilityType avail = choice.getAvailabilityDescription();
        String content = avail.getContent();
        assertEquals("This data is free to all.", content);

        interaction = interactionList.getInteraction(1);
        choice = interaction.getInteractionElementTypeChoice();
        avail = choice.getAvailabilityDescription();
        content = avail.getContent();
        assertEquals("This data is free to all.", content);

    }

    /**
     * Verify that  No Global Experimental Items exist,
     * and that all experimental references have been replaced
     * with redundant copies.
     */
    private void validateExperiments(Entry entry) {
        ExperimentList1 list = entry.getExperimentList1();
        assertTrue(list == null);
        InteractionList interactionList = entry.getInteractionList();
        InteractionElementType interaction =
                interactionList.getInteraction(0);
        ExperimentList expList = interaction.getExperimentList();
        ExperimentListItem expItem = expList.getExperimentListItem(0);
        ExperimentType experiment = expItem.getExperimentDescription();
        CvType cv = experiment.getInteractionDetection();
        String shortLabel = cv.getNames().getShortLabel();
        assertEquals("classical two hybrid", shortLabel);
    }


    /**
     * Verify that interactions no longer contain protein interactors, but
     * that they do contain protein interactor refs.
     */
    private void validateInteractions(Entry entry) {
        InteractionList interactionList = entry.getInteractionList();
        for (int i = 0; i < interactionList.getInteractionCount(); i++) {
            InteractionElementType interaction =
                    interactionList.getInteraction(i);
            ParticipantList participantList = interaction.getParticipantList();
            for (int j = 0; j < participantList.getProteinParticipantCount();
                 j++) {
                ProteinParticipantType proteinParticipant =
                        participantList.getProteinParticipant(j);
                ProteinParticipantTypeChoice choice =
                        proteinParticipant.getProteinParticipantTypeChoice();
                ProteinInteractorType protein = choice.getProteinInteractor();
                assertTrue(protein == null);
                RefType proteinRef = choice.getProteinInteractorRef();
                assertTrue(proteinRef != null);
            }
        }
    }

    /**
     * Verify that only one YCR038C interactor exists.
     * @param entry
     */
    private void validateInteractors(Entry entry) {
        PsiUtil util = new PsiUtil();
        int counter = 0;
        InteractorList interactorList = entry.getInteractorList();
        for (int i = 0; i < interactorList.getProteinInteractorCount(); i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            String id = protein.getId();
            if (id.equals("YCR038C")) {
                counter++;
                ExternalReference refs[] = util.extractRefs(protein);
                assertEquals("Entrez GI", refs[0].getDatabase());
            }
        }
        assertEquals(1, counter);
    }
}