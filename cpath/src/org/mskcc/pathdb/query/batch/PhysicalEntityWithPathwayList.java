package org.mskcc.pathdb.query.batch;

import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import java.util.ArrayList;

/**
 * Encapsulates a single physical entity and a list of all pathways that this entity
 * participants in.
 *
 * @author Ethan Cerami
 */
public class PhysicalEntityWithPathwayList {
    private BioPaxRecordSummary peSummary;
    private ArrayList<BioPaxRecordSummary> pathwayList;
    private String externalId;
    private String officialName;
    private ExternalDatabaseRecord externalDb;

    /**
     * Gets a BioPaxRecord summary of the physical entity object.
     * @return BioPAXRecord summary of the physical entity object.
     */
    public BioPaxRecordSummary getPeSummary () {
        return peSummary;
    }

    /**
     * Sets a BioPaxRecord summary of the physical entity object.
     * @param peSummary BioPAXRecord summary of the physical entity object.
     */
    public void setPeSummary (BioPaxRecordSummary peSummary) {
        this.peSummary = peSummary;
    }

    /**
     * Gets a list of all pathways that this physical entity participates in.
     * @return ArrayList of pathway BioPaxRecordSummary Objects.
     */
    public ArrayList<BioPaxRecordSummary> getPathwayList () {
        return pathwayList;
    }

    /**
     * Sets a list of all pathways that this physical entity participates in.
     * @param pathwayList ArrayList of pathway BioPaxRecordSummary Objects.
     */
    public void setPathwayList (ArrayList<BioPaxRecordSummary> pathwayList) {
        this.pathwayList = pathwayList;
    }

    /**
     * Gets the external ID, used to initially identify the physical entity.
     * @return External ID, used to initially identify the physical entity.
     */
    public String getExternalId () {
        return externalId;
    }

    /**
     * Sets the external ID, used to initially identify the physical entity.
     * @param externalId External ID, used to initially identify the physical entity.
     */
    public void setExternalId (String externalId) {
        this.externalId = externalId;
    }

    /**
     * Gets the external database record, used to initially identify the physical entity.
     * @return External database record, used to initially identify the physical entity.
     */
    public ExternalDatabaseRecord getExternalDb () {
        return externalDb;
    }

    /**
     * Sets the external database record, used to initially identify the physical entity.
     * @param externalDb External database record, used to initially identify the physical entity.
     */
    public void setExternalDb (ExternalDatabaseRecord externalDb) {
        this.externalDb = externalDb;
    }

    /**
     * Gets the official name of the physical entity.
     * For example, if the physical entity has a HUGO name, this name appears here.
     *
     * @return official name of the physical entity.
     */
    public String getOfficialName () {
        return officialName;
    }

    /**
     * Sets the official name of the physical entity.
     * For example, if the physical entity has a HUGO name, this name appears here.
     * @param officialName official name of the physical entity.
     */
    public void setOfficialName (String officialName) {
        this.officialName = officialName;
    }
}