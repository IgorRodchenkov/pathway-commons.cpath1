package org.mskcc.pathdb.lucene;

import org.apache.lucene.document.Field;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.util.XmlStripper;

import java.util.ArrayList;
import java.io.IOException;

/**
 * Encapsulates a PSI-MI Interaction Record scheduled for indexing in Lucene.
 * <P>
 * Indexes the following fields:
 * <UL>
 * <LI>Database Source Name
 * <LI>Experiment PMID
 * <LI>Experiment Interaction Type Name
 * <LI>Experiment Interaction Type Controlled Vocabulary ID.
 * </UL>
 * @author  Ethan Cerami
 */
public class PsiInteractionToIndex implements ItemToIndex {

    /**
     * Lucene Field for Interactor Information.
     */
    public static final String FIELD_INTERACTOR = "interactor";

    /**
     * Lucene Field for Organism Information.
     */
    public static final String FIELD_ORGANISM = "organism";

    /**
     * Lucene Field for Storing Pub Med ID.
     */
    public static final String FIELD_PMID = "pmid";

    /**
     * Lucene Field for Storing Interaction Type Information.
     */
    public static final String FIELD_INTERACTION_TYPE =
            "interaction_type";

    /**
     * Lucene Field for Storing Database Name.
     */
    public static final String FIELD_DATABASE = "database";

    private ArrayList fields = new ArrayList();

    /**
     * Constructor.  Only available within the lucene package.
     * The only way to construct the object is via the Factory class.
     * @param xmlAssembly XmlAssembly.
     * @throws IOException Input Output Error.
     */
    PsiInteractionToIndex (long cpathId, XmlAssembly xmlAssembly)
            throws IOException {
        EntrySet entrySet = (EntrySet) xmlAssembly.getXmlObject();

        //  Index All Interactors and Interactions.
        for (int i=0; i< entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            indexInteractorData(interactorList);
            InteractionList interactionList = entry.getInteractionList();
            indexInteractionData (interactionList);
        }

        //  Index All Terms -->  Default Field.
        String xml = xmlAssembly.getXmlString();
        String terms = XmlStripper.stripTags(xml);
        fields.add(Field.Text(LuceneIndexer.FIELD_ALL, terms));

        //  Store (but, don't index) cPath ID
        fields.add(Field.UnIndexed(LuceneIndexer.FIELD_CPATH_ID,
                Long.toString(cpathId)));
    }

    /**
     * Gets Total Number of Fields to Index.
     * @return total number of fields to index.
     */
    public int getNumFields() {
        return fields.size();
    }

    /**
     * Gets Field at specified index.
     * @param index Index value.
     * @return Lucene Field Object.
     */
    public Field getField(int index) {
        return (Field) fields.get(index);
    }

    /**
     * Indexes All Interactors.
     * This includes all names, xrefs, and organism data.
     * @param interactorList List of Interactors.
     */
    private void indexInteractorData (InteractorList interactorList) {
        StringBuffer interactorTokens = new StringBuffer();
        StringBuffer organismTokens = new StringBuffer();
        for (int i=0; i<interactorList.getProteinInteractorCount(); i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            appendNameTokens(protein.getNames(), interactorTokens);
            appendXrefTokens(protein.getXref(), interactorTokens);
            appendOrganismTokens(protein, organismTokens);
        }
        fields.add(Field.Text(FIELD_INTERACTOR, 
                interactorTokens.toString()));
        fields.add(Field.Text(FIELD_ORGANISM, 
                organismTokens.toString()));
    }

    /**
     * Indexes all Interactions.
     * This includes all pmids, and interaction detection data.
     * @param interactionList List of Interactions.
     */
    private void indexInteractionData (InteractionList interactionList) {
        StringBuffer pmidTokens = new StringBuffer();
        StringBuffer interactionTypeTokens = new StringBuffer();
        StringBuffer dbTokens = new StringBuffer();
        for (int i=0; i<interactionList.getInteractionCount(); i++) {
            InteractionElementType interaction =
                    interactionList.getInteraction(i);
            ExperimentList experimentList = interaction.getExperimentList();
            if (experimentList != null) {
                appendExperimentTokens (experimentList, pmidTokens,
                        interactionTypeTokens);
            }
            appendXrefTokens(interaction.getXref(), dbTokens);
        }
        fields.add(Field.Text(FIELD_PMID, pmidTokens.toString()));
        fields.add(Field.Text(FIELD_INTERACTION_TYPE,
                interactionTypeTokens.toString()));
        fields.add(Field.Text(FIELD_DATABASE, dbTokens.toString()));
    }

    /**
     * Appends Experimental Data Tokens.
     */
    private void appendExperimentTokens (ExperimentList experimentList,
            StringBuffer pmidTokens, StringBuffer interactionTypeTokens) {
        for (int i=0; i<experimentList.getExperimentListItemCount(); i++) {
            ExperimentListItem expItem =
                    experimentList.getExperimentListItem(i);
            if (expItem != null) {
                ExperimentType expType = expItem.getExperimentDescription();
                if (expType != null) {
                    BibrefType bibRef = expType.getBibref();
                    if (bibRef != null) {
                        XrefType xref = bibRef.getXref();
                        appendXrefTokens(xref, pmidTokens);
                    }
                    CvType cvType = expType.getInteractionDetection();
                    appendCvTypeTokens(cvType, interactionTypeTokens);
                }
            }
        }
    }

    /**
     * Appends Organism Tokens.
     */
    private void appendOrganismTokens (ProteinInteractorType protein,
            StringBuffer tokens) {
        Organism organism = protein.getOrganism();
        if (organism != null) {
            int ncbi_tax_id = organism.getNcbiTaxId();
            appendToken (tokens, Integer.toString(ncbi_tax_id));
            appendNameTokens(organism.getNames(), tokens);
        }
    }

    /**
     * Appends Name Tokens.
     */
    private void appendNameTokens(NamesType names, StringBuffer tokens) {
         if (names != null) {
            appendToken (tokens, names.getShortLabel());
            appendToken (tokens, names.getFullName());
        }
    }

    /**
     * Appends CV Tokens.
     */
    private void appendCvTypeTokens(CvType cvType,
            StringBuffer tokens) {
        if (cvType != null) {
            this.appendNameTokens(cvType.getNames(), tokens);
            this.appendXrefTokens(cvType.getXref(), tokens);
        }
    }

    /**
     * Appends Xref Tokens.
     */
    private void appendXrefTokens (XrefType xref, StringBuffer tokens) {
        if (xref != null) {
            DbReferenceType primaryRef = xref.getPrimaryRef();
            appendDbRefTokens (tokens, primaryRef);
            for (int i=0; i<xref.getSecondaryRefCount(); i++) {
                DbReferenceType secondaryRef = xref.getSecondaryRef(i);
                appendDbRefTokens (tokens, secondaryRef);
            }
        }
    }

    /**
     * Appends DbRef Tokens.
     */
    private String appendDbRefTokens (StringBuffer tokens,
            DbReferenceType dbRef) {
        if (dbRef != null) {
            appendToken (tokens, dbRef.getDb());
            appendToken (tokens, dbRef.getId());
        }
        return tokens.toString();
    }

    /**
     * Appends New Token to List.
     */
    private void appendToken (StringBuffer tokens, String token) {
        if (token != null && token.length() > 0) {
            tokens.append(token+" ");
        }
    }
}