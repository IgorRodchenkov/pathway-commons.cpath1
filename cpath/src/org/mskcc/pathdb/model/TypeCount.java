package org.mskcc.pathdb.model;

/**
 * Summarizes Type with Count.
 *
 * @author Ethan Cerami.
 */
public class TypeCount {
    private String type;
    private int count;

    /**
     * Gets the type name.
     * @return type name.
     */
    public String getType () {
        return type;
    }

    /**
     * Sets the type name.
     * @param type type name.
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * Gets the count.
     * @return count.
     */
    public int getCount () {
        return count;
    }

    /**
     * Sets the count.
     * @param count count.
     */
    public void setCount (int count) {
        this.count = count;
    }
}
