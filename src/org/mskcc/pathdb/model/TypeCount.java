package org.mskcc.pathdb.model;

/**
 * Summarizes Type with Count.
 *
 * @author Ethan Cerami.
 */
public class TypeCount {
    private String type;
    private int count;
    private String command;

    /**
     * Constructor.
     * @param command Command String.
     */
    public TypeCount(String command) {
        this.command = command;
    }

    /**
     * Gets Command String.
     * @return command string.
     */
    public String getCommand() {
        return this.command;
    }

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
