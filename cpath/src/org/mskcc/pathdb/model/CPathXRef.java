package org.mskcc.pathdb.model;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;

/**
 * Stores a Single External Reference.
 * <P>
 * An External Reference consists of a Database:ID Pair.  This is similar
 * to the External Reference Object in the Data Services layer, except that
 * it stores primary Ids to the cPath External Database Table.
 *
 * @author Ethan Cerami
 */
public class CPathXRef {
    private int dbId;
    private String linkedToId;

    /**
     * Constructor.
     *
     * @param dbId       Primary ID of External Database.
     * @param linkedToId Identifier, as stored in the External Database.
     */
    public CPathXRef(int dbId, String linkedToId) {
        this.dbId = dbId;
        this.linkedToId = linkedToId;
    }

    /**
     * Get the Primary ID of the External Database.
     *
     * @return integer id.
     */
    public int getDbId() {
        return dbId;
    }

    /**
     * Sets the Primary ID of the External Database.
     *
     * @param dbId integer id.
     */
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    /**
     * Gets the Identifier, as stored in the External Database.
     *
     * @return String id.
     */
    public String getLinkedToId() {
        return linkedToId;
    }

    /**
     * Sets te Identifier, as stored in the External Database.
     *
     * @param linkedToId String id.
     */
    public void setLinkedToId(String linkedToId) {
        this.linkedToId = linkedToId;
    }

    /**
     * Provides a Text Description of the Xref.
     *
     * @return Description of ID Mapping.
     */
    public String toString() {
        DaoExternalDb dao = new DaoExternalDb();
        String dbName = null;
        try {
            ExternalDatabaseRecord dbRecord1 = dao.getRecordById(dbId);
            dbName = dbRecord1.getName();
        } catch (DaoException e) {
            dbName = "Unknown";
        }
        return new String(dbName + ": " + linkedToId);
    }

    /**
     * Overrides Equals Method.
     *
     * @param obj Object of Interest.
     * @return true or false.
     */
    public boolean equals(Object obj) {
        if (obj instanceof CPathXRef) {
            CPathXRef xref = (CPathXRef) obj;
            if (xref.getDbId() == dbId
                    && xref.getLinkedToId().equals(linkedToId)) {
                return true;
            }
        }
        return false;
    }
}
