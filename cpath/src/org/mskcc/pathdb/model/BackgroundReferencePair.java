// $Id: BackgroundReferencePair.java,v 1.6 2006-06-09 19:22:03 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
 * Stores a pair of Background Reference Records.
 *
 * @author Ethan Cerami.
 */
public class BackgroundReferencePair extends BackgroundReference {
    private int dbId2;
    private String linkedToId2;
    private int primaryId;
    private ReferenceType refType;

    /**
     * No-arg Constructor.
     */
    public BackgroundReferencePair() {
    }

    /**
     * Constructor.
     *
     * @param dbId1       External Database ID for Reference #1.
     * @param linkedToId1 LinkedToId for Reference #1.
     * @param dbId2       External Databse ID for Reference #2.
     * @param linkedToId2 LinkedToId for Reference #2.
     * @param refType     Reference Type Object.
     */
    public BackgroundReferencePair(int dbId1, String linkedToId1, int dbId2,
            String linkedToId2, ReferenceType refType) {
        super(dbId1, linkedToId1);
        this.dbId2 = dbId2;
        this.linkedToId2 = linkedToId2;
        this.refType = refType;
    }

    /**
     * Get the External Database ID for Reference #2.
     *
     * @return integer id.
     */
    public int getDbId2() {
        return dbId2;
    }

    /**
     * Sets the External Database ID for Reference #2.
     *
     * @param db2 integer id.
     */
    public void setDbId2(int db2) {
        this.dbId2 = db2;
    }

    /**
     * Gets the LinkedToID for Reference #2.
     *
     * @return ID string.
     */
    public String getLinkedToId2() {
        return linkedToId2;
    }

    /**
     * Sets the LinkedToID for Reference #2.
     *
     * @param linkedToId2 ID String.
     */
    public void setLinkedToId2(String linkedToId2) {
        this.linkedToId2 = linkedToId2;
    }

    /**
     * Gets Primary ID of this Background Reference Pair, as stored in cPath.
     *
     * @return integer id.
     */
    public int getPrimaryId() {
        return primaryId;
    }

    /**
     * Sets Primary ID of this Background Reference Pair.
     *
     * @param primaryId integer id.
     */
    public void setPrimaryId(int primaryId) {
        this.primaryId = primaryId;
    }

    /**
     * Gets the Reference Type.
     *
     * @return ReferenceType Object.
     */
    public ReferenceType getReferenceType() {
        return refType;
    }

    /**
     * Sets the Reference Type.
     *
     * @param refType ReferenceType Object.
     */
    public void setReferenceType(ReferenceType refType) {
        this.refType = refType;
    }

    /**
     * Provides a Text Description of the Record.
     *
     * @return Description of ID Mapping.
     */
    public String toString() {
        DaoExternalDb dao = new DaoExternalDb();
        String db1Name = null;
        String db2Name = null;
        try {
            ExternalDatabaseRecord dbRecord1 = dao.getRecordById(getDbId1());
            db1Name = dbRecord1.getName();
        } catch (DaoException e) {
            db1Name = "Unknown";
        }
        try {
            ExternalDatabaseRecord dbRecord2 = dao.getRecordById(dbId2);
            db2Name = dbRecord2.getName();
        } catch (DaoException e) {
            db2Name = "Unknown";
        }
        String linkGraphic = " <--> ";
        return new String(refType + ", " + db1Name + ": " + getLinkedToId1()
                + linkGraphic + db2Name + ": " + linkedToId2);
    }

    /**
     * Generates an integer hash code for quick lookups.
     * Consider that we have two Background Reference Objects:
     * <p/>
     * 1:ABC <--> 2:XYZ; and 2:XYZ <--> 1:ABC
     * <BR>
     * These two records are functionaly equivalent, and therefore
     * result in identical hash codes.
     *
     * @return hashcode integer value.
     */
    public int hashCode() {
        StringBuffer code = new StringBuffer(refType + ":");
        int dbId1 = getDbId1();
        String linkedToId1 = getLinkedToId1();
        if (dbId1 < dbId2) {
            code.append(dbId1 + ":" + linkedToId1 + "#");
            code.append(dbId2 + ":" + linkedToId2);
        } else {
            code.append(dbId2 + ":" + linkedToId2 + "#");
            code.append(dbId1 + ":" + linkedToId1);
        }
        return code.toString().hashCode();
    }
}
