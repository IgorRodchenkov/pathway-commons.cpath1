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
package org.mskcc.pathdb.test.taglib;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.taglib.InteractionTable;

import java.util.ArrayList;

/**
 * Tests the Custom JSP InteractionTable Class.
 *
 * @author Ethan Cerami
 */
public class TestInteractionTable extends TestCase {

    /**
     * Tests the InteractionTable Custom Tag.
     *
     * @throws Exception All Exceptions.
     */
    public void testTag() throws Exception {
        ArrayList interactions = new ArrayList();
        Interaction interaction = new Interaction();

        ArrayList interactors = new ArrayList();
        Interactor interactorA = new Interactor();
        interactorA.setName("protein1");
        Interactor interactorB = new Interactor();
        interactorB.setName("protein2");
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference("Entrez GI", "12345");
        interactorB.setExternalRefs(refs);
        interactors.add(interactorA);
        interactors.add(interactorB);
        interaction.setInteractors(interactors);
        interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME,
                "Classical Two Hybrid");
        interaction.addAttribute(InteractionVocab.PUB_MED_ID,
                "12345");
        interactions.add(interaction);

        InteractionTable table = new InteractionTable();
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery("protein1");
        table.setProtocolRequest(request);
        //table.setInteractions(interactions);
        table.doStartTag();
        String html = table.getHtml();

        //  Test Table Header
        int index = html.indexOf("Interactions for:  protein1");
        assertTrue(index > 0);

        //  Test Pub Med Link
        index = html.indexOf("<A HREF=\"http://www.ncbi.nlm.nih.gov:80/"
                + "entrez/query.fcgi?cmd=Retrieve&db=protein&list_uids=12345&"
                + "dopt=GenPept\">12345</A>");
        assertTrue(index > 0);

        //  Test NCBI Link
        index = html.indexOf("<A HREF=\"http://www.ncbi.nlm.nih.gov:80"
                + "/entrez/query.fcgi?cmd=Retrieve&db=protein&list_uids"
                + "=12345&dopt=GenPept\">");
        assertTrue(index > 0);

        //  Test Experimental System
        index = html.indexOf("Classical Two Hybrid");
        assertTrue(index > 0);
    }
}