// $Id: BioPaxEntityTypeMap.java,v 1.10 2007-09-17 20:19:29 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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
package org.mskcc.pathdb.model;

import org.mskcc.pathdb.action.BioPaxParentChild;

import java.util.HashMap;

/**
 * Hashmap which maps BioPax Entity Type to Plain English.
 *
 * @author Benjamin Gross
 */
public class BioPaxEntityTypeMap {

    /**
     * Gets the complete HashMap of all BioPAX Entity Types.
     * Includes all pathways, physical entities and interactions.
     * @return HashMap Object.
     */
    public static HashMap getCompleteMap () {
        return getMap(true);
    }

    /**
     * Gets the HashMap of indexed entities only.
     * Includes pathway, protein, small molecule, physical entities.
     * Interaction types are *not* included.
     * @return HashMap Object.
     */
    public static HashMap getIndexedEntitiesOnly() {
        return getMap(false);
    }

    /**
     * Creates the appropriate HashMap.
     * @param includeAll   Flag to include/not include all entity types types.
     * @return HashMap Object.
     */
    private static HashMap getMap (boolean includeAll) {
        HashMap map = new HashMap();
        map.put("pathway", "Pathway");
        map.put("protein", "Protein");
        map.put("smallMolecule", "Small Molecule");
        map.put("physicalEntity", "Physical Entity");
        if (includeAll) {
            map.put("complex", "Complex");
            map.put("rna", "RNA");
            map.put("dna", "DNA");
            map.put("transportWithBiochemicalReaction", "Transport with Biochemical Reaction");
            map.put("transport", "Transport Reaction");
            map.put("complexAssembly", "Complex Assembly");
            map.put("biochemicalReaction", "Biochemical Reaction");
            map.put("conversion", "Conversion Reaction");
            map.put("modulation", "Modulation Reaction");
            map.put("catalysis", "Catalysis Reaction");
            map.put("control", "Control Reaction");
            map.put("physicalInteraction", "Physical Interaction");
            map.put("interaction", "Interaction");
        }
        return map;
    }
}