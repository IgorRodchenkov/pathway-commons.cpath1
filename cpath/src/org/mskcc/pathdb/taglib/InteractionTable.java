/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
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
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.taglib;

import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.ProteinWithWeight;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;
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
    private EntrySet entrySet;
    private HashMap interactorMap = new HashMap();
    private ProteinInteractorType targetProtein;
    private boolean interactionDetailsShown = false;
    private Pager pager;
    private int currentIndex;

    /**
     * Sets Interaction Parameter.
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
     * Sets Protocol Request Parameter.
     *
     * @param request Protocol Request
     */
    public void setProtocolRequest(ProtocolRequest request) {
        this.protocolRequest = request;
    }

    /**
     * Sets Interactor Set
     *
     * @param interactorList Interactor List
     */
    public void setInteractorList(List interactorList) {
        if (interactorList.size() == 1) {
            ProteinWithWeight proteinWithWeight = (ProteinWithWeight)
                    interactorList.get(0);
            targetProtein = proteinWithWeight.getProtein();
        } else {
            targetProtein = null;
        }
    }

    /**
     * Start Tag Processing.
     *
     * @throws DaoException Database Access Error.
     */
    protected void subDoStartTag() throws DaoException, MapperException {
        //  Create Page Title
        createPageTitle();

        //  Output Target Interactor (optional)
        outputTargetInteractor();

        //  Output All Matching Interactions
        outputInteractions();
    }

    /**
     * Creates Page Title:  Interaction View or Protein View
     */
    private void createPageTitle() {
        String title = "Interaction View";
        if (targetProtein != null) {
            title = "Protein View";
        }
        createHeader(title);
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputInteractions() {
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
        pager = new Pager(protocolRequest, xmlAssembly.getNumHits());
        protocolRequest.setFormat(ProtocolConstants.FORMAT_HTML);
        String pagerLinks = pager.getHeaderHtml();

        protocolRequest.setFormat(ProtocolConstants.FORMAT_PSI);
        String url = protocolRequest.getUri();
        startRow();
        this.append("<td colspan=2>" + pagerLinks + "</td>");
        this.append("<td colspan=2>");
        this.append("<div class='right'>");
        this.append("<IMG SRC=\"jsp/images/xml_doc.gif\">&nbsp;");
        outputLink("View PSI-MI XML Format", url);
        this.append("</div>");
        this.append("</td>");
        this.endRow();

    }

    /**
     * Outputs Complete List of Interactions.
     */
    private void outputInteractionList() {
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
                    append("<TR><TD COLSPAN=3><BR></TD></TR>");
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
            InteractionElementType interaction) {
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
        append("<td>Record Source</td>");
        endRow();
    }

    /**
     * Outputs Protein Information.
     *
     * @param protein Protein Object.
     */
    private void outputProtein(ProteinInteractorType protein,
            InteractionElementType interaction, boolean isSelfInteracting) {
        String proteinId = protein.getId();
        String fullName = protein.getNames().getFullName();
        Organism organism = protein.getOrganism();
        startRow();
        outputProteinName(proteinId, fullName, isSelfInteracting);
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
            String url = this.getOrganismLink(taxonomyId);
            append("<TD class='cpath3'>"
                    + "<A TITLE='Get All Records for Organism:  "
                    + fullName + "' HREF='" + url + "'>"
                    + fullName + "</A></TD>");
        }
    }

    /**
     * Outputs Protein Name.
     */
    private void outputProteinName(String proteinId, String fullName,
            boolean isSelfInteracting) {
        String link = getInteractionLink(LuceneIndexer.FIELD_INTERACTOR_ID
                + ":" + proteinId, ProtocolConstants.FORMAT_HTML);
        append("<TD class='cpath3'>");
        if (targetProtein != null) {
            append(currentIndex + ".  ");
            currentIndex++;
        }
        append("<A TITLE='Link to Protein View' "
                + "HREF='" + link + "'>" + fullName + "</A>");
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
    private void outputInteractionDetails(InteractionElementType interaction) {
        ExperimentList expList = interaction.getExperimentList();
        if (targetProtein == null) {
            append("<td width='300' rowspan=2 class='cpath2'>");
        } else {
            append("<td width='300' rowspan=1 class='cpath2'>");
        }
        append("<table>");
        for (int i = 0; i < expList.getExperimentListItemCount(); i++) {
            startRow();
            ExperimentListItem expItem = expList.getExperimentListItem(i);
            ExperimentType expType = expItem.getExperimentDescription();
            outputCvType(expType.getInteractionDetection());
            outputBibRef(expType.getBibref());
            append("</tr>");
        }
        append("</table>");
        append("</td>");
        outputPrimaryRef(interaction);
    }

    private void outputPrimaryRef(InteractionElementType interaction) {
        if (targetProtein == null) {
            append("<td rowspan=2 class='cpath2'>");
        } else {
            append("<td rowspan=1 class='cpath2'>");
        }
        XrefType xref = interaction.getXref();
        if (xref != null) {
            DbReferenceType primaryRef = xref.getPrimaryRef();
            if (primaryRef != null) {
                String db = primaryRef.getDb();
                String id = primaryRef.getId();

                //  NOTE:  This code is here because DIP has annoying URL
                //  links for viewing interaction records.  It requires that
                //  you remove the last letter in the interaction ID.
                if (db.equals("DIP")) {
                    String trucatedId = id.substring(0, id.length() - 1);
                    String url = "http://dip.doe-mbi.ucla.edu/dip/DIPview."
                            + "cgi?IK=" + trucatedId;
                    append(db + ":  ");
                    append("<A HREF='" + url + "'>" + id + "</A>");
                } else {
                    append(db + ":  " + id);
                }
            }
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
                append("<TD width=150>" + names.getShortLabel() + "</TD>");
            }
        }
    }

    /**
     * Outputs Bibliographic Reference.
     * Ensures that Primary Reference and all Secondary References
     * are outputted correctly.
     */
    private void outputBibRef(BibrefType bibRef) {
        append("<TD width=150>");
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
        append("PMID:  <A TITLE='Link to PubMed Reference'"
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
        if (targetProtein != null) {
            startTable();
            outputTargetName(targetProtein.getNames());
            outputTargetOrganism(targetProtein.getOrganism());
            outputExternalReferences(targetProtein.getId());
            endTable();
        }
    }

    /**
     * Outputs Target Interactor Name.
     */
    private void outputTargetName(NamesType names) {
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
     */
    private void outputTargetOrganism(Organism organism) {
        startRow();
        this.append("<td width=30% class='cpath1'>Organism:</th>");
        NamesType names = organism.getNames();
        if (names != null) {
            String shortName = names.getShortLabel();
            String fullName = names.getFullName();
            append("<TD>");
            if (fullName != null && fullName.length() > 0) {
                append(fullName);
            } else if (shortName != null && shortName.length() > 0) {
                append(shortName);
            } else {
                append("Not Specified");
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
        startRow();
        this.append("<td class='cpath1'>External References:</th>");
        DaoExternalLink dao = new DaoExternalLink();
        ArrayList links = dao.getRecordsByCPathId(Long.parseLong(id));
        append("<TD VALIGN=TOP>");
        for (int i = 0; i < links.size(); i++) {
            ExternalLinkRecord link = (ExternalLinkRecord) links.get(i);
            ExternalDatabaseRecord db = link.getExternalDatabase();
            append("- " + db.getName() + ": ");
            if (link.getWebLink() != null) {
                outputLink(link.getLinkedToId(), link.getWebLink(),
                        "Link to:  " + db.getName());
            } else {
                append(link.getLinkedToId());
            }
            append("<BR>");
        }
        if (links.size() == 0) {
            append("No External References Specified");
        }
        append("</UL></TD>");
        endRow();
    }
}