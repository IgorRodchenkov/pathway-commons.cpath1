// $Id: TestEntitySummaryParser.java,v 1.3 2006-02-10 22:41:13 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.mskcc.pathdb.schemas.biopax.summary.EntitySummaryParser;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummary;
import org.mskcc.pathdb.schemas.biopax.summary.ControlInteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.PhysicalInteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.ConversionInteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.ParticipantSummaryComponent;

/**
 * Tests the EntitySummaryParser Class.
 *
 * @author Benjamin Gross.
 */
public class TestEntitySummaryParser extends TestCase {

	/**
	 * ArrayList to hold InteractionSummaryComponents.
	 */
	ArrayList components = null;

	/**
	 * EntitySummaryParser ref.
	 */
	EntitySummaryParser entitySummaryParser = null;

	/**
	 * InteractionSummary ref.
	 */
	EntitySummary entitySummary = null;

	/**
	 * ParticipantSummaryComponent ref.
	 */
	ParticipantSummaryComponent summaryComponent = null;

    /**
     * Tests invalid cpath record id handling.
     *
     * @throws Exception All Exceptions.
     */
    public void testInvalidRecordID() throws Exception {

		// catch invalid record id exception
		try{
			entitySummaryParser = new EntitySummaryParser(-999);
		}
		catch (IllegalArgumentException e){
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
		entitySummaryParser = new EntitySummaryParser(17);
		entitySummary = entitySummaryParser.getEntitySummary();

		// this should be a physical interaction summary
		assertTrue(entitySummary instanceof PhysicalInteractionSummary);
		PhysicalInteractionSummary physicalInteractionSummary = (PhysicalInteractionSummary)entitySummary;

		// get type
		System.out.println("interaction type: " + physicalInteractionSummary.getInteractionType());

		// get left side components list
		components = physicalInteractionSummary.getParticipants();
		cnt = components.size();
 		assertTrue(cnt == 2);

		// get participants
		summaryComponent = (ParticipantSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		summaryComponent = (ParticipantSummaryComponent)components.get(1);
		assertEquals("ETV5", summaryComponent.getName());
		assertTrue(19 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());
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
		entitySummaryParser = new EntitySummaryParser(20);
		entitySummary = entitySummaryParser.getEntitySummary();

		// this should be a control interaction summary
		assertTrue(entitySummary instanceof ControlInteractionSummary);
		ControlInteractionSummary controlInteractionSummary = (ControlInteractionSummary)entitySummary;

		// assert control type
		assertEquals("ACTIVATION", controlInteractionSummary.getControlType());

		// get controllers
		components = controlInteractionSummary.getControllers();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific controller
		summaryComponent = (ParticipantSummaryComponent)components.get(0);
		assertEquals("MAPK1", summaryComponent.getName());
		assertTrue(22 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		// get controlled
		components = controlInteractionSummary.getControlled();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific controlled
		Object potentialConversionInteractionSummary = components.get(0);
		assertTrue(potentialConversionInteractionSummary instanceof ConversionInteractionSummary);

		// controlled is a conversion, lets test its components
		ConversionInteractionSummary conversionInteractionSummary = (ConversionInteractionSummary)potentialConversionInteractionSummary;

		// get left side components list
		components = conversionInteractionSummary.getLeftSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific left component
		summaryComponent = (ParticipantSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		// get right side components list
		components = conversionInteractionSummary.getRightSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific right component
		summaryComponent = (ParticipantSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		ArrayList featureList = summaryComponent.getFeatureList();
		assertTrue(featureList.size() == 1);
		assertEquals("phosphorylation site", (String)featureList.get(0));
	}


    /**
     * Tests conversion interaction summary parsing.
     *
     * @throws Exception All Exceptions.
     */
    public void testConversionInteractionSummaryParsing() throws Exception {

		// shared vars
		int cnt = 0;

		// perform the interaction parsing
		entitySummaryParser = new EntitySummaryParser(23);
		entitySummary = entitySummaryParser.getEntitySummary();

		// this should be a conversion interaction summary
		assertTrue(entitySummary instanceof ConversionInteractionSummary);
		ConversionInteractionSummary conversionInteractionSummary = (ConversionInteractionSummary)entitySummary;

		// get left side components list
		components = conversionInteractionSummary.getLeftSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific left component
		summaryComponent = (ParticipantSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		// get right side components list
		components = conversionInteractionSummary.getRightSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific right component
		summaryComponent = (ParticipantSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		ArrayList featureList = summaryComponent.getFeatureList();
		assertTrue(featureList.size() == 1);
		assertEquals("phosphorylation site", (String)featureList.get(0));
	}

    /**
     * Tests Conversion Interaction from IOB.
     * @throws Exception All Exceptions.
     */
    public void testIOBData() throws Exception {
		entitySummaryParser = new EntitySummaryParser(31);
		entitySummary = entitySummaryParser.getEntitySummary();
        ConversionInteractionSummary summary = (ConversionInteractionSummary)entitySummary;
        List left = summary.getLeftSideComponents();
        ParticipantSummaryComponent component = (ParticipantSummaryComponent) left.get(0);
        List synList = component.getSynonyms();
        System.out.println("SynList Size:  " + synList.size());
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
