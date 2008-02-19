// $Id: BinaryInteractionAssemblyBase.java,v 1.3 2008-02-19 21:17:51 grossben Exp $
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
package org.mskcc.pathdb.schemas.binary_interaction.assembly;

// imports
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.io.sif.InteractionRule;
import org.biopax.paxtools.io.sif.SimpleInteractionConverter;
import org.biopax.paxtools.io.sif.level2.BinaryInteractionType;
import org.mskcc.pathdb.schemas.binary_interaction.util.BinaryInteractionUtil;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

/**
 * BinaryInteractionAssemblyBase class.
 * (contains logic to deal with paxtools - simply interactions).
 *
 * @author Benjamin Gross
 */
public abstract class BinaryInteractionAssemblyBase {

	/**
	 * ref to paxtools model
	 */
	protected Model bpModel;

	/**
	 * ref to simple interaction converter
	 */
	protected SimpleInteractionConverter converter;

	/**
	 * Constructor.
	 *
	 * @param bpModel Model
	 * @pararm ruleTypes List<String>
	 */
	public BinaryInteractionAssemblyBase(Model bpModel, List<String> ruleTypes) {

		// init args
		this.bpModel = bpModel;
		this.converter = createConverter(ruleTypes);
	}

	/**
	 * Creates a simple converter.
	 *
	 * @param ruleTypes List<String>
	 * @return SimpleInteractionConverter
	 */
	private SimpleInteractionConverter createConverter(List<String> ruleTypes) {

		InteractionRule[] rules = BinaryInteractionUtil.getRuleClasses();
		Map<String, Boolean> options = new HashMap<String, Boolean>();

		for (InteractionRule rule : rules) {
			for (BinaryInteractionType ruleType : rule.getRuleTypes()) {
				if (ruleTypes.contains(ruleType.getTag())) {
					options.put(ruleType.getTag(), true);
				}
				else {
					options.put(ruleType.getTag(), false);
				}
			}
		}

		// outta here
		return new SimpleInteractionConverter(options, rules);
	}
}