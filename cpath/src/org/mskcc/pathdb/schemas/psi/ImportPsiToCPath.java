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
package org.mskcc.pathdb.schemas.psi;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.jdom.Text;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.dataservices.schemas.psi.Organism;
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.schemas.psi.PsiUtil;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.references.BackgroundReferenceService;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.sql.transfer.MissingDataException;
import org.mskcc.pathdb.sql.transfer.UpdatePsiInteractor;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ExternalReferenceUtil;
import org.mskcc.pathdb.util.tool.ConsoleUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Imports a single PSI-MI record into cPath.
 * <p/>
 * <P>Below is pseudo-code for how the import works:
 * <OL>
 * <LI>Normalize the XML Document
 * <UL>
 * <LI>Given a PSI-MI document, transform the document into a
 * mixed canonical/noncanonical version. Specifically, move all
 * interactors to the top of the document, and remove redundancy
 * among interactors. Move all other top elements, e.g. experiment
 * descriptions into the redundant, non-canonical form.  This
 * operation is performed via PsiUti.getNormalizedDocument().
 * </LI>
 * </UL>
 * </LI>
 * <LI>Chop XML into Fragments
 * <UL>
 * <LI>Chop the XML document into multiple record-like XML
 * documents. Each interactor becomes one XML document
 * fragment, and each interaction becomes one XML document
 * fragment. These will be well-formed, but invalid XML
 * document fragments.  Chopping is currently done via
 * Castor generated objects.
 * </LI>
 * </UL>
 * </LI>
 * <LI>Process all Interactors
 * <OL>
 * <LI>For each interacor:
 * <OL>
 * <LI>Determine if this interactor already exists within
 * the database.  We determine existence of interactors
 * via the DaoExternalLink.lookUpByExternalRefs() method.
 * This method checks all external references of the
 * interactor.  If a match is found, the cPath record
 * is returned.
 * </LI>
 * <LI>If the interactor exists, extract its cPath ID
 * and add any new external references.</LI>
 * <LI>Else
 * <OL>
 * <LI>Add the new interactor XML Blob and all
 * external links to cPath.</LI>
 * <LI>Each new interactor will receive a new cPathId.
 * Update the XML Blob to contain this new
 * cPath Id.
 * </LI>
 * </OL>
 * </LI>
 * </OL>
 * </LI>
 * </OL>
 * </LI>
 * <LI>Process all Interactions
 * <UL>
 * <LI>Modify all interaction XML fragments by replacing all interactor
 * Refs with the matching cPath Ids. For example, in the original
 * document, we may have (A interacts with B). After processing
 * is complete, we have something like (cpathID:22214 interacts with
 * cpathID:58225).  This operation is performed via the PsiUtil.
 * updateInteractions() method.
 * </LI>
 * <LI>For each interaction:
 * <OL>
 * <LI>Store XML fragment as a blob to main cPath table.</LI>
 * <LI>Add internal links that point to
 * interactors. These can be used as bidirectional links.
 * For example, given an interactor, you can determine all the
 * interactions it is involved in; given an interaction,
 * you can determine all the interactors involved.
 * </LI>
 * </UL>
 * </LI>
 * </OL>
 *
 * @author Ethan Cerami
 */
public class ImportPsiToCPath {
    private PsiUtil psiUtil;
    private ImportSummary summary;
    private ProgressMonitor pMonitor;

    /**
     * Contains a HashMap of Protein IDs in the original XML file
     * to cPath Ids in the current database.
     */
    private HashMap idMap;

