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
     * @param refs Array of External Reference objects.
     */
    protected void setExistingExternalRefs(ExternalReference refs[]) {
        this.refsA = refs;
    }

    /**
     * Sets External References for New Interactor.
     * @param refs Array of External Reference objects.
     */
    protected void setNewExternalRefs(ExternalReference refs[]) {
        this.refsB = refs;
    }

    /**
     * Sets the cPath ID for existing interactor.
     * @param id cPath Id.
     */
    protected void setcPathId(long id) {
        this.cpathId = id;
    }

    /**
     * Gets the cPath ID for Interactor A.
     * @return cPath Id.
     */
    public long getcPathId() {
        return this.cpathId;
    }

    /**
     * Determines if Existing Interactor Record needs to be updated with
     * data defined by new interactor.
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
            DaoExternalLink linker = new DaoExternalLink();
            linker.addMulipleRecords(cpathId, newRefs, true);
        }
    }
}