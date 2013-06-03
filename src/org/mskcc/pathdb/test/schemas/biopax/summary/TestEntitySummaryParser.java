// $Id: TestEntitySummaryParser.java,v 1.16 2007-05-11 18:37:17 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

// imports

import junit.framework.TestCase;
import org.mskcc.pathdb.schemas.biopax.summary.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the EntitySummaryParser Class.
 *
 * @author Benjamin Gross.
 */
public class TestEntitySummaryParser extends TestCase {

    /**
     * ArrayList to hold InteractionSummaryComponents.
     */
    private ArrayList components = null;

    /**
     * EntitySummaryParser ref.
     */
    private EntitySummaryParser entitySummaryParser = null;

    /**
     * InteractionSummary ref.
     */
    private EntitySummary entitySummary = null;

    /**
     * ParticipantSummaryComponent ref.
     */
    private ParticipantSummaryComponent summaryComponent = null;

    /**
     * Tests invalid cpath record id handling.
     *
     * @throws Exception All Exceptions.
     */
    public void testInvalidRecordID() throws Exception {

        // catch invalid record id exception
        try {
            entitySummaryParser = new EntitySummaryParser(-999);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
            return;
        }

        // were proper exceptions thrown ?
        assertTrue(false);
    }

    /**
     * Test physical interaction summary parsing.
     *
     * @throws Exception All Exceptions.
     */
    public void testPhysicalInteractionSummaryParsing() throws Exception {

        // shared var
        int cnt;

        // perform the interaction parsing
        entitySummaryParser = new EntitySummaryParser(362);
        entitySummary = entitySummaryParser.getEntitySummary();

        // this should be a physical interaction summary
        assertTrue(entitySummary instanceof PhysicalInteractionSummary);
        PhysicalInteractionSummary physicalInteractionSummary =
                (PhysicalInteractionSummary) entitySummary;

        // get physical interaction type
        assertEquals("direct interaction reaction",
                physicalInteractionSummary.getInteractionType());

        // get left side components list
        components = physicalInteractionSummary.getParticipants();
        cnt = components.size();
        assertTrue(cnt == 2);

        // get participants
        summaryComponent = (ParticipantSummaryComponent) components.get(0);
        assertEquals("AR", summaryComponent.getName());
        assertTrue(418 == summaryComponent.getRecordID());
        assertEquals("nucleus", summaryComponent.getCellularLocation());
        assertTrue(null == summaryComponent.getFeatureList());

        summaryComponent = (ParticipantSummaryComponent) components.get(1);
        assertEquals("APPL-Human_AKT1-Human", summaryComponent.getName());
        assertTrue(468 == summaryComponent.getRecordID());
        assertTrue(null == summaryComponent.getFeatureList());
        // this component is a complex, test for members
        ArrayList complexMemberList = summaryComponent.getComponentList();
        assertTrue(2 == complexMemberList.size());
        assertEquals("APPL", ((BioPaxRecordSummary) complexMemberList.get(0)).getName());
        assertEquals("AKT1", ((BioPaxRecordSummary) complexMemberList.get(1)).getName());
    }

