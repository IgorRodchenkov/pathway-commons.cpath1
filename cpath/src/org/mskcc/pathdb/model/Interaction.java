package org.mskcc.pathdb.model;

/**
 * Encapsulates a Single Interaction.
 *
 * @author Ethan Cerami
 */
public class Interaction {
    /**
     * Name of Node A.
     */
    private Protein nodeA;

    /**
     * Name of Node B.
     */
    private Protein nodeB;

    /**
     * Experimental System String.
     */
    private String experimentalSystem;

    /**
     * PubMed Identifiers.
     */
    private String[] pubMedIds;

    /**
     * Interaction Direction.
     */
    private String direction;

    /**
     * Interaction owner/source.
     */
    private String owner;

    /**
     * Gets Node A.
     * @return Node A Protein.
     */
    public Protein getNodeA() {
        return nodeA;
    }

    /**
     * Sets Node A.
     * @param nodeA Node A Protein.
     */
    public void setNodeA(Protein nodeA) {
        this.nodeA = nodeA;
    }

    /**
     * Gets Node B.
     * @return Node B Protein.
     */
    public Protein getNodeB() {
        return nodeB;
    }

    /**
     * Sets Node B.
     * @param nodeB Node B.
     */
    public void setNodeB(Protein nodeB) {
        this.nodeB = nodeB;
    }

    /**
     * Gets the Experimental System.
     * @return Experimental System String.
     */
    public String getExperimentalSystem() {
        return experimentalSystem;
    }

    /**
     * Sets the Experimental System.
     * @param experimentalSystem Experimental System String.
     */
    public void setExperimentalSystem(String experimentalSystem) {
        this.experimentalSystem = experimentalSystem;
    }

    /**
     * Gets the associated PubMed ID.
     * @return PubMed ID.
     */
    public String[] getPubMedIds() {
        return pubMedIds;
    }

    /**
     * Sets the associated PubMed ID.
     * @param pubMedIds Pub Med Identifier.
     */
    public void setPubMedIds(String[] pubMedIds) {
        this.pubMedIds = pubMedIds;
    }

    /**
     * Gets the Interaction Direction.
     * @return Interaction Direction.
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets the Interaction Direction.
     * @param direction Interaction Direction.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Gets the Owner / Source.
     * @return Owner / Source String.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the Owner / Source.
     * @param owner Owner / Source String.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
