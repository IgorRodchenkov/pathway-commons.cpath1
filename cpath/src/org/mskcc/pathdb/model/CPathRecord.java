package org.mskcc.pathdb.model;

import java.util.Date;

/**
 * JavaBean to Encapsulate a cPath Record.
 *
 * @author Ethan Cerami
 */
public class CPathRecord {

    /**
     * NCBI Taxonomy ID is not specified.
     */
    public static final int TAXONOMY_NOT_SPECIFIED = -9999;

    private long id;
    private String name;
    private String description;
    private CPathRecordType type;
    private String specType;
    private int ncbiTaxonomyId;
    private String xmlContent;
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
     * @return Primary Key Id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the Primary Key Id.
     * @param id Primary Key Id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the Entity Name.
     * @return Entity Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Entity Name.
     * @param name Entity Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Entity Description.
     * @return Entity Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Entity Description.
     * @param description Entity Description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Record Type.  Constrained to the Enumeration List
     * defined by CPathRecordType.
     * @return CPathRecordType Object.
     */
    public CPathRecordType getType() {
        return type;
    }

    /**
     * Sets the Record Type.  Constrained to the Enumeration List
     * defined by CPathRecordType.
     * @param type CPathRecordType Object.
     */
    public void setType(CPathRecordType type) {
        this.type = type;
    }

    /**
     * Gets the most specific class type in the ontology for this record.
     * @return Specific Class Type String.
     */
    public String getSpecType() {
        return specType;
    }

    /**
     * Sets the most specific class type in the ontology for this record.
     * @param specType Specific Class Type String.
     */
    public void setSpecType(String specType) {
        this.specType = specType;
    }

    /**
     * Gets the NCBI Taxonomy ID.  If taxonomy Id is not specified, this
     * method will return CPathRecord.TAXONOMY_NOT_SPECIFIED.
     * @return Taxonomy ID or CPathRecord.TAXONOMY_NOT_SPECIFIED.
     */
    public int getNcbiTaxonomyId() {
        return ncbiTaxonomyId;
    }

    /**
     * Sets the NCBI Taxonomy ID.
     * @param ncbiTaxonomyId Taxonomy ID or CPathRecord.TAXONOMY_NOT_SPECIFIED.
     */
    public void setNcbiTaxonomyId(int ncbiTaxonomyId) {
        this.ncbiTaxonomyId = ncbiTaxonomyId;
    }

    /**
     * Gets XML Content Associated with Record.
     * This is usually a well-formed, but not necessarily valid XML document
     * fragment.
     * @return XML String.
     */
    public String getXmlContent() {
        return xmlContent;
    }

    /**
     * Sets the XML Content Associated with Record.
     * This is usually a well-formed, but not necessarily valid XML document
     * fragment.
     * @param xmlContent XML String.
     */
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    /**
     * Gets Creation TimeStamp.
     * @return Date Created.
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets Creation TimeStamp.
     * @param createTime Date Created.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets Update TimeStamp.
     * @return Date Update.
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets Update TimeStamp.
     * @param updateTime Date Updated.
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}