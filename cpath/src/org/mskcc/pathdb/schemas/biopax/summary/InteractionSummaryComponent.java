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
 * This class encapsulates
 * the members of a physical interaction component.
 *
 * @author Benjamin Gross.
 */
public class InteractionSummaryComponent {

	/**
	 * The component name.
	 */
	private String name;

	/**
	 * The component id.
	 */
	private long recordID;

	/**
	 * The cellular location.
	 */
	private String cellularLocation;

	/**
	 * The feature list ArrayList.
	 */
	private ArrayList featureList = new ArrayList();

	/**
	 * Set the component name.
	 *
	 * @param name String
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * Returns the component name.
	 *
	 * @return String
	 */
	public String getName(){
		return name;
	}

	/**
	 * Sets the record id.
	 *
	 * @param recordID long
	 */
	public void setRecordID(long recordID){
		this.recordID = recordID;
	}

	/**
	 * Returns the record id.
	 *
	 * @return long
	 */
	public long getRecordID(){
		return recordID;
	}

	/**
	 * Sets the cellular location.
	 *
	 * @param cellularLocation String.
	 */
	public void setCellularLocation(String cellularLocation){
		this.cellularLocation = cellularLocation;
	}

	/**
	 * Returns the cellular location.
	 *
	 * @return String
	 */
	public String getCellularLocation(){
		return cellularLocation;
	}

	/**
	 * Sets the feature list ArrayList.
	 *
	 * @param featureList ArrayList
	 */
	public void setFeatureList(ArrayList featureList){
		this.featureList = featureList;
	}

	/**
	 * Returns the feature list ArrayList.
	 *
	 * @return ArrayList
	 */
	public ArrayList getFeatureList(){
		return featureList;
	}
}
