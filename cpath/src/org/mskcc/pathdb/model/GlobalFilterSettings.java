package org.mskcc.pathdb.model;

import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoOrganism;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalFilterSettings {

    /**
     * Session Field for GLOBAL_FILTER_SETTINGS.
     */
    public final static String GLOBAL_FILTER_SETTINGS = "GLOBAL_FILTER_SETTINGS";
	public final static int ALL_ORGANISMS_FILTER_VALUE = Integer.MAX_VALUE;

    private HashSet snapshotSet = new HashSet();
    private HashSet organismSet = new HashSet();

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
        return snapshotSet.contains(new Long(snapshotId));
    }

    public boolean isOrganismSelected (int ncbiTaxonomyId) {
        return organismSet.contains(new Integer(ncbiTaxonomyId));
    }

    public Set getSnapshotIdSet() {
        return snapshotSet;
    }

    public Set getOrganismTaxonomyIdSet() {
        return organismSet;
    }

    public void setSnapshotsSelected (List snapshotIds) {
        snapshotSet = new HashSet();
        if (snapshotIds != null) {
            for (int i=0; i<snapshotIds.size(); i++) {
                snapshotSet.add(snapshotIds.get(i));
            }
        }
    }

    public void setOrganismSelected (List organismTaxonomyIds) {
        organismSet = new HashSet();
        if (organismTaxonomyIds != null) {
            for (int i=0; i<organismTaxonomyIds.size(); i++) {
                organismSet.add(organismTaxonomyIds.get(i));
            }
        }
    }
}
