package org.mskcc.pathdb.format;

import org.mskcc.pathdb.model.ExternalReference;
import org.mskcc.pathdb.model.Interaction;
import org.mskcc.pathdb.model.Protein;
import org.mskcc.pathdb.xml.psi.Bibref;
import org.mskcc.pathdb.xml.psi.Entry;
import org.mskcc.pathdb.xml.psi.ExperimentDescription;
import org.mskcc.pathdb.xml.psi.ExperimentList;
import org.mskcc.pathdb.xml.psi.InteractionDetection;
import org.mskcc.pathdb.xml.psi.InteractionList;
import org.mskcc.pathdb.xml.psi.InteractionType;
import org.mskcc.pathdb.xml.psi.InteractorList;
import org.mskcc.pathdb.xml.psi.InteractorRef;
import org.mskcc.pathdb.xml.psi.Names;
import org.mskcc.pathdb.xml.psi.Organism;
import org.mskcc.pathdb.xml.psi.ParticipantList;
import org.mskcc.pathdb.xml.psi.PrimaryRef;
import org.mskcc.pathdb.xml.psi.ProteinInteractor;
import org.mskcc.pathdb.xml.psi.ProteinParticipant;
import org.mskcc.pathdb.xml.psi.ProteinParticipantTypeChoice;
import org.mskcc.pathdb.xml.psi.SecondaryRef;
import org.mskcc.pathdb.xml.psi.Xref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Converts Internal Object Model to PSI-MI Format.
 *
 * Official version of PSI-MI is available at:
 * http://psidev.sourceforge.net/mi/xml/src/MIF.xsd
 *
 * @author Ethan Cerami
 */
public class PsiFormatter {
    /**
     * ArrayList of Protein-Protein Interactions.
     */
    private ArrayList interactions;

    /**
     * Pub Med Database.
     */
    private static final String PUB_MED_DB = "pubmed";

    /**
     * Constructor.
     * @param interactions ArrayList of Interactions.
     */
    public PsiFormatter(ArrayList interactions) {
        this.interactions = interactions;
    }

    /**
     * Gets PSI XML.
     * @return Root PSI Element.
     */
    public Entry getPsiXml() {
        Entry entry = new Entry();

        //  Get Interactor List
        InteractorList interactorList = getInteractorList();

        //  Get Interaction List
        InteractionList interactionList = getInteractionList();

        //  Add to Entry node
        entry.setInteractorList(interactorList);
        entry.setInteractionList(interactionList);
        return entry;
    }

    /**
     * Gets Interactor List.
     * @return Castor InteractorList.
     */
    private InteractorList getInteractorList() {
        HashMap proteinSet = getNonRedundantProteins();
        InteractorList interactorList = new InteractorList();

        //  Iterate through all Proteins
        Iterator iterator = proteinSet.values().iterator();
        while (iterator.hasNext()) {

            //  Create new Interactor
            ProteinInteractor interactor = new ProteinInteractor();
            Protein protein = (Protein) iterator.next();
            setNameId(protein, interactor);
            setOrganism(interactor);
            setExternalRefs(protein, interactor);

            //  Add to Interactor List
            interactorList.addProteinInteractor(interactor);
        }
        return interactorList;
    }

    /**
     * Gets Interaction List.
     * @return Castor InteractionList.
     */
    private InteractionList getInteractionList() {
        InteractionList interactionList = new InteractionList();
        //  Iterate through all interactions
        for (int i = 0; i < interactions.size(); i++) {

            //  Create New Interaction
            org.mskcc.pathdb.xml.psi.Interaction castorInteraction =
                    new org.mskcc.pathdb.xml.psi.Interaction();
            Interaction interaction = (Interaction) interactions.get(i);

            //  Add Experiment List
            ExperimentList expList = getExperimentDescription(interaction);
            castorInteraction.setExperimentList(expList);

            //  Add Participants
            ParticipantList participantList = getParticipantList(interaction);
            castorInteraction.setParticipantList(participantList);

            //  Add InteractionType
            InteractionType interactionType = getInteractionType(interaction);
            castorInteraction.addInteractionType(interactionType);

            //  Add to Interaction List
            interactionList.addInteraction(castorInteraction);
        }
        return interactionList;
    }

