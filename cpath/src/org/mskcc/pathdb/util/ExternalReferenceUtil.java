package org.mskcc.pathdb.util;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;

import java.util.ArrayList;

/**
 * Misc Utilities related to External References.
 *
 * @author Ethan Cerami
 */
public class ExternalReferenceUtil {

    /**
     * Filters out non-identity references.  We don't want to use non-identity
     * references, e.g. GO or PubMed to determine protein identity.
     *
     * @param refs Array of External References.
     * @return Filtered Array of External References.
     */
    public static ExternalReference[] filterOutNonIdReferences
            (ExternalReference[] refs) throws DaoException {
        DaoExternalDb dao = new DaoExternalDb();
        ArrayList filteredRefList = new ArrayList();
        if (refs == null) {
            return null;
        }
        for (int i = 0; i < refs.length; i++) {
            ExternalReference ref = refs[i];
            String dbName = ref.getDatabase();
            ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbName);
            if (dbRecord.getDbType().equals(ReferenceType.IDENTITY)) {
                filteredRefList.add(ref);
            }
        }
        ExternalReference filteredRefs[] =
                new ExternalReference[filteredRefList.size()];
        return (ExternalReference[]) filteredRefList.toArray(filteredRefs);
    }
}
