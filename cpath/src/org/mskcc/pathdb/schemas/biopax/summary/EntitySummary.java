// $Id: EntitySummary.java,v 1.1 2006-02-10 19:59:04 grossb Exp $
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

/**
 * This is the base class of an interaction summary.
 *
 * @author Benjamin Gross.
 */
public class EntitySummary {

	/**
	 * CPath record ID of the record that this class summarizes.
	 */
	private long id;

	/**
	 * The name of the entity.
	 */
    private String name;

	/**
	 * The specific type of the entity.
	 */
    private String specificType;

	/**
     * Constructor.
     */
    public EntitySummary(){
	}

	/**
     * Constructor.
	 *
	 * @param id long
	 * @param name String
	 * @param specificType String
     */
    public EntitySummary(long id, String name, String specificType){

		// init our members
		this.id = id;
		this.name = name;
		this.specificType = specificType;
	}

    /**
     * Sets the cpath id for this summary.
     *
     * @param id long
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the Primary Key Id.
     *
     * @return long
     */
    public long getId(){
        return id;
    }

    /**
     * Sets the Entity Name.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Entity Name.
     *
     * @return String
     */
    public String getName(){
        return name;
    }

    /**
     * Sets the most specific class type in the ontology for this summary.
     *
     * @param specificType String
     */
    public void setSpecificType(String specificType) {
        this.specificType = specificType;
    }

    /**
     * Gets the most specific class type in the ontology for this summary.
     *
     * @return String
     */
    public String getSpecificType() {
        return specificType;
    }
}
