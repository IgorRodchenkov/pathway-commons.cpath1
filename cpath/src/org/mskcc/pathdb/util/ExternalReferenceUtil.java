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
     * @throws DaoException Error accessing database.
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
