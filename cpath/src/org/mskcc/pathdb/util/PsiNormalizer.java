package org.mskcc.pathdb.util;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.*;

import java.io.StringReader;
import java.util.HashMap;

/**
 * Normalizes a PSI-MI XML Document in preparation for submission to cPath.
 *
 * @author Ethan Cerami
 */
public class PsiNormalizer {
    private String xml;
    private HashMap interactorMap;
    private HashMap availabilityMap;
    private HashMap experimentMap;
    private EntrySet entrySet;

    /**
     * Constructor.
     * @param xml XML Document String.
     */
    public PsiNormalizer(String xml) {
        this.xml = xml;
        this.interactorMap = new HashMap();
        this.availabilityMap = new HashMap();
        this.experimentMap = new HashMap();
    }

    /**
     * Gets the Normalized Document.
     * @return PSI Entry Set Object.
     * @throws ValidationException Validation Error in Document.
     * @throws MarshalException Error Marshalling Document.
     */
    public EntrySet getNormalizedDocument() throws ValidationException,
            MarshalException {
        normalizeDoc(xml);
        return this.entrySet;
    }

    /**
     * Normalize the Document.
     */
    private void normalizeDoc(String xml) throws ValidationException,
            MarshalException {
        StringReader reader = new StringReader(xml);
        entrySet = EntrySet.unmarshalEntrySet(reader);

        preprocessEntries();

        //  Process all Entries.
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            InteractionList interactionList = entry.getInteractionList();
            for (int j = 0; j < interactionList.getInteractionCount(); j++) {
                InteractionElementType interaction =
                        interactionList.getInteraction(j);
                copyAvailablityEntity(interaction);
                copyExperiments(interaction);

                ParticipantList participantList =
                        interaction.getParticipantList();
                for (int k = 0; k < participantList.
                        getProteinParticipantCount(); k++) {
                    ProteinParticipantType proteinParticipant =
                            participantList.getProteinParticipant(k);
                    ProteinParticipantTypeChoice choice =
                            proteinParticipant.
                            getProteinParticipantTypeChoice();
                    ProteinInteractorType protein =
                            choice.getProteinInteractor();
                    createInteractorRef(protein, interactorList,
                            proteinParticipant);
                }
            }
        }
    }

    /**
     * Removes Experiment References and replaces them with actual
     * Experiment Description Entities.
     */
    private void copyExperiments(InteractionElementType interaction) {
        ExperimentList expList = interaction.getExperimentList();
        if (expList != null) {
            for (int i = 0; i < expList.getExperimentListItemCount(); i++) {
                ExperimentListItem expItem = expList.getExperimentListItem(i);
                RefType ref = expItem.getExperimentRef();
                if (ref != null) {
                    String id = ref.getRef();
                    ExperimentType exp = (ExperimentType) experimentMap.get(id);
                    expItem.setExperimentDescription(exp);
                    expItem.setExperimentRef(null);
                }
            }
        }
    }

    /**
     * Removes Availability References and replaces them with actual
     * Availablity Entities.
     */
    private void copyAvailablityEntity(InteractionElementType interaction) {
        InteractionElementTypeChoice choice =
                interaction.getInteractionElementTypeChoice();
        if (choice != null) {
            RefType ref = choice.getAvailabilityRef();
            if (ref != null) {
                String id = ref.getRef();
                AvailabilityType availability = (AvailabilityType)
                        availabilityMap.get(id);
                choice = new InteractionElementTypeChoice();
                choice.setAvailabilityDescription(availability);
                interaction.setInteractionElementTypeChoice(choice);
            }
        }
    }

    /**
     * Pre-Process all Entries
     */
    private void preprocessEntries() {
        //  Preprocess all Entries.
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            //  If No InteractorList Exists, create one.
            if (interactorList == null) {
                interactorList = new InteractorList();
                entry.setInteractorList(interactorList);
            }
            extractAllInteractors(interactorList);

            //  Extract all AvailabilityList Items, and then remove them.
            AvailabilityList availablityList = entry.getAvailabilityList();
            extractAllAvailabilityItems(availablityList);
            entry.setAvailabilityList(null);

            //  Extract all ExperimentList Items, and then remove them.
            ExperimentList1 expList = entry.getExperimentList1();
            extractAllExperimentItems(expList);
            entry.setExperimentList1(null);
        }
    }

    /**
     * Conditionally Replaces a ProteinInteractor with a ProteinInteractorRef.
     */
    private void createInteractorRef(ProteinInteractorType protein,
            InteractorList interactorList,
            ProteinParticipantType participantType) {
        if (protein != null) {
            String id = protein.getId();

            //  Check that Interactor already exists in hashmap.
            //  If it doesn't already exist, add it to interactorList and
            //  interactor map.
            if (!interactorMap.containsKey(id)) {
                interactorList.addProteinInteractor(protein);
                interactorMap.put(id, protein);
            }

            //  Replace Protein with a ProteinInteractorRef
            RefType interactorRef = new RefType();
            interactorRef.setRef(id);
            ProteinParticipantTypeChoice choice = new
                    ProteinParticipantTypeChoice();
            choice.setProteinInteractorRef(interactorRef);
            participantType.setProteinParticipantTypeChoice(choice);
        }
    }

    /**
     * Stores all Canonical Interactors in a HashMap.
     */
    private void extractAllInteractors(InteractorList interactorList) {
        if (interactorList != null) {
            for (int i = 0; i < interactorList.getProteinInteractorCount();
                 i++) {
                ProteinInteractorType protein =
                        interactorList.getProteinInteractor(i);
                String id = protein.getId();
                interactorMap.put(id, protein);
            }
        }
    }

    /**
     * Stores all Availability Items in a HashMap.
     */
    private void extractAllAvailabilityItems(AvailabilityList list) {
        if (list != null) {
            for (int i = 0; i < list.getAvailabilityCount(); i++) {
                AvailabilityType type = list.getAvailability(i);
                String id = type.getId();
                availabilityMap.put(id, type);
            }
        }
    }

    /**
     * Stores all Experiment Items in a HashMap.
     */
    private void extractAllExperimentItems(ExperimentList1 list) {
        if (list != null) {
            for (int i = 0; i < list.getExperimentDescriptionCount(); i++) {
                ExperimentType exp = list.getExperimentDescription(i);
                String id = exp.getId();
                experimentMap.put(id, exp);
            }
        }
    }
}