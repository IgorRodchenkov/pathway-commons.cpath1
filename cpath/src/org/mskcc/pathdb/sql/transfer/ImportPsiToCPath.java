package org.mskcc.pathdb.sql.transfer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.jdom.Text;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.util.PsiUtil;

import java.io.StringWriter;
import java.util.HashMap;

/**
 * Imports a single PSI-MI record into cPath.
 *
 * <P>Below is pseudo-code for how the import works:
 * <OL>
 *  <LI>Normalize the XML Document
 *      <UL>
 *          <LI>Given a PSI-MI document, transform the document into a
 *          mixed canonical/noncanonical version. Specifically, move all
 *          interactors to the top of the document, and remove redundancy
 *          among interactors. Move all other top elements, e.g. experiment
 *          descriptions into the redundant, non-canonical form.  This
 *          operation is performed via PsiUti.getNormalizedDocument().
 *          </LI>
 *      </UL>
 *  </LI>
 *  <LI>Chop XML into Fragments
 *      <UL>
 *          <LI>Chop the XML document into multiple record-like XML
 *          documents. Each interactor becomes one XML document
 *          fragment, and each interaction becomes one XML document
 *          fragment. These will be well-formed, but invalid XML
 *          document fragments.  Chopping is currently done via
 *          Castor generated objects.
 *          </LI>
 *      </UL>
 *  </LI>
 *  <LI>Process all Interactors
 *      <OL>
 *          <LI>For each interacor:
 *              <OL>
 *                  <LI>Determine if this interactor already exists within
 *                  the database.  We determine existence of interactors
 *                  via the DaoExternalLink.lookUpByByExternalRefs() method.
 *                  This method checks all external references of the
 *                  interactor.  If a match is found, the cPath record
 *                  is returned.
 *                  </LI>
 *                  <LI>If the interactor exists, extract its cPath ID.</LI>
 *                  <LI>Else
 *                      <OL>
 *                          <LI>Add the new interactor XML Blob and all
 *                          external links to cPath.</LI>
 *                          <LI>Each new interactor will receive a new cPathId.
 *                              Update the XML Blob to contain this new
 *                              cPath Id.
 *                          </LI>
 *                      </OL>
 *                  </LI>
 *               </OL>
 *          </LI>
 *      </OL>
 * </LI>
 * <LI>Process all Interactions
 *      <UL>
 *          <LI>Modify all interaction XML fragments by replacing all interactor
 *          Refs with the matching cPath Ids. For example, in the original
 *          document, we may have (A interacts with B). After processing
 *          is complete, we have something like (cpathID:22214 interacts with
 *          cpathID:58225).  This operation is performed via the PsiUtil.
 *          updateInteractions() method.
 *          </LI>
 *          <LI>For each interaction:
 *              <OL>
 *                  <LI>Store XML fragment as a blob to main cPath table.</LI>
 *                  <LI>Add internal links that point to
 *                  interactors. These can be used as bidirectional links.
 *                  For example, given an interactor, you can determine all the
 *                  interactions it is involved in; given an interaction,
 *                  you can determine all the interactors involved.
 *          </LI>
 *      </UL>
 * </LI>
 * </OL>
 * @author Ethan Cerami
 */
public class ImportPsiToCPath {
    private PsiUtil psiUtil;
    private ImportSummary summary;

    /**
     * Contains a HashMap of Protein IDs in the original XML file
     * to cPath Ids in the current database.
     */
    private HashMap idMap;

    /**
     * Adds Specified PSI-MI Record.
     * @param xml PSI-MI XML Record.
     * @param validateExternalRefs Validates External References.
     * @return Import Summary Object.
     * @throws ImportException Indicates Error in Import.
     */
    public ImportSummary addRecord(String xml, boolean validateExternalRefs)
            throws ImportException {
        summary = new ImportSummary();
        idMap = new HashMap();
        try {
            // Steps 1-2:  Normalize PSI Document, chop into parts.
            System.out.println("Normalizing PSI Document...");
            psiUtil = new PsiUtil();
            EntrySet entrySet = null;
            entrySet = psiUtil.getNormalizedDocument(xml);
            System.out.println("Normalization Complete:  Document is valid");

            //  Validate Interactors / External References.
            if (validateExternalRefs) {
                validateInteractors(entrySet);
            }

            //  Step 3:  Process all Interactors.
            processInteractors(entrySet);

            //  Step 4:  Process all Interactions.
            processInteractions(entrySet);
            return summary;
        } catch (ValidationException e) {
            throw new ImportException("ValidationException:  "
                    + e.getMessage());
        } catch (MarshalException e) {
            throw new ImportException("MarshalException:  "
                    + e.getMessage());
        } catch (DaoException e) {
            throw new ImportException("DaoException:  "
                    + e.getMessage());
        } catch (ExternalDatabaseNotFoundException e) {
            throw new ImportException("ExternalDatabaseNotFoundException:  "
                    + e.getMessage());
        } catch (MapperException e) {
            throw new ImportException("MapperException:  "
                    + e.getMessage());
        }
    }

