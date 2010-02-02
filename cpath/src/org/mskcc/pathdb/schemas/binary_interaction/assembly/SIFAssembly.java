// $Id: SIFAssembly.java,v 1.6 2010-02-02 17:22:29 grossben Exp $
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
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.io.sif.SimpleInteraction;
import org.biopax.paxtools.io.simpleIO.SimpleEditorMap;

import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
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
		this(bpModel, ruleTypes, false);
	}

	/**
	 * Constructor.
	 *
	 * @param bpModel Model
	 * @param reduceComplexes boolean
	 * @pararm ruleTypes List<String>
	 */
	public SIFAssembly(Model bpModel, List<String> ruleTypes, boolean reduceComplexes) {
		// init args
		super(bpModel, ruleTypes, reduceComplexes);
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

        // get return string
		String toReturn = cookSIFString(out.toString());
		out.close();

		// outta here
		return toReturn;
	}

    /**
     * Our implementation of BinaryInteractionAssembly.getBinaryInteractionStrings().
     *
     * @return List<String> - size 2, first string is edges, second string is nodes
	 * @throws IOException
     */
    public List<String> getExtendedBinaryInteractionStrings() throws IOException {

		// create outputstream to pass into simple interaction converter
		ByteArrayOutputStream edges = new ByteArrayOutputStream();
		ByteArrayOutputStream nodes = new ByteArrayOutputStream();

		// write out binary interactions
		converter.writeInteractionsInSIFNX(bpModel, edges, nodes, true, new SimpleEditorMap(BioPAXLevel.L2), "NAME", "XREF");

        // do a bit of cooking
		String edgesString = cookSIFString(edges.toString());
		String nodesString = cookSIFString(nodes.toString());

        // outta here
		List<String> toReturn = new ArrayList<String>();
		toReturn.add(edgesString);
		toReturn.add(nodesString);
		return toReturn;
	}

	private String cookSIFString(String sifString) {

        if (sifString.indexOf("http://cbio.mskcc.org/cpath#CPATH") > -1) {
            sifString = sifString.replaceAll("http://cbio.mskcc.org/cpath#CPATH-", "");
        } else if (sifString.indexOf("http://www.biopax.org/examples/proteomics-interaction") > -1){
            //  This is a special case to handle the unit test data.
            sifString = sifString.replaceAll("http://www.biopax.org/examples/proteomics-interaction#", "");
        }

		return sifString;
	}
}