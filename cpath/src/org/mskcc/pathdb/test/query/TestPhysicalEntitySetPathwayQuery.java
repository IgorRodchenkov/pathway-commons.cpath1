// $Id: TestPhysicalEntitySetPathwayQuery.java,v 1.1 2006-11-07 21:24:39 grossb Exp $
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
import org.mskcc.pathdb.query.PhysicalEntitySetQuery;

import java.util.Set;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests the PhysicalEntitySetQuery.getPhysicalEntitySetPathways() method.
 *
 * @author Benjamin Gross
 */
public class TestPhysicalEntitySetPathwayQuery extends TestCase {

    /**
	 * Tests the PhysicalEntitySetQuery.getPhysicalEntitySetPathways() method.
     *
     * @throws Exception All Exceptions.
     */
    public void testPhysicalEntitySetPathwayQuery() throws Exception {

		//long[] pathwayRecordIDs = { 170, 189 }; // must be decending order of membership
		//long[] physicalEntityRecordIDs = { 222, 224, 220, 223, 225 };
		//executeAndCheckQuery(physicalEntityRecordIDs, pathwayRecordIDs);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the physical entity set - pathway query";
    }

	/**
	 * Given gene set and pathway set,
	 * calls PhysicalEntitySetQuery.getPhysicalEntitySetPathways()
	 * and checks result
	 *
	 * @param physicalEntityRecordIDs long[]
	 * @param pathwayRecordIDs long[]
	 */
	private void executeAndCheckQuery(long[] physicalEntityRecordIDs, long[] pathwayRecordIDs)
		throws Exception {

		// perform query
		long[] queryResults =
			PhysicalEntitySetQuery.getPhysicalEntitySetPathways(physicalEntityRecordIDs);

		// check pathwayRecordIDs size against queryResults size
		Assert.assertEquals(pathwayRecordIDs.length, queryResults.length);

		// check pathwayRecordIDs values
		// against queryResult values in decending ranked order
		int lc = -1;
		for (long queryResult : queryResults) {
			Assert.assertEquals(queryResult, pathwayRecordIDs[++lc]);
		}
	}
}
