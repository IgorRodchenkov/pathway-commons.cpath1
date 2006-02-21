// $Id: InteractionTable.java,v 1.56 2006-02-21 22:51:10 grossb Exp $
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
package org.mskcc.pathdb.taglib;

import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.ProteinWithWeight;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Custom JSP Tag for Displaying Interactions.
 *
 * @author Ethan Cerami
 */
public class InteractionTable extends HtmlTable {
    private ProtocolRequest protocolRequest;
    private XmlAssembly xmlAssembly;
    private List interactorList;
    private EntrySet entrySet;
    private HashMap interactorMap = new HashMap();
    private ProteinInteractorType targetProtein;
    private boolean interactionDetailsShown = false;
    private Pager pager;
    private int currentIndex;

    /**
     * Sets the XML Assembly Parameter.
     *
     * @param xmlAssembly XmlAssembly Object
     */
    public void setXmlAssembly(XmlAssembly xmlAssembly) {
        this.xmlAssembly = xmlAssembly;
        if (xmlAssembly != null && !xmlAssembly.isEmpty()) {
            entrySet = (EntrySet) xmlAssembly.getXmlObject();
        }
    }

    /**
     * Sets the Protocol Request Parameter.
     *
     * @param request Protocol Request
     */
    public void setProtocolRequest(ProtocolRequest request) {
        this.protocolRequest = request;
    }

    /**
     * Sets the InteractorList.
     * This represents a list of matching interactors which match the user's
     * original query.
     *
     * @param interactorList Interactor List
     */
    public void setInteractorList(List interactorList) {
        this.interactorList = interactorList;
    }

    /**
     * Resets all Variables back to null.
     */
    public void release() {
        super.release();
        protocolRequest = null;
        xmlAssembly = null;
        interactorList = null;
        entrySet = null;
        interactorMap = null;
        targetProtein = null;
        interactionDetailsShown = false;
        pager = null;
        currentIndex = 0;
    }

    /**
     * Start Tag Processing.
     *
     * @throws DaoException Database Access Error.
     */
    protected void subDoStartTag() throws DaoException, MapperException {
        //  Determine Protein or Interaction View
        determineView();

        //  Create Page Header / Title
        String title = getPageTitle();
        createHeader(title);

        //  Output Target Interactor (optional)
        if (targetProtein != null) {
            outputTargetInteractor();
        }

        //  Output All Matching Interactions
        outputInteractions();
    }

    /**
     * Determine if this is a protein view or an interaction view.
     */
    private void determineView() {
        if (protocolRequest.getQuery() != null
                && protocolRequest.getQuery().indexOf
                (LuceneConfig.FIELD_INTERACTOR_ID) >= 0) {
            ProteinWithWeight proteinWithWeight = (ProteinWithWeight)
                    interactorList.get(0);
            targetProtein = proteinWithWeight.getProtein();
        } else {
            targetProtein = null;
        }
    }

    /**
     * Gets Page Title:  Interaction View or Protein View
     */
    private String getPageTitle() {
        String title = null;
        if (targetProtein == null) {
            title = "Interaction View";
        } else {
            title = "Protein View";
        }
        return title;
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputInteractions() throws DaoException {
        startTable();
        if (xmlAssembly.isEmpty()) {
            noMatchesFound();
        } else {
            outputNumInteractions();
            outputInteractionList();
        }
        endTable();
    }

    /**
     * Outputs Number of Matching Interactions and PSI-MI Link.
     */
    private void outputNumInteractions() {
        protocolRequest.setFormat(ProtocolConstants.FORMAT_XML);
        String psiUrl = protocolRequest.getUri();

        pager = new Pager(protocolRequest, xmlAssembly.getNumHits());
        protocolRequest.setFormat(ProtocolConstants.FORMAT_HTML);
        String pagerLinks = pager.getHeaderHtml();

        startRow();
        this.append("<td colspan=3>" + pagerLinks + "</td>");
        this.append("<td colspan=1 align=right>");
        this.append("<IMG SRC=\"jsp/images/xml_doc.gif\">&nbsp;");
        outputLink("View PSI-MI XML Format", psiUrl,
                "View Data in PSI-MI XML Format");
        this.append("</td>");
        this.endRow();

    }

    /**
     * Outputs Complete List of Interactions.
     */
    private void outputInteractionList() throws DaoException {
        currentIndex = pager.getStartIndex() + 1;
        //  Iterate through all Entries
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            extractInteractors(interactorList);
            InteractionList interactionList = entry.getInteractionList();
            for (int j = 0; j < interactionList.getInteractionCount(); j++) {
                if (targetProtein == null
                        || (targetProtein != null && j == 0)) {
                    outputInteractionHeaders(j);
                }
                InteractionElementType interaction =
                        interactionList.getInteraction(j);
                ParticipantList pList = interaction.getParticipantList();
                outputInteractorList(pList, interaction);
                interactionDetailsShown = false;
                if (targetProtein == null
                        || (targetProtein != null
                        && j == interactionList.getInteractionCount() - 1)) {
                    append("<TR><TD COLSPAN=4><BR></TD></TR>");
                }
            }
        }
    }

    /**
     * Outputs All Interactors in List.
     *
     * @param pList       Interactor List.
     * @param interaction Interaction Object.
     */
    private void outputInteractorList(ParticipantList pList,
            InteractionElementType interaction) throws DaoException {
        int matches = 0;
        boolean isSelfInteracting = false;
        for (int i = 0; i < pList.getProteinParticipantCount(); i++) {
            ProteinParticipantType pType =
                    pList.getProteinParticipant(i);
            ProteinParticipantTypeChoice choice =
                    pType.getProteinParticipantTypeChoice();
            RefType ref = choice.getProteinInteractorRef();
            String refId = ref.getRef();
            ProteinInteractorType protein = (ProteinInteractorType)
                    interactorMap.get(refId);
            boolean localMatch = false;
            if (targetProtein != null
                    && targetProtein.getId().equals(protein.getId())) {
                localMatch = true;
                matches++;
            }
            if (matches > 1) {
                isSelfInteracting = true;
            }
            if (!localMatch || isSelfInteracting) {
                outputProtein(protein, interaction, isSelfInteracting);
            }
        }
    }

    /**
     * Outputs No Matching Interactions Found.
     */
    private void noMatchesFound() {
        append("<tr class='a'>");
        append("<td colspan=4>No Matching Interactions Found.  "
                + "Please try again.</td>");
        endRow();
    }

    /**
     * Outputs Interaction Headers.
     */
    private void outputInteractionHeaders(int index) {
        startRow();
        if (targetProtein == null) {
            append("<th colspan=4>" + currentIndex + ". Interaction</th>");
            currentIndex++;
        } else {
            append("<th colspan=4>  This Protein interacts with the "
                    + "following other proteins:</th>");
        }
        endRow();
        append("<TR class='b'>");
        append("<td>Interactor</td>");
        append("<td>Organism</td>");
        append("<td>Experimental Evidence</td>");
        append("<td>Database Source</td>");
        endRow();
    }

    /**
     * Outputs Protein Information.
     *
     * @param protein Protein Object.
     */
    private void outputProtein(ProteinInteractorType protein,
            InteractionElementType interaction, boolean isSelfInteracting)
            throws DaoException {
        String proteinId = protein.getId();
        Organism organism = protein.getOrganism();
        startRow();
        outputInteractorName(proteinId, protein.getNames(), isSelfInteracting);
        outputOrganism(organism);
        if (!interactionDetailsShown) {
            outputInteractionDetails(interaction);
            interactionDetailsShown = true;
        }
        endRow();
    }

    /**
     * Outputs Organism Information.
     */
    private void outputOrganism(Organism organism) {
        String fullName;
        if (organism != null) {
            NamesType names = organism.getNames();
            int taxonomyId = organism.getNcbiTaxId();
            fullName = names.getFullName();
            if (fullName == null) {
                fullName = names.getShortLabel();
            }
            String url = this.getOrganismLink(taxonomyId);
            append("<TD class='cpath3'>"
                    + "<A TITLE='View All Records for Organism:  "
                    + fullName + "' HREF='" + url + "'>"
                    + fullName + "</A></TD>");
        } else {
            append("<TD class='cpath3'>Not Specified</TD>");
        }
    }

    /**
     * Outputs Interactor Name.
     */
    private void outputInteractorName(String proteinId, NamesType name,
            boolean isSelfInteracting) {
        String link = getInteractionLink(LuceneConfig.FIELD_INTERACTOR_ID
                + ":" + proteinId, ProtocolConstants.FORMAT_HTML);
        append("<TD class='cpath3'>");
        if (targetProtein != null) {
            append(currentIndex + ".  ");
            currentIndex++;
        }
        String label = TagUtil.getLabel(name);
        append("<A TITLE='View Protein Details' "
                + "HREF='" + link + "'>" + label + "</A>");
        if (isSelfInteracting) {
            append("[Self Interacting]");
        }
        append("</TD>");
    }

    /**
     * Outputs Experiment Information.
     *
     * @param interaction Interaction Object.
     */
    private void outputInteractionDetails(InteractionElementType interaction)
            throws DaoException {
        ExperimentList expList = interaction.getExperimentList();
        int count = interaction.getParticipantList().
                getProteinParticipantCount();
        if (targetProtein == null) {

            append("<td rowspan='" + count + "' class='cpath2'>");
        } else {
            append("<td rowspan=1 class='cpath2'>");
        }
        append("<table>");
        for (int i = 0; i < expList.getExperimentListItemCount(); i++) {
            startRow();
            ExperimentListItem expItem = expList.getExperimentListItem(i);
            ExperimentType expType = expItem.getExperimentDescription();
            if (expType != null) {
                outputCvType(expType.getInteractionDetection());
                outputBibRef(expType.getBibref());
            }
            append("</tr>");
        }
        append("</table>");
        append("</td>");
        outputPrimaryRef(interaction);
    }

    private void outputPrimaryRef(InteractionElementType interaction)
            throws DaoException {
        int count = interaction.getParticipantList().
                getProteinParticipantCount();
        if (targetProtein == null) {
            append("<td rowspan='" + count + "' class='cpath2'>");
        } else {
            append("<td rowspan=1 class='cpath2'>");
        }
        XrefType xref = interaction.getXref();
        if (xref != null) {
            DbReferenceType primaryRef = xref.getPrimaryRef();
            if (primaryRef != null) {
                String db = primaryRef.getDb();
                String id = primaryRef.getId();

                //  NOTE:  The DIP code is here because DIP has annoying URL
                //  links for viewing interaction records.  It requires that
                //  you remove the last letter in the interaction ID.
                //  Note also that DIP URLs for Interactions are distinct
                //  from DIP URLs for Interactors.
                if (db.equals("DIP")) {
                    String trucatedId = id.substring(0, id.length() - 1);
                    String url = "http://dip.doe-mbi.ucla.edu/dip/DIPview."
                            + "cgi?IK=" + trucatedId;
                    append(db + ":  ");
                    append("<A TITLE='External Link to: DIP' "
                            + "HREF='" + url + "'>" + id + "</A>");
                } else {
                    DaoExternalDb daoExternalDb = new DaoExternalDb();
                    ExternalDatabaseRecord dbRecord =
                            daoExternalDb.getRecordByTerm(db);
                    if (dbRecord != null) {
                        String url = dbRecord.getUrlWithId(id);
                        if (url != null) {
                            append(db + ":  ");
                            append("<A TITLE='External Link to: ' " + db
                                    + "' HREF='" + url + "'>" + id + "</A>");
                        }
                    } else {
                        append(db + ":  " + id);
                    }
                }
            }
        } else {
            append("Source Information Not Available");
        }
        append("</td>");
    }

    /**
     * Outputs CV Type.
     */
    private void outputCvType(CvType cvType) {
        if (cvType != null) {
            NamesType names = cvType.getNames();
            if (names != null) {
                append("<TD>" + names.getShortLabel() + "</TD>");
            }
        }
    }

    /**
     * Outputs Bibliographic Reference.
     * Ensures that Primary Reference and all Secondary References
     * are outputted correctly.
     */
    private void outputBibRef(BibrefType bibRef) {
        append("<TD>");
        if (bibRef != null) {
            XrefType xref = bibRef.getXref();
            DbReferenceType primaryRef = xref.getPrimaryRef();
            if (primaryRef != null) {
                outputPmid(primaryRef);
            }
            for (int i = 0; i < xref.getSecondaryRefCount(); i++) {
                DbReferenceType secondaryRef = xref.getSecondaryRef(i);
                append("<BR>");
                outputPmid(secondaryRef);
            }
        }
        append("</TD>");
    }

    private void outputPmid(DbReferenceType primaryRef) {
        String pmid = primaryRef.getId();
        String url = this.getPubMedLink(pmid);
        append("PMID:  <A TITLE='External Link to PubMed Reference'"
                + " HREF='" + url + "'>" + pmid + "</A>");
    }

    /**
     * Extracts All Interactors and Places in Global HashMap.
     *
     * @param interactorList List of Interactors
     */
    private void extractInteractors(InteractorList interactorList) {
        for (int i = 0; i < interactorList.getProteinInteractorCount(); i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            String id = protein.getId();
            interactorMap.put(id, protein);
        }
    }

    /**
     * Gets PubMedLink.
     *
     * @param pmid PMID.
     * @return URL to PubMed.
     */
    private String getPubMedLink(String pmid) {
        String url = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?"
                + "cmd=Retrieve&db=PubMed&list_uids=" + pmid + "&dopt=Abstract";
        return url;
    }

    /**
     * Outputs Target Interactors
     */
    private void outputTargetInteractor() throws DaoException {
        startTable();
        outputTargetNames(targetProtein.getNames());
        outputTargetOrganism(targetProtein.getOrganism());
        outputExternalReferences(targetProtein.getId());
        endTable();
    }

    /**
     * Outputs Target Interactor Names.
     */
    private void outputTargetNames(NamesType names) {
        startRow();
        String shortLabel = names.getShortLabel();
        if (shortLabel != null && shortLabel.length() > 0) {
            this.append("<td class='cpath1'>Short Name:</th>");
            outputDataField(shortLabel);
        }
        endRow();
        startRow();
        String fullName = names.getFullName();
        if (fullName == null || fullName.length() == 0) {
            fullName = "Not Specified";
        }
        this.append("<td class='cpath1'>Full Name / Description:</th>");
        outputDataField(fullName);
        endRow();
    }

    /**
     * Outputs Target Organism.
     * Make sure to check for null organism (see bug report # 0000469).
     */
    private void outputTargetOrganism(Organism organism) {
        startRow();
        append("<td class='cpath1'>Organism:</th>");
        append("<TD>");
        if (organism != null && organism.getNames() != null) {
            NamesType names = organism.getNames();
            if (names != null) {
                String shortName = names.getShortLabel();
                String fullName = names.getFullName();
                if (fullName != null && fullName.length() > 0) {
                    append(fullName);
                } else if (shortName != null && shortName.length() > 0) {
                    append(shortName);
                } else {
                    append("Not Specified");
                }
            }
        } else {
            append("Not Specified");
        }
        append("</TD>");
        endRow();
    }

    /**
     * Outputs External References.
     */
    private void outputExternalReferences(String id) throws DaoException {
        ArrayList affyList = new ArrayList();
        startRow();
        this.append("<td class='cpath1'>External References:</th>");
        DaoExternalLink dao = DaoExternalLink.getInstance();
        ArrayList links = dao.getRecordsByCPathId(Long.parseLong(id));
        Collections.sort(links);
        append("<TD VALIGN=TOP>");
        for (int i = 0; i < links.size(); i++) {
            ExternalLinkRecord link = (ExternalLinkRecord) links.get(i);
            ExternalDatabaseRecord db = link.getExternalDatabase();
            if (db.getMasterTerm().equalsIgnoreCase("Affymetrix")) {
                affyList.add(link);
            } else {
                append("- " + db.getName() + ": ");
                if (link.getWebLink() != null) {
                    outputLink(link.getLinkedToId(), link.getWebLink(),
                            "External Link to:  " + db.getName());
                } else {
                    append(link.getLinkedToId());
                }
                append("<BR>");
            }
        }
        if (links.size() == 0) {
            append("No External References Specified");
        }
        append("</UL></TD>");
        endRow();

        if (affyList.size() > 0) {
            startRow();
            append("<td class='cpath1'>Affymetrix IDs:</th>");
            append("<td>");
            append("<DIV id='showAffy' class='show'>"
                    + "[<A href='#' onClick=\"changeStyle('affy', 'show'); "
                    + "changeStyle('showAffy', 'hide'); "
                    + "changeStyle('hideAffy', 'show'); "
                    + "return false;\">"
                    + "Show Affymetrix IDs</A>]</DIV>");
            append("<DIV id='hideAffy' class='hide'>"
                    + "[<A href='#' onClick=\"changeStyle('affy', 'hide'); "
                    + "changeStyle('showAffy', 'show'); "
                    + "changeStyle('hideAffy', 'hide'); "
                    + "return false;\">Hide Affymetrix IDs</A>]</DIV>");
            append("<DIV id='affy' class='hide'>");
            for (int i = 0; i < affyList.size(); i++) {
                ExternalLinkRecord link = (ExternalLinkRecord) affyList.get(i);
                append("- " + link.getLinkedToId() + "<BR>");
            }
            append("</DIV>");
            append("</TD>");
            endRow();
        }
    }
}
