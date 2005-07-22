/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
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

import java.util.ArrayList;
import java.util.Date;

/**
 * JavaBean to Encapsulate an External Database Record.
 * <P>
 * PSI-MI and BioPax do not yet have controlled vocabularies for external
 * references.  For example, one PSI file might include a reference to "SWP",
 * and another file might include a reference to "SWISS-PROT".  To accommodote
 * a varying list of terms, each database record can be associated with multiple
 * controlled vocabulary terms.  The full list of terms is available via
 * the getCvTerms() method.
 *
 * @author Ethan Cerami
 */
public class ExternalDatabaseRecord {
    private int id;
    private String name;
    private String description;
    private ArrayList cvTerms;
    private String fixedCvTerm;
    private String url;
    private String sampleId;
    private ReferenceType dbType;
    private Date createTime;
    private Date updateTime;

    /**
     * Gets the Database ID.
     *
     * @return External Database ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Database ID.
     *
     * @param id External Database ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets Database Name.
     *
     * @return Database Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets Database Name.
     *
     * @param name Databse Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets Database Description.
     *
     * @return Database Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets Database Description.
     *
     * @param description Database Description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets List of Controlled Vocabulary Terms which match this database.
     *
     * @return ArrayList of String terms.
     */
    public ArrayList getCvTerms() {
        return this.cvTerms;
    }

    /**
     * Sets List of Controlled Vocabulary Terms which match this database.
     *
     * @param terms ArrayList of String terms.
     */
    public void setCvTerms(ArrayList terms) {
        this.cvTerms = terms;
    }

    /**
     * Gets URL for Retrieving individual record from the database.
     *
     * @return URL String.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets URL for Retrieving a specific individual record from the database.
     *
     * @param primaryId Primary ID.
     * @return URL String.
     */
    public String getUrlWithId(String primaryId) {
        if (primaryId != null) {
            //  Hard-Coded Fix for HPRD Ids.
            primaryId = primaryId.replaceAll("HPRD_", "");
            if (url != null && url.trim().length() > 0) {
                return url.replaceAll("%ID%", primaryId);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Sets URL for Retrieving individual record from the database.
     *
     * @param url URL String.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets a Sample ID, used to generate sample live links.
     * @return SampleId String.
     */
    public String getSampleId() {
        return sampleId;
    }

    /**
     * Sets a Sample ID, used to generate sample live links.
     * @param sampleId Sample ID String.
     */
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * Gets the TimeStamp when this record was created.
     *
     * @return Time Created.
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets the TimeStamp when this record was created.
     *
     * @param createTime Date Object.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets the TimeStamp when this record was updated.
     *
     * @return Time Updated.
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets the TimeStamp when this records was updated.
     *
     * @param updateTime Date Object.
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Gets the fixed (or normalized) CV Term.
     *
     * @return CV Term.
     */
    public String getFixedCvTerm() {
        return fixedCvTerm;
    }

    /**
     * Sets the fixed (or normalized) CV Term.
     *
     * @param fixedCvTerm CV Term String.
     */
    public void setFixedCvTerm(String fixedCvTerm) {
        this.fixedCvTerm = fixedCvTerm;
    }

    /**
     * Gets the External Reference Type
     *
     * @return ReferenceType Object.
     */
    public ReferenceType getDbType() {
        return dbType;
    }

    /**
     * Sets the External Reference Type Object.
     *
     * @param refType ReferenceType Object.
     */
    public void setDbType(ReferenceType refType) {
        this.dbType = refType;
    }
}