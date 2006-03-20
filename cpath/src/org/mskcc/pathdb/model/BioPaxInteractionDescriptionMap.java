// $Id: BioPaxInteractionDescriptionMap.java,v 1.8 2006-03-20 21:55:31 grossb Exp $
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

import java.util.HashMap;

/**
 * Hashmap which maps BioPax Entity Type to its description.
 *
 * @author Benjamin Gross
 */
public class BioPaxInteractionDescriptionMap extends HashMap {

    /**
     * Constructor.
     */
    public BioPaxInteractionDescriptionMap() {
        put("interaction", "A single biological relationship between two or more entities.");
        put("physicalInteraction", "An interaction in which at least one participant is a "
                + "physical entity, e.g. a binding event.");
        put("control", "An interaction in which one entity regulates, modifies, or otherwise "
                + "influences another. Two types of control interactions are defined: activation "
                + "and inhibition.");
        put("catalysis", "A control interaction in which a physical entity (a catalyst) "
                + "increases the rate of a conversion interaction by lowering its "
                + "activation energy.");
        put("modulation", "A control interaction in which a physical entity modulates "
                + "a catalysis interaction.");
        put("conversion", "An interaction in which one or more entities is physically "
                + "transformed into one or more other entities.");
        put("biochemicalReaction", "A conversion interaction in which one or more entities "
                + "(substrates) undergo covalent changes to become one or more other "
                + "entities (products).");
        put("transportWithBiochemicalReaction", "A conversion interaction that is both a "
                + "biochemicalReaction and a transport.");
        put("complexAssembly", "A conversion interaction in which a set of physical entities, at "
                + "least one being a macromolecule (protein, RNA, or DNA), aggregate via "
                + "non-covalent interactions.");
        put("transport", "A conversion interaction in which an entity (or set of entities) changes "
                + "location within or with respect to the cell.");
        put("complex", "A physical entity whose structure is comprised of other physical entities "
                + "bound to each other non-covalently, at least one of which is a macromolecule "
                + "(protein, DNA, or RNA).");
        put("pathway", "A set or series of interactions, often forming a network, which "
			    + "biologists have found useful to group together for organizational, "
			    + "historic, biophysical or other reasons.");
    }
}