    /**
     * Test control interaction summary parsing.
     *
     * @throws Exception All Exceptions.
     */
    public void testControlInteractionSummaryParsing() throws Exception {

        // shared var
        int cnt;

        // perform the interaction parsing
        entitySummaryParser = new EntitySummaryParser(175);
        entitySummary = entitySummaryParser.getEntitySummary();

        // this should be a control interaction summary
        assertTrue(entitySummary instanceof ControlInteractionSummary);
        ControlInteractionSummary controlInteractionSummary =
                (ControlInteractionSummary) entitySummary;

        // assert control type
        assertEquals("ACTIVATION_CATALYSIS", controlInteractionSummary.getControlType());

        // get controllers
        components = controlInteractionSummary.getControllers();
        cnt = components.size();
        assertTrue(cnt == 1);

        // get specific controller
        summaryComponent = (ParticipantSummaryComponent) components.get(0);
        assertEquals("PRKCG", summaryComponent.getName());
        assertTrue(238 == summaryComponent.getRecordID());
        assertEquals("cytoplasm", summaryComponent.getCellularLocation());
        assertTrue(null == summaryComponent.getFeatureList());

        // get controlled
        components = controlInteractionSummary.getControlled();
        cnt = components.size();
        assertTrue(cnt == 1);

        // get specific controlled
        Object potentialConversionInteractionSummary = components.get(0);
        assertTrue(potentialConversionInteractionSummary instanceof ConversionInteractionSummary);

        // controlled is a conversion, lets test its components
        ConversionInteractionSummary conversionInteractionSummary =
                (ConversionInteractionSummary) potentialConversionInteractionSummary;

        // get left side components list
        components = conversionInteractionSummary.getLeftSideComponents();
        cnt = components.size();
        assertTrue(cnt == 1);

        // get specific left component
        summaryComponent = (ParticipantSummaryComponent) components.get(0);
        assertEquals("DAB2", summaryComponent.getName());
        assertTrue(250 == summaryComponent.getRecordID());
        assertEquals("cytoplasm", summaryComponent.getCellularLocation());
        assertTrue(null == summaryComponent.getFeatureList());

        // get right side components list
        components = conversionInteractionSummary.getRightSideComponents();
        cnt = components.size();
        assertTrue(cnt == 1);

        // get specific right component
        summaryComponent = (ParticipantSummaryComponent) components.get(0);
        assertEquals("DAB2", summaryComponent.getName());
        assertTrue(250 == summaryComponent.getRecordID());
        assertEquals("cytoplasm", summaryComponent.getCellularLocation());
        ArrayList featureList = summaryComponent.getFeatureList();
        assertTrue(featureList.size() == 1);
        BioPaxFeature feature = (BioPaxFeature) featureList.get(0);
        assertEquals("phosphorylated", feature.getTerm());
    }


    /**
     * Tests conversion interaction summary parsing.
     *
     * @throws Exception All Exceptions.
     */
    public void testConversionInteractionSummaryParsing() throws Exception {

        // shared vars
        int cnt;

        // perform the interaction parsing
        entitySummaryParser = new EntitySummaryParser(158);
        entitySummary = entitySummaryParser.getEntitySummary();

        // this should be a conversion interaction summary
        assertTrue(entitySummary instanceof ConversionInteractionSummary);
        ConversionInteractionSummary conversionInteractionSummary =
                (ConversionInteractionSummary) entitySummary;

        // get left side components list
        components = conversionInteractionSummary.getLeftSideComponents();
        cnt = components.size();
        assertTrue(cnt == 1);

        // get specific left component
        summaryComponent = (ParticipantSummaryComponent) components.get(0);
        assertEquals("TGFBR2", summaryComponent.getName());
        assertTrue(207 == summaryComponent.getRecordID());
        assertEquals("plasma membrane", summaryComponent.getCellularLocation());
        assertTrue(null == summaryComponent.getFeatureList());

        // get right side components list
        components = conversionInteractionSummary.getRightSideComponents();
        cnt = components.size();
        assertTrue(cnt == 1);

        // get specific right component
        summaryComponent = (ParticipantSummaryComponent) components.get(0);
        assertEquals("TGFBR2", summaryComponent.getName());
        assertTrue(207 == summaryComponent.getRecordID());
        assertEquals("plasma membrane", summaryComponent.getCellularLocation());

        // feature list
        ArrayList featureList = summaryComponent.getFeatureList();
        assertTrue(featureList.size() == 6);
        BioPaxFeature feature = (BioPaxFeature) featureList.get(0);
        assertEquals("phosphorylated", feature.getTerm());
    }

    /**
     * Tests Conversion Interaction from IOB.
     *
     * @throws Exception All Exceptions.
     */
    public void testIOBData() throws Exception {
        entitySummaryParser = new EntitySummaryParser(158);
        entitySummary = entitySummaryParser.getEntitySummary();
        ConversionInteractionSummary summary = (ConversionInteractionSummary) entitySummary;
        List left = summary.getLeftSideComponents();
        ParticipantSummaryComponent component = (ParticipantSummaryComponent) left.get(0);
        List synList = component.getSynonyms();
        assertEquals(6, synList.size());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the EntitySummaryParser Class";
    }
}
