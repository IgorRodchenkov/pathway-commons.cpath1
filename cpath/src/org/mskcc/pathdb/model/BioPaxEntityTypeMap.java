// $Id: BioPaxEntityTypeMap.java,v 1.6 2006-12-15 18:59:02 cerami Exp $
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
public class BioPaxEntityTypeMap extends HashMap {

    /**
     * Constructor.
     */
    public BioPaxEntityTypeMap() {
        put("complex", "Complexes");
        put("pathway", "Pathways");
        put("protein", "Proteins");
        put("rna", "RNA");
        put("dna", "DNA");
        put("smallMolecule", "Small Molecules");
        put("physicalEntity", "Physical Entities");
        put("transportWithBiochemicalReaction", "Transport with Biochemical Reaction");
        put("transport", "Transport Reactions");
        put("complexAssembly", "Complex Assemblies");
        put("biochemicalReaction", "Biochemical Reactions");
        put("conversion", "Conversion Reactions");
        put("modulation", "Modulation Reactions");
        put("catalysis", "Catalysis Reactions");
        put("control", "Control Reactions");
        put("physicalInteraction", "Physical Interactions");
        put("interaction", "Interactions");
        put(BioPaxParentChild.GET_PATHWAY_ROOTS, "Pathways");
        put(BioPaxParentChild.GET_PE_LEAVES, "Molecules");
    }

}
