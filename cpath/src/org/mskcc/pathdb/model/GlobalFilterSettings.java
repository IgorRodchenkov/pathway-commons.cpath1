package org.mskcc.pathdb.model;

import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoOrganism;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the User's Current Search Filter Settings.
 * This object is usually stored in the user's session.
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class GlobalFilterSettings implements Cloneable {

    /**
     * Session Field for GLOBAL_FILTER_SETTINGS.
     */
    public final static String GLOBAL_FILTER_SETTINGS = "GLOBAL_FILTER_SETTINGS";

    /**
     * Flag to indicate that user wants _all_ organisms.
     */
    public final static int ALL_ORGANISMS_FILTER_VALUE = Integer.MAX_VALUE;

    /**
     * Session / Parameter Argument for Entity Type.
     */
    public final static String NARROW_BY_ENTITY_TYPES_FILTER_NAME = "ENTITY_TYPE";

    /**
     * Flag to indicate that users wants _all_ entity types.
     */
    public final static String NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL = "ALL_ENTITY_TYPE";

    /**
     * Session / Parameter Argument for Data Source.
     */
    public final static String NARROW_BY_DATA_SOURCES_FILTER_NAME = "NARROW_BY_DATA_SOURCE";

    /**
     * Flag to indicate that Data Source selections are stored in the Global Filter Settings.
     */
    public final static String NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL = "GLOBAL_FILTER_SETTINGS";

    private HashSet<Long> snapshotSet = new HashSet<Long>();
    private HashSet<Integer> organismSet = new HashSet<Integer>();
	private HashSet<String> entityTypeSet = new HashSet<String>();

    /**
     * Constructor.
     * @throws DaoException Database Error.
     */
    public GlobalFilterSettings() throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllDatabaseSnapshots();
        for (int i=0; i<list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            snapshotSet.add(new Long(snapshotRecord.getId()));
        }
        organismSet.add(ALL_ORGANISMS_FILTER_VALUE);
        entityTypeSet.add(NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL);
    }

    /**
     * Is the specified snapshot ID currently selected by the user.
     * @param snapshotId    snapshot ID.
     * @return true or false.
     */
    public boolean isSnapshotSelected (long snapshotId) {
        return snapshotSet.contains(snapshotId);
    }

    /**
     * Is the specified NCBI Taxonomy ID currently selected by the user.
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     * @return true or false.
     */
    public boolean isOrganismSelected (int ncbiTaxonomyId) {
        return organismSet.contains(ncbiTaxonomyId);
    }

    /**
     * Is the specified Entity Type currently selected by the user.
     * // TODO:  Entity Type should be an enumerated value
     * @param entityType Entity Type.
     * @return true or false.
     */
    public boolean isEntityTypeSelected (String entityType) {
        return entityTypeSet.contains(entityType);
    }

    /**
     * Gets the set of all currrently selected snapshot/database IDs.
     * @return Set of Long Values.
     */
    public Set<Long> getSnapshotIdSet() {
        return snapshotSet;
    }

    /**
     * Gets the set of all currently selected NCBI Taxonomy IDs.
     * @return Set of Integer Values.
     */
    public Set<Integer> getOrganismTaxonomyIdSet() {
        return organismSet;
    }

    /**
     * Gets the set of all currently selected entity types.
     * // TODO:  Update when entity type is an enumerated value.
     * @return Set of String Values.
     */
    public Set<String> getEntityTypeSet() {
        return entityTypeSet;
    }

    /**
     * Store all specified snapshot IDs as selected.
     * @param snapshotIds List of Long values.
     */
    public void setSnapshotsSelected (List <Long> snapshotIds) {
        snapshotSet = new HashSet();
        if (snapshotIds != null) {
            for (int i=0; i<snapshotIds.size(); i++) {
                snapshotSet.add(snapshotIds.get(i));
            }
        }
    }

    /**
     * Stores all specified organism NCBI Taxonomy IDs as selected.
     * @param organismTaxonomyIds List of NCBI Taxonomy IDs.
     */
    public void setOrganismSelected (List <Integer> organismTaxonomyIds) {
        organismSet = new HashSet();
        if (organismTaxonomyIds != null) {
            for (int i=0; i<organismTaxonomyIds.size(); i++) {
                organismSet.add(organismTaxonomyIds.get(i));
            }
        }
    }

    /**
     * Stores all specified entity types as selected.
     * // TODO:  update when entity type is an enumerated value.
     * @param entityTypes List of Entity Types.
     */
    public void setEntityTypeSelected (List<String> entityTypes) {
        entityTypeSet = new HashSet();
        if (entityTypes != null) {
			for (String type : entityTypes) {
                entityTypeSet.add(type);
            }
        }
    }

    /**
     * Clones the GlobalFilterSettings Object.
     * @return Cloned Global Filter Settings Object.
     * @throws CloneNotSupportedException Clone Error.
     */
    public GlobalFilterSettings clone() throws CloneNotSupportedException {
		return (GlobalFilterSettings)super.clone();
	}
}
