// $Id: EntitySummary.java,v 1.8 2007-05-14 20:23:56 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import java.io.Serializable;
import java.util.List;

// imports

/**
 * This is the base class of an interaction summary.
 *
 * @author Benjamin Gross.
 */
public class EntitySummary implements Serializable {

    /**
     * CPath record ID of the record that this class summarizes.
     */
    private long recordID;

    /**
     * The name of the entity.
     */
    private String name;

    /**
     * The specific type of the entity.
     */
    private String specificType;

    /**
     * Snapshot ID.
     */
    private long snapshotId;

    /**
     * External Links.
     */
    private List externalLinks;

    /**
     * Constructor.
     */
    public EntitySummary() {
    }

    /**
     * Constructor.
     *
     * @param recordID     long
     * @param name         String
     * @param specificType String
     */
    public EntitySummary(long recordID, String name, String specificType) {

        // init our members
        this.recordID = recordID;
        this.name = name;
        this.specificType = specificType;
    }

    /**
     * Sets the cpath id for this summary.
     *
     * @param recordID long
     */
    public void setId(long recordID) {
        this.recordID = recordID;
    }

    /**
     * Gets the cpath id for this summary.
     *
     * @return long
     */
    public long getRecordID() {
        return recordID;
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
    public String getName() {
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

    /**
     * Gets snapshot ID.
     * @return snapshot ID.
     */
    public long getSnapshotId() {
        return snapshotId;
    }

    /**
     * Sets snapshot ID.
     * @param snapshotId snapshot ID.
     */
    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * Set the record external links list.
     *
     * @param externalLinks List
     */
    public void setExternalLinks(List externalLinks) {
        this.externalLinks = externalLinks;
    }

    /**
     * Returns the record external links list.
     *
     * @return List
     */
    public List getExternalLinks() {
        return externalLinks;
    }
}