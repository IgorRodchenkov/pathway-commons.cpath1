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
public class ExternalDatabase {
    private int id;
    private String name;
    private String description;
    private ArrayList cvTerms;
    private String url;
    private Date createTime;
    private Date updateTime;

    /**
     * Gets the Database ID.
     * @return External Database ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Database ID.
     * @param id External Database ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets Database Name.
     * @return Database Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets Database Name.
     * @param name Databse Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets Database Description.
     * @return Database Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets Database Description.
     * @param description Database Description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets List of Controlled Vocabulary Terms which match this database.
     * @return ArrayList of String terms.
     */
    public ArrayList getCvTerms() {
        return this.cvTerms;
    }

    /**
     * Sets List of Controlled Vocabulary Terms which match this database.
     * @param terms ArrayList of String terms.
     */
    public void setCvTerms(ArrayList terms) {
        this.cvTerms = terms;
    }

    /**
     * Gets URL for Retrieving individual record from the database.
     * @return URL String.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets URL for Retrieving individual record from the database.
     * @param url URL String.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the TimeStamp when this record was created.
     * @return Time Created.
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets the TimeStamp when this record was created.
     * @param createTime Date Object.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets the TimeStamp when this record was updated.
     * @return Time Updated.
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets the TimeStamp when this records was updated.
     * @param updateTime Date Object.
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}