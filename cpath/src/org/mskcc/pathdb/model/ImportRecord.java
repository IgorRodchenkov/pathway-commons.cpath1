package org.mskcc.pathdb.model;

import java.util.Date;

/**
 * JavaBean to Encapsulate an Import Record.
 *
 * @author Ethan Cerami
 */
public class ImportRecord {
    private String data;
    private String description;
    private int importId;
    private String status;
    private Date createTime;
    private Date updateTime;
    private String md5Hash;
    /**
     * New Record Status.
     */
    public static final String STATUS_NEW = "NEW";
    /**
     * Transferred Record Status.
     */
    public static final String STATUS_TRANSFERRED = "TRANSFERRED";

    /**
     * Gets Import Record Data.
     *
     * @return Import Record Data String.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets Import Data Record.
     *
     * @param data Import Record Data String.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the Import Description.
     *
     * @return Description String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Import Description.
     *
     * @param description Description String.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets Import Record ID.
     *
     * @return Import Record ID.
     */
    public int getImportId() {
        return importId;
    }

    /**
     * Gets Import Record ID.
     *
     * @param importId Import Record ID.
     */
    public void setImportId(int importId) {
        this.importId = importId;
    }

    /**
     * Gets Import Record Status.
     *
     * @return Import Record Status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets Import Record Status.
     *
     * @param status Import Record Status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets Creation TimeStamp.
     *
     * @return Creation Date Object.
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets Creation TimeStamp.
     *
     * @param createTime Creation Date Object.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets Updated TimeStamp.
     *
     * @return Updated Date Object.
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets Updated TimeStamp.
     *
     * @param updateTime Data Object.
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Gets MD5 Hash.
     *
     * @return Base64 Encoded MD5 Hash.
     */
    public String getMd5Hash() {
        return md5Hash;
    }

    /**
     * Sets MD5 Hash.
     *
     * @param md5Hash Base64 Encoded MD5 Hash.
     */
    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }
}