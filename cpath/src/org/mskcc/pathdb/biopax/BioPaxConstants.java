package org.mskcc.pathdb.biopax;

import java.util.Set;
import java.util.HashSet;

public class BioPaxConstants {

    /**
     * BioPAX Class:  physicalEntity.
     */
    public final static String PHYSICAL_ENTITY = "physicalEntity";

    /**
     * BioPAX Class:  complex.
     */
    public final static String COMPLEX = "complex";

    /**
     * BioPAX Class:  dna
     */
    public final static String DNA = "dna";

    /**
     * BioPAX Class:  protein
     */
    public final static String PROTEIN = "protein";

    /**
     * BioPAX Class:  rna
     */
    public final static String RNA = "rna";

    /**
     * BioPAX Class:  smallMolecule.
     */
    public final static String SMALL_MOLECULE = "smallMolecule";

    /**
     * BioPAX Class:  interaction.
     */
    public final static String INTERACTION = "interaction";

    /**
     * BioPAX Class:  physicalInteraction.
     */
    public final static String PHYSICAL_INTERACTION =
            "physicalInteraction";

    /**
     * BioPAX Class: control
     */
    public final static String CONTROL = "control";

    /**
     * BioPAX Class:  catalysis
     */
    public final static String CATAYLSIS = "catalysis";

    /**
     * BioPAX Class:  modulation
     */
    public final static String MODULATION = "modulation";

    /**
     * BioPAX Class: conversion
     */
    public final static String CONVERSION = "conversion";

    /**
     * BioPAX Class:  biochemicalReaction
     */
    public final static String BIOCHEMICAL_REACTION
            = "biochemicalReaction";

    /**
     * BioPAX Class:  transportWithBiochemicalReaction
     */
    public final static String TRANSPORT_WITH_BIOCHEMICAL_REACTION
            = "transportWithBiochemicalReaction";

    /**
     * BioPAX Class:  complexAssembly
     */
    public final static String COMPLEX_ASSEMBLY = "complexAssembly";

    /**
     * BioPAX Class: transport
     */
    public final static String TRANSPORT = "transport";

    /**
     * BioPAX Class:  pathway
     */
    public final static String PATHWAY = "pathway";

    /**
     * Set of All Physical Entity Types.
     */
    private Set physicalEntitySet = new HashSet();

    /**
     * Set of All Interaction Types.
     */
    private Set interactionSet = new HashSet();

    /**
     * Set of All Pathway Types.
     */
    private Set pathwaySet = new HashSet();

    /**
     * Constructor.
     */
    public BioPaxConstants () {

        //  Initialize Physical Entity Set
        physicalEntitySet.add(COMPLEX);
        physicalEntitySet.add(DNA);
        physicalEntitySet.add(PROTEIN);
        physicalEntitySet.add(RNA);
        physicalEntitySet.add(SMALL_MOLECULE);

        //  Initialize Interaction Set
        interactionSet.add(INTERACTION);
        interactionSet.add(PHYSICAL_INTERACTION);
        interactionSet.add(CONTROL);
        interactionSet.add(CATAYLSIS);
        interactionSet.add(MODULATION);
        interactionSet.add(CONVERSION);
        interactionSet.add(BIOCHEMICAL_REACTION);
        interactionSet.add(TRANSPORT_WITH_BIOCHEMICAL_REACTION);
        interactionSet.add(COMPLEX_ASSEMBLY);
        interactionSet.add(TRANSPORT);

        //  Intialize Pathway Set
        pathwaySet.add(PATHWAY);
    }

    /**
     * Determines if the Specified Element is of type:  physical entity.
     * @param elementName Element Name.
     * @return boolean value.
     */
    public boolean isPhysicalEntity (String elementName) {
        return physicalEntitySet.contains(elementName);
    }

    /**
     * Determines if the Specified Element is of type:  interaction.
     * @param elementName Element Name.
     * @return boolean value.
     */
    public boolean isInteraction (String elementName) {
        return interactionSet.contains(elementName.trim());
    }

    /**
     * Determines if the Specified Element is of type:  pathway.
     * @param elementName Element Name.
     * @return boolean value.
     */
    public boolean isPathway (String elementName) {
        return pathwaySet.contains(elementName.trim());
    }
}