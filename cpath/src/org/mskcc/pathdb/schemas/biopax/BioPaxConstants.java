/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.schemas.biopax;

import org.jdom.Namespace;

import java.util.HashSet;
import java.util.Set;

/**
 * BioPAX Constants.
 *
 * @author Ethan Cerami.
 */
public class BioPaxConstants {

    /**
     * BioPAX Class:  physicalEntity.
     */
    public static final String PHYSICAL_ENTITY = "physicalEntity";

    /**
     * BioPAX Class:  complex.
     */
    public static final String COMPLEX = "complex";

    /**
     * BioPAX Class:  dna
     */
    public static final String DNA = "dna";

    /**
     * BioPAX Class:  protein
     */
    public static final String PROTEIN = "protein";

    /**
     * BioPAX Class:  rna
     */
    public static final String RNA = "rna";

    /**
     * BioPAX Class:  smallMolecule.
     */
    public static final String SMALL_MOLECULE = "smallMolecule";

    /**
     * BioPAX Class:  interaction.
     */
    public static final String INTERACTION = "interaction";

    /**
     * BioPAX Class:  physicalInteraction.
     */
    public static final String PHYSICAL_INTERACTION =
            "physicalInteraction";

    /**
     * BioPAX Class: control
     */
    public static final String CONTROL = "control";

    /**
     * BioPAX Class:  catalysis
     */
    public static final String CATAYLSIS = "catalysis";

    /**
     * BioPAX Class:  modulation
     */
    public static final String MODULATION = "modulation";

    /**
     * BioPAX Class: conversion
     */
    public static final String CONVERSION = "conversion";

    /**
     * BioPAX Class:  biochemicalReaction
     */
    public static final String BIOCHEMICAL_REACTION
            = "biochemicalReaction";

    /**
     * BioPAX Class:  transportWithBiochemicalReaction
     */
    public static final String TRANSPORT_WITH_BIOCHEMICAL_REACTION
            = "transportWithBiochemicalReaction";

    /**
     * BioPAX Class:  complexAssembly
     */
    public static final String COMPLEX_ASSEMBLY = "complexAssembly";

    /**
     * BioPAX Class: transport
     */
    public static final String TRANSPORT = "transport";

    /**
     * BioPAX Class:  pathway
     */
    public static final String PATHWAY = "pathway";

    /**
     * BioPax Utility Class:  XRef Unification.
     */
    public static final String XREF_UNIFICATION = "unificationXref";

    /**
     * BioPax Utility Class:  XRef Relationship.
     */
    public static final String XREF_RELATIONSHIP = "relationshipXref";


    /**
     * BioPAX Namespace Prefix.
     */
    public static final String BIOPAX_NAMESPACE_PREFIX = "bp";

    /**
     * BioPAX Level 1 Namespace URI.
     */
    public static final String BIOPAX_LEVEL_1_NAMESPACE_URI =
            "http://www.biopax.org/release/biopax-level1.owl#";

    /**
     * BioPAX Level 2 Namespace URI.
     */
    public static final String BIOPAX_LEVEL_2_NAMESPACE_URI =
            "http://www.biopax.org/release/biopax-level2.owl#";

    /**
     * BioPAX Level 1 Namespace.
     */
    public static final Namespace BIOPAX_LEVEL_1_NAMESPACE =
            Namespace.getNamespace(BIOPAX_NAMESPACE_PREFIX,
                    BIOPAX_LEVEL_1_NAMESPACE_URI);

    /**
     * BioPAX Level 2 Namespace.
     */
    public static final Namespace BIOPAX_LEVEL_2_NAMESPACE =
            Namespace.getNamespace(BIOPAX_NAMESPACE_PREFIX,
                    BIOPAX_LEVEL_2_NAMESPACE_URI);

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
    public BioPaxConstants() {

        //  Initialize Physical Entity Set
        physicalEntitySet.add(PHYSICAL_ENTITY);
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
     *
     * @param elementName Element Name.
     * @return boolean value.
     */
    public boolean isPhysicalEntity(String elementName) {
        return physicalEntitySet.contains(elementName);
    }

    /**
     * Determines if the Specified Element is of type:  interaction.
     *
     * @param elementName Element Name.
     * @return boolean value.
     */
    public boolean isInteraction(String elementName) {
        return interactionSet.contains(elementName.trim());
    }

    /**
     * Determines if the Specified Element is of type:  pathway.
     *
     * @param elementName Element Name.
     * @return boolean value.
     */
    public boolean isPathway(String elementName) {
        return pathwaySet.contains(elementName.trim());
    }

    /**
     * Determines if the Specified Element is of type:  BioPAX entity.
     * If the class is derived from entity, this method returns true.
     * For example, if the class is of type:  physicalEntity, interaction,
     * pathway, or any subclasses of these, this method will return true.
     *
     * @param elementName Element Name.
     * @return boolean value
     */
    public boolean isBioPaxEntity(String elementName) {
        if (physicalEntitySet.contains(elementName)
                || interactionSet.contains(elementName)
                || pathwaySet.contains(elementName)) {
            return true;
        } else {
            return false;
        }
    }
}