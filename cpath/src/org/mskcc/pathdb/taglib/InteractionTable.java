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
import org.mskcc.pathdb.model.ExternalDatabase;
import org.mskcc.pathdb.model.ExternalLink;
import org.mskcc.pathdb.sql.DaoExternalLink;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Interaction Table.
 *
 * @author Ethan Cerami
 */
public class InteractionTable extends HtmlTable {
    private String uid;
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
     * @param uid UID String.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Start Tag Processing.
     * @throws IOException Input Output Exceptions.
     */
    protected void subDoStartTag() throws IOException {
        try {
            startTable("Interactions for:  " + uid);
            String headers[] = {
                "Interactor", "External References",
                "Experimental System", "PubMed Reference"};

            createTableHeaders(headers);
            outputInteractions();
            endTable();
        } catch (Exception e) {
            this.append("Error:  " + e);
        }
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputInteractions() throws SQLException,
            ClassNotFoundException {
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            ArrayList interactors = interaction.getInteractors();
            append("<TR>");
            Interactor interactor = pickInteractorToDisplay(interactors);
            String url = getInteractionLink(interactor.getName());
            outputDataField(interactor.getName(), url);
            outputExternalReferences(interactor);
            String expSystem = (String) interaction.getAttribute
                    (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
            outputDataField(expSystem);
            String pmid = (String) interaction.getAttribute
                    (InteractionVocab.PUB_MED_ID);
            url = getPubMedLink(pmid);
            outputDataField(pmid, url);
        }
        append("</TR>");
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
     * Gets Internal Link to "get interactions".
     * @param id Unique ID.
     * @return URL back to CPath.
     */
    private String getInteractionLink(String id) {
        String url = "/ds/dataservice?version=1.0&cmd=retrieve_interactions&"
                + "db=grid&format=html&uid=" + id;
        return url;
    }

    /**
     * Outputs External References.
     */
    private void outputExternalReferences(Interactor interactor)
            throws SQLException, ClassNotFoundException {
        DaoExternalLink dao = new DaoExternalLink();
        String id = (String) interactor.getAttribute(InteractorVocab.LOCAL_ID);
        ArrayList links = dao.getRecordsByCPathId(Integer.parseInt(id));
        append("<TD VALIGN=TOP><UL>");
        for (int i = 0; i < links.size(); i++) {
            ExternalLink link = (ExternalLink) links.get(i);
            ExternalDatabase db = link.getExternalDatabase();
            append("<LI>" + db.getName() + ": ");
            outputLink(link.getLinkedToId(), link.getWebLink(),
                    db.getDescription());
        }
        append("</UL></TD>");
    }

    /**
     * Picks correct interactor to display to User.
     */
    private Interactor pickInteractorToDisplay(ArrayList interactors) {
        Interactor interactor0 = (Interactor) interactors.get(0);
        Interactor interactor1 = (Interactor) interactors.get(1);
        String name0 = interactor0.getName();
        String name1 = interactor1.getName();

        // If both interactors are the same, this is a self-interacting
        // interaction.
        if (name0.equals(uid) && name1.equals(uid)) {
            return interactor0;
        } else if (name0.equals(uid)) {
            return interactor1;
        } else {
            return interactor0;
        }
    }
}