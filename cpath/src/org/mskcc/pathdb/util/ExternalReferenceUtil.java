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
     * Utility method for creating a union of two lists of External References.
     *
     * @param refs1 Array of External Reference Objects.
     * @param refs2 Array of External Reference Objects.
     * @return union of refs1 and ref2.
     */
    public static ExternalReference[] createUnifiedList(ExternalReference[]
            refs1, ExternalReference[] refs2) {
        ExternalReference union[] = new ExternalReference[refs1.length
                + refs2.length];
        for (int i = 0; i < refs1.length; i++) {
            union[i] = refs1[i];
        }
        for (int i = 0; i < refs2.length; i++) {
            union[refs1.length + i] = refs2[i];
        }
        return union;
    }

    /**
     * Filters External Reference List by Reference Type.
     *
     * @param refs    Array of External Reference Objects.
     * @param refType Reference Type to filter for.
     * @return Array of External Reference Objects of type refType.
     * @throws DaoException Error Accessing Database.
     */
    public static ExternalReference[] filterByReferenceType
            (ExternalReference[] refs, ReferenceType refType)
            throws DaoException {
        DaoExternalDb dao = new DaoExternalDb();
        ArrayList filteredRefList = new ArrayList();
        if (refs == null) {
            return null;
        }
        for (int i = 0; i < refs.length; i++) {
            ExternalReference ref = refs[i];
            String dbName = ref.getDatabase();
            ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbName);
            if (dbRecord.getDbType().equals(refType)) {
                filteredRefList.add(ref);
            }
        }
        ExternalReference filteredRefs[] =
                new ExternalReference[filteredRefList.size()];
        return (ExternalReference[]) filteredRefList.toArray(filteredRefs);
    }

    /**
     * Removes Duplicates from List.
     *
     * @param refs Array of External Reference Objects.
     * @return Array of External Reference Objects.
     */
    public static ExternalReference[] removeDuplicates(ExternalReference
            refs[]) {
        HashSet set = new HashSet();
        for (int i = 0; i < refs.length; i++) {
            set.add(refs[i]);
        }
        return (ExternalReference[]) set.toArray
                (new ExternalReference[set.size()]);
    }
}