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

import org.mskcc.pathdb.util.NaturalOrderComparator;


/**
 * JavaBean to Encapsulate an External Link Record.
 *
 * @author Ethan Cerami
 */
public class ExternalLinkRecord implements Comparable {
    private long id;
    private long cpathId;
    private int externalDbId;
    private String linkedToId;
    private ExternalDatabaseRecord db;

    /**
     * Gets External Link ID.
     *
     * @return External Link ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets External Link ID.
     *
     * @param id External Link ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets CPath ID.
     *
     * @return CPath ID.
     */
    public long getCpathId() {
        return cpathId;
    }

    /**
     * Sets CPath ID.
     *
     * @param cpathId CPath ID.
     */
    public void setCpathId(long cpathId) {
        this.cpathId = cpathId;
    }

    /**
     * Gets External Database ID.
     *
     * @return External Database ID.
     */
    public int getExternalDbId() {
        return externalDbId;
    }

    /**
     * Sets External Database ID.
     *
     * @param externalDbId External Database ID.
     */
    public void setExternalDbId(int externalDbId) {
        this.externalDbId = externalDbId;
    }

    /**
     * Gets Linked To ID.
     *
     * @return Linked To ID.
     */
    public String getLinkedToId() {
        return linkedToId;
    }

    /**
     * Sets Linked To ID.
     *
     * @param linkedToId Linked To ID.
     */
    public void setLinkedToId(String linkedToId) {
        this.linkedToId = linkedToId;
    }

    /**
     * Gets the External Database.
     *
     * @return External Database Record.
     */
    public ExternalDatabaseRecord getExternalDatabase() {
        return db;
    }

    /**
     * Sets the External Database.
     *
     * @param db External Database Record.
     */
    public void setExternalDatabase(ExternalDatabaseRecord db) {
        this.db = db;
        if (db != null) {
            this.externalDbId = db.getId();
        }
    }

    /**
     * Gets the Web Link to the specified Resource.
     *
     * @return URL String.
     */
    public String getWebLink() {
        if (db != null) {
            return db.getUrlWithId(this.linkedToId);
        } else {
            return null;
        }
    }

    /**
     * Comparison Operator.
     *
     * @param o Other Object.
     * @return integer value.
     */
    public int compareTo(Object o) {
        if (o instanceof ExternalLinkRecord) {
            ExternalLinkRecord other = (ExternalLinkRecord) o;
            String otherDbName = other.getExternalDatabase().getName();
            if (!otherDbName.equals(db.getName())) {
                return db.getName().compareTo(otherDbName);
            } else {
                String otherId = other.getLinkedToId();
                NaturalOrderComparator comparator =
                        new NaturalOrderComparator();
                return comparator.compare(linkedToId, otherId);
            }
        } else {
            return 0;
        }
    }
}