// $Id: TestPhysicalEntitySetInteractionQuery.java,v 1.1 2006-11-07 21:04:07 grossb Exp $
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
package org.mskcc.pathdb.test.query;

// imports
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.mskcc.pathdb.query.PhysicalEntitySetQuery;

/**
 * Tests the PhysicalEntitySetQuery.getPhysicalEntitySetInteractions() method.
 *
 * @author Benjamin Gross
 */
public class TestPhysicalEntitySetInteractionQuery extends TestCase {

    /**
	 * Tests the PhysicalEntitySetQuery.getPhysicalEntitySetInteractions() method.
     *
     * @throws Exception All Exceptions.
     */
    public void testPhysicalEntitySetInteractionQuery() throws Exception {

		long[] interactionRecordIDs = { 170, 189 };
		long[] physicalEntityRecordIDs = { 222, 224, 220, 223, 225 };
		executeAndCheckQuery(physicalEntityRecordIDs, interactionRecordIDs);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the physical entity set - interaction query";
    }

	/**
	 * Given gene set and interaction set,
	 * calls PhysicalEntitySetQuery.getPhysicalEntitySetInteractions()
	 * and checks result
	 *
	 * @param physicalEntityRecordIDs long[]
	 * @param interactionRecordIDs long[]
	 */
	private void executeAndCheckQuery(long[] physicalEntityRecordIDs, long[] interactionRecordIDs)
		throws Exception {

		// perform query
		Set<PhysicalEntitySetQuery.PhysicalEntitySetInteractionsQueryResult> queryResults =
			PhysicalEntitySetQuery.getPhysicalEntitySetInteractions(physicalEntityRecordIDs);

		// check interactionRecordIDs size against queryResults size
		Assert.assertEquals(interactionRecordIDs.length, queryResults.size());

		// check interactionRecordIDs values against queryResult values
		for (PhysicalEntitySetQuery.PhysicalEntitySetInteractionsQueryResult queryResult : queryResults) {
			boolean resultFound = false;
			long queryResultID = queryResult.getInteractionRecordID();
			//for (int lc = 0; lc < interactionRecordIDs.length; lc++) {
			for (long id : interactionRecordIDs) {
				if (queryResultID == id) {//interactionRecordIDs[lc]) {
					resultFound = true;
					break;
				}
			}
			Assert.assertTrue(resultFound);
		}
	}
}
