// $Id: BBGeneRecord.java,v 1.2 2007-03-13 15:23:46 grossb Exp $
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
 * JavaBean to Encapsulate a BB Gene Record.
 *
 * @author Benjamin Gross
 */
public class BBGeneRecord implements Serializable {

	/**
	 * entrezGeneID
	 */
    private String entrezGeneID;

	/**
	 * kegg gene name
	 */
    private String geneName;

    /**
     * Constructor.
     *
	 * @param entrezGeneID
	 * @param geneName
     */
    public BBGeneRecord(String entrezGeneID, String geneName) {

		// init members
        this.entrezGeneID = entrezGeneID;
		this.geneName = geneName;
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

    /**
     * Gets gene name
     *
     * @return String
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     * Sets gene name.
     *
     * @param geneName String
     */
    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }
}
