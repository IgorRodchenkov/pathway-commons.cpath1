package org.mskcc.pathdb.model;

import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;

import java.util.Comparator;

/**
 * Encapsulates a Protein With a Score/Weight.
 * Used to sort a list of proteins.
 *
 * @author Ethan Cerami.
 */
public class ProteinWithWeight implements Comparator {
    private int numHits;
    private ProteinInteractorType protein;

    /**
     * Constructor.
     */
    public ProteinWithWeight() {
        protein = null;
        numHits = 0;
    }

    /**
     * Constructor.
     *
     * @param protein Protein Object.
     * @param numHits Number of Hits.
     */
    public ProteinWithWeight(ProteinInteractorType protein, int numHits) {
        this.protein = protein;
        this.numHits = numHits;
    }

    /**
     * Gets Number of Hits.
     *
     * @return Number of Hits.
     */
    public int getNumHits() {
        return numHits;
    }

    /**
     * Gets Protein Object.
     *
     * @return Protein Object.
     */
    public ProteinInteractorType getProtein() {
        return protein;
    }

    /**
     * Compares/orders two proteins.
     *
     * @param object1 Protein1
     * @param object2 Protein2
     * @return int value indicating sort order.
     */
    public int compare(Object object1, Object object2) {
        ProteinWithWeight protein1 = (ProteinWithWeight) object1;
        ProteinWithWeight protein2 = (ProteinWithWeight) object2;
        String id1 = protein1.getProtein().getId();
        String id2 = protein2.getProtein().getId();
        return id1.compareTo(id2);
    }
}
