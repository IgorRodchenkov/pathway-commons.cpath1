package org.mskcc.pathdb.sql.transfer;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathXRef;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoIdMap;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * cPath ID Mapping Service.
 * <P>
 * The cPath ID Mapping Service is a distinct subsystem of cPath responsible
 * for storing external references for uniquely identifying biological entities.
 * <P>
 * For example, the ID Mapping Service may store multiple external references
 * for identifying a single protein, e.g. Affymetrix ID, LocusLink ID, etc.
 * Upon data import, cPath will query the ID subsystem for any matching
 * identifiers, and transfer them to the core cPath database.
 * <P>
 * Here is a concrete example:  user imports a PSI-MI file which contains
 * the Q6PK17_HUMAN protein.  This protein defines a single external reference
 * pointing to SWISSPROT:AAH08943.  Upon import, cPath queries the ID mapping
 * subsystem for all equivalent identifiers, and finds the following three
 * matches:  Affymetrix:1552275_3p_s_at, UniGene:Hs.77646, and
 * RefSeq:NP_060241.  Based on this data, cPath updates the PSI-MI protein
 * record to include a total of four external references, and the new
 * identifiers are permanently linked to the protein.  An end-user searching
 * for 1552275_3p_s_at will therefore find a link to Q6PK17_HUMAN, even though
 * the original PSI-MI file did not contain this information.
 *
 * @author Ethan Cerami.
 */
public class IdMappingService {

    /**
     * Queries the ID Mapping Subsystem for a list of equivalent external
     * references.
     *
     * @param refs Array of External Reference Objects.
     * @return ArrayList of All Equivalent External Reference Objects.
     *         External Reference objects are normalized to the
     *         cPath Database fixed controlled vocabularly term.
     * @throws DaoException Error Accessing Database.
     */
    public ArrayList getEquivalenceList(ExternalReference[] refs)
            throws DaoException {
        //  Create Normalized Set of Initial XRefs.
        //  We want to return a list of external references which is
        //  distinct from the parameter list.  The only way to ensure this
        //  is to normalize the incoming list to use fixed controlled
        //  vocabularly terms.
        HashSet initialSet = createNormalizedXRefSet(refs);

        //  Create a Non-Redundant Set of External References
        HashSet hitList = new HashSet();

        //  Iterate through all existing External References
        for (int i = 0; i < refs.length; i++) {

            //  Finds a Complete List of Equivalent External References
            ArrayList list = getEquivalenceList(refs[i]);

            //  Add Each Equivalent Reference to the Non-Redundant Set
            for (int j = 0; j < list.size(); j++) {
                ExternalReference ref = (ExternalReference) list.get(j);
                if (!initialSet.contains(ref)) {
                    hitList.add(ref);
                }
            }
        }
        return new ArrayList(hitList);
    }

    /**
     * Queries the ID Mapping Subsystem for a list of equivalent external
     * references.
     *
     * @param xref External Reference Object.
     * @return ArrayList of All Equivalent External Reference Objects.
     * @throws DaoException Error Accessing Database.
     */
    public ArrayList getEquivalenceList(ExternalReference xref)
            throws DaoException {
        //  Create a Non-Redundant Set of External References
        HashSet hitList = new HashSet();

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
        CPathXRef cpathXRef = new CPathXRef(dbRecord.getId(), xref.getId());
        DaoIdMap daoId = new DaoIdMap();
        ArrayList list = daoId.getEquivalenceList(cpathXRef);

        //  Transform all Matches into External Reference Objects.
        for (int i = 0; i < list.size(); i++) {
            CPathXRef cPathMatch = (CPathXRef) list.get(i);

            //  Look up Database Name
            dbRecord = dao.getRecordById(cPathMatch.getDbId());

            //  Create New External Reference Object.
            ExternalReference match = new ExternalReference
                    (dbRecord.getFixedCvTerm(), cPathMatch.getLinkedToId());
            if (!match.equals(xref)) {
                hitList.add(match);
            }
        }
        return new ArrayList(hitList);
    }

    /**
     * Utility method for creating a union of two lists of External References.
     *
     * @param refList ArrayList of External Reference Object.
     * @param refs    Array of External Objects.
     * @return ArrayList of ExternalReference Objects.
     */
    public ArrayList createUnifiedList(ArrayList refList,
            ExternalReference[] refs) {
        HashSet union = new HashSet();
        union.addAll(refList);
        for (int i = 0; i < refs.length; i++) {
            union.add(refs[i]);
        }
        ArrayList list = new ArrayList(union);
        return list;
    }

    /**
     * Creates a Non-Redundant Set of Normalized External Reference Objects.
     * Normalization sets the Database Name to the Fixed Controlled Vocabulary
     * Term, as defined by cPath.
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