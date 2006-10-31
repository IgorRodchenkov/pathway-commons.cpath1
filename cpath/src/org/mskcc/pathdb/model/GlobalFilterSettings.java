package org.mskcc.pathdb.model;

import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GlobalFilterSettings {

    /**
     * Session Field for GLOBAL_FILTER_SETTINGS.
     */
    public final static String GLOBAL_FILTER_SETTINGS = "GLOBAL_FILTER_SETTINGS";

    private HashSet snapshotSet = new HashSet();

    public GlobalFilterSettings() throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllDatabaseSnapshots();
        for (int i=0; i<list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            snapshotSet.add(new Long(snapshotRecord.getId()));
        }
    }

    public boolean isSnapshotSelected (long snapshotId) {
        if (snapshotSet.contains(new Long(snapshotId))) {
            return true;
        } else {
            return false;
        }
    }

    public void setSnapshotsSelected (List snapshotIds) {
        snapshotSet = new HashSet();
        if (snapshotIds != null) {
            for (int i=0; i<snapshotIds.size(); i++) {
                snapshotSet.add(snapshotIds.get(i));
            }
        }
    }
}
