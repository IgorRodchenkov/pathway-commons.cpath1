// $Id: BioPaxEntityTypeMap.java,v 1.7 2006-12-15 20:03:24 cerami Exp $
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
        put("complex", "Complex");
        put("pathway", "Pathway");
        put("protein", "Protein");
        put("rna", "RNA");
        put("dna", "DNA");
        put("smallMolecule", "Small Molecule");
        put("physicalEntity", "Physical Entity");
        put("transportWithBiochemicalReaction", "Transport with Biochemical Reaction");
        put("transport", "Transport Reaction");
        put("complexAssembly", "Complex Assembly");
        put("biochemicalReaction", "Biochemical Reaction");
        put("conversion", "Conversion Reaction");
        put("modulation", "Modulation Reaction");
        put("catalysis", "Catalysis Reaction");
        put("control", "Control Reaction");
        put("physicalInteraction", "Physical Interaction");
        put("interaction", "Interaction");
        put(BioPaxParentChild.GET_PATHWAY_ROOTS, "Pathways");
        put(BioPaxParentChild.GET_PE_LEAVES, "Molecules");
    }

}
