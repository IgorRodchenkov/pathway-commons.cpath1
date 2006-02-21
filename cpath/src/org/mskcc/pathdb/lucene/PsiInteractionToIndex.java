// $Id: PsiInteractionToIndex.java,v 1.17 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.lucene;

import org.apache.lucene.document.Field;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.util.xml.XmlStripper;
import org.mskcc.pathdb.util.xml.XmlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Encapsulates a PSI-MI Interaction Record scheduled for indexing in Lucene.
 * <P>
 * Currently indexes the following content:
 * <TABLE WIDTH=100%>
 * <TR>
 * <TH ALIGN=LEFT><B>Content</B></TH>
 * <TH ALIGN=LEFT><B>Field</B></TH>
 * <TH ALIGN=LEFT><B>Notes</B></TH>
 * </TR>
 * <TR>
 * <TD>All terms after XML Stripping</TD>
 * <TD>FIELD_ALL</TD>
 * </TR>
 * <TR>
 * <TD>All interactor information, including name(s), organism, and external
 * references
 * </TD>
 * <TD>FIELD_INTERACTOR</TD>
 * </TR>
 * <TR>
 * <TD>All interactor cPath IDs</TD>
 * <TD>FIELD_INTERACTOR_ID</TD>
 * </TR>
 * <TR>
 * <TD>Interaction cPath ID</TD>
 * <TD>FIELD_CPATH_ID</TD>
 * </TR>
 * <TR>
 * <TD>Interaction:  Experiment Type</TD>
 * <TD>FIELD_EXPERIMENT_TYPE</TD>
 * </TR>
 * <TR>
 * <TD>Interaction: Pub Med IDs</TD>
 * <TD>FIELD_PMID</TD>
 * </TR>
 * <TR>
 * <TD>Interaction: Database Source</TD>
 * <TD>FIELD_DATABASE</TD>
 * </TR>
 * <TR>
 * <TD VALIGN=TOP>Organism Data</TD>
 * <TD VALIGN=TOP>FIELD_ORGANISM</TD>
 * <TD VALIGN=TOP>The "Browse by Organism" web page and the "Quick Browse"
 * web component work by automatically running queries on the FIELD_ORGANISM.
 * </TD>
 * </TR>
 * </TABLE>
 *
 * @author Ethan Cerami
 */
public class PsiInteractionToIndex implements ItemToIndex {

    /**
     * Lucene Field for Interactor Information.
     */
    public static final String FIELD_INTERACTOR = "interactor";

    /**
     * Lucene Field for Storing Pub Med ID.
     */
    public static final String FIELD_PMID = "pmid";

    /**
     * Lucene Field for Storing Interaction Type Information.
     */
    public static final String FIELD_EXPERIMENT_TYPE =
            "experiment_type";

    /**
     * Lucene Field for Storing Database Name.
     */
    public static final String FIELD_DATABASE = "database";

    /**
     * Internal List of all Fields scheduled for Indexing.
     */
    private ArrayList fields = new ArrayList();

    /**
     * Constructor.
     * Only available within the Lucene package.
     * The only way to construct the object is via the Factory class.
     *
     * @param xmlAssembly XmlAssembly.
     * @throws IOException Input Output Error.
     */
    PsiInteractionToIndex(long cpathId, XmlAssembly xmlAssembly)
            throws IOException {
        EntrySet entrySet = (EntrySet) xmlAssembly.getXmlObject();

        boolean furtherIndexing = false;
        ConfigurableIndexCollector configurableIndexCollector = 
             new ConfigurableIndexCollector();;
        
        furtherIndexing = configurableIndexCollector.enabled();
        
        //  Index All Interactors and Interactions.
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            indexInteractorData(interactorList);
            InteractionList interactionList = entry.getInteractionList();
            indexInteractionData(interactionList);
            
            if (furtherIndexing) {
                indexConfigurableFields(configurableIndexCollector, entry);
            }
        }

        //  Index All Terms -->  Default Field.
        String xml = xmlAssembly.getXmlString();
        String terms = XmlStripper.stripTags(xml, true);
        fields.add(Field.Text(LuceneConfig.FIELD_ALL, terms));

