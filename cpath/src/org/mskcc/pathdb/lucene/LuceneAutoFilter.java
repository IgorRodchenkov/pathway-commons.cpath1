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
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        List dataSourceList = new ArrayList();
        if (filterSettings != null) {
            Set idSet = filterSettings.getSnapshotIdSet();
            if (idSet != null && idSet.size() > 0) {
                Iterator iterator = idSet.iterator();
                while (iterator.hasNext()) {
                    Long idLong = (Long) iterator.next();
                    ExternalDatabaseSnapshotRecord record = dao.getDatabaseSnapshot(idLong);
                    dataSourceList.add(record.getExternalDatabase().getName());
                }
            }
        }
        String revisedQuery = addFiltersToQuery(q, LuceneConfig.FIELD_DATA_SOURCE,
                dataSourceList);
        Set organismSet = filterSettings.getOrganismTaxonomyIdSet();
        List organismList = new ArrayList();
        Iterator iterator = organismSet.iterator();
        while (iterator.hasNext()) {
            Integer id = (Integer) iterator.next();
            organismList.add(id.toString());
        }
        return addFiltersToQuery (revisedQuery, LuceneConfig.FIELD_ORGANISM,
            organismList);
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
                    revisedQuery.append(field + ":" + value);
                    if (i < list.size() -1) {
                        revisedQuery.append(" OR ");
                    }
            }
            revisedQuery.append(")");
        }
        return revisedQuery.toString();
    }
}
