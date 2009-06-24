package org.mskcc.pathdb.lucene;

import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;

import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Automatically adjusts incoming search query to reflect global filter settings.
 *
 * @author Ethan Cerami.
 */
public class LuceneAutoFilter {

    /**
     * Massages Lucene Query to take into account Global Filter Settings.
     * @param q                 Query.
     * @param filterSettings    Filter Settings.
     * @return                  Modified Query.
     * @throws DaoException     Database Access Error.
     */
    public static String addFiltersToQuery (String q,
            GlobalFilterSettings filterSettings) throws DaoException {

        //  surround user query by parentheses
        q = "(" + q + ")";

		// data source filter
        String revisedQuery = processDataSourceFilter(q, filterSettings);

		// organism filter
		revisedQuery = processOrganismFilter(revisedQuery, filterSettings);

		// entity type
		revisedQuery = processRecordTypeFilter(revisedQuery, filterSettings);

		// outta here
		return revisedQuery;
    }

	/**
	 * Process data source filter
	 *
	 * @param q String
	 * @param filterSettings GlobalFilterSettings
	 *
	 * @return String
	 */
	private static String processDataSourceFilter(String q, GlobalFilterSettings filterSettings)
		throws DaoException {

        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();

		// data source filter
        List dataSourceList = new ArrayList();
        if (filterSettings != null) {
            Set idSet = filterSettings.getSnapshotIdSet();
            ArrayList allNetworkDbSnapshots = dao.getAllNetworkDatabaseSnapshots();
            //  A note regarding the last conditional:  if the user has selected all
            //  databases, there is no need to explicitly filter for each one.
            if (idSet != null && idSet.size() > 0 && idSet.size() != allNetworkDbSnapshots.size()) {
                Iterator iterator = idSet.iterator();
                while (iterator.hasNext()) {
                    Long idLong = (Long) iterator.next();
                    ExternalDatabaseSnapshotRecord record = dao.getDatabaseSnapshot(idLong);
                    dataSourceList.add(record.getExternalDatabase().getMasterTerm());
                }
            }
        }
        return addFiltersToQuery(q, LuceneConfig.FIELD_DATA_SOURCE, dataSourceList);
	}

	/**
	 * Process organism source filter
	 *
	 * @param q String
	 * @param filterSettings GlobalFilterSettings
	 *
	 * @return String
	 */
	private static String processOrganismFilter(String q, GlobalFilterSettings filterSettings) {

        Set<Integer> organismSet = filterSettings.getOrganismTaxonomyIdSet();
        List organismList = new ArrayList();
		boolean allOrganismsFilterSet = false;
        for (Integer id : organismSet) {
			if (id == GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE) {
				allOrganismsFilterSet = true;
				break;
			}
            organismList.add(id.toString());
        }

		// skip organism filter if "All organisms" filter option was choosen
		return (!allOrganismsFilterSet) ?
			addFiltersToQuery (q, LuceneConfig.FIELD_ORGANISM, organismList) : q;
	}

	/**
	 * Process record type filter
	 *
	 * @param q String
	 * @param filterSettings GlobalFilterSettings
	 *
	 * @return String
	 */
	private static String processRecordTypeFilter(String q, GlobalFilterSettings filterSettings) {

		Set<String>  entityTypeSet = filterSettings.getEntityTypeSet();
		List entityTypeList = new ArrayList();
		boolean allEntityTypeFilterSet = false;
		for (String type : entityTypeSet) {
			if (type.equals(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL)) {
				allEntityTypeFilterSet = true;
				break;
			}
			entityTypeList.add(type);
		}

		// skip entity type filter if "All types" filter option was choosen
		return (!allEntityTypeFilterSet) ?
			addFiltersToQuery (q, LuceneConfig.FIELD_RECORD_TYPE, entityTypeList) : q;
	}

    /**
     * Massages Lucene Query to take into account specified data sources.
     * @param q                 Query.
     * @param list    List of Database names.
     * @return Modified Query.
     */
    public static String addFiltersToQuery(String q, String field, List list) {
        StringBuffer revisedQuery = new StringBuffer(q);
        if (list != null && list.size() >0) {
            revisedQuery.append(" AND (");
            for (int i=0; i<list.size(); i++) {
                    String value = (String) list.get(i);
                    revisedQuery.append(field + ":" + "\"" + value + "\"");
                    if (i < list.size() -1) {
                        revisedQuery.append(" OR ");
                    }
            }
            revisedQuery.append(")");
        }
        return revisedQuery.toString();
    }
}