        //  Index cPath ID
        fields.add(Field.Text(LuceneConfig.FIELD_CPATH_ID,
                Long.toString(cpathId)));
    }


    /**
     * use configuration properties to collect further fields to index
     * 
     * @param entry
     */
    private void indexConfigurableFields(
            ConfigurableIndexCollector configurableIndexCollector, 
            Entry entry) {
        configurableIndexCollector.setContext(entry);

        ArrayList indexTokenList;
        String fieldName;

        // run through each configured indexable item
        while (configurableIndexCollector.next()) {
            StringBuffer extraTokens = new StringBuffer();
            indexTokenList = configurableIndexCollector.getIndexTokens();
            fieldName = configurableIndexCollector.getIndexField();

            // combine the individual tokens
            for (Iterator iter = indexTokenList.iterator(); iter.hasNext();) {
                String token = (String) iter.next();
                appendToken(extraTokens, XmlUtil.normalizeText(token));
            }

            // add them to the index
            if (extraTokens.length() > 0) {
                fields.add(Field.Text(XmlUtil.normalizeText(fieldName),
                        extraTokens.toString()));
            }
        }
    }
    
    /**
     * Gets Total Number of Fields to Index.
     *
     * @return total number of fields to index.
     */
    public int getNumFields() {
        return fields.size();
    }

    /**
     * Gets Field at specified index.
     *
     * @param index Index value.
     * @return Lucene Field Object.
     */
    public Field getField(int index) {
        return (Field) fields.get(index);
    }

    /**
     * Indexes All Interactors.
     * This includes all names, xrefs, and organism data.
     *
     * @param interactorList List of Interactors.
     */
    private void indexInteractorData(InteractorList interactorList) {
        StringBuffer interactorIdTokens = new StringBuffer();
        StringBuffer interactorTokens = new StringBuffer();
        StringBuffer organismTokens = new StringBuffer();
        int size = interactorList.getProteinInteractorCount();
        for (int i = 0; i < size; i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            appendNameTokens(protein.getNames(), interactorTokens);
            appendToken(interactorIdTokens, protein.getId());
            appendXrefTokens(protein.getXref(), interactorTokens);
            appendOrganismTokens(protein, organismTokens);
        }
        fields.add(Field.Text(FIELD_INTERACTOR, interactorTokens.toString()));
        fields.add(Field.Text(LuceneConfig.FIELD_INTERACTOR_ID,
                interactorIdTokens.toString()));
        fields.add(Field.Text(LuceneConfig.FIELD_ORGANISM,
                organismTokens.toString()));
    }

    /**
     * Indexes all Interactions.
     * This includes all pmids, interaction detection data, and database source.
     *
     * @param interactionList List of Interactions.
     */
    private void indexInteractionData(InteractionList interactionList) {
        StringBuffer pmidTokens = new StringBuffer();
        StringBuffer interactionTypeTokens = new StringBuffer();
        StringBuffer dbTokens = new StringBuffer();
        for (int i = 0; i < interactionList.getInteractionCount(); i++) {
            InteractionElementType interaction =
                    interactionList.getInteraction(i);
            ExperimentList experimentList = interaction.getExperimentList();
            if (experimentList != null) {
                appendExperimentTokens(experimentList, pmidTokens,
                        interactionTypeTokens);
            }
            appendXrefTokens(interaction.getXref(), dbTokens);
        }
        fields.add(Field.Text(FIELD_PMID, pmidTokens.toString()));
        fields.add(Field.Text(FIELD_EXPERIMENT_TYPE,
                interactionTypeTokens.toString()));
        fields.add(Field.Text(FIELD_DATABASE, dbTokens.toString()));
    }

    /**
     * Appends Experimental Data Tokens.
     */
    private void appendExperimentTokens(ExperimentList experimentList,
            StringBuffer pmidTokens, StringBuffer interactionTypeTokens) {
        for (int i = 0; i < experimentList.getExperimentListItemCount(); i++) {
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
    private void appendOrganismTokens(ProteinInteractorType protein,
            StringBuffer tokens) {
        Organism organism = protein.getOrganism();
        if (organism != null) {
            int ncbiTaxId = organism.getNcbiTaxId();
            appendToken(tokens, Integer.toString(ncbiTaxId));
            appendNameTokens(organism.getNames(), tokens);
        }
    }

    /**
     * Appends Name Tokens.
     */
    private void appendNameTokens(NamesType names, StringBuffer tokens) {
        if (names != null) {
            String shortName = names.getShortLabel();
            String fullName = names.getFullName();
            appendToken(tokens, XmlUtil.normalizeText(shortName));
            appendToken(tokens, XmlUtil.normalizeText(fullName));
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
    private void appendXrefTokens(XrefType xref, StringBuffer tokens) {
        if (xref != null) {
            DbReferenceType primaryRef = xref.getPrimaryRef();
            appendDbRefTokens(tokens, primaryRef);
            for (int i = 0; i < xref.getSecondaryRefCount(); i++) {
                DbReferenceType secondaryRef = xref.getSecondaryRef(i);
                appendDbRefTokens(tokens, secondaryRef);
            }
        }
    }

    /**
     * Appends DbRef Tokens.
     */
    private String appendDbRefTokens(StringBuffer tokens,
            DbReferenceType dbRef) {
        if (dbRef != null) {
            appendToken(tokens, dbRef.getDb());
            appendToken(tokens, dbRef.getId());
        }
        return tokens.toString();
    }

    /**
     * Appends New Token to List.
     */
    private void appendToken(StringBuffer tokens, String token) {
        if (token != null && token.length() > 0) {
            tokens.append(token + " ");
        }
    }
}
