// $Id: ExternalDatabaseSnapshotRecord.java,v 1.1 2006-08-25 16:54:07 cerami Exp $
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

/**
 * Encapsulates information regarding a database snapshot record.
 *
 * @author Ethan Cerami.
 */
public class ExternalDatabaseSnapshotRecord {
    private ExternalDatabaseRecord externalDatabase;
    private Date snapshotDate;
    private String snapshotVersion;

    /**
     * Constructor.
     * @param externalDatabase      ExternalDatabaseRecord.
     * @param snapshotDate          Snapshot date.
     * @param snapshotVersion       Snapshot verision #.
     */
    public ExternalDatabaseSnapshotRecord (ExternalDatabaseRecord
            externalDatabase, Date snapshotDate, String snapshotVersion) {
        this.externalDatabase = externalDatabase;
        this.snapshotDate = snapshotDate;
        this.snapshotVersion = snapshotVersion;
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
}
