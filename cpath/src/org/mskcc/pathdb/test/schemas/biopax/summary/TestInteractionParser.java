// $Id: TestInteractionParser.java,v 1.3 2006-01-30 14:23:36 grossb Exp $
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
import junit.framework.TestCase;

import org.mskcc.pathdb.schemas.biopax.summary.InteractionParser;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.ControlInteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.PhysicalInteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.ConversionInteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryComponent;

/**
 * Tests the InteractionParser Class.
 *
 * @author Benjamin Gross.
 */
public class TestInteractionParser extends TestCase {

	/**
	 * ArrayList to hold InteractionSummaryComponents.
	 */
	ArrayList components = null;

	/**
	 * InteractionParser ref.
	 */
	InteractionParser interactionParser = null;

	/**
	 * InteractionSummary ref.
	 */
	InteractionSummary interactionSummary = null;

	/**
	 * InteractionSummaryComponent ref.
	 */
	InteractionSummaryComponent summaryComponent = null;

    /**
     * Tests invalid cpath record id handling.
     *
     * @throws Exception All Exceptions.
     */
    public void testInvalidRecordID() throws Exception {

		// catch invalid record id exception
		try{
			interactionParser = new InteractionParser(-999);
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
		interactionParser = new InteractionParser(17);
		interactionSummary = interactionParser.getInteractionSummary();

		// this should be a control interaction summary
		assertTrue(interactionSummary instanceof PhysicalInteractionSummary);

		// get left side components list
		components = interactionSummary.getLeftSideComponents();
		cnt = components.size();
 		assertTrue(cnt == 2);

		// get left hand components
		summaryComponent = (InteractionSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		summaryComponent = (InteractionSummaryComponent)components.get(1);
		assertEquals("ETV5", summaryComponent.getName());
		assertTrue(19 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		// physical interaction has no right compontents
		components = interactionSummary.getRightSideComponents();
		assertTrue(null == components);
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
		interactionParser = new InteractionParser(20);
		interactionSummary = interactionParser.getInteractionSummary();

		// this should be a control interaction summary
		assertTrue(interactionSummary instanceof ControlInteractionSummary);

		// assert control type
		assertEquals("ACTIVATION", ((ControlInteractionSummary)interactionSummary).getControlType());

		// get left side components list
		components = interactionSummary.getLeftSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific left component
		summaryComponent = (InteractionSummaryComponent)components.get(0);
		assertEquals("MAPK1", summaryComponent.getName());
		assertTrue(22 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		// get right side components list
		components = interactionSummary.getRightSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific right component
		summaryComponent = (InteractionSummaryComponent)components.get(0);
		assertEquals("Phosphorylation", summaryComponent.getName());
		assertTrue(21 == summaryComponent.getRecordID());
		assertTrue(null == summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());
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
		interactionParser = new InteractionParser(23);
		interactionSummary = interactionParser.getInteractionSummary();

		// this should be a conversion interaction summary
		assertTrue(interactionSummary instanceof ConversionInteractionSummary);

		// get left side components list
		components = interactionSummary.getLeftSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific left component
		summaryComponent = (InteractionSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		assertTrue(null == summaryComponent.getFeatureList());

		// get right side components list
		components = interactionSummary.getRightSideComponents();
		cnt = components.size();
		assertTrue(cnt == 1);

		// get specific right component
		summaryComponent = (InteractionSummaryComponent)components.get(0);
		assertEquals("AR", summaryComponent.getName());
		assertTrue(18 == summaryComponent.getRecordID());
		assertEquals("nucleus", summaryComponent.getCellularLocation());
		ArrayList featureList = summaryComponent.getFeatureList();
		assertTrue(featureList.size() == 1);
		assertEquals("phosphorylation site", (String)featureList.get(0));
	}

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the InteractionParser Class";
    }
}
