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
package org.mskcc.pathdb.sql.references;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.BackgroundReference;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.dao.DaoBackgroundReferences;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * cPath Background Reference Service.
 * <P>
 * The cPath Background Reference Service is a distinct subsystem of cPath
 * responsible for storing external references for biological entities.
 * <P>
 * There are two main types of external references:
 * <UL>
 * <LI>Unification:  A unification reference uniquely identifies a biological
 * entity.  It can be used to unify multiple entity records into a single
 * record.  For example, UnitProt:P10275 and PIR:A39248 uniquely
 * identify the Androgen Receptor protein in human.  Using unification
 * references, cPath will create a single entity reference containing
 * both references.
 * <LI>LinkOut:  A linkout reference provides a linkout from one biological
 * entity to another, or from one biological entity to an external annotation.
 * For example, UniProt:P10275 may contain a reference to the Ensembl Gene:
 * ENSG00000169083, and may also contain multiple references to Gene
 * Ontology (GO), such as: GO:0005497:androgen binding, and GO:0004882:androgen
 * receptor activity.
 * </UL>
 *
 * @author Ethan Cerami.
 */
public class BackgroundReferenceService {

    /**
     * Queries the background reference subsystem for a complete list of
     * unification references.
     *
     * @param refs Array of External Reference Objects.
     * @return ArrayList of all equivalent External Reference Objects.
     *         This represents the union of External References derived from
     *         the parameter list plus all newly discovered External Reference
     *         as derived from the Bacgkround Reference service.
     *         External Reference objects are normalized to the cPath Database
     *         fixed controlled vocabularly term.
     * @throws DaoException Error Accessing Database.
     */
    public ArrayList getUnificationReferences(ExternalReference[] refs)
            throws DaoException {
        //  Create Normalized Set of Initial XRefs.
        HashSet initialSet = createNormalizedXRefSet(refs);

        //  Iterate through all existing External References
        for (int i = 0; i < refs.length; i++) {

            //  Finds a Complete List of Equivalent External References
            ArrayList list = getUnificationReferences(refs[i]);

            //  Add Each Equivalent Reference to the Non-Redundant Set
            for (int j = 0; j < list.size(); j++) {
                ExternalReference ref = (ExternalReference) list.get(j);
                if (!initialSet.contains(ref)) {
                    initialSet.add(ref);
                }
            }
        }
        return new ArrayList(initialSet);
    }

    /**
     * Queries the background reference subsystem for a list of
     * unification references.
     *
     * @param xref External Reference Object.
     * @return ArrayList of all equivalent External Reference Objects.
     *         This represents the union of the single External Reference
     *         derived from the parameter list plus all newly discovered
     *         External Reference as derived from the Bacgkround Reference
     *         service.  External Reference objects are normalized to the
     *         cPath Database fixed controlled vocabularly term.
     * @throws DaoException Error Accessing Database.
     */
    public ArrayList getUnificationReferences(ExternalReference xref)
            throws DaoException {
        //  Implementation Note:  both dao.getRecordByTerm() and
        //  dao.getRecordById() use an internal cache, and will therefore
        //  be very fast.

        //  Create a Non-Redundant Set of External References
        HashSet unionSet = new HashSet();
        unionSet.add(xref);

        //  Look up Primary ID of Database, as stored in cPath.
        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord = dao.getRecordByTerm
                (xref.getDatabase());
        if (dbRecord == null) {
            throw new IllegalArgumentException("External Database: "
                    + xref.getDatabase() + " does not exist in database.");
        }

        //  Normalize database name;  this must be done to ensure that
        //  equals() method works below
        xref.setDatabase(dbRecord.getFixedCvTerm());

        //  Look up Equivalence List
        BackgroundReference cpathXRef = new BackgroundReference
                (dbRecord.getId(), xref.getId());
        DaoBackgroundReferences daoId = new DaoBackgroundReferences();
        ArrayList backgroundRefList = daoId.getEquivalenceList(cpathXRef);

        transformToExternalReferenceList(unionSet, backgroundRefList);
        return new ArrayList(unionSet);
    }

    /**
     * Queries the Background Reference Service for a List of Link Out
     * References.
     *
     * @param refs Array of External Reference Objects
     * @return ArrayList of External Reference Objects.
     * @throws DaoException Error accessing database.
     */
    public ArrayList getLinkOutReferences(ExternalReference refs[])
            throws DaoException {
        HashSet set = new HashSet();
        for (int i = 0; i < refs.length; i++) {
            ArrayList linkOuts = getLinkOutReferences(refs[i]);
            set.addAll(linkOuts);
        }
        return new ArrayList(set);
    }

    /**
     * Queries the Background Reference Service for a List of Link Out
     * References.
     *
     * @param ref External Reference Object.
     * @return ArrayList of External Reference Objects.
     * @throws DaoException Error accessing database.
     */
    public ArrayList getLinkOutReferences(ExternalReference ref)
            throws DaoException {
        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord = dao.getRecordByTerm
                (ref.getDatabase());
        if (dbRecord == null) {
            throw new IllegalArgumentException("External Database: "
                    + ref.getDatabase() + " does not exist in database.");
        }
        BackgroundReference backgroundRef = new BackgroundReference
                (dbRecord.getId(), ref.getId());
        DaoBackgroundReferences dao2 = new DaoBackgroundReferences();
        ArrayList backgroundRefList = dao2.getLinkOutList(backgroundRef);
        HashSet set = new HashSet();
        transformToExternalReferenceList(set, backgroundRefList);
        return new ArrayList(set);
    }

    /**
     * Transform BackgroundReference Objects to External Reference Object.
     */
    private void transformToExternalReferenceList(HashSet unionSet,
            ArrayList backgroundRefList) throws DaoException {
        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord;
        //  Transform all Matches into External Reference Objects.
        for (int i = 0; i < backgroundRefList.size(); i++) {
            BackgroundReference cPathMatch = (BackgroundReference)
                    backgroundRefList.get(i);

            //  Look up Database Name
            dbRecord = dao.getRecordById(cPathMatch.getDbId1());

            //  Create New External Reference Object.
            ExternalReference match = new ExternalReference
                    (dbRecord.getFixedCvTerm(), cPathMatch.getLinkedToId1());
            unionSet.add(match);
        }
    }

    /**
     * Creates a Non-Redundant Set of Normalized External Reference Objects.
     * Normalization sets the Database Name to the Fixed Controlled Vocabulary
     * Term, as defined by cPath.
     * <P>
     * Implementation Note:  dao.getRecordByTerm() uses an internal cache.
     * In most cases, this method will therefore be extremely fast.
     */
    private HashSet createNormalizedXRefSet(ExternalReference[] xrefs)
            throws DaoException {
        HashSet set = new HashSet();
        for (int i = 0; i < xrefs.length; i++) {
            DaoExternalDb dao = new DaoExternalDb();
            ExternalDatabaseRecord dbRecord = dao.getRecordByTerm
                    (xrefs[i].getDatabase());
            if (dbRecord == null) {
                throw new IllegalArgumentException("External Database: "
                        + xrefs[i].getDatabase()
                        + " does not exist in database.");
            }

            //  Normalize database name to fixed CV Term.
            xrefs[i].setDatabase(dbRecord.getFixedCvTerm());
            set.add(xrefs[i]);
        }
        return set;
    }
}