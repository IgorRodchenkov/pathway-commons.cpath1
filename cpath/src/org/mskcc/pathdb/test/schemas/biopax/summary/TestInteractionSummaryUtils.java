// $Id: TestInteractionSummaryUtils.java,v 1.15 2007-05-03 15:50:24 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.test.schemas.biopax.summary;

import junit.framework.TestCase;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummaryParser;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils;

/**
 * Tests the InteractionSummaryUtils Class.
 *
 * @author Ethan Cerami
 */
public class TestInteractionSummaryUtils extends TestCase {
    private String testName;

    /**
     * Validate the summary of a Conversion Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testConversionInteractionSummary() throws Exception {
        testName = "Test Conversion Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(10);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.
                createInteractionSummaryString(interactionSummary);

        // Verify Left Side is present
        assertTrue(summary.indexOf("3-phosphoglycerate") > 0);

        //  Verify Right Side is present
        assertTrue(summary.indexOf("2-phosphoglycerate") > 0);

        // Verify Some Synonyms
        assertTrue(summary.indexOf("3-phospho-glyceri") > 0);
    }

    /**
     * Validate the summary of a Conversion Interaction (Phosphorylation).
     *
     * @throws Exception All Exceptions.
     */
    public void testPhosphorylationInteractionSummary() throws Exception {
        testName = "Test Phosphorylation Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(172);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.
                createInteractionSummaryString(interactionSummary);
        System.out.println(summary);
        assertTrue(summary.indexOf("SMAD3</a> (phosphorylated)") > 0);
    }

    /**
     * Validate the summary of a Control Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testControlInteractionSummary() throws Exception {
        testName = "Test Control Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(159);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.
                createInteractionSummaryString(interactionSummary);
        assertTrue(summary.indexOf("activates [") > 0);
    }

    /**
     * Validate the summary of a Transport Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testTransportInteractionSummary() throws Exception {
        testName = "Test Transport Interaction Summary";
        EntitySummaryParser entityParser = new EntitySummaryParser(123);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.
                createInteractionSummaryString(interactionSummary);

        //  Validate cellular component on left
        assertTrue(summary.indexOf("(in cytoplasm)") > 0);

        //  Validate cellular component on right
        assertTrue(summary.indexOf("(in nucleus)") > 0);
    }

    /**
     * Returns Name of Unit Test.
     *
     * @return Name of Unit Test.
     */
    public String getName() {
        return "Test the Creation of HTML Interaction Summaries:  " + testName;
    }
}
