package org.mskcc.pathdb.model;

import java.util.Date;

/**
 * JavaBean to Encapsulate an External Link Record.
 *
 * @author Ethan Cerami
 */
public class ExternalLinkRecord {
    private long id;
    private long cpathId;
    private int externalDbId;
    private String linkedToId;
    private Date createTime;
    private Date updateTime;
    private ExternalDatabaseRecord db;

    /**
     * Gets External Link ID.
     * @return External Link ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets External Link ID.
     * @param id External Link ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets CPath ID.
     * @return CPath ID.
     */
    public long getCpathId() {
        return cpathId;
    }

    /**
     * Sets CPath ID.
     * @param cpathId CPath ID.
     */
    public void setCpathId(long cpathId) {
        this.cpathId = cpathId;
    }

    /**
     * Gets External Database ID.
     * @return External Database ID.
     */
    public int getExternalDbId() {
        return externalDbId;
    }

    /**
     * Sets External Database ID.
     * @param externalDbId External Database ID.
     */
    public void setExternalDbId(int externalDbId) {
        this.externalDbId = externalDbId;
    }

    /**
     * Gets Linked To ID.
     * @return Linked To ID.
     */
    public String getLinkedToId() {
        return linkedToId;
    }

    /**
     * Sets Linked To ID.
     * @param linkedToId Linked To ID.
     */
    public void setLinkedToId(String linkedToId) {
        this.linkedToId = linkedToId;
    }

    /**
     * Gets Creation Time.
     * @return Date Object.
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets Creation Time.
     * @param createTime Date Object.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets Update Time.
     * @return Date Object.
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets Update Time.
     * @param updateTime Date Object.
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Gets the External Database.
     * @return External Database Record.
     */
    public ExternalDatabaseRecord getExternalDatabase() {
        return db;
    }

    /**
     * Sets the External Database.
     * @param db External Database Record.
     */
    public void setExternalDatabase(ExternalDatabaseRecord db) {
        this.db = db;
        if (db != null) {
            this.externalDbId = db.getId();
        }
    }

    /**
     * Gets the Web Link to the specified Resource.
     * @return URL String.
     */
    public String getWebLink() {
        if (db != null) {
            String dbUrl = db.getUrl();
            if (dbUrl != null) {
                return dbUrl.replaceAll("%ID%", this.linkedToId);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}