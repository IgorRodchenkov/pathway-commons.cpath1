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

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Interaction Table.
 *
 * @author Ethan Cerami
 */
public class InteractionTable extends HtmlTable {
    private String uid;
    private ArrayList interactions;
    private ExternalLinks links;

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
        links = new ExternalLinks();
        startTable("Interactions for:  " + uid);
        String headers[] = {
            "Interactor", "External References",
            "Experimental System", "PubMed Reference"};

        createTableHeaders(headers);
        outputInteractions();
        endTable();
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputInteractions() {
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            ArrayList interactors = interaction.getInteractors();
            append("<TR>");
            for (int j = 0; j < interactors.size(); j++) {
                Interactor interactor = (Interactor) interactors.get(j);
                if (!interactor.getName().equalsIgnoreCase(uid)) {
                    String url = links.getInteractionLink(interactor.getName());
                    outputDataField(interactor.getName(), url);
                    outputExternalReferences(interactor);
                }
            }
            String expSystem = (String) interaction.getAttribute
                    (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
            outputDataField(expSystem);
            String pmid = (String) interaction.getAttribute
                    (InteractionVocab.PUB_MED_ID);
            String url = links.getNcbiLink(pmid);
            outputDataField(pmid, url);
        }
        append("</TR>");
    }

    /**
     * Outputs External References.
     */
    private void outputExternalReferences(Interactor interactor) {
        ExternalReference refs[] = interactor.getExternalRefs();
        if (refs != null) {
            append("<TD VALIGN=TOP>");
            append("<UL");
            for (int i = 0; i < refs.length; i++) {
                String db = refs[i].getDatabase();
                String id = refs[i].getId();
                append("<LI>" + db);
                append(":  ");
                String url = links.getExternalLink(db, id);
                if (url != null) {
                    append("<A HREF=\"" + url + "\">" + id + "</A>");
                } else {
                    append(id);
                }
            }
            append("</TD>");
        } else {
            append("<TD>&nbsp;</TD>");
        }
    }
}