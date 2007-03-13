// $Id: BBInternalLinkRecord.java,v 1.1 2007-03-13 14:44:04 grossb Exp $
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
package org.mskcc.pathdb.model.bb;

/**
 * JavaBean to Encapsulate a BB Internal Link Record.
 *
 * @author Benjamin Gross
 */
public class BBInternalLinkRecord {

	/**
	 * pathway id
	 */
    private String pathwayID;

	/**
	 * entrez gene id
	 */
    private String entrezGeneID;

    /**
     * No-arg Constructor.
     */
    public BBInternalLinkRecord() {
    }

    /**
     * Constructor.
     *
     * @param pathwayID String
     * @param entrezGeneID String
     */
    public BBInternalLinkRecord(String pathwayID, String entrezGeneID) {
        this.pathwayID = pathwayID;
        this.entrezGeneID = entrezGeneID;
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
     * Gets entrez gene id.
     *
     * @return String
     */
    public String getEntrezGeneID() {
        return entrezGeneID;
    }

    /**
     * Sets entrez gene id.
     *
     * @param entrezGeneID String
     */
    public void setEntrezGeneID(String entrezGeneID) {
        this.entrezGeneID = entrezGeneID;
    }
}
