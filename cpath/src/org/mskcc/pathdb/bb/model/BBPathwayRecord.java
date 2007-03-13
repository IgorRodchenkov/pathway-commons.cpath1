// $Id: BBPathwayRecord.java,v 1.3 2007-03-13 15:29:43 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.bb.model;

// imports
import java.io.Serializable;

/**
 * JavaBean to Encapsulate a BB Pathwayr Record.
 *
 * @author Benjamin Gross
 */
public class BBPathwayRecord implements Serializable {

	/**
	 * pathway id
	 */
    private String pathwayID;

	/**
	 * pathway name
	 */
    private String pathwayName;

	/**
	 * source
	 */
    private String source;

	/**
	 * url
	 */
    private String url;

    /**
     * Constructor.
     *
	 * @param pathwayID  
	 * @param pathwayName
	 * @param source
	 * @param url
     */
    public BBPathwayRecord(String pathwayID, String pathwayName, String source, String url) {

		// init members
		this.pathwayID = pathwayID;
		this.pathwayName = pathwayName;
		this.source = source;
		this.url = url;
    }

    /**
     * Gets the pathway id.
     *
     * @return String
     */
    public String getPathwayID() {
        return pathwayID;
    }

    /**
     * Sets the pathway id.
     *
     * @param pathwayID String
     */
    public void setPathwayID(String pathwayID) {
        this.pathwayID = pathwayID;
    }

    /**
     * Gets pathway name.
     *
     * @return String
     */
    public String getPathwayName() {
        return pathwayName;
    }

    /**
     * Sets pathway name.
     *
     * @param pathwayName String
     */
    public void setPathwayName(String pathwayName) {
        this.pathwayName = pathwayName;
    }

    /**
     * Gets pathway source.
     *
     * @return String
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets pathway source.
     *
     * @param source String
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets pathway url.
     *
     * @return String
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets pathway url.
     *
     * @param source String
     */
    public void setURL(String url) {
        this.url = url;
    }
}
