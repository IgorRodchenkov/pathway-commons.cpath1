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

import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;

import java.util.ArrayList;
import java.util.HashMap;

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

    /**
     * Sets Interaction Parameter.
     * @param xmlAssembly XmlAssembly Object
     */
    public void setXmlAssembly(XmlAssembly xmlAssembly) {
        this.xmlAssembly = xmlAssembly;
        if (!xmlAssembly.isEmpty()) {
            entrySet = (EntrySet) xmlAssembly.getXmlObject();
        }
    }

    /**
     * Sets Protocol Request Parameter.
     * @param request Protocol Request
     */
    public void setProtocolRequest(ProtocolRequest request) {
        this.protocolRequest = request;
    }

    /**
     * Start Tag Processing.
     * @throws DaoException Database Access Error.
     */
    protected void subDoStartTag() throws DaoException, MapperException {
        protocolRequest.setFormat(ProtocolConstants.FORMAT_PSI);
        String url = protocolRequest.getUri();
        String title = "Matching Interactions";

        createHeader(title);
        startTable();
        outputNumInteractions(url);
        outputInteractions();
        endTable();
    }

    private void outputNumInteractions(String url) {
        if (!xmlAssembly.isEmpty()) {
            this.startRow(1);
            this.append("<td colspan='3'>Total Number of Matches:  "
                    + xmlAssembly.getNumHits());
            this.append("</td>");
            this.append("<td colspan=4>");
            this.append("<div class='right'>");
            this.append("<IMG SRC=\"jsp/images/xml_doc.gif\">&nbsp;");
            outputLink("View PSI-MI XML Format", url);
            this.append("</div>");
            this.append("</td>");
            this.endRow();
            append("<TR><TD COLSPAN=6><BR></TD></TR>");
        }
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputInteractions() throws DaoException {
        if (xmlAssembly.isEmpty()) {
            append("<tr class='a'>");
            append("<td colspan=4>No Matching Interactions Found.  "
                    + "Please try again.</td>");
            append("</tr>");
        } else {
            for (int i=0; i<entrySet.getEntryCount(); i++) {
                Entry entry = entrySet.getEntry(i);
                InteractorList interactorList = entry.getInteractorList();
                extractInteractors (interactorList);
                InteractionList interactionList = entry.getInteractionList();
                for (int j=0; j<interactionList.getInteractionCount(); j++) {
                    outputInteractionHeaders();
                    InteractionElementType interaction =
                            interactionList.getInteraction(j);
                    ParticipantList pList = interaction.getParticipantList();
                    for (int k=0; k<pList.getProteinParticipantCount(); k++) {
                        ProteinParticipantType pType =
                                pList.getProteinParticipant(k);
                        ProteinParticipantTypeChoice choice =
                                pType.getProteinParticipantTypeChoice();
                        RefType ref = choice.getProteinInteractorRef();
                        String refId = ref.getRef();
                        ProteinInteractorType protein = (ProteinInteractorType)
                                interactorMap.get(refId);
                        outputProtein(k, protein);
                    }
                    outputExpAndRefs (interaction);
                    append("<TR class='functnbar3'><TD COLSPAN=6></TD></TR>");
                    append("<TR><TD COLSPAN=4><BR></TD></TR>");
                }
            }
        }
    }

    private void outputInteractionHeaders() {
        append("<tr class='tabs'>");
        append("<th colspan=6>Interaction</th>");
        append("</tr>");
        append("<TR class='b'>");
        append("<td>Interactor</td>");
        append("<td>Label</td>");
        append("<td>Description</td>");
        append("<td>Organism</td>");
        append("</TR>");
    }

    /**
     * Outputs Protein Information.
     * @param index Index Number.
     * @param protein Protein Object.
     */
    private void outputProtein(int index, ProteinInteractorType protein) {
        String shortLabel = protein.getNames().getShortLabel();
        String fullName = protein.getNames().getFullName();
        Organism organism = protein.getOrganism();
        char indexChar = (char) (index + 65);
        append("<TR>");
        append("<TD>"+indexChar+"</TD>");
        append("<TD>"+shortLabel+"</TD>");
        append("<TD>"+fullName+"</TD>");
        if (organism != null) {
            NamesType names = organism.getNames();
            int taxonomy_id = organism.getNcbiTaxId();
            fullName = names.getFullName();
            String url = this.getInteractionLink("organism:"+taxonomy_id,
                    ProtocolConstants.FORMAT_HTML);
            append ("<TD><A TITLE='Get All Records for Organism:  " +
                    fullName+"' HREF='"+url+"'>"+fullName+"</A></TD>");
        }
        append("</TR>");
    }

    /**
     * Outputs Experiment Information.
     * @param interaction Interaction Object.
     */
    private void outputExpAndRefs (InteractionElementType interaction) {
        append("<tr class='b'>");
        append("<td colspan=1>Experiment</td>");
        append("<td colspan=2>Interaction Type</td>");
        append("<td colspan=1>Reference</td>");
        append("</tr>");
        ExperimentList expList = interaction.getExperimentList();
        for (int i=0; i<expList.getExperimentListItemCount(); i++) {
            append("<tr>");
             append ("<td>&nbsp;&nbsp;"+(i+1)+".</td>");
            ExperimentListItem expItem = expList.getExperimentListItem(i);
            ExperimentType expType = expItem.getExperimentDescription();
            CvType interactionType = expType.getInteractionDetection();
            if (interactionType != null) {
                NamesType names = interactionType.getNames();
                if (names != null) {
                    append("<TD colspan=2>"+names.getShortLabel()+"</TD>");
                }
            }
            BibrefType bibRef = expType.getBibref();
            if (bibRef != null) {
                XrefType xref = bibRef.getXref();
                DbReferenceType primaryRef = xref.getPrimaryRef();
                if (primaryRef != null) {
                    String pmid = primaryRef.getId();
                    String url = this.getPubMedLink(pmid);
                    append ("<TD><A TITLE='View Reference at PubMed'"
                            + " HREF='"+url+"'>"+pmid+"</A></TD>");
                }
            }
            append("</tr>");
        }
    }

    /**
     * Extracts All Interactors and Places in Global HashMap.
     * @param interactorList List of Interactors
     */
    private void extractInteractors (InteractorList interactorList) {
        for (int i=0; i<interactorList.getProteinInteractorCount(); i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            String id = protein.getId();
            interactorMap.put (id, protein);
        }
    }

    /**
     * Gets PubMedLink.
     * @param pmid PMID.
     * @return URL to PubMed.
     */
    private String getPubMedLink(String pmid) {
        String url = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?"
                + "cmd=Retrieve&db=PubMed&list_uids=" + pmid + "&dopt=Abstract";
        return url;
    }
}