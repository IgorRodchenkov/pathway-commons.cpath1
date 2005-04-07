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

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.DbReferenceType;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.dataservices.schemas.psi.XrefType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.util.tool.ConsoleUtil;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    private HashMap dbMap;
    private int numProteinsWithoutXrefs;
    private ArrayList proteinsWithOutRefs = new ArrayList();

    /**
     * Constructor.
     *
     * @param taxonomyId  NCBI Taxonomy ID.
     * @param consoleMode Console Flag.  Set to true for console tools.
     * @throws DaoException Error Connecting to Database.
     */
    public CountAffymetrixIdsTask(int taxonomyId, boolean consoleMode)
            throws DaoException {
        super("Counting Affymetrix IDs", consoleMode);
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Counting Affymetrix IDs for Organism  "
                + " -->  NCBI Taxonomy ID:  " + taxonomyId);
        this.taxonomyId = taxonomyId;
        this.execute();
        pMonitor.setCurrentMessage("\nTotal Number of Records "
                + "for NCBI Taxonomy ID " + taxonomyId + ":  "
                + this.totalNumRecords);

        if (totalNumRecords > 0) {
            double percent = (affyCount / (double) totalNumRecords) * 100.0;
            DecimalFormat formatter = new DecimalFormat("###,###.##");
            String percentOut = formatter.format(percent);
            pMonitor.setCurrentMessage("Of these, " + affyCount
                    + " (" + percentOut
                    + "%) have Affymetrix IDs.");
        }

        pMonitor.setCurrentMessage("\nOf those proteins without Affymetrix "
                + "IDs, the following databases were found:  ");
        Iterator keys = dbMap.keySet().iterator();
        while (keys.hasNext()) {
            String dbName = (String) keys.next();
            Integer counter = (Integer) dbMap.get(dbName);
            if (consoleMode) {
                System.out.println(dbName + ":  " + counter);
            }
        }
        pMonitor.setCurrentMessage("\nTotal Number of Proteins that have no "
                + "external database identifiers:  " + numProteinsWithoutXrefs);
        pMonitor.setCurrentMessage("\nThe following proteins have no "
                + "external database identifiers:  ");
        for (int i = 0; i < proteinsWithOutRefs.size(); i++) {
            ProteinInteractorType protein =
                    (ProteinInteractorType) proteinsWithOutRefs.get(i);
            pMonitor.setCurrentMessage(protein.getNames().getShortLabel());
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
        dbMap = new HashMap();
        ProgressMonitor pMonitor = this.getProgressMonitor();

        //  Retrieve all Physical Entities for Specified Organism
        DaoCPath dao = new DaoCPath();
        ArrayList records = dao.getRecordByTaxonomyID
                (CPathRecordType.PHYSICAL_ENTITY, taxonomyId);

        this.totalNumRecords = records.size();
        pMonitor.setMaxValue(records.size());
        pMonitor.setCurValue(1);

        //  Examine Each Physical Entity
        for (int i = 0; i < records.size(); i++) {
            ConsoleUtil.showProgress(pMonitor);
            CPathRecord record = (CPathRecord) records.get(i);
            String xmlContent = record.getXmlContent();
            if (xmlContent.toLowerCase().indexOf("affymetrix") > -1) {
                affyCount++;
            } else {
                trackOtherIds(xmlContent, dbMap);
            }
            pMonitor.incrementCurValue();
        }
    }

    private void trackOtherIds(String xmlContent, HashMap dbMap)
            throws DaoException {
        StringReader reader = new StringReader(xmlContent);
        try {
            ProteinInteractorType protein =
                    ProteinInteractorType.unmarshalProteinInteractorType
                    (reader);
            XrefType xref = protein.getXref();
            if (xref != null) {
                DbReferenceType primaryRef = xref.getPrimaryRef();
                if (primaryRef != null) {
                    incrementMapCounter(primaryRef, dbMap);
                    for (int i = 0; i < xref.getSecondaryRefCount(); i++) {
                        DbReferenceType secondaryRef = xref.getSecondaryRef(i);
                        incrementMapCounter(secondaryRef, dbMap);
                    }
                }
                if (primaryRef == null && xref.getSecondaryRefCount() == 0) {
                    recordEmptyProtein(protein);
                    numProteinsWithoutXrefs++;
                }
            } else {
                recordEmptyProtein(protein);
                numProteinsWithoutXrefs++;
            }
        } catch (ValidationException e) {
            System.err.println("Failed while processing XML:  " + xmlContent);
            throw new DaoException(e);
        } catch (MarshalException e) {
            System.err.println("Failed while processing XML:  " + xmlContent);
            throw new DaoException(e);
        }
    }

    private void recordEmptyProtein(ProteinInteractorType protein) {
        proteinsWithOutRefs.add(protein);
    }

    private void incrementMapCounter(DbReferenceType dbRef, HashMap dbMap) {
        String db = dbRef.getDb();
        if (db.equals("uniprot")) {
            System.out.println("uniprot: " + dbRef.getId());
        }
        if (dbMap.containsKey(db)) {
            Integer counter = (Integer) dbMap.get(db);
            counter = new Integer(counter.intValue() + 1);
            dbMap.put(db, counter);
        } else {
            dbMap.put(db, new Integer(1));
        }
    }
}