    /**
     * Adds Specified PSI-MI Record.
     *
     * @param xml                  PSI-MI XML Record.
     * @param validateExternalRefs Validates External References.
     * @param removeAllXrefs       Automatically Removes all Xrefs (not recmd).
     * @param pMonitor             Progress Monitor Object.
     * @return Import Summary Object.
     * @throws org.mskcc.pathdb.sql.transfer.ImportException Indicates Error in Import.
     */
    public ImportSummary addRecord(String xml, boolean validateExternalRefs,
            boolean removeAllXrefs, ProgressMonitor pMonitor)
            throws ImportException {
        summary = new ImportSummary();
        idMap = new HashMap();
        this.pMonitor = pMonitor;

        if (removeAllXrefs) {
            pMonitor.setCurrentMessage("Warning!  Data Import will "
                    + "automatically remove all PSI-MI "
                    + "interaction xrefs.");
        }        

        try {
            // Steps 1-2:  Normalize PSI Document, chop into parts.
            pMonitor.setCurrentMessage("Step 1 of 4:  "
                    + "Normalizing PSI Document");
            psiUtil = new PsiUtil(pMonitor);
            EntrySet entrySet = null;
            entrySet = psiUtil.getNormalizedDocument(xml, removeAllXrefs);
            pMonitor.setCurrentMessage
                    ("Normalization Complete:  Document is valid.");

            //  Validate Interactors / External References.
            if (validateExternalRefs) {
                validateExternalRefs(entrySet);
            }

            //  Step 3:  Process all Interactors.
            processInteractors(entrySet);

            //  Step 4:  Process all Interactions.
            processInteractions(entrySet);
            return summary;
        } catch (IOException e) {
            throw new ImportException(e);
        } catch (ValidationException e) {
            throw new ImportException(e);
        } catch (MarshalException e) {
            throw new ImportException(e);
        } catch (MissingDataException e) {
            throw new ImportException(e);
        } catch (DaoException e) {
            throw new ImportException(e);
        } catch (ExternalDatabaseNotFoundException e) {
            throw new ImportException(e);
        }
    }

    /**
     * Validates all Interactors and External References.
     */
    private void validateExternalRefs(EntrySet entrySet)
            throws DaoException, ExternalDatabaseNotFoundException,
            MissingDataException {
        pMonitor.setCurrentMessage("Step 2 of 4:  Validating All External "
                + "References");
        DaoExternalLink linker = new DaoExternalLink();

        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);

            //  Validate All Interactors
            InteractorList interactors = entry.getInteractorList();
            InteractionList interactions = entry.getInteractionList();

            pMonitor.setMaxValue(interactors.getProteinInteractorCount()
                    + interactions.getInteractionCount());
            for (int j = 0; j < interactors.getProteinInteractorCount(); j++) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
                ProteinInteractorType protein =
                        interactors.getProteinInteractor(j);
                try {
                    ExternalReference[] refs =
                            extractExternalReferences(protein);
                    linker.validateExternalReferences(refs);
                } catch (MissingDataException e) {
                    reportInteractorErrorDetails(protein, e, j, interactors);
                }
            }

            //  Validate All Interactions
            for (int j = 0; j < interactions.getInteractionCount(); j++) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
                InteractionElementType interaction =
                        interactions.getInteraction(j);
                try {
                    ExternalReference refs[] =
                            psiUtil.extractXrefs(interaction.getXref());
                    linker.validateExternalReferences(refs);
                } catch (MissingDataException e) {
                    reportInteractionErrorDetails(interaction, e, j,
                            interactions);
                }
            }
        }

    }

    /**
     * Provide Context Sensitive Error Details for Interactors.
     */
    private void reportInteractorErrorDetails(ProteinInteractorType protein,
            MissingDataException e, int j, InteractorList interactors)
            throws MissingDataException {
        StringWriter writer = new StringWriter();
        try {
            protein.marshal(writer);
        } catch (MarshalException e1) {
            e.printStackTrace(System.err);
        } catch (ValidationException e1) {
            e.printStackTrace(System.err);
        }
        throw new MissingDataException("PSI-MI Protein ["
                + protein.getNames().getShortLabel() + ":"
                + protein.getNames().getFullName() + ":"
                + protein.getId() + "] is missing data:  "
                + e.getMessage() + " Protein is located at "
                + "index position " + (j + 1) + " of "
                + interactors.getProteinInteractorCount() + "."
                + "\n\nData Dump of Offending Protein XML follows:  \n\n"
                + writer.toString());
    }

    /**
     * Provide Context Sensitive Error Details for Interactions.
     */
    private void reportInteractionErrorDetails(InteractionElementType
            interaction, MissingDataException e, int j,
            InteractionList interactions) throws MissingDataException {
        StringWriter writer = new StringWriter();
        try {
            interaction.marshal(writer);
        } catch (MarshalException e1) {
            e.printStackTrace(System.err);
        } catch (ValidationException e1) {
            e.printStackTrace(System.err);
        }
        throw new MissingDataException("PSI-MI Interaction "
                + "is missing data:  " + e.getMessage()
                + " Interaction is located at index position "
                + (j + 1) + " of " + interactions.getInteractionCount()
                + "." + "  \n\nData Dump of Offending Interaction XML "
                + "follows:  \n\n"
                + writer.toString());
    }

    /**
     * Processes all Interactors
     */
    private void processInteractors(EntrySet entrySet)
            throws DaoException, MarshalException, ValidationException,
            MissingDataException, IOException {
        DaoExternalLink externalLinker = new DaoExternalLink();
        pMonitor.setCurrentMessage("Step 3 of 4:  Process all Interactors");
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactors = entry.getInteractorList();
            pMonitor.setMaxValue(interactors.getProteinInteractorCount());
            for (int j = 0; j < interactors.getProteinInteractorCount(); j++) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
                summary.incrementNumPhysicalEntitiesProcessed();
                ProteinInteractorType protein =
                        interactors.getProteinInteractor(j);

                // Normalize, then extract all External Refs
                psiUtil.normalizeXrefs(protein.getXref());
                ExternalReference[] refs = extractExternalReferences(protein);

                //  Split References into two subsets:  first subset
                //  contains those used for UNIFICATION;  second subset
                //  contains those used for LINK_OUT.
                ExternalReference[] unificationRefs =
                        ExternalReferenceUtil.filterByReferenceType
                        (refs, ReferenceType.PROTEIN_UNIFICATION);

                ExternalReference[] linkOutRefs =
                        ExternalReferenceUtil.filterByReferenceType
                        (refs, ReferenceType.LINK_OUT);

                ArrayList records = externalLinker.lookUpByExternalRefs
                        (unificationRefs);
                //  Step 3.1.2 - 3.1.3
                if (records.size() > 0) {
                    CPathRecord record = (CPathRecord) records.get(0);
                    //  Conditionally Update the Interactor Record with
                    //  new external references.
                    UpdatePsiInteractor updater = new UpdatePsiInteractor
                            (record, protein, pMonitor);
                    updater.doUpdate();
                    idMap.put(protein.getId(), new Long(record.getId()));
                    summary.incrementNumPhysicalEntitiesFound();
                    String refListText = getReferencesAsText(unificationRefs);
                    pMonitor.setCurrentMessage("\nExisting interactor found "
                            + " in cPath,  " + "based on xrefs:  "
                            + refListText);
                } else {
                    //  Query Background Reference Service for other
                    //  unification identifiers.
                    unificationRefs = queryUnificationService(unificationRefs);

                    //  Query Background Reference Service for LinkOuts
                    ExternalReference backgroundLinkOutRefs[] =
                            queryLinkOutService(unificationRefs);

                    //  Create the union of all  UNIFICATION Refs and
                    //  LINK_OUT Refs
                    ExternalReference allRefs[] =
                            ExternalReferenceUtil.createUnifiedList
                            (unificationRefs, linkOutRefs);
                    if (backgroundLinkOutRefs != null
                            && backgroundLinkOutRefs.length > 0) {
                        allRefs =
                                ExternalReferenceUtil.createUnifiedList(allRefs,
                                        backgroundLinkOutRefs);
                    }

                    //  Remove any duplicates in the Reference List
                    allRefs = ExternalReferenceUtil.removeDuplicates(allRefs);

                    //  Update the PSI-MI XRef Data Model with all newly derived
                    //  External References.
                    XrefType xref = protein.getXref();
                    psiUtil.addExternalReferences(xref, allRefs);

                    //  Save the interactor to the database
                    try {
                        saveInteractor(protein, allRefs);
                    } catch (IllegalArgumentException e) {
                        pMonitor.setCurrentMessage("\nError occurred while "
                                + "processing interator:  "
                                + protein.getId());
                        pMonitor.setCurrentMessage("Containing XRefs:");
                        for (int k = 0; k < refs.length; k++) {
                            pMonitor.setCurrentMessage(refs[k].toString());
                        }
                        throw e;
                    }
                    summary.incrementNumPhysicalEntitiesSaved();
                }
            }
        }
    }


    /**
     * Extracts External References from Protein Interactor.
     */
    private ExternalReference[] extractExternalReferences
            (ProteinInteractorType protein) throws MissingDataException {
        ExternalReference refs[] = psiUtil.extractRefs(protein);
        return refs;
    }

    /**
     * Processes all Interactions
     */
    private void processInteractions(EntrySet entrySet)
            throws DaoException, MarshalException, ValidationException,
            IOException, MissingDataException, ImportException {
        pMonitor.setCurrentMessage("Step 4 of 4:  Process all Interactions");
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractionList interactions = entry.getInteractionList();

            //  Step 4.1:  Update all Interactor Refs to point to cPath Ids
            psiUtil.updateInteractions(interactions, idMap);

            pMonitor.setMaxValue(interactions.getInteractionCount());
            for (int j = 0; j < interactions.getInteractionCount(); j++) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
                InteractionElementType interaction =
                        interactions.getInteraction(j);
                saveInteraction(interaction);
            }
        }
    }

    /**
     * Saves New Interactor to Database.
     * Step 3.1.3.
     */
    private void saveInteractor(ProteinInteractorType protein,
            ExternalReference[] refs) throws MarshalException,
            ValidationException, DaoException, IOException {
        DaoCPath cpath = new DaoCPath();
        //  Extract Important Data:  name, description, taxonomy Id.
        String xml = marshalProtein(protein);

        String name = protein.getNames().getShortLabel();

        if (name == null || name.trim().length() == 0) {
            name = protein.getNames().getFullName();
        }

        String desc = protein.getNames().getFullName();

        //  Remove all surrounding and internal white space.
        Text jdomText = new Text(name);
        name = jdomText.getTextNormalize();
        jdomText = new Text(desc);
        desc = jdomText.getTextNormalize();

        Organism organism = protein.getOrganism();
        int taxId = CPathRecord.TAXONOMY_NOT_SPECIFIED;
        if (organism != null) {
            taxId = organism.getNcbiTaxId();
        }

        //  Add New Record to cPath
        long cpathId = cpath.addRecord(name, desc, taxId,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, xml, refs);

        //  Add to Id Hash Map
        idMap.put(protein.getId(), new Long(cpathId));

        //  Step 3.1.3.2
        //  Update XML with new CPath ID.
        protein.setId(Long.toString(cpathId));
        xml = marshalProtein(protein);
        cpath.updateXml(cpathId, xml);

        //  Update Global Organism Table
        if (organism != null) {
            DaoOrganism daoOrganism = new DaoOrganism();
            if (!daoOrganism.recordExists(taxId)) {
                NamesType namesType = organism.getNames();
                if (namesType.getFullName() != null) {
                    daoOrganism.addRecord(taxId, namesType.getFullName(),
                            namesType.getShortLabel());
                }
            }
        }
    }

    /**
     * Saves New Interaction to Database.
     */
    private void saveInteraction(InteractionElementType interaction)
            throws MarshalException, ValidationException, DaoException,
            IOException, MissingDataException, ImportException {
        DaoCPath cpath = new DaoCPath();
        summary.incrementNumInteractionsSaved();

        //  Extract References, if they exist.
        XrefType xref = interaction.getXref();
        ExternalReference allRefs[] = psiUtil.extractXrefs(xref);
        conditionallyDeleteInteraction(allRefs);

        //  Set Name, Description; and Marshal XML.
        String xml = this.marshalInteraction(interaction);
        String name = "Interaction";
        String desc = "Interaction";
        int taxId = CPathRecord.TAXONOMY_NOT_SPECIFIED;

        //  Add New Record to cPath
        long cpathId = cpath.addRecord(name, desc, taxId,
                CPathRecordType.INTERACTION,
                BioPaxConstants.PHYSICAL_INTERACTION, XmlRecordType.PSI_MI,
                xml, allRefs);

        //  Creates Internal Links Between Interaction Record
        //  and all Interactor Records.
        long idList[] = psiUtil.extractInteractorIds(interaction);
        DaoInternalLink linker = new DaoInternalLink();
        linker.addRecords(cpathId, idList);
    }

    /**
     * Conditional Deletes the Specified Interaction.
     */
    private void conditionallyDeleteInteraction(ExternalReference refs[])
            throws DaoException, ImportException {
        // Filter out References which are not used for unique interaction
        // identification.
        if (refs == null) {
            return;
        }
        ExternalReference[] filteredRefs =
                ExternalReferenceUtil.filterByReferenceType
                (refs, ReferenceType.INTERACTION_UNIFICATION);
        DaoExternalLink linker = new DaoExternalLink();
        DaoCPath cpath = new DaoCPath();
        ArrayList records = linker.lookUpByExternalRefs(filteredRefs);
        if (records.size() > 0) {
            CPathRecord record = (CPathRecord) records.get(0);

            //  Make sure this is an interaction; part of bug #524.
            if (record.getType() == CPathRecordType.INTERACTION) {
                //  Delete the Interaction Record and all
                //  associated internal/external links.
                cpath.deleteRecordById(record.getId());
                summary.incrementNumInteractionsClobbered();
                String refList = getReferencesAsText(filteredRefs);
                pMonitor.setCurrentMessage("\nWarning!  Clobbering existing "
                        + "interaction, based on xrefs:  "
                        + refList.toString());
            } else {
                String refStr = getReferencesAsText(refs);
                throw new ImportException("Interaction contains the "
                        + "following xrefs:  " + refStr + ".  However, one of "
                        + "these xrefs is already used by an interactor. "
                        + "The interaction xrefs therefore cannot be trusted.  "
                        + "Please double check them.  Aborting import.");
            }
        }
    }

    private String getReferencesAsText(ExternalReference[] refs) {
        StringBuffer refList = new StringBuffer();
        for (int i = 0; i < refs.length; i++) {
            String db = refs[i].getDatabase();
            String id = refs[i].getId();
            refList.append(" [db=" + db + ", id=" + id + "]");
        }
        return refList.toString();
    }

    /**
     * Marshal Protein XML.
     */
    private String marshalProtein(ProteinInteractorType protein)
            throws MarshalException, ValidationException, IOException {
        // Instantiate Marshaller Directly, so that we can turn validation off
        // Validation takes extra time / memory, and we have already validated
        // the original XML document.
        StringWriter writer = new StringWriter();
        Marshaller marshaller = new Marshaller(writer);
        marshaller.setValidation(false);
        marshaller.marshal(protein);
        return writer.toString();
    }

    /**
     * Marshal Interaction XML.
     */
    private String marshalInteraction(InteractionElementType interaction)
            throws MarshalException, ValidationException, IOException {
        // Instantiate Marshaller Directly, so that we can turn validation off
        // Validation takes extra time / memory, and we have already validated
        // the original XML document.
        StringWriter writer = new StringWriter();
        Marshaller marshaller = new Marshaller(writer);
        marshaller.setValidation(false);
        marshaller.marshal(interaction);
        return writer.toString();
    }

    /**
     * Queries the Background Reference Service for a list of
     * PROTEIN_UNIFICATION References.
     *
     * @param unificationRefs Array of External Reference Objects.
     * @return Array of External Reference Objects.
     */
    private ExternalReference[] queryUnificationService(ExternalReference[]
            unificationRefs) throws DaoException {
        //  Only execute query if we have existing unification references.
        if (unificationRefs != null && unificationRefs.length > 0) {
            BackgroundReferenceService refService =
                    new BackgroundReferenceService();
            ArrayList backgroundUnificationRefs =
                    refService.getUnificationReferences(unificationRefs);

            //  Return the complete unification list.
            return (ExternalReference[]) backgroundUnificationRefs.toArray
                    (new ExternalReference[backgroundUnificationRefs.size()]);
        } else {
            return unificationRefs;
        }
    }

    /**
     * Queries the Background Reference Service for a list of LINK_OUT
     * References.
     *
     * @param unificationRefs Array of External Reference Objects.
     * @return Array of External Reference Objects.
     */
    private ExternalReference[] queryLinkOutService(ExternalReference[]
            unificationRefs) throws DaoException {
        //  Only execute query if we have existing unification references.
        if (unificationRefs != null && unificationRefs.length > 0) {
            BackgroundReferenceService refService =
                    new BackgroundReferenceService();
            ArrayList linkOutRefs =
                    refService.getLinkOutReferences(unificationRefs);

            //  Return the complete unification list.
            return (ExternalReference[]) linkOutRefs.toArray
                    (new ExternalReference[linkOutRefs.size()]);
        } else {
            return null;
        }
    }
}