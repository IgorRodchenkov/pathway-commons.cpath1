package org.mskcc.pathdb.model;

/**
 * Encapsulates a Single External Reference.
 *
 * @author Ethan Cerami
 */
public class ExternalReference {
    /**
     * Database Name.
     */
    private String database;

    /**
     * Database ID.
     */
    private String id;

    /**
     * Constructor.
     * @param db Database name.
     * @param id Database id.
     */
    public ExternalReference (String db, String id) {
        this.database = db;
        this.id = id;
    }

    /**
     * Gets the Database Name.
     * @return Database name.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the Database ID.
     * @return Database ID.
     */
    public String getId() {
        return id;
    }
}
