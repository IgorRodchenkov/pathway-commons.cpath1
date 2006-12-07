// $Id: BioPaxRecordSummary.java,v 1.7 2006-12-07 15:44:23 grossb Exp $
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

// imports

import java.util.List;
import java.io.Serializable;

/**
 * This class represents a biopax record summary.
 * It contains all the biopax record info (less interaction summary)
 * that would want to be displayed on a biopax record page.
 *
 * @author Benjamin Gross.
 */
public class BioPaxRecordSummary implements Serializable {

    /**
     * The cpath record id.
     */
    protected long recordID;

    /**
     * The record type.
     */
    protected String type;

    /**
     * The record name.
     */
    protected String name;

    /**
     * The record short name.
     */
    protected String shortName;

    /**
     * The record synonyms.
     */
    protected List synonyms;

    /**
     * The record organism.
     */
    protected String organism;

    /**
     * The record data source.
     */
    protected String dataSource;

    /**
     * The record data source snapshot id.
     */
    protected Long snapshotId;

    /**
     * The record availability.
     */
    protected String availability;

    /**
     * The record external links.
     */
    protected List externalLinks;

    /**
     * The record comment.
     */
    protected String comment;

    /**
     * Sets the cpath record id.
     *
     * @param recordID long
     */
    public void setRecordID(long recordID) {
        this.recordID = recordID;
    }

    /**
     * Gets the cpath record id.
     *
     * @return long
     */
    public long getRecordID() {
        return recordID;
    }

    /**
     * Set the record type.
     *
     * @param type String
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the record type.
     *
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Set the record name.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the record name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Set the record short name.
     *
     * @param shortName String
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Returns the record short name.
     *
     * @return String
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Set the record synonyms list.
     *
     * @param synonyms List
     */
    public void setSynonyms(List synonyms) {
        this.synonyms = synonyms;
    }

    /**
     * Returns the record synonym list.
     *
     * @return List
     */
    public List getSynonyms() {
        return synonyms;
    }

    /**
     * Set the record organism.
     *
     * @param organism String
     */
    public void setOrganism(String organism) {
        this.organism = organism;
    }

    /**
     * Returns the record organism.
     *
     * @return String
     */
    public String getOrganism() {
        return organism;
    }

    /**
     * Set the record data source.
     *
     * @param dataSource String
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns the record data source.
     *
     * @return String
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * Set the record data source snapshot id.
     *
     * @param dataSource String
     */
    public void setDataSourceSnapshotId(Long snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * Returns the record data source snapshot id.
     *
     * @return Long
     */
    public Long getDataSourceSnapshotId() {
        return snapshotId;
    }

    /**
     * Set the record availability.
     *
     * @param availability String
     */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /**
     * Returns the record availabilty.
     *
     * @return String
     */
    public String getAvailability() {
        return availability;
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

    /**
     * Set the record comment.
     *
     * @param comment String
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns the record comment.
     *
     * @return String
     */
    public String getComment() {
        return comment;
    }
}
