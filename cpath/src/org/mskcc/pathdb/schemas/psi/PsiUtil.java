// $Id: PsiUtil.java,v 1.4 2006-02-22 22:47:50 grossb Exp $
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
package org.mskcc.pathdb.schemas.psi;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.transfer.MissingDataException;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Normalizes a PSI-MI XML Document in preparation for submission to cPath.
 *
 * @author Ethan Cerami
 */
public class PsiUtil {
    private ProgressMonitor pMonitor;
    private HashMap interactorMap;
    private HashMap availabilityMap;
    private HashMap experimentMap;
    private EntrySet entrySet;
    private boolean removeAllXRefs;

    /**
     * Constructor.
     *
     * @param pMonitor ProgressMonitor Object
     */
    public PsiUtil(ProgressMonitor pMonitor) {
        this.pMonitor = pMonitor;
    }

    /**
     * Gets the Normalized PSI Document.
     *
     * @param xml            XML Document String.
     * @param removeAllXrefs Automatically Removes all XRefs (not recommended).
     * @return PSI Entry Set Object.
     * @throws ValidationException Validation Error in Document.
     * @throws MarshalException    Error Marshalling Document.
     */
    public EntrySet getNormalizedDocument(String xml, boolean removeAllXrefs)
            throws ValidationException, MarshalException {
        this.removeAllXRefs = removeAllXrefs;
        this.interactorMap = new HashMap();
        this.availabilityMap = new HashMap();
        this.experimentMap = new HashMap();
        return normalizeDoc(xml);
    }

    /**
     * Updates the Specified Interactor with a New ID.
     *
     * @param newId      New Id, usually a cPath Id.
     * @param interactor Castor Protein Interactor.
     */
    public void updateInteractorId(String newId,
            ProteinInteractorType interactor) {
        interactor.setId(newId);
    }

    /**
     * Update Interactions with New Interactor Ids.
     *
     * @param interactions InteractionList Object.
     * @param idMap        HashMap of Interactor IDs to cPathIds.
     */
    public void updateInteractions(InteractionList interactions,
            HashMap idMap) {
        for (int i = 0; i < interactions.getInteractionCount(); i++) {
            InteractionElementType interaction =
                    interactions.getInteraction(i);
            ParticipantList pList = interaction.getParticipantList();
            for (int j = 0; j < pList.getProteinParticipantCount(); j++) {
                ProteinParticipantType type = pList.getProteinParticipant(j);
                ProteinParticipantTypeChoice choice =
                        type.getProteinParticipantTypeChoice();
                RefType refType = choice.getProteinInteractorRef();
                String ref = refType.getRef();
                Long cPathId = (Long) idMap.get(ref);
                if (cPathId == null) {
                    throw new NullPointerException("No cPath ID found for: "
                            + ref);
                }
                refType.setRef(cPathId.toString());
            }
        }
    }

    /**
     * Extract Interactor IDs.
     *
     * @param interaction Interaction Object.
     * @return ArrayList of cPathIds.
     */
    public long[] extractInteractorIds
            (InteractionElementType interaction) {
        ArrayList ids = new ArrayList();
        ParticipantList pList = interaction.getParticipantList();
        for (int j = 0; j < pList.getProteinParticipantCount(); j++) {
            ProteinParticipantType type = pList.getProteinParticipant(j);
            ProteinParticipantTypeChoice choice =
                    type.getProteinParticipantTypeChoice();
            RefType refType = choice.getProteinInteractorRef();
            String ref = refType.getRef();
            ids.add(ref);
        }
        long longIds[] = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            String ref = (String) ids.get(i);
            longIds[i] = Long.parseLong(ref);
        }
        return longIds;
    }

    /**
     * Extracts All External References for specified Protein Interactor.
     *
     * @param cProtein Castor Protein Object.
     * @return Array of External Reference Objects.
     * @throws MissingDataException Indicates Missing Data.
     */
    public ExternalReference[] extractRefs(ProteinInteractorType cProtein)
            throws MissingDataException {
        XrefType xref = cProtein.getXref();
        return this.extractXrefs(xref);
    }

    /**
     * Extracts All External References for XrefType object.
     *
     * @param xref XrefType Object.
     * @return Array of External Reference Objects.
     * @throws MissingDataException Indicates Missing Data.
     */
    public ExternalReference[] extractXrefs(XrefType xref)
            throws MissingDataException {
        ArrayList refList = new ArrayList();
        if (xref != null) {
            DbReferenceType primaryRef = xref.getPrimaryRef();
            if (primaryRef != null) {
                createExternalReference(primaryRef.getDb(), primaryRef.getId(),
                        refList);
            }
            for (int i = 0; i < xref.getSecondaryRefCount(); i++) {
                DbReferenceType secondaryRef = xref.getSecondaryRef(i);
                createExternalReference(secondaryRef.getDb(),
                        secondaryRef.getId(), refList);
            }
            ExternalReference refs [] =
                    new ExternalReference[refList.size()];
            refs = (ExternalReference[]) refList.toArray(refs);
            return refs;
        } else {
            return null;
        }
    }

    /**
     * Normalizes all XRefs to FIXED_CV_TERMS.
     * <p/>
     * As a first temporary measure, we now remove version information from
     * linked ids.  For example, the following ID:  NP_000680.2 is transformed
     * to:  NP_000680.  This enables us to import HPRD data and map RefSeq
     * IDs to Affymetrix IDs.
     * <p/>
     * As a second temporary measure, we divide RefSeq IDs into RefSeq Proteins
     * and all other RefSeq IDs.  This enables us to use RefSeq proteins for
     * PROTEIN_UNIFICATION.
     *
     * @param xref XrefType Object.
     * @throws DaoException Data Access Exception.
     */
    public void normalizeXrefs(XrefType xref) throws DaoException {
        if (xref != null) {
            DbReferenceType primaryRef = xref.getPrimaryRef();
            if (primaryRef != null) {
                String id = primaryRef.getId();
                String db = primaryRef.getDb();
                String normalizedDb = getNormalizedDatabase(db, id);
                primaryRef.setDb(normalizedDb);
                primaryRef.setId(stripVersionInfo(primaryRef.getId()));
            }
            for (int i = 0; i < xref.getSecondaryRefCount(); i++) {
                DbReferenceType secondaryRef = xref.getSecondaryRef(i);
                String id = secondaryRef.getId();
                String db = secondaryRef.getDb();
                String normalizedDb = getNormalizedDatabase(db, id);
                secondaryRef.setDb(normalizedDb);
                secondaryRef.setId(stripVersionInfo(secondaryRef.getId()));
            }
        }
    }

    /**
     * Looks up a Database Term, and Normalizes it to the Fixed Controlled
     * Vocabulary Term.
     * <p/>
     * Also provides a bit of hardcoded work for handling REF_SEQ IDs.
     */
    private String getNormalizedDatabase(String db, String id)
            throws DaoException {
        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(db);

        //  If this is a REF_SEQ ID, determine if it's a PROTEIN.
        if (dbRecord.getMasterTerm().equalsIgnoreCase("REF_SEQ")
                && id != null && id.charAt(1) == 'P') {
            dbRecord = dao.getRecordByTerm("REF_SEQ PROTEIN");
        }
        String newDb = dbRecord.getMasterTerm();
        return newDb;
    }

    /**
     * Remove Version Information from LinkedToID.
     *
     * @param id LinkedToID.
     */
    private String stripVersionInfo(String id) {
        if (id != null) {
            int index = id.indexOf(".");
            if (index > -1) {
                id = id.substring(0, index);
            }
        }
        return id;
    }

    /**
     * Removes Empty Secondary XRefs.
     * This method is primarily available as a work-around to PSI-MI Data
     * from HPRD, which contains empty secondary xrefs.
     * JUnit Test is in TestPsiUtil.java.
     *
     * @param xref XrefType Object.
     */
    public void removeEmptySecondaryRefs(XrefType xref) {
        ArrayList safeRefs = new ArrayList();
        if (xref != null) {
            //  First, get all Secondary Refs
            DbReferenceType secondaryRefs[] = xref.getSecondaryRef();

            //  Check for Null SecondaryRefs
            if (secondaryRefs != null) {

                //  Remove all SecondaryRefs
                xref.removeAllSecondaryRef();

                //  Then, add back only valid secondary refs
                for (int i = 0; i < secondaryRefs.length; i++) {
                    DbReferenceType dbRef = secondaryRefs[i];
                    String db = dbRef.getDb();
                    String id = dbRef.getId();
                    if (db == null || db.trim().length() == 0) {
                        if (id == null) {
                            id = "No id attribute available either";
                        }
                        pMonitor.setCurrentMessage
                                ("Warning!  Removing Secondary Xref with "
                                + "empty db attribute: [xref id = "
                                + id.trim() + "]");
                    } else if (id == null || id.trim().length() == 0) {
                        if (db == null) {
                            db = "No db attribute available either";
                        }
                        pMonitor.setCurrentMessage
                                ("Warning!  Removing Secondary Xref with "
                                + "empty id attribute:  [xref db = "
                                + db.trim() + "]");
                    } else {
                        dbRef.setDb(db.trim());
                        dbRef.setId(id.trim());
                        safeRefs.add(dbRef);
                    }
                }

                //  Reset XRef SecondaryRefs.
                int size = safeRefs.size();
                if (size > 0) {
                    DbReferenceType[] safeArray = new DbReferenceType[size];
                    for (int i = 0; i < size; i++) {
                        safeArray[i] = (DbReferenceType) safeRefs.get(i);
                    }
                    xref.setSecondaryRef(safeArray);
                }
            }
        }
    }

    /**
     * Creates ExternalReference.
     */
    private void createExternalReference(String db, String id,
            ArrayList refList) throws MissingDataException {
        //  Added Check for Null or Empty DB;  part of bug fix #508
        if (db == null || db.trim().length() == 0) {
            throw new MissingDataException("Xref db is null or empty "
                    + " [xref id:  " + id + "]");
        }
        //  Added Check for Null or Empty ID;  part of bug fix #508
        if (id == null || id.trim().length() == 0) {
            throw new MissingDataException("Xref id is null or empty. "
                    + " [xref db:  " + db + "]");
        }
        ExternalReference ref = new ExternalReference(db.trim(), id.trim());
        refList.add(ref);
    }

    /**
     * Normalize the Document.
     */
    private EntrySet normalizeDoc(String xml) throws ValidationException,
            MarshalException {
        StringReader reader = new StringReader(xml);
        entrySet = EntrySet.unmarshalEntrySet(reader);

        preprocessEntries();

        //  Process all Entries.
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            InteractionList interactionList = entry.getInteractionList();
            for (int j = 0; j < interactionList.getInteractionCount(); j++) {
                InteractionElementType interaction =
                        interactionList.getInteraction(j);

                //  Conditionally Remove All Interaction XRefs
                if (this.removeAllXRefs) {
                    interaction.setXref(null);
                }
                removeEmptySecondaryRefs(interaction.getXref());
                copyAvailablityEntity(interaction);
                copyExperiments(interaction);

                ParticipantList participantList =
                        interaction.getParticipantList();
                for (int k = 0; k < participantList.
                        getProteinParticipantCount(); k++) {
                    ProteinParticipantType proteinParticipant =
                            participantList.getProteinParticipant(k);
                    ProteinParticipantTypeChoice choice =
                            proteinParticipant.
                            getProteinParticipantTypeChoice();
                    ProteinInteractorType protein =
                            choice.getProteinInteractor();
                    createInteractorRef(protein, interactorList,
                            proteinParticipant);
                }
            }
        }
        return entrySet;
    }

    /**
     * Removes Experiment References and replaces them with actual
     * Experiment Description Entities.
     */
    private void copyExperiments(InteractionElementType interaction) {
        ExperimentList expList = interaction.getExperimentList();
        if (expList != null) {
            for (int i = 0; i < expList.getExperimentListItemCount(); i++) {
                ExperimentListItem expItem = expList.getExperimentListItem(i);
                RefType ref = expItem.getExperimentRef();
                if (ref != null) {
                    String id = ref.getRef();
                    ExperimentType exp = (ExperimentType) experimentMap.get(id);
                    if (exp != null) {
                        exp.setId("NO_ID");
                        expItem.setExperimentDescription(exp);
                        expItem.setExperimentRef(null);
                    } else {
                        System.out.println("Warning!  No Experiment found "
                                + " for experiment ref:  " + id);
                    }
                }
            }
        }
    }

    /**
     * Removes Availability References and replaces them with actual
     * Availablity Entities.
     */
    private void copyAvailablityEntity(InteractionElementType interaction) {
        InteractionElementTypeChoice choice =
                interaction.getInteractionElementTypeChoice();
        if (choice != null) {
            RefType ref = choice.getAvailabilityRef();
            if (ref != null) {
                String id = ref.getRef();
                AvailabilityType availability = (AvailabilityType)
                        availabilityMap.get(id);
                choice = new InteractionElementTypeChoice();
                availability.setId("NO_ID");
                choice.setAvailabilityDescription(availability);
                interaction.setInteractionElementTypeChoice(choice);
            }
        }
    }

    /**
     * Pre-Process all Entries
     */
    private void preprocessEntries() {
        //  Preprocess all Entries.
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            //  If No InteractorList Exists, create one.
            if (interactorList == null) {
                interactorList = new InteractorList();
                entry.setInteractorList(interactorList);
            }
            extractAllInteractors(interactorList);

            //  Extract all AvailabilityList Items, and then remove them.
            AvailabilityList availablityList = entry.getAvailabilityList();
            extractAllAvailabilityItems(availablityList);
            entry.setAvailabilityList(null);

            //  Extract all ExperimentList Items, and then remove them.
            ExperimentList1 expList = entry.getExperimentList1();
            extractAllExperimentItems(expList);
            entry.setExperimentList1(null);
        }
    }

    /**
     * Conditionally Replaces a ProteinInteractor with a ProteinInteractorRef.
     */
    private void createInteractorRef(ProteinInteractorType protein,
            InteractorList interactorList,
            ProteinParticipantType participantType) {
        if (protein != null) {
            String id = protein.getId();

            //  Check that Interactor already exists in hashmap.
            //  If it doesn't already exist, add it to interactorList and
            //  interactor map.
            if (!interactorMap.containsKey(id)) {
                interactorList.addProteinInteractor(protein);
                interactorMap.put(id, protein);
            }

            //  Replace Protein with a ProteinInteractorRef
            RefType interactorRef = new RefType();
            interactorRef.setRef(id);
            ProteinParticipantTypeChoice choice = new
                    ProteinParticipantTypeChoice();
            choice.setProteinInteractorRef(interactorRef);
            participantType.setProteinParticipantTypeChoice(choice);
        }
    }

    /**
     * Stores all Canonical Interactors in a HashMap.
     */
    private void extractAllInteractors(InteractorList interactorList) {
        if (interactorList != null) {
            for (int i = 0; i < interactorList.getProteinInteractorCount();
                 i++) {
                ProteinInteractorType protein =
                        interactorList.getProteinInteractor(i);
                removeEmptySecondaryRefs(protein.getXref());
                String id = protein.getId();
                interactorMap.put(id, protein);
            }
        }
    }

    /**
     * Stores all Availability Items in a HashMap.
     */
    private void extractAllAvailabilityItems(AvailabilityList list) {
        if (list != null) {
            for (int i = 0; i < list.getAvailabilityCount(); i++) {
                AvailabilityType type = list.getAvailability(i);
                String id = type.getId();
                availabilityMap.put(id, type);
            }
        }
    }

    /**
     * Stores all Experiment Items in a HashMap.
     */
    private void extractAllExperimentItems(ExperimentList1 list) {
        if (list != null) {
            for (int i = 0; i < list.getExperimentDescriptionCount(); i++) {
                ExperimentType exp = list.getExperimentDescription(i);
                String id = exp.getId();
                experimentMap.put(id, exp);
            }
        }
    }

    /**
     * Adds New External References to the PSI-MI XRef Object.
     * Ensures that a single External Reference is not added twice.
     *
     * @param xref      PSI-MI XRef Object.
     * @param extraRefs Array of External Reference Objects.
     * @throws MissingDataException Indicates Missing Data.
     */
    public void addExternalReferences(XrefType xref, ExternalReference
            extraRefs[])
            throws MissingDataException {

        //  Track existing set of references.
        ExternalReference existingRefs[] = extractXrefs(xref);
        HashSet set = new HashSet();
        if (existingRefs != null) {
            for (int i = 0; i < existingRefs.length; i++) {
                set.add(existingRefs[i]);
            }
        }

        if (extraRefs != null) {
            for (int i = 0; i < extraRefs.length; i++) {
                ExternalReference ref = extraRefs[i];
                if (!set.contains(ref)) {
                    if (xref.getPrimaryRef() == null) {
                        DbReferenceType primaryRef = new DbReferenceType();
                        primaryRef.setDb(ref.getDatabase());
                        primaryRef.setId(ref.getId());
                        xref.setPrimaryRef(primaryRef);
                    } else {
                        DbReferenceType secondaryRef = new DbReferenceType();
                        secondaryRef.setDb(ref.getDatabase());
                        secondaryRef.setId(ref.getId());
                        xref.addSecondaryRef(secondaryRef);
                    }
                }
            }
        }
    }
}
