package org.mskcc.pathdb.sql.transfer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.Marshaller;
import org.jdom.Text;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ConsoleUtil;
import org.mskcc.pathdb.util.PsiUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.StringWriter;
import java.io.IOException;
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
     * @param verbose              verbosity flag.
     * @param pMonitor             Progress Monitor Object.
     * @return Import Summary Object.
     * @throws ImportException Indicates Error in Import.
     */
    public ImportSummary addRecord(String xml, boolean validateExternalRefs,
            boolean verbose, ProgressMonitor pMonitor) throws ImportException {
        summary = new ImportSummary();
        idMap = new HashMap();
        this.pMonitor = pMonitor;
        try {
            // Steps 1-2:  Normalize PSI Document, chop into parts.
            if (verbose) {
                System.out.println("Normalizing PSI Document...");
            }
            pMonitor.setCurrentMessage("Step 1:  Normalizing PSI Document");
            psiUtil = new PsiUtil();
            EntrySet entrySet = null;
            entrySet = psiUtil.getNormalizedDocument(xml);
            if (verbose) {
                System.out.println
                        ("Normalization Complete:  Document is valid");
            }

            //  Validate Interactors / External References.
            if (validateExternalRefs) {
                validateExternalRefs(entrySet, verbose);
            }

            //  Step 3:  Process all Interactors.
            processInteractors(entrySet, verbose);

            //  Step 4:  Process all Interactions.
            processInteractions(entrySet, verbose);
            return summary;
        } catch (IOException e) {
            throw new ImportException(e);
        } catch (ValidationException e) {
            throw new ImportException(e);
        } catch (MarshalException e) {
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
    private void validateExternalRefs(EntrySet entrySet, boolean verbose)
            throws DaoException, ExternalDatabaseNotFoundException {
        if (verbose) {
            System.out.println("Validating all External References:  ");
        }
        pMonitor.setCurrentMessage("Step 2:  Validating All External "
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
                ConsoleUtil.showProgress(verbose, pMonitor);
                ProteinInteractorType protein =
                        interactors.getProteinInteractor(j);
                ExternalReference[] refs = extractExternalReferences(protein);
                linker.validateExternalReferences(refs);
            }

            //  Validate All Interactions
            for (int j = 0; j < interactions.getInteractionCount(); j++) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(verbose, pMonitor);
                InteractionElementType interaction =
                        interactions.getInteraction(j);
                ExternalReference refs[] =
                        psiUtil.extractXrefs(interaction.getXref());
                linker.validateExternalReferences(refs);
            }
        }
        if (verbose) {
            System.out.println();
        }
    }

    /**
     * Processes all Interactors
     */
    private void processInteractors(EntrySet entrySet, boolean verbose)
            throws DaoException, MarshalException, ValidationException,
            IOException {
        DaoExternalLink externalLinker = new DaoExternalLink();

        if (verbose) {
            System.out.println("Processing all Interactors:  ");
        }
        pMonitor.setCurrentMessage("Step 3:  Process all Interactors");
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactors = entry.getInteractorList();
            pMonitor.setMaxValue(interactors.getProteinInteractorCount());
            for (int j = 0; j < interactors.getProteinInteractorCount(); j++) {
                ConsoleUtil.showProgress(verbose, pMonitor);
                pMonitor.incrementCurValue();
                summary.incrementNumInteractorsProcessed();
                ProteinInteractorType protein =
                        interactors.getProteinInteractor(j);

                // Normalize, then extract External Refs
                psiUtil.normalizeXrefs(protein.getXref());
                ExternalReference[] refs = extractExternalReferences(protein);

                ArrayList records = externalLinker.lookUpByExternalRefs(refs);
                //  Step 3.1.2 - 3.1.3
                if (records.size() > 0) {
                    System.out.print("#");
                    CPathRecord record = (CPathRecord) records.get(0);
                    //  Conditionally Update the Interactor Record with
                    //  new external references.
                    UpdatePsiInteractor updater = new UpdatePsiInteractor
                          (protein);
                    updater.doUpdate();
                    idMap.put(protein.getId(), new Long(record.getId()));
                    summary.incrementNumInteractorsFound();
                } else {
                    saveInteractor(protein, refs);
                    summary.incrementNumInteractorsSaved();
                }
            }
        }
        if (verbose) {
            System.out.println();
        }
    }

    /**
     * Extracts External References from Protein Interactor.
     */
    private ExternalReference[] extractExternalReferences
            (ProteinInteractorType protein) {
        ExternalReference refs[] = psiUtil.extractRefs(protein);
        return refs;
    }

    /**
     * Processes all Interactors
     */
    private void processInteractions(EntrySet entrySet, boolean verbose)
            throws DaoException, MarshalException, ValidationException,
            IOException {
        if (verbose) {
            System.out.println("Processing all Interactions:  ");
        }
        pMonitor.setCurrentMessage("Step 4:  Process all Interactions");
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractionList interactions = entry.getInteractionList();

            //  Step 4.1:  Update all Interactor Refs to point to cPath Ids
            psiUtil.updateInteractions(interactions, idMap);

            pMonitor.setMaxValue(interactions.getInteractionCount());
            for (int j = 0; j < interactions.getInteractionCount(); j++) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(verbose, pMonitor);
                InteractionElementType interaction =
                        interactions.getInteraction(j);
                saveInteraction(interaction);
            }
        }
        if (verbose) {
            System.out.println();
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

        if (name == null || name.length() == 0) {
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
                CPathRecordType.PHYSICAL_ENTITY, xml, refs);

        //  Add to Id Hash Map
        idMap.put(protein.getId(), new Long(cpathId));

        //  Step 3.1.3.2
        //  Update XML with new CPath ID.
        protein.setId(Long.toString(cpathId));
        xml = marshalProtein(protein);
        cpath.updateXml(cpathId, xml);

        //  Update Global Organism Table
        DaoOrganism daoOrganism = new DaoOrganism();
        if (!daoOrganism.recordExists(taxId)) {
            NamesType namesType = organism.getNames();
            if (namesType.getFullName() != null) {
                daoOrganism.addRecord(taxId, namesType.getFullName(),
                        namesType.getShortLabel());
            }
        }
    }

    /**
     * Saves New Interaction to Database.
     */
    private void saveInteraction(InteractionElementType interaction)
            throws MarshalException, ValidationException, DaoException,
            IOException {
        DaoCPath cpath = new DaoCPath();
        summary.incrementNumInteractionsSaved();

        //  Extract References, if they exist.
        XrefType xref = interaction.getXref();
        ExternalReference refs[] = psiUtil.extractXrefs(xref);

        //  Conditionally delete existing interaction (if it exists)
        //  New Interaction clobbers old interaction.
        if (refs != null) {
            conditionallyDeleteInteraction(refs);
        }

        //  Set Name, Description; and Marshal XML.
        String xml = this.marshalInteraction(interaction);
        String name = "Interaction";
        String desc = "Interaction";
        int taxId = CPathRecord.TAXONOMY_NOT_SPECIFIED;

        //  Add New Record to cPath
        long cpathId = cpath.addRecord(name, desc, taxId,
                CPathRecordType.INTERACTION, xml, refs);

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
            throws DaoException {
        DaoExternalLink linker = new DaoExternalLink();
        DaoCPath cpath = new DaoCPath();
        ArrayList records = linker.lookUpByExternalRefs(refs);
        if (records.size() > 0) {
            CPathRecord record = (CPathRecord) records.get(0);
            //  Delete the Interaction Record and all
            //  associated internal/external links.
            cpath.deleteRecordById(record.getId());
            summary.incrementNumInteractionsClobbered();
        }
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
}