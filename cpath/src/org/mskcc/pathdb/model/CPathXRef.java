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
            dbName = dbRecord1.getFixedCvTerm();
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
