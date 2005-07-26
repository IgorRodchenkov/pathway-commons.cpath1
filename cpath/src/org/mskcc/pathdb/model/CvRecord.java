package org.mskcc.pathdb.model;

import java.util.ArrayList;

/**
 * JavaBean to Encapsulate a set of Controlled Vocabulary Terms.
 *
 * Controlled Vocabulary terms are primarily used to identify external
 * databases.  For example, the terms:  SWISS-PROT, SWP and UNIPROT are all
 * controlled terms which map to the same SWISS-PROT external database.  Upon
 * import, we map individual terms to an external database, and then normalize
 * the term to the master term.  For example, if an import file contains
 * the terms:  SWISS-PROT or SWP, we map these to the SWISS-PROT database,
 * but normalize all the terms to UNIPROT.
 *
 * @author Ethan Cerami
 */
public class CvRecord {
    private String masterTerm;
    private ArrayList synonymTerms;

    /**
     * Gets the master controlled vocabulary term.
     * @return term String.
     */
    public String getMasterTerm() {
        return masterTerm;
    }

    /**
     * Sets the master controlled vocabulary term.
     * @param masterTerm term String.
     */
    public void setMasterTerm(String masterTerm) {
        this.masterTerm = masterTerm;
    }

    /**
     * Gets the List of Synonym Terms.
     * @return ArrayList of String terms.
     */
    public ArrayList getSynonymTerms() {
        return synonymTerms;
    }

    /**
     * Sets the List of Synonmy Terms.
     * @param synonymTerms ArrayList of String terms.
     */
    public void setSynonymTerms(ArrayList synonymTerms) {
        this.synonymTerms = synonymTerms;
    }
}
