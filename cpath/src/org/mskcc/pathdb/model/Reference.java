// $Id: Reference.java,v 1.2 2006-12-22 13:42:15 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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

/**
 * Class to encapsulate PubMed table record.
 *
 * @author Benjamin Gross
 */
public class Reference {

	// reference id
    private String id;
	
	// database
	private String database;

	// data
    private String year;

	// title
	private String title;

	// authors
	private String[] authors;

	// source
	private String source;

    /**
     * Sets the ref id.
     *
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ref id.
     *
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the database.
     *
     * @param database String
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * Gets the database.
	 *
     * @return String
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Sets the year.
     *
     * @param year String
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Gets the year.
	 *
     * @return String
     */
    public String getYear() {
        return year;
    }


    /**
     * Sets the title.
     *
     * @param title String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the title.
	 *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the authors.
     *
     * @param authors String[]
     */
    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    /**
     * Gets the author.
	 *
     * @return String[]
     */
    public String[] getAuthors() {
        return authors;
    }

    /**
     * Sets the source.
     *
     * @param source String
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the source.
	 *
     * @return String
     */
    public String getSource() {
        return source;
    }

	/**
	 * Returns a "reference" string for display purposes.
	 * 
	 * @return String
	 */
	public String getReferenceString() {

		return (database.equalsIgnoreCase("PubMed")) ?
			(title + " " + source) : 
			(database + ":" + id);
	}
}
