package org.mskcc.pathdb.util;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Misc Utilities related to External References.
 *
 * @author Ethan Cerami
 */
public class ExternalReferenceUtil {

    /**
     * Extracts only thoses references which can be used for Protein
     * Unification.  Automatically filters out all LINK_OUT references.
     *
     * @param refs Array of External References.
     * @return Filtered Array of External References.
     */
    public static ExternalReference[] extractProteinUnificationRefs
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
            if (dbRecord.getDbType().equals
                    (ReferenceType.PROTEIN_UNIFICATION)) {
                filteredRefList.add(ref);
            }
        }
        ExternalReference filteredRefs[] =
                new ExternalReference[filteredRefList.size()];
        return (ExternalReference[]) filteredRefList.toArray(filteredRefs);
    }

    /**
     * Utility method for creating a union of two lists of External References.
     *
     * @param refList ArrayList of External Reference Object.
     * @param refs    Array of External Objects.
     * @return ArrayList of ExternalReference Objects.
     */
    public static ArrayList createUnifiedList(ArrayList refList,
            ExternalReference[] refs) {
        HashSet union = new HashSet();
        union.addAll(refList);
        for (int i = 0; i < refs.length; i++) {
            union.add(refs[i]);
        }
        ArrayList list = new ArrayList(union);
        return list;
    }
}
