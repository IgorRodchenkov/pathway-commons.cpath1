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
package org.mskcc.pathdb.task;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.util.ConsoleUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Given a TaxonomyId, this class locates all physical entity records
 * for the specified organism, and calculates how many of these records have
 * Affymetrix identifiers.
 *
 * @author Ethan Cerami.
 */
public class CountAffymetrixIdsTask extends Task {
    private static final String AFFYMETRIX_NAME = "Affymetrix";
    private int taxonomyId;
    private int affyCount = 0;
    private int totalNumRecords;

    /**
     * Constructor.
     *
     * @param taxonomyId NCBI Taxonomy ID.
     * @param consoleMode Console Flag.  Set to true for console tools.
     * @throws DaoException Error Connecting to Database.
     */
    public CountAffymetrixIdsTask(int taxonomyId, boolean consoleMode)
            throws DaoException {
        super("Counting Affymetrix IDs", consoleMode);
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Counting Affymetrix IDs for Organism:  "
                + taxonomyId);
        this.taxonomyId = taxonomyId;
        this.execute();
        pMonitor.setCurrentMessage("\nTotal Number of Records for Organism:  "
                + this.totalNumRecords);
        if (totalNumRecords > 0) {
            double percent = (affyCount / (double) totalNumRecords) * 100.0;
            DecimalFormat formatter = new DecimalFormat("###,###.##");
            String percentOut = formatter.format(percent);
            pMonitor.setCurrentMessage("Of these, " + affyCount
                    + " (" + percentOut
                    + "%) have Affymetrix IDs.");
        }
    }

    /**
     * Gets Total Number of Physical Entities for Organism.
     *
     * @return integer value.
     */
    public int getTotalNumRecords() {
        return this.totalNumRecords;
    }

    /**
     * Gets Number of Physical Entities for Organism that contain an Affymetrix
     * identifier.
     *
     * @return integer value.
     */
    public int getNumRecordsWithAffymetrixIds() {
        return this.affyCount;
    }

    /**
     * Performs LookUp.
     *
     * @throws DaoException Error Connecting to Database.
     */
    private void execute() throws DaoException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        //  First, Look up Affymetrix Database Id.
        int affyDbId = lookUpAffyDBId();

        //  Next, retrieve all Physical Entities from Specified Organism
        DaoCPath dao = new DaoCPath();
        DaoExternalLink externalLinker = new DaoExternalLink();
        ArrayList records = dao.getRecordByTaxonomyID
                (CPathRecordType.PHYSICAL_ENTITY, taxonomyId);

        this.totalNumRecords = records.size();
        pMonitor.setMaxValue(records.size());
        pMonitor.setCurValue(1);

        //  Examine Each Physical Entity
        for (int i = 0; i < records.size(); i++) {
            ConsoleUtil.showProgress(pMonitor);
            CPathRecord record = (CPathRecord) records.get(i);
            ArrayList links =
                    externalLinker.getRecordsByCPathId(record.getId());

            //  Look for Affymetrix Link
            boolean hasOneOrMoreAffyIds = false;
            for (int j = 0; j < links.size(); j++) {
                ExternalLinkRecord link = (ExternalLinkRecord) links.get(j);
                int dbId = link.getExternalDbId();
                if (dbId == affyDbId) {
                    hasOneOrMoreAffyIds = true;
                }
            }
            pMonitor.incrementCurValue();
            if (hasOneOrMoreAffyIds) {
                affyCount++;
            }
        }
    }

    /**
     * Looks up Internal Identifier for Affymetrix External Database.
     *
     * @return Internal Database ID.
     * @throws DaoException Error Connecting to Database.
     */
    private int lookUpAffyDBId() throws DaoException {
        DaoExternalDb externalDb = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord =
                externalDb.getRecordByTerm(AFFYMETRIX_NAME);
        return dbRecord.getId();
    }
}