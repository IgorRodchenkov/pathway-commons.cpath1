// $Id: UpdateInteractor.java,v 1.8 2006-02-22 22:47:51 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.sql.transfer;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;

/**
 * Updates Interactor Record with newly obtained data.
 * To illustrate, consider the following scenario:
 * Protein A exists in CPath, and it contains the following two external links:
 * <PRE>
 * 1.  SWISS-PROT:ABC123
 * 2.  PIR:XYZ123
 * </PRE>
 * We now import Protein X, which contains the following two external links:
 * <PRE>
 * 1. SWISS-PROT:ABC123
 * 2. LocusLink:LOCUS123
 * </PRE>
 * When the UpdateInteractor doUpdate() method is called, this class
 * will add the new external link:  LocusLink:LOCUS123 to Protein A.
 * Effectively, this stores all external references defined by the union
 * of protein A and protein X.
 *
 * @author Ethan Cerami
 */
public abstract class UpdateInteractor {
    private static final long NOT_SET = -9999;
    private ExternalReference refsA[];
    private ExternalReference refsB[];
    private long cpathId = NOT_SET;

    /**
     * Sets External References for Existing Interactor.
     *
     * @param refs Array of External Reference objects.
     */
    protected void setExistingExternalRefs(ExternalReference refs[]) {
        this.refsA = refs;
    }

    /**
     * Sets External References for New Interactor.
     *
     * @param refs Array of External Reference objects.
     */
    protected void setNewExternalRefs(ExternalReference refs[]) {
        this.refsB = refs;
    }

    /**
     * Sets the cPath ID for existing interactor.
     *
     * @param id cPath Id.
     */
    protected void setcPathId(long id) {
        this.cpathId = id;
    }

    /**
     * Gets the cPath ID for Interactor A.
     *
     * @return cPath Id.
     */
    public long getcPathId() {
        return this.cpathId;
    }

    /**
     * Determines if Existing Interactor Record needs to be updated with
     * data defined by new interactor.
     *
     * @return true or false.
     */
    public boolean needsUpdating() {
        if (cpathId == NOT_SET) {
            return false;
        } else {
            ArrayList union = new ArrayList();
            for (int i = 0; i < refsA.length; i++) {
                union.add(refsA[i]);
            }
            for (int i = 0; i < refsB.length; i++) {
                boolean contains = union.contains(refsB[i]);
                if (!contains) {
                    union.add(refsB[i]);
                }
            }
            return (union.size() > refsA.length);
        }
    }

    /**
     * Updates Existing Interactor with New External References.
     *
     * @throws DaoException Error Adding new data to database.
     */
    public void doUpdate() throws DaoException {
        if (needsUpdating()) {
            ArrayList aList = new ArrayList();
            ArrayList newList = new ArrayList();

            //  Determine which External References are new.
            for (int i = 0; i < refsA.length; i++) {
                aList.add(refsA[i]);
            }
            for (int i = 0; i < refsB.length; i++) {
                ExternalReference ext = refsB[i];
                if (!aList.contains(ext)) {
                    newList.add(ext);
                }
            }

            //  Create External Links for New References
            ExternalReference newRefs [] =
                    new ExternalReference[newList.size()];
            newRefs = (ExternalReference[]) newList.toArray(newRefs);
            DaoExternalLink linker = DaoExternalLink.getInstance();
            linker.addMulipleRecords(cpathId, newRefs, true);
        }
    }
}
