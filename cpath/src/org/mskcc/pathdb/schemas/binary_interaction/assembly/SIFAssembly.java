// $Id: SIFAssembly.java,v 1.1 2008-01-16 02:04:46 grossben Exp $
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

import java.util.List;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * BinaryInteractionAssembly which generates SIF.
 *
 * @author Benjamin Gross
 */
public class SIFAssembly extends BinaryInteractionAssemblyBase implements BinaryInteractionAssembly {

	/**
	 * Constructor.
	 *
	 * @param bpModel Model
	 * @pararm ruleTypes List<String>
	 */
	public SIFAssembly(Model bpModel, List<String> ruleTypes) {
		// init args
		super(bpModel, ruleTypes);
	}

    /**
     * Our implementation of BinaryInteractionAssembly.getBinaryInteractionString().
     *
     * @return String
	 * @throws IOException
     */
    public String getBinaryInteractionString() throws IOException {

		// create outputstream to pass into simple interaction converter
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// write out binary interactions
		converter.writeInteractionsInSIF(bpModel, out);

		// outta here
		return out.toString();
	}
}