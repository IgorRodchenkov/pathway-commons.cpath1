package org.mskcc.pathdb.model;

import org.mskcc.pathdb.action.BioPaxParentChild;

import java.util.HashMap;

/**
 * Hashmap which maps BioPax Entity Type to Plain English.
 *
 * @author Benjamin Gross
 */
public class BioPaxTabs {
    HashMap childMap = new HashMap();
    HashMap parentMap = new HashMap();

    /**
     * Constructor.
     */
    public BioPaxTabs () {
        childMap.put ("complex" , "Complexes");
        childMap.put ("pathway", "Sub-Pathways");
        childMap.put ("protein", "Proteins");
        childMap.put ("smallMolecule", "Small Molecules");
        childMap.put ("physicalEntity", "Physical Entities");
        childMap.put ("transportWithBiochemicalReaction", "Transport with Biochemical Reaction");
        childMap.put ("transport", "Transport");
        childMap.put ("complexAssembly", "Complex Assemblies");
        childMap.put ("biochemicalReaction", "Biochemical Reactions");
        childMap.put ("conversion", "Conversion");
        childMap.put ("modulation", "Modulation");
        childMap.put ("catalysis", "Catalysis");
        childMap.put ("control", "Control Reactions");
        childMap.put ("physicalInteraction", "Physical Interactions");
        childMap.put ("interaction", "Interactions");
        childMap.put (BioPaxParentChild.GET_SUB_UNITS, "Sub-Units");
        childMap.put (BioPaxParentChild.GET_PATHWAY_ROOTS, "Pathways");
        childMap.put (BioPaxParentChild.GET_PE_LEAVES, "Molecules");

        parentMap.putAll(childMap);
    }

    public String getTabLabel (String command, String type) {
        if (command.equals(BioPaxParentChild.GET_CHILDREN)) {
            return (String) childMap.get(type);
        } else {
            return (String) parentMap.get(type);
        }
    }

}
