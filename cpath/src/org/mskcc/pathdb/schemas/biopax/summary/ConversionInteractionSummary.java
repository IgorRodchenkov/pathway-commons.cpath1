// $Id: ConversionInteractionSummary.java,v 1.2 2006-02-09 21:49:45 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
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

// package
package org.mskcc.pathdb.schemas.biopax.summary;

// imports
import java.util.ArrayList;

/**
 * This class contains the information
 * needed to construct a conversion interaction summary string.
 *
 * @author Benjamin Gross.
 */
public class ConversionInteractionSummary extends PhysicalInteractionSummary {

	/**
	 * The left side components of this conversion interaction.
	 */
	private ArrayList leftSideComponents = null;

	/**
	 * The right side components of this conversion interaction.
	 */
	private ArrayList rightSideComponents = null;

	/**
     * Constructor.
	 *
	 * @param participants ArrayList
	 * @param leftSideComponents ArrayList
	 * @param rightSideComponents ArrayList
     */
    public ConversionInteractionSummary(ArrayList participants, ArrayList leftSideComponents, ArrayList rightSideComponents) {

		// init our members
		super(participants);
		this.leftSideComponents = leftSideComponents;
		this.rightSideComponents = rightSideComponents;
	}

  	/**
	 * Returns the ArrayList of Left Components.
  	 *
  	 * @return ArrayList
  	 */
 	public ArrayList getLeftSideComponents(){
 		return leftSideComponents;
 	}
 
 	/**
 	 * Returns the ArrayList of Right Components.
 	 *
 	 * @return ArrayList
 	 */
 	public ArrayList getRightSideComponents(){
 		return rightSideComponents;
  	}
}