    /**
     * Gets the Interaction Participant List.
     * @param interaction Interaction object.
     * @return Castor Participant List.
     */
    private ParticipantList getParticipantList(Interaction interaction) {
        ParticipantList participantList = new ParticipantList();

        //  Add Node A
        String nodeId1 = interaction.getNodeA().getOrfName();
        ProteinParticipant participant1 = createParticipant(nodeId1);
        participantList.addProteinParticipant(participant1);

        //  Add Node B
        String nodeId2 = interaction.getNodeB().getOrfName();
        ProteinParticipant participant2 = createParticipant(nodeId2);
        participantList.addProteinParticipant(participant2);

        return participantList;
    }

    /**
     * Gets the Interaction Type.
     * @param interaction Intraction object.
     * @return Castor InteractionType object.
     */
    private InteractionType getInteractionType(Interaction interaction) {
        InteractionType interactionType = new InteractionType();

        //  Create Interaction Name
        Names interactionNames = createName
                ("aggregation", "aggregation");
        interactionType.setNames(interactionNames);

        //  Reference Controlled Vocabulary.
        Xref xref = createXRef("goid", "MI:0191");
        interactionType.setXref(xref);

        return interactionType;
    }

    /**
     * Gets Experiment Description.
     * @param interaction Interaction object.
     * @return Castor InteractionElementTypeChoice object.
     */
    private ExperimentList getExperimentDescription
            (Interaction interaction) {
        //  Create New Experiment List
        ExperimentList expList = new ExperimentList();

        //  Create New Experiment Description
        ExperimentDescription expDescription = new ExperimentDescription();

        //  Set Experimental ID
        expDescription.setId("no_id");

        //  Set Bibliographic Reference
        Bibref bibRef = createBibRef("N/A", "N/A");
        expDescription.setBibref(bibRef);

        //  Set Interaction Detection
        InteractionDetection interactionDetection =
                getInteractionDetection(interaction);
        expDescription.setInteractionDetection(interactionDetection);

        //  Set Choice Element
        expList.addExperimentDescription(expDescription);
        return expList;
    }

    /**
     * Gets Interaction Detection element.
     * @param interaction Interaction.
     * @return InteractionDetection Object.
     */
    private InteractionDetection getInteractionDetection
            (Interaction interaction) {
        InteractionDetection interactionDetection = new InteractionDetection();
        String idStr = interaction.getExperimentalSystem();
        if (idStr.equals("Affinity Precipitation")) {
            Names names = createName("affinity chromatography technologies",
                    "affinity chromatography technologies");
            interactionDetection.setNames(names);
            Xref xref = createXRef("goid", "MI:0004");
            interactionDetection.setXref(xref);
        } else if (idStr.equals("Two Hybrid")) {
            Names names = createName("classical two hybrid",
                    "classical two hybrid");
            interactionDetection.setNames(names);
            Xref xref = createXRef("goid", "MI:0018");
            interactionDetection.setXref(xref);
        } else {
            Names names = createName(idStr, idStr);
            interactionDetection.setNames(names);
            Xref xref = createXRef("N/A", "N/A");
            interactionDetection.setXref(xref);
        }
        return interactionDetection;
    }

    /**
     * Sets Protein Name and ID.
     * @param protein Protein Object
     * @param interactor Castor Protein Interactor Object.
     */
    private void setNameId(Protein protein, ProteinInteractor interactor) {
        Names names = new Names();
        names.setShortLabel(protein.getOrfName());
        names.setFullName(protein.getDescription());
        interactor.setNames(names);
        interactor.setId(protein.getOrfName());
    }

    /**
     * Sets Protein Organism.
     * Currently Hard-coded to Yeast.
     * @param interactor Castor Protein Interactor Object.
     */
    private void setOrganism(ProteinInteractor interactor) {
        Organism organism = new Organism();
        organism.setNcbiTaxId(4932);
        Names orgNames = new Names();
        orgNames.setShortLabel("baker's yeast");
        orgNames.setFullName("Saccharomyces cerevisiae");
        organism.setNames(orgNames);
        interactor.setOrganism(organism);
    }

    /**
     * Sets Protein External References.
     * @param protein Protein Object.
     * @param interactor Castor Protein Interactor Object.
     */
    private void setExternalRefs(Protein protein,
            ProteinInteractor interactor) {
        ExternalReference refs [] = protein.getExternalRefs();
        if (refs.length > 0) {
            Xref xref = new Xref();
            //  First External Reference becomes the Primary Reference
            PrimaryRef primaryRef = new PrimaryRef();
            primaryRef.setDb(refs[0].getDatabase());
            primaryRef.setId(refs[0].getId());
            xref.setPrimaryRef(primaryRef);

            //  All others become Secondary References
            for (int i = 0; i < refs.length; i++) {
                SecondaryRef secondaryRef = new SecondaryRef();
                secondaryRef.setDb(refs[i].getDatabase());
                secondaryRef.setId(refs[i].getId());
                xref.addSecondaryRef(secondaryRef);
            }
            interactor.setXref(xref);
        }
    }

    /**
     * Gets a complete list of NonRedundant Proteins.
     * @return HashMap of NonRedundant Proteins.
     */
    private HashMap getNonRedundantProteins() {
        HashMap proteins = new HashMap();
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            Protein nodeA = interaction.getNodeA();
            Protein nodeB = interaction.getNodeB();
            addToHashMap(nodeA, proteins);
            addToHashMap(nodeB, proteins);
        }
        return proteins;
    }

    /**
     * Conditionally adds Protein to HashMap.
     * @param protein Protein Object.
     * @param proteins HashMap of NonRedundant Proteins.
     */
    private void addToHashMap(Protein protein, HashMap proteins) {
        String orfName = protein.getOrfName();
        if (!proteins.containsKey(orfName)) {
            proteins.put(orfName, protein);
        }
    }

    /**
     * Creates a new Names Object.
     * @param shortLabel Short Name Label.
     * @param fullName Full Name/Description.
     * @return Castor Names Object.
     */
    private Names createName(String shortLabel, String fullName) {
        Names names = new Names();
        names.setShortLabel(shortLabel);
        names.setFullName(fullName);
        return names;
    }

    /**
     * Creates a Primary Reference.
     * @param database Database.
     * @param id ID String.
     * @return Castor XRef object
     */
    private Xref createXRef(String database, String id) {
        Xref xref = new Xref();
        PrimaryRef primaryRef = new PrimaryRef();
        primaryRef.setDb(database);
        primaryRef.setId(id);
        xref.setPrimaryRef(primaryRef);
        return xref;
    }

    /**
     * Creates a Bibliography Reference.
     * @param database Database.
     * @param id ID String.
     * @return Castor Bibref Object.
     */
    private Bibref createBibRef(String database, String id) {
        Xref xref = createXRef(database, id);
        Bibref bibRef = new Bibref();
        bibRef.setXref(xref);
        return bibRef;
    }

    /**
     * Create New Protein Participant.
     * @param id Protein ID.
     * @return Castor Protein Participant Object.
     */
    private ProteinParticipant createParticipant(String id) {
        ProteinParticipant participant = new ProteinParticipant();
        ProteinParticipantTypeChoice choice =
                new ProteinParticipantTypeChoice();
        InteractorRef ref = new InteractorRef();
        ref.setRef(id);
        choice.setInteractorRef(ref);
        participant.setProteinParticipantTypeChoice(choice);
        return participant;
    }
}