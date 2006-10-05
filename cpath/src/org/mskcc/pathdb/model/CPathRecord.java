// $Id: CPathRecord.java,v 1.12 2006-10-05 20:08:24 cerami Exp $
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

import java.io.Serializable;
import java.util.Date;

/**
 * JavaBean to Encapsulate a cPath Record.
 *
 * @author Ethan Cerami
 */
public class CPathRecord implements Serializable {

    /**
     * NCBI Taxonomy ID is not specified.
     */
    public static final int TAXONOMY_NOT_SPECIFIED = -9999;

    /**
     * "Not Available" String.
     */
    public static final String NA_STRING = "N/A";

    private long id;
    private String name;
    private String description;
    private CPathRecordType type;
    private XmlRecordType xmlType;
    private String specType;
    private int ncbiTaxonomyId;
    private String xmlContent;
    private long snapshotId;
    private boolean cpathGenerated;
    private Date createTime;
    private Date updateTime;

    /**
     * Constructor.
     */
    public CPathRecord() {
        this.ncbiTaxonomyId = CPathRecord.TAXONOMY_NOT_SPECIFIED;
    }

    /**
     * Gets the Primary Key Id.
     *
     * @return Primary Key Id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the Primary Key Id.
     *
     * @param id Primary Key Id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the Entity Name.
     *
     * @return Entity Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Entity Name.
     *
     * @param name Entity Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Entity Description.
     *
     * @return Entity Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Entity Description.
     *
     * @param description Entity Description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Record Type.  Constrained to the Enumeration List
     * defined by CPathRecordType.
     *
     * @return CPathRecordType Object.
     */
    public CPathRecordType getType() {
        return type;
    }

    /**
     * Sets the Record Type.  Constrained to the Enumeration List
     * defined by CPathRecordType.
     *
     * @param type CPathRecordType Object.
     */
    public void setType(CPathRecordType type) {
        this.type = type;
    }

    /**
     * Gets the most specific class type in the ontology for this record.
     *
     * @return Specific Class Type String.
     */
    public String getSpecificType() {
        return specType;
    }

    /**
     * Sets the most specific class type in the ontology for this record.
     *
     * @param specType Specific Class Type String.
     */
    public void setSpecType(String specType) {
        this.specType = specType;
    }

    /**
     * Gets the NCBI Taxonomy ID.  If taxonomy Id is not specified, this
     * method will return CPathRecord.TAXONOMY_NOT_SPECIFIED.
     *
     * @return Taxonomy ID or CPathRecord.TAXONOMY_NOT_SPECIFIED.
     */
    public int getNcbiTaxonomyId() {
        return ncbiTaxonomyId;
    }

    /**
     * Sets the NCBI Taxonomy ID.
     *
     * @param ncbiTaxonomyId Taxonomy ID or CPathRecord.TAXONOMY_NOT_SPECIFIED.
     */
    public void setNcbiTaxonomyId(int ncbiTaxonomyId) {
        this.ncbiTaxonomyId = ncbiTaxonomyId;
    }

    /**
     * Gets XML Content Type.
     *
     * @return XmlRecordType Object.
     */
    public XmlRecordType getXmlType() {
        return xmlType;
    }

    /**
     * Sets XML Content Type.
     *
     * @param xmlType XmlRecordType Object.
     */
    public void setXmlType(XmlRecordType xmlType) {
        this.xmlType = xmlType;
    }

    /**
     * Gets XML Content Associated with Record.
     * This is usually a well-formed, but not necessarily valid XML document
     * fragment.
     *
     * @return XML String.
     */
    public String getXmlContent() {
        return xmlContent;
    }

    /**
     * Sets the XML Content Associated with Record.
     * This is usually a well-formed, but not necessarily valid XML document
     * fragment.
     *
     * @param xmlContent XML String.
     */
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    /**
     * Gets Creation TimeStamp.
     *
     * @return Date Created.
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets Creation TimeStamp.
     *
     * @param createTime Date Created.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets Update TimeStamp.
     *
     * @return Date Update.
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets Update TimeStamp.
     *
     * @param updateTime Date Updated.
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Gets the snapshot ID.
     * @return snapshot ID.
     */
    public long getSnapshotId() {
        return snapshotId;
    }

    /**
     * Sets the snapshot ID.
     * @param snapshotId
     */
    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * Determines if record was auto-generated by cPath.
     * @return true or false.
     */
    public boolean isCpathGenerated() {
        return cpathGenerated;
    }

    /**
     * Sets if record was auto-generated by cPath.
     * @param cpathGenerated
     */
    public void setCpathGenerated(boolean cpathGenerated) {
        this.cpathGenerated = cpathGenerated;
    }
}
