package org.mskcc.pathdb.model;

import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.lucene.OrganismStats;

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
     * Flag to indicate that users wants _all_ record types.
     */
    public final static String NARROW_BY_RECORD_TYPES_ALL = "ALL";

    /**
     * Flag to indicate that user wants only pathway records.
     */
    public final static String NARROW_BY_RECORD_TYPES_PATHWAYS = "PATHWAY";

    /**
     * Flag to indicate that user wants only physical entity records.
     */
    public final static String NARROW_BY_RECORD_TYPES_PHYSICAL_ENTITIES = "PHYSICAL_ENTITY";    

    /**
     * Session / Parameter Argument for Data Source.
     */
    public final static String NARROW_BY_DATA_SOURCES_FILTER_NAME = "snapshot_id";

    /**
     * Flag to indicate that Data Source selections are stored in the Global Filter Settings.
     */
    public final static String NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL = "GLOBAL_FILTER_SETTINGS";

    private HashSet<Long> snapshotSet = new HashSet<Long>();
    private HashSet<Integer> organismSet = new HashSet<Integer>();
	private HashSet<String> recordTypeSet = new HashSet<String>();

    /**
     * Constructor.
     * @throws DaoException Database Error.
     */
    public GlobalFilterSettings() throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllNetworkDatabaseSnapshots();
        for (int i=0; i<list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            snapshotSet.add(new Long(snapshotRecord.getId()));
        }
        organismSet.add(ALL_ORGANISMS_FILTER_VALUE);
        recordTypeSet.add(NARROW_BY_RECORD_TYPES_ALL);
    }

    /**
     * Resets the Filter to include all database snapshots, not just
     * snapshots from pathway / network databases.
     * Primarily used for JUnit tests.
     * 
     * @throws DaoException Database Error.
     */
    public void resetToIncludeAllDatabaseSnaphosts () throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllDatabaseSnapshots();
        for (int i=0; i<list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            snapshotSet.add(new Long(snapshotRecord.getId()));
        }
        organismSet.add(ALL_ORGANISMS_FILTER_VALUE);
        recordTypeSet.add(NARROW_BY_RECORD_TYPES_ALL);
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
     * Is the specified Record Type currently selected by the user.
     * @param recordType Record Type.
     * @return true or false.
     */
    public boolean isRecordTypeSelected(String recordType) {
        return recordTypeSet.contains(recordType);
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
     * Gets the set of all currently selected record types.
     * @return Set of String Values.
     */
    public Set<String> getRecordTypeSet() {
        return recordTypeSet;
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
     * Stores all specified record types as selected.
     * @param recordTypes List of Record Types.
     */
    public void setRecordTypeSelected(List<String> recordTypes) {
        recordTypeSet = new HashSet();
        if (recordTypes != null) {
			for (String type : recordTypes) {
                recordTypeSet.add(type);
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

    /**
     * Gets the Filter Summary.
     * @return Filter Summary.
     */
    public String getFilterSummary() {
        ArrayList <String> organismsSelected = new ArrayList <String>();
        StringBuffer summary = new StringBuffer();

        try {
            OrganismStats orgStats = OrganismStats.getInstance();
            List<Organism> allOrganismsList = orgStats.getListSortedByName();

            //  Process Organisms
            for (Organism organism : allOrganismsList) {
                if (isOrganismSelected(organism.getTaxonomyId())) {
                    organismsSelected.add(organism.getSpeciesName());
                }
            }
        } catch (QueryException e) {
        } catch (DaoException e) {
        }

        //  Then, do different things, depending on All, 1, or >1 Organisms Selected.
        if (organismsSelected.size() == 0) {
            summary.append("All Organisms");
        } else if (organismsSelected.size() == 1) {
            summary.append(organismsSelected.get(0));
        } else {
            createToolTip("Organisms Selected:" , "Organisms", summary, organismsSelected);
        }

        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = null;
        try {
            list = dao.getAllNetworkDatabaseSnapshots();
        } catch (DaoException e) {
        }

        //  Process Data Sources
        //  First, figure out which data sources are selected.
        ArrayList <String> dataSourcesSelected = new ArrayList <String>();
        for (int i = 0; i < list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            if (isSnapshotSelected(snapshotRecord.getId())) {
                dataSourcesSelected.add(snapshotRecord.getExternalDatabase().getName());
            }
        }

        //  Then, do different things, depending on All, 1, or >1 Data Sources Selected.
        summary.append (", ");
        if (dataSourcesSelected.size() == list.size()) {
            summary.append ("All Data Sources");
        } else if (dataSourcesSelected.size() == 1) {
                summary.append (dataSourcesSelected.get(0));
        } else {
            createToolTip("Data Sources Selected:" , "Data Sources", summary, dataSourcesSelected);
        }
        return summary.toString();
    }

    /**
     * Creates a Tool Tip Using the Overlib Javascript Library.
     */
    private void createToolTip(String title, String linkName, StringBuffer summary,
            ArrayList<String> list) {
        summary.append ("<span class='filter_details'><a href=\"#\" " +
                "onmouseover=\"return overlib('<DIV CLASS=popup>" +
                "<DIV CLASS=popup_caption>" + title + "</DIV><DIV CLASS=popup_text>" +
                "<UL>");
        for (String item: list) {
            summary.append ("<LI>" + item + "</LI>");
        }
        summary.append ("</UL></DIV></DIV>', FULLHTML, WRAP, CELLPAD, 5, OFFSETY, 0); " +
                "return true;\" onmouseout=\"return nd();\"> "
                + list.size() + " " + linkName + "</a></span>");
    }
}
