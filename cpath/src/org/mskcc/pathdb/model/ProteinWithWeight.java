package org.mskcc.pathdb.model;

import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;

import java.util.Comparator;

public class ProteinWithWeight implements Comparator {
    private int numHits;
    private ProteinInteractorType protein;

    public ProteinWithWeight() {
        protein = null;
        numHits = 0;
    }

    public ProteinWithWeight(ProteinInteractorType protein, int numHits) {
        this.protein = protein;
        this.numHits = numHits;
    }

    public int getNumHits() {
        return numHits;
    }

    public ProteinInteractorType getProtein() {
        return protein;
    }

    public int compare(Object object1, Object object2) {
        ProteinWithWeight protein1 = (ProteinWithWeight) object1;
        ProteinWithWeight protein2 = (ProteinWithWeight) object2;
        //        Integer numHits1 = new Integer(protein1.getNumHits());
        //        Integer numHits2 = new Integer(protein2.getNumHits());
        //        return numHits1.compareTo(numHits2);
        String id1 = protein1.getProtein().getId();
        String id2 = protein2.getProtein().getId();
        return id1.compareTo(id2);
    }
}
