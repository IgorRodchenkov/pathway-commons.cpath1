package org.mskcc.pathdb.model;

/**
 * JavaBean to Encapsulate an Organism Record.
 *
 * @author Ethan Cerami
 */
public class Organism {
    private int taxonomyId;
    private String speciesName;
    private String commonName;

    /**
     * Constructor.
     *
     * @param taxonomyId  TaxonomyID.
     * @param speciesName SpeciesName.
     * @param commonName  CommonName.
     */
    public Organism(int taxonomyId, String speciesName,
            String commonName) {
        this.taxonomyId = taxonomyId;
        this.speciesName = speciesName;
        this.commonName = commonName;
    }

    /**
     * Gets NCBI Taxonomy ID.
     *
     * @return Taxonomy Identifier.
     */
    public int getTaxonomyId() {
        return taxonomyId;
    }

    /**
     * Sets NCBI TaxonomyID.
     *
     * @param taxonomyId Taxonomy Identifier.
     */
    public void setTaxonomyId(int taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    /**
     * Gets Species Name.
     *
     * @return Species Name.
     */
    public String getSpeciesName() {
        return speciesName;
    }

    /**
     * Sets Species Name.
     *
     * @param speciesName Species Name.
     */
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    /**
     * Gets Common Name of Organism.
     *
     * @return Common Name.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Sets Common Name of Organism.
     *
     * @param commonName Common Name.
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
}