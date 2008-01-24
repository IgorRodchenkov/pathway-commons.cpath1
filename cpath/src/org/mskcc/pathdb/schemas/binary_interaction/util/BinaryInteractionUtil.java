// $Id: BinaryInteractionUtil.java,v 1.3 2008-01-24 15:32:28 grossben Exp $
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
import org.biopax.paxtools.io.sif.*;
import org.biopax.paxtools.io.sif.level2.*;
import org.biopax.paxtools.io.sif.level2.BinaryInteractionType.*;

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
																			 new ControlsTogetherRule(),
																			 new ParticipatesRule());

	// initialive rule type list and binaryInteractionTypeMap
	private static final ArrayList<String> ruleTypes = new ArrayList<String>();
	private static final Map<String, BinaryInteractionType> binaryInteractionTypeMap = new HashMap<String, BinaryInteractionType>();
	static {
		// interate through each rule class and get each rule type
		for (InteractionRule rule : possibleRules) {
			for (String ruleType : rule.getRuleTypes()) {
				// add ruleType to ruletype list
				ruleTypes.add(ruleType);
				// add to binaryInteractionTypeMap
				BinaryInteractionType[] binaryInteractionTypes = BinaryInteractionType.values();
				for (BinaryInteractionType binaryInteractionType : binaryInteractionTypes) {
					if (binaryInteractionType.getTag().equals(ruleType)) {
						binaryInteractionTypeMap.put(ruleType, binaryInteractionType);
					}
				}
			}
		}
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

		// outta here
		return (List<String>)ruleTypes.clone();
	}

	/**
	 * Given a rule type, returns the rule type description.
	 *
	 * @param ruleType String
	 * @return String
	 */
	public static String getRuleTypeDescription(String ruleType) {
		return binaryInteractionTypeMap.get(ruleType).getDescription();
	}
}