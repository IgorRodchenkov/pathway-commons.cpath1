// $Id: BinaryInteractionUtil.java,v 1.2 2008-01-22 17:48:07 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2008 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
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
package org.mskcc.pathdb.schemas.binary_interaction.util;

// imports
import org.biopax.paxtools.io.sif.InteractionRule;
import org.biopax.paxtools.io.sif.level2.ControlRule;
import org.biopax.paxtools.io.sif.level2.ComponentRule;
import org.biopax.paxtools.io.sif.level2.ParticipatesRule;
import org.biopax.paxtools.io.sif.level2.ConsecutiveCatalysisRule;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class which provides various util methods to suppport binary interactions.
 *
 * @author Benjamin Gross
 */
public class BinaryInteractionUtil {

	// possible rules
	private static final List<InteractionRule> possibleRules = Arrays.asList(new ComponentRule(),
																			 new ConsecutiveCatalysisRule(),
																			 new ControlRule(),
																			 new ParticipatesRule());

	// rule description map - this is temporary, descrip should come from paxtool rule classes themselves.
	private static final Map<String, String> ruleTypeDescriptionMap = new HashMap<String, String>();
	static {
		ruleTypeDescriptionMap.put(ComponentRule.COMPONENT_OF, "This rule infers an interaction if the first entity is a component of the second entity, which is a complex. This interaction is transient in the sense that A component_of B and B component_of C implies A component_of C. This interaction is directed.");
		ruleTypeDescriptionMap.put(ComponentRule.COMPONENT_IN_SAME, "This rule infers an interaction if two entities belong to at least one molecular complex. This does not necessarily mean they interact directly. In a complex with n molecules, this rule will create a clique composed of n(n-1)/2 interactions. This interaction is undirected.");
		ruleTypeDescriptionMap.put(ConsecutiveCatalysisRule.SEQUENTIAL_CATALYSIS, "This rule infers an interaction if A and B catalyzes two conversions that are connected via a common molecule, and where potentially that common substrate is produced by the former and consumed by the latter. This rule is directed.");
		ruleTypeDescriptionMap.put(ControlRule.CONTROLS_STATE_CHANGE, "This rule infers an interaction if the first entity catalyses a reaction that either consumes or produces the second entity. More specifically, this rule will find an interaction between two entities A and B if and only if A controls a conversion which B participates and appears both on the left or right side of the conversion. This rule is directed.");
		ruleTypeDescriptionMap.put(ControlRule.CONTROLS_METABOLIC_CHANGE, "This rule infers an interaction if the first entity catalyses a reaction that either consumes or produces the second entity. More specifically, this rule will find an interaction between two entities A and B if and only if A controls a conversion which B participates and appears only on the left or right side of the conversion but not both. This rule is directed.");
		ruleTypeDescriptionMap.put(ParticipatesRule.PARTICIPATES_CONVERSION, "This rule infers an interaction if both A and B participates in a conversion as substrates or products. Controllers are not included. This rule is undirected.");
		ruleTypeDescriptionMap.put(ParticipatesRule.PARTICIPATES_INTERACTION, "This rule infers an interaction if both A and B participates in an interaction as participants. Controllers are not included. This rule is undirected.");
	}

	/**
	 * Gets arrary of all binary interaction rule classes.
	 *
	 * @return InteractionRule[]
	 */
	public static InteractionRule[] getRuleClasses() {
		return possibleRules.toArray(new InteractionRule[possibleRules.size()]);
	}

	/**
	 * Gets list of binary interaction rule types for all rule classes.
	 *
	 * @return List<String>
	 */
	public static List<String> getRuleTypes() {

		// list to return
		List<String> toReturn = new ArrayList<String>();

		// interate through each rule class and get each rule type
		for (InteractionRule rule : possibleRules) {
			for (String ruleType : rule.getRuleTypes()) {
				toReturn.add(ruleType);
			}
		}

		// outta here
		return toReturn;
	}

	/**
	 * Given a rule type, returns the rule type description.
	 *
	 * @param ruleType String
	 * @return String
	 */
	public static String getRuleTypeDescription(String ruleType) {
		return ruleTypeDescriptionMap.get(ruleType);
	}
}