    /**
     * Validates all Interactors and External References.
     */
    private void validateInteractors(EntrySet entrySet) throws DaoException,
            ExternalDatabaseNotFoundException {
        System.out.print("Validating all External References:  ");
        DaoExternalLink linker = new DaoExternalLink();
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactors = entry.getInteractorList();
            for (int j = 0; j < interactors.getProteinInteractorCount(); j++) {
                System.out.print(".");
                ProteinInteractorType protein =
                        interactors.getProteinInteractor(j);
                ExternalReference[] refs = extractExternalReferences(protein);
                linker.validateExternalReferences(refs);
            }
        }
        System.out.println();
    }

    /**
     * Processes all Interactors
     */
    private void processInteractors(EntrySet entrySet)
            throws DaoException, MarshalException, ValidationException,
            MapperException {
        DaoExternalLink externalLinker = new DaoExternalLink();

        System.out.print("Processing all Interactors:  ");
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactors = entry.getInteractorList();
            for (int j = 0; j < interactors.getProteinInteractorCount(); j++) {
                System.out.print(".");
                summary.incrementNumInteractorsProcessed();
                ProteinInteractorType protein =
                        interactors.getProteinInteractor(j);
                ExternalReference[] refs = extractExternalReferences(protein);
                CPathRecord record =
                        externalLinker.lookUpByByExternalRefs(refs);
                //  Step 3.1.2 - 3.1.3
                if (record != null) {
                    idMap.put(protein.getId(), new Long(record.getId()));
                    summary.incrementNumInteractorsFound();
                } else {
                    saveInteractor(protein, refs);
                    summary.incrementNumInteractorsSaved();
                }
            }
        }
        System.out.println();
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
    private void processInteractions(EntrySet entrySet)
            throws DaoException, MarshalException, ValidationException {
        System.out.print("Processing all Interactions:  ");
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractionList interactions = entry.getInteractionList();

            //  Step 4.1:  Update all Interactor Refs to point to cPath Ids
            psiUtil.updateInteractions(interactions, idMap);

            for (int j = 0; j < interactions.getInteractionCount(); j++) {
                System.out.print(".");
                summary.incrementNumInteractionsSaved();
                InteractionElementType interaction =
                        interactions.getInteraction(j);
                saveInteraction(interaction);
            }
        }
        System.out.println();
    }

    /**
     * Saves New Interactor to Database.
     * Step 3.1.3.
     */
    private void saveInteractor(ProteinInteractorType protein,
            ExternalReference[] refs) throws MarshalException,
            ValidationException, MapperException, DaoException {
        DaoCPath cpath = new DaoCPath();

        //  Extract Important Data:  name, description, taxonomy Id.
        String xml = marshalProtein(protein);
        String name = protein.getNames().getShortLabel();

        if (name == null || name.length() == 0) {
            MapPsiToInteractions mapper = new MapPsiToInteractions(null, null);
            name = mapper.extractNameSubstitute(protein.getId(), refs);
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
    }

    /**
     * Saves New Interaction to Database.
     */
    private void saveInteraction(InteractionElementType interaction)
            throws MarshalException, ValidationException, DaoException {
        DaoCPath cpath = new DaoCPath();

        //  Extract Important Data:  name, description, taxonomy Id.
        String xml = this.marshalInteraction(interaction);
        String name = "Interaction";
        String desc = "Interaction";
        int taxId = CPathRecord.TAXONOMY_NOT_SPECIFIED;

        //  Add New Record to cPath
        long cpathId = cpath.addRecord(name, desc, taxId,
                CPathRecordType.INTERACTION, xml);

        long idList[] = psiUtil.extractInteractorIds(interaction);
        DaoInternalLink linker = new DaoInternalLink();
        linker.addRecords(cpathId, idList);
    }

    /**
     * Marshal Protein XML.
     */
    private String marshalProtein(ProteinInteractorType protein)
            throws MarshalException, ValidationException {
        StringWriter writer = new StringWriter();
        protein.marshal(writer);
        String xml = writer.toString();
        return xml;
    }

    /**
     * Marshal Interaction XML.
     */
    private String marshalInteraction(InteractionElementType interaction)
            throws MarshalException, ValidationException {
        StringWriter writer = new StringWriter();
        interaction.marshal(writer);
        String xml = writer.toString();
        return xml;
    }
}