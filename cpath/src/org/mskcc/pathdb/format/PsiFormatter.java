package org.mskcc.pathdb.format;

import org.mskcc.pathdb.xml.psi.EntrySet;
import org.mskcc.pathdb.xml.psi.Entry;
import org.mskcc.pathdb.xml.psi.InteractorList;
import org.mskcc.pathdb.xml.psi.InteractionList;
import org.mskcc.pathdb.xml.psi.ParticipantList;
import org.mskcc.pathdb.xml.psi.ExperimentList;
import org.mskcc.pathdb.xml.psi.Organism;
import org.mskcc.pathdb.xml.psi.ProteinParticipantTypeChoice;
import org.mskcc.pathdb.xml.psi.ProteinInteractorType;
import org.mskcc.pathdb.xml.psi.NamesType;
import org.mskcc.pathdb.xml.psi.ProteinParticipantType;
import org.mskcc.pathdb.xml.psi.RefType;
import org.mskcc.pathdb.xml.psi.XrefType;
import org.mskcc.pathdb.xml.psi.BibrefType;
import org.mskcc.pathdb.xml.psi.DbReferenceType;
import org.mskcc.pathdb.xml.psi.ExperimentListItem;
import org.mskcc.pathdb.xml.psi.ExperimentType;
import org.mskcc.pathdb.xml.psi.CvType;
import org.mskcc.pathdb.model.Interaction;
import org.mskcc.pathdb.model.Protein;
import org.mskcc.pathdb.model.ExternalReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import sun.rmi.rmic.Names;

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
     * PSI-MI Controlled Vocabulary Reference.
     */
    private static final String PSI_MI = "PSI-MI";

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
    public EntrySet getPsiXml() {
        // Create Entry Set and Entry
        EntrySet entrySet = new EntrySet ();
        entrySet.setLevel(1);
        entrySet.setVersion(1);
        Entry entry = new Entry();

        //  Get Interactor List
        InteractorList interactorList = getInteractorList();

        //  Get Interaction List
        InteractionList interactionList = getInteractionList();

        //  Add to Entry node
        entry.setInteractorList(interactorList);
        entry.setInteractionList(interactionList);
        entrySet.addEntry(entry);
        return entrySet;
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
            ProteinInteractorType interactor = new ProteinInteractorType();
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
            org.mskcc.pathdb.xml.psi.InteractionElementType castorInteraction =
                    new org.mskcc.pathdb.xml.psi.InteractionElementType();
            Interaction interaction = (Interaction) interactions.get(i);

            //  Add Experiment List
            ExperimentList expList = getExperimentDescription(interaction);
            castorInteraction.setExperimentList(expList);

            //  Add Participants
            ParticipantList participantList = getParticipantList(interaction);
            castorInteraction.setParticipantList(participantList);

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
        ProteinParticipantType participant1 = createParticipant(nodeId1);
        participantList.addProteinParticipant(participant1);

        //  Add Node B
        String nodeId2 = interaction.getNodeB().getOrfName();
        ProteinParticipantType participant2 = createParticipant(nodeId2);
        participantList.addProteinParticipant(participant2);

        return participantList;
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
        ExperimentListItem expItem = new ExperimentListItem();
        ExperimentType expDescription = new ExperimentType();
        expItem.setExperimentDescription(expDescription);

        //  Set Experimental ID
        expDescription.setId("no_id");

        //  Set Bibliographic Reference
        BibrefType bibRef = null;
        String pmids[] = interaction.getPubMedIds();
        if (pmids.length > 0) {
            bibRef = createBibRef(PUB_MED_DB, pmids[0]);
            expDescription.setBibref(bibRef);
        }

        //  Set Interaction Detection
        CvType interactionDetection =
                getInteractionDetection(interaction);
        expDescription.setInteractionDetection(interactionDetection);

        //  Set Choice Element
        expList.addExperimentListItem(expItem);
        return expList;
    }

    /**
     * Gets Interaction Detection element.
     * @param interaction Interaction.
     * @return InteractionDetection Object.
     */
    private CvType getInteractionDetection
            (Interaction interaction) {
        CvType interactionDetection = new CvType();
        String idStr = interaction.getExperimentalSystem();
        if (idStr.equals("Affinity Precipitation")) {
            NamesType names = createName("affinity chromatography technologies",
                    null);
            interactionDetection.setNames(names);
            XrefType xref = createXRef(PSI_MI, "MI:0004");
            interactionDetection.setXref(xref);
        } else if (idStr.equals("Two Hybrid")) {
            NamesType names = createName("classical two hybrid", null);
            interactionDetection.setNames(names);
            XrefType xref = createXRef(PSI_MI, "MI:0018");
            interactionDetection.setXref(xref);
        } else {
            NamesType names = createName(idStr, null);
            interactionDetection.setNames(names);
            XrefType xref = createXRef("N/A", "N/A");
            interactionDetection.setXref(xref);
        }
        return interactionDetection;
    }

    /**
     * Sets Protein Name and ID.
     * @param protein Protein Object
     * @param interactor Castor Protein Interactor Object.
     */
    private void setNameId(Protein protein, ProteinInteractorType interactor) {
        NamesType names = new NamesType();
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
    private void setOrganism(ProteinInteractorType interactor) {
        Organism organism = new Organism();
        organism.setNcbiTaxId(4932);
        NamesType orgNames = new NamesType();
        orgNames.setShortLabel("baker's yeast");
        orgNames.setFullName("Saccharomyces cerevisiae");
        organism.setNames(orgNames);
        interactor.setOrganism(organism);
    }

    /**
     * Sets Protein External References.
     * Filters out any redundant external references.
     * @param protein Protein Object.
     * @param interactor Castor Protein Interactor Object.
     */
    private void setExternalRefs(Protein protein,
            ProteinInteractorType interactor) {
        HashSet set = new HashSet();
        ExternalReference refs [] = protein.getExternalRefs();
        if (refs.length > 0) {
            XrefType xref = new XrefType();
            //  First External Reference becomes the Primary Reference
            String key = generateXRefKey(refs[0]);
            createPrimaryKey(refs[0], xref);
            set.add(key);

            //  All others become Secondary References
            if (refs.length > 1) {
                for (int i = 1; i < refs.length; i++) {
                    key = this.generateXRefKey(refs[i]);
                    if (!set.contains(key)) {
                        createSecondaryKey(refs[i], xref);
                        set.add(key);
                    }
                }
            }
            interactor.setXref(xref);
        }
    }

    /**
     * Generates XRef Key.
     * @param ref External Reference
     * @return Hash Key.
     */
    private String generateXRefKey(ExternalReference ref) {
        String key = ref.getDatabase() + "." + ref.getId();
        return key;
    }

    /**
     * Creates Primary Key.
     * @param ref External Reference.
     * @param xref Castor XRef.
     */
    private void createPrimaryKey(ExternalReference ref, XrefType xref) {
        DbReferenceType primaryRef = new DbReferenceType();
        primaryRef.setDb(ref.getDatabase());
        primaryRef.setId(ref.getId());
        xref.setPrimaryRef(primaryRef);
    }

    /**
     * Creates Secondary Key.
     * @param ref External Reference
     * @param xref Castro XRef.
     */
    private void createSecondaryKey(ExternalReference ref, XrefType xref) {
        DbReferenceType secondaryRef = new DbReferenceType();
        secondaryRef.setDb(ref.getDatabase());
        secondaryRef.setId(ref.getId());
        xref.addSecondaryRef(secondaryRef);
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
    private NamesType createName(String shortLabel, String fullName) {
        NamesType names = new NamesType();
        names.setShortLabel(shortLabel);
        if (fullName != null) {
            names.setFullName(fullName);
        }
        return names;
    }

    /**
     * Creates a Primary Reference.
     * @param database Database.
     * @param id ID String.
     * @return Castor XRef object
     */
    private XrefType createXRef(String database, String id) {
        XrefType xref = new XrefType();
        DbReferenceType primaryRef = new DbReferenceType();
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
    private BibrefType createBibRef(String database, String id) {
        XrefType xref = createXRef(database, id);
        BibrefType bibRef = new BibrefType();
        bibRef.setXref(xref);
        return bibRef;
    }

    /**
     * Create New Protein Participant.
     * @param id Protein ID.
     * @return Castor Protein Participant Object.
     */
    private ProteinParticipantType createParticipant(String id) {
        ProteinParticipantType participant = new ProteinParticipantType();
        ProteinParticipantTypeChoice choice =
                new ProteinParticipantTypeChoice();
        RefType ref = new RefType();
        ref.setRef(id);
        choice.setProteinInteractorRef(ref);
        participant.setProteinParticipantTypeChoice(choice);
        return participant;
    }
}