package org.mskcc.pathdb.model;

/**
 * Encapsulates a single Gene Ontology (GO) Term.
 *
 * @author Ethan Cerami
 */
public class GoTerm {
    /**
     * GO ID.
     */
    private String id;

    /**
     * GO Name.
     */
    private String name;

    /**
     * Constructor.
     * @param id GO ID.
     * @param name GO Name.
     */
    public GoTerm (String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the GO Id.
     * @return GO ID.
     */
    public String getID() {
        return this.id;
    }

    /**
     * Gets the GO Name.
     * @return GO Name.
     */
    public String getName() {
        return this.name;
    }
}
