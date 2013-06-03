// $Id: ImportRecord.java,v 1.11 2006-10-06 14:35:00 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import java.util.Date;

/**
 * JavaBean to Encapsulate an Import Record.
 *
 * @author Ethan Cerami
 */
public class ImportRecord {
    private String data;
    private String description;
    private XmlRecordType xmlType;
    private int importId;
    private long snapshotId;
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
     * Invalid Record Status.
     */
    public static final String STATUS_INVALID = "INVALID";

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

    /**
     * Gets XML Record Type.
     *
     * @return XmlRecordType Object.
     */
    public XmlRecordType getXmlType() {
        return xmlType;
    }

    /**
     * Sets XML Record Type.
     *
     * @param xmlType XmlRecordType Object.
     */
    public void setXmlType(XmlRecordType xmlType) {
        this.xmlType = xmlType;
    }

    /**
     * Gets the snapsot ID.
     * @return snapshot ID.
     */
    public long getSnapshotId() {
        return snapshotId;
    }

    /**
     * Sets the snapshot ID.
     * @param snapshotId snapshot ID.
     */
    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }
}
