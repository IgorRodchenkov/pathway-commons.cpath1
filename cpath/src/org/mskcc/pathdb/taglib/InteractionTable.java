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
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Interaction Table.
 *
 * @author Ethan Cerami
 */
public class InteractionTable extends HtmlTable {
    private ProtocolRequest protocolRequest;
    private ArrayList interactions;

    /**
     * Sets Interaction Parameter.
     * @param interactions ArrayList of Interaction objects.
     */
    public void setInteractions(ArrayList interactions) {
        this.interactions = interactions;
    }

    /**
     * Sets UID Parameter.
     * @param request Protocol Request
     */
    public void setProtocolRequest(ProtocolRequest request) {
        this.protocolRequest = request;
    }

    /**
     * Start Tag Processing.
     * @throws DaoException Database Access Error.
     */
    protected void subDoStartTag() throws DaoException {
        String title = "Matching Interactions";

        append("<table width=100% cellpadding=7 cellspacing=0>"
                + "<tr><td colspan=2 bgcolor=#666699><u>"
                + "<b><big>" + title + "</big>"
                + "</b></u><br></td>");
        append("<td colspan=2>");
        protocolRequest.setFormat(ProtocolConstants.FORMAT_PSI);
        String url = protocolRequest.getUri();
        if (interactions.size() > 0) {
            append("<IMG SRC=\"jsp/images/xml_doc.gif\">&nbsp;");
            outputLink("View PSI-MI XML Format", url);
        }

        append("</td>");
        append("</tr>");
        String headers[] = {
            "Interactor", "Interactor", "Experimental System",
            "PubMed Reference"};

        createTableHeaders(headers);
        outputInteractions();
        endTable();
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputInteractions() throws DaoException {
        if (interactions.size() == 0) {
            append("<TR>");
            append("<TD COLSPAN=4>No Matching Interactions Found.  "
                    + "Please try again.</TD>");
            append("</TR>");
        }
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            ArrayList interactors = interaction.getInteractors();
            append("<TR>");
            Interactor interactor = (Interactor) interactors.get(0);
//            String url = getInteractionLink(interactor0.getName(),
//                    ProtocolConstants.FORMAT_HTML);
            outputInteractor(interactor);

            interactor = (Interactor) interactors.get(1);
            outputInteractor(interactor);

            String expSystem = (String) interaction.getAttribute
                    (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
            outputDataField(expSystem);
            String pmid = (String) interaction.getAttribute
                    (InteractionVocab.PUB_MED_ID);
            String url = getPubMedLink(pmid);
            outputDataField(pmid, url);
            append("</TR>");
            append("<TR><TD COLSPAN=4><HR></TD></TR>");
        }
    }

    private void outputInteractor(Interactor interactor) {
        if (interactor != null) {
            String name = interactor.getName();
            String desc = (String) interactor.getAttribute
                    (InteractorVocab.FULL_NAME);
            String org = (String) interactor.getAttribute
                    (InteractorVocab.ORGANISM_SPECIES_NAME);
            if (org == null) {
                org = (String) interactor.getAttribute
                        (InteractorVocab.ORGANISM_COMMON_NAME);
            }
            StringBuffer interactorHtml = new StringBuffer();
            interactorHtml.append(name + "<BR><UL>");
            if (desc != null) {
                interactorHtml.append("<LI>"+desc);
            }
            if (org != null) {
                interactorHtml.append("<LI>Organism:  " + org);
            }
            interactorHtml.append("</UL>");
            this.outputDataField(interactorHtml.toString());
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

    /**
     * Outputs External References.
     */
    private void outputExternalReferences(Interactor interactor)
            throws DaoException {
        DaoExternalLink dao = new DaoExternalLink();
        String id = (String) interactor.getAttribute(InteractorVocab.LOCAL_ID);
        if (id != null) {
            ArrayList links = dao.getRecordsByCPathId(Integer.parseInt(id));
            append("<TD VALIGN=TOP><UL>");
            for (int i = 0; i < links.size(); i++) {
                ExternalLinkRecord link = (ExternalLinkRecord) links.get(i);
                ExternalDatabaseRecord db = link.getExternalDatabase();
                append("<LI>" + db.getName() + ": ");
                outputLink(link.getLinkedToId(), link.getWebLink(),
                        db.getDescription());
            }
        }
        append("</UL></TD>");
    }

    /**
     * Picks correct interactor to display to User.
     */
//    private Interactor pickInteractorToDisplay(ArrayList interactors) {
//        Interactor interactor0 = (Interactor) interactors.get(0);
//        Interactor interactor1 = (Interactor) interactors.get(1);
//        String name0 = interactor0.getName();
//        String name1 = interactor1.getName();
//
//        // If both interactors are the same, this is a self-interacting
//        // interaction.
//        if (name0.equals(uid) && name1.equals(uid)) {
//            return interactor0;
//        } else if (name0.equals(uid)) {
//            return interactor1;
//        } else {
//            return interactor0;
//        }
//    }
}