/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.model;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;

/**
 * Java Bean for Storing ID Map Records.
 *
 * @author Ethan Cerami.
 */
public class IdMapRecord {
    private int db1, db2;
    private String id1, id2;
    private int primaryId;

    /**
     * No-arg Constructor.
     */
    public IdMapRecord() {
    }

    /**
     * Constructor.
     *
     * @param db1 Primary ID of Database 1.
     * @param id1 ID used in Database 1.
     * @param db2 Primary ID of Database 2.
     * @param id2 ID used in Database 2.
     */
    public IdMapRecord(int db1, String id1, int db2, String id2) {
        this.db1 = db1;
        this.db2 = db2;
        this.id1 = id1;
        this.id2 = id2;
    }

    /**
     * Gets Primary ID of Database 1.
     *
     * @return integer id.
     */
    public int getDb1() {
        return db1;
    }

    /**
     * Sets Primary ID of Database 1.
     *
     * @param db1 integer id.
     */
    public void setDb1(int db1) {
        this.db1 = db1;
    }

    /**
     * Gets Primary ID of Database 2.
     *
     * @return integer id.
     */
    public int getDb2() {
        return db2;
    }

    /**
     * Sets Primary ID of Database 2.
     *
     * @param db2 integer id.
     */
    public void setDb2(int db2) {
        this.db2 = db2;
    }

    /**
     * Gets ID in Database 1.
     *
     * @return ID string.
     */
    public String getId1() {
        return id1;
    }

    /**
     * Sets ID in Databse 1.
     *
     * @param id1 ID String.
     */
    public void setId1(String id1) {
        this.id1 = id1;
    }

    /**
     * Gets ID in Database 2.
     *
     * @return ID string.
     */
    public String getId2() {
        return id2;
    }

    /**
     * Sets ID in Databse 1.
     *
     * @param id2 ID String.
     */
    public void setId2(String id2) {
        this.id2 = id2;
    }

    /**
     * Gets Primary ID of this ID Map Record.
     *
     * @return integer id.
     */
    public int getPrimaryId() {
        return primaryId;
    }

    /**
     * Sets Primary ID of this ID Map Record.
     *
     * @param primaryId integer id.
     */
    public void setPrimaryId(int primaryId) {
        this.primaryId = primaryId;
    }

    /**
     * Provides a Text Description of the ID Mapping.
     *
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
        return new String(db1Name + ": " + id1 + " <--> "
                + db2Name + ": " + id2);
    }

    /**
     * Generates an integer hash code for quick lookups.
     * Consider that we have two IdMapRecord Objects:
     * <P>
     * 1:ABC -- 2:XYZ; and 2:XYZ -- 1:ABC
     * <BR>
     * These two records are functionaly equivalent, and therefore
     * result in identical hash codes.
     *
     * @return hashcode integer value.
     */
    public int hashCode() {
        StringBuffer code = new StringBuffer();
        if (db1 < db2) {
            code.append(db1 + ":" + id1 + "#");
            code.append(db2 + ":" + id2);
        } else {
            code.append(db2 + ":" + id2 + "#");
            code.append(db1 + ":" + id1);
        }
        return code.toString().hashCode();
    }
}