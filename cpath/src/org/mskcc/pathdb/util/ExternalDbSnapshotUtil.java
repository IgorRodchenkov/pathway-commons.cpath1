package org.mskcc.pathdb.util;

import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;

import java.util.ArrayList;

/**
 * External Database Snapshot Utility Class.
 *
 * @author Ethan Cerami.
 */
public class ExternalDbSnapshotUtil {

    /**
     * Static Method to Remove PROTEIN_UNIFICATION Databases.
     * @param allSnapshots All Snapshots.
     * @return All Snapshots except PROTEIN_UNIFICATION Databases.
     */
    public static ArrayList<ExternalDatabaseSnapshotRecord>
        removeProteinUnificationSnapshots(ArrayList<ExternalDatabaseSnapshotRecord> allSnapshots) {
        ArrayList<ExternalDatabaseSnapshotRecord> list =
                new ArrayList<ExternalDatabaseSnapshotRecord>();
        for (ExternalDatabaseSnapshotRecord snapshotRecord : allSnapshots) {
            ExternalDatabaseRecord dbRecord = snapshotRecord.getExternalDatabase();
            if (dbRecord != null) {
                ReferenceType referenceType = dbRecord.getDbType();
                if (referenceType.equals(ReferenceType.PROTEIN_UNIFICATION)) {
                    list.add(snapshotRecord);
                }
            }
        }
        return list;
    }
}
