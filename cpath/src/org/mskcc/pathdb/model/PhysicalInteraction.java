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
package org.mskcc.pathdb.model;

// imports
import java.util.Vector;

/**
 * This class encapsulates
 * the members of a physical interaction.
 *
 * @author Benjamin Gross.
 */
public class PhysicalInteraction {

	/**
	 * The physical interaction type.
	 */
	private String physicalInteractionType;

	/**
	 * Our vector of controllers or substrates (left side guys).
	 */
	private Vector leftSideComponents = null;

	/**
	 * Our vector of controlled or products (right side guys).
	 */
	private Vector rightSideComponents = null;

	/**
     * Constructor.
	 *
	 * @param leftSideComponents Vector.
	 * @param rightSideComponents Vector.
     */
    public PhysicalInteraction(Vector leftSideComponents, Vector rightSideComponents) {
		this (new String("Not Available"), leftSideComponents, rightSideComponents);
	}

	/**
     * Constructor.
	 *
	 * @param physicalInteractionType String.
	 * @param leftSideComponents Vector.
	 * @param rightSideComponents Vector.
     */
    public PhysicalInteraction(String physicalInteractionType, Vector leftSideComponents, Vector rightSideComponents) {

		// init our members
		this.physicalInteractionType = physicalInteractionType;
		this.leftSideComponents = leftSideComponents;
		this.rightSideComponents = rightSideComponents;
	}

	/**
	 * Returns the physical interaction type.
	 *
	 * @return String.
	 */
	public String getPhysicalInteractionType(){
		return physicalInteractionType;
	}

	/**
	 * Returns the vector of leftSideComponents.
	 *
	 * @return Vector.
	 */
	public Vector getLeftSideComponents(){
		return leftSideComponents;
	}

	/**
	 * Returns the vector of rightSideComponents.
	 *
	 * @return Vector.
	 */
	public Vector getRightSideComponents(){
		return rightSideComponents;
	}
}
