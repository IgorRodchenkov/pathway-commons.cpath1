package org.mskcc.pathdb.model;

/**
 * Encapsulate Basic Protein Information.
 *
 * @author Ethan Cerami.
 */
public class Protein {
    /**
     * ORF Name.
     */
    private String orfName;

    /**
     * Local ID.
     * Used internally by data service.
     */
    private String localId;

    /**
     * Description.
     */
    private String description;

    /**
     * Array of Gene names.
     */
    private String[] geneNames;

    /**
     * Array of External References.
     */
    private ExternalReference[] externalRefs;

    /**
     * Bundle of GO Terms.
     */
    private GoBundle goBundle;

    /**
     * Gets the ORF Name.
     * @return ORF Name.
     */
    public String getOrfName() {
        return orfName;
    }

    /**
     * Sets the ORF Name.
     * @param orfName ORF Name.
     */
    public void setOrfName(String orfName) {
        this.orfName = orfName;
    }

    /**
     * Gets the Local ID.
     * @return Local ID.
     */
    public String getLocalId() {
        return localId;
    }

    /**
     * Sets the Local ID.
     * @param localId Local ID.
     */
    public void setLocalId(String localId) {
        this.localId = localId;
    }

    /**
     * Gets the Description.
     * @return Description String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Description.
     * @param description Description String.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Gene Names.
     * @return Array of Gene Name Strings.
     */
    public String[] getGeneNames() {
        return geneNames;
    }

    /**
     * Sets the Gene Names.
     * @param geneNames Array of Gene Name Strings.
     */
    public void setGeneNames(String[] geneNames) {
        this.geneNames = geneNames;
    }

    /**
     * Gets the External References.
     * @return Array of External Reference objects.
     */
    public ExternalReference[] getExternalRefs() {
        return externalRefs;
    }

    /**
     * Sets the External References.
     * @param externalRefs Array of External Reference objects.
     */
    public void setExternalRefs(ExternalReference[] externalRefs) {
        this.externalRefs = externalRefs;
    }

    /**
     * Gets the Go Bundle.
     * @return GoBundle object.
     */
    public GoBundle getGoBundle() {
        return goBundle;
    }

    /**
     * Sets the Go Bundle.
     * @param goBundle GoBundle object.
     */
    public void setGoBundle(GoBundle goBundle) {
        this.goBundle = goBundle;
    }
}
