package org.mskcc.pathdb.model;

/**
 * Stores a Bundle of GO (Gene Onology) Terms.
 * Primarily used as a convenience object for transporting multiple pieces
 * of data within one object.
 *
 * @author Ethan Cerami
 */
public class GoBundle {
    /**
     * Array of GO Function Terms.
     */
    private GoTerm[] goFunctions;

    /**
     * Array of GO Process Terms.
     */
    private GoTerm[] goProcesses;

    /**
     * Array of GO Component Terms.
     */
    private GoTerm[] goComponents;

    /**
     * Array of GO Special Terms.
     */
    private GoTerm[] goSpecial;

    /**
     * Gets all GO Function Terms.
     * @return Array of GoTerm objects.
     */
    public GoTerm[] getGoFunctions() {
        return goFunctions;
    }

    /**
     * Sets all GO Function Terms.
     * @param goFunctions Array of GoTerm objects.
     */
    public void setGoFunctions(GoTerm[] goFunctions) {
        this.goFunctions = goFunctions;
    }

    /**
     * Gets all GO Process Terms.
     * @return Array of GoTerm objects.
     */
    public GoTerm[] getGoProcesses() {
        return goProcesses;
    }

    /**
     * Sets all GO Process Terms.
     * @param goProcesses Array of GoTerm objects.
     */
    public void setGoProcesses(GoTerm[] goProcesses) {
        this.goProcesses = goProcesses;
    }

    /**
     * Gets all GO Component Terms.
     * @return Array of GoTerm objects.
     */
    public GoTerm[] getGoComponents() {
        return goComponents;
    }

    /**
     * Sets all GO Component Terms.
     * @param goComponents Array of GoTerm objects.
     */
    public void setGoComponents(GoTerm[] goComponents) {
        this.goComponents = goComponents;
    }

    /**
     * Gets all GO Special Terms.
     * @return Array of GoTerm objects.
     */
    public GoTerm[] getGoSpecial() {
        return goSpecial;
    }

    /**
     * Sets all GO Special Terms.
     * @param goSpecial Array of GoTerm objects.
     */
    public void setGoSpecial(GoTerm[] goSpecial) {
        this.goSpecial = goSpecial;
    }
}
