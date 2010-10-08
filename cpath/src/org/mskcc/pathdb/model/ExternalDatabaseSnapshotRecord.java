// $Id: ExternalDatabaseSnapshotRecord.java,v 1.3 2010-10-08 16:22:54 grossben Exp $
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

import java.util.Date;
import java.io.Serializable;

/**
 * Encapsulates information regarding a database snapshot record.
 *
 * @author Ethan Cerami.
 */
public class ExternalDatabaseSnapshotRecord implements Serializable {
    private long id;
    private ExternalDatabaseRecord externalDatabase;
    private Date snapshotDate;
    private String snapshotVersion;
	private long numPathways;
	private long numInteractions;
	private long numPhysicalEntities;

    /**
     * Constructor.
     * @param externalDatabase      ExternalDatabaseRecord.
     * @param snapshotDate          Snapshot date.
     * @param snapshotVersion       Snapshot verision #.
	 * @param numPathways
	 * @param numInteractions
	 * @param numPhysicalEntities;
     */
    public ExternalDatabaseSnapshotRecord (ExternalDatabaseRecord externalDatabase, Date snapshotDate, String snapshotVersion,
										   long numPathways, long numInteractions, long numPhysicalEntities) {
        this.externalDatabase = externalDatabase;
        this.snapshotDate = snapshotDate;
        this.snapshotVersion = snapshotVersion;
		this.numPathways = numPathways;
		this.numInteractions = numInteractions;
		this.numPhysicalEntities = numPhysicalEntities;
    }

    /**
     * Gets external database record.
     * @return ExternalDatabaseRecord.
     */
    public ExternalDatabaseRecord getExternalDatabase() {
        return externalDatabase;
    }

    /**
     * Gets snapshot date.
     * @return snapshot date.
     */
    public Date getSnapshotDate() {
        return snapshotDate;
    }

    /**
     * Gets snapshot version.
     * @return snapshot version #.
     */
    public String getSnapshotVersion() {
        return snapshotVersion;
    }

    /**
     * Gets the Primary ID.
     * @return Primary ID.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Sets the Primary ID.
     * @param id Primary ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets number of pathways introduced by this snapshot.
     * @return long
     */
    public long getNumPathways() {
        return this.numPathways;
    }

    /**
     * Sets the number of pathways introduced by this snapshot.
     * @param numPathways long
     */
    public void setNumPathways(long numPathways) {
        this.numPathways = numPathways;
    }

    /**
     * Gets number of interactions introduced by this snapshot.
     * @return long
     */
    public long getNumInteractions() {
        return this.numInteractions;
    }

    /**
     * Sets the number of interactions introduced by this snapshot.
     * @param numInteractions long
     */
    public void setNumInteractions(long numInteractions) {
        this.numInteractions = numInteractions;
    }

    /**
     * Gets number of physical entities introduced by this snapshot.
     * @return long
     */
    public long getNumPhysicalEntities() {
        return this.numPhysicalEntities;
    }

    /**
     * Sets the number of physical entities introduced by this snapshot.
     * @param numPhysicalEntities long
     */
    public void setNumPhysicalEntities(long numPhysicalEntities) {
        this.numPhysicalEntities = numPhysicalEntities;
    }
}
