package org.mskcc.pathdb.model;

/**
 * Contains Summary Information for Import to Database.
 *
 * @author Ethan Cerami
 */
public class ImportSummary {
    private int numInteractorsProcessed;
    private int numInteractorsSaved;
    private int numInteractorsFound;
    private int numInteractionsSaved;
    private int numInteractionsClobbered;

    /**
     * Gets Total Number of Interactors Processed.
     *
     * @return integer.
     */
    public int getNumInteractorsProcessed() {
        return numInteractorsProcessed;
    }

    /**
     * Increments Number of Interactors Processed.
     */
    public void incrementNumInteractorsProcessed() {
        this.numInteractorsProcessed++;
    }

    /**
     * Gets Number of Interactors Saved to the cPath database.
     *
     * @return integer.
     */
    public int getNumInteractorsSaved() {
        return numInteractorsSaved;
    }


    /**
     * Increments Number of Interactors Saved to the cPath database.
     */
    public void incrementNumInteractorsSaved() {
        this.numInteractorsSaved++;
    }

    /**
     * Gets Number of Interactors Found in the Database.
     * These interactors were identified by their external references,
     * and will not be saved in the database.
     *
     * @return integer.
     */
    public int getNumInteractorsFound() {
        return numInteractorsFound;
    }

    /**
     * Incremenets the Number of Interactors Found in the Database.
     */
    public void incrementNumInteractorsFound() {
        this.numInteractorsFound++;
    }

    /**
     * Gets Number of Interactions Saved to the cPath Database.
     *
     * @return integer.
     */
    public int getNumInteractionsSaved() {
        return this.numInteractionsSaved;
    }

    /**
     * Increments Number of Interactions Saved to the Database.
     */
    public void incrementNumInteractionsSaved() {
        this.numInteractionsSaved++;
    }

    /**
     * Gets Number of Interactions Saved to the cPath Database.
     *
     * @return integer.
     */
    public int getNumInteractionsClobbered() {
        return this.numInteractionsClobbered;
    }

    /**
     * Increments Number of New Interactions which clobbered old interactions.
     */
    public void incrementNumInteractionsClobbered() {
        this.numInteractionsClobbered++;
    }


}