package org.mskcc.pathdb.model;

import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;

public class IdMapRecord {
    private int db1, db2;
    private String id1, id2;
    private int primaryId;

    public IdMapRecord () {}

    public IdMapRecord (int db1, String id1, int db2, String id2) {
        this.db1 = db1;
        this.db2 = db2;
        this.id1 = id1;
        this.id2 = id2;
    }

    public int getDb1() {
        return db1;
    }

    public void setDb1(int db1) {
        this.db1 = db1;
    }

    public int getDb2() {
        return db2;
    }

    public void setDb2(int db2) {
        this.db2 = db2;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public int getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(int primaryId) {
        this.primaryId = primaryId;
    }

    /**
     * Provides a Text Description of the ID Mapping.
     * @return Description of ID Mapping.
     */
    public String toString() {
        DaoExternalDb dao = new DaoExternalDb();
        String db1Name = null;
        String db2Name = null;
        try {
            ExternalDatabaseRecord dbRecord1 = dao.getRecordById(db1);
            db1Name = dbRecord1.getName();
        } catch (DaoException e) {
            db1Name = "Unknown";
        }
        try {
            ExternalDatabaseRecord dbRecord2 = dao.getRecordById(db2);
            db2Name = dbRecord2.getName();
        } catch (DaoException e) {
            db2Name = "Unknown";
        }
        return new String (db1Name + ": " + id1 + " <--> "
            + db2Name + ": " + id2);
    }
}
