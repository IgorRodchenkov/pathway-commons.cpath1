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
package org.mskcc.pathdb.test.service;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.services.ReadInteractions;
import org.mskcc.pathdb.util.CPathConstants;

import java.util.ArrayList;

/**
 * Tests the ReadinteractionsFromGrid Service Class.
 *
 * @author Ethan Cerami
 */
public class TestReadInteractionsFromGrid extends TestCase {
    private static final String SAMPLE_ORF = "YER006W";

    /**
     * Tests against live GRID_LOCAL Service.
     * @throws Exception Indicates Error.
     */
    public void testGridService() throws Exception {
        try {
            DataServiceFactory factory = DataServiceFactory.getInstance();
            ReadInteractions service = (ReadInteractions)
                    factory.getService
                    (CPathConstants.READ_INTERACTIONS_FROM_GRID);
            ArrayList interactions =
                    service.getInteractionsById(SAMPLE_ORF);
            validateData(interactions, 0, "YER006W", "YPL211W",
                    "Affinity Precipitation", "AB", "11583615");
            validateData(interactions, 6, "YER006W", "YPL146C",
                    "Affinity Precipitation", "AB", "11583615");
        } catch (DataServiceException e) {
            Throwable t = e.getCause();
            t.printStackTrace();
        }
    }

    /**
     * Validates Interaction Data.
     * @param interactions ArrayList of Interactions.
     * @param index Index value into ArrayList.
     * @param geneA ORF Name for GeneA.
     * @param geneB ORF Name for GeneB.
     * @param expectedExperiment Experimental System.
     * @param expectedDirection Direction of Interaction.
     * @param pid PubMed Id.
     */
    private void validateData(ArrayList interactions, int index, String geneA,
            String geneB, String expectedExperiment, String expectedDirection,
            String pid) {
        Interaction interaction = (Interaction) interactions.get(index);
        ArrayList interactors = interaction.getInteractors();
        Interactor nodeA = (Interactor) interactors.get(0);
        Interactor nodeB = (Interactor) interactors.get(1);
        assertEquals(geneA, nodeA.getName());
        assertEquals(geneB, nodeB.getName());
        String experiment = (String) interaction.getAttribute
                (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
        assertEquals(expectedExperiment, experiment);
        String direction = (String) interaction.getAttribute
                (InteractionVocab.DIRECTION);
        assertEquals(expectedDirection, direction);
        String pmid = (String) interaction.getAttribute
                (InteractionVocab.PUB_MED_ID);
        assertEquals(pid, pmid);
    }
}