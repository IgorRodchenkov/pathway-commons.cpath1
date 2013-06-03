// $Id: TestPhysicalEntitySetPathwayQuery.java,v 1.8 2007-05-16 14:04:28 grossben Exp $
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
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.util.PhysicalEntitySetUtil;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

import java.util.ArrayList;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests the PhysicalEntitySetUtil.getPhysicalEntitySetPathways() method.
 *
 * @author Benjamin Gross
 */
public class TestPhysicalEntitySetPathwayQuery extends TestCase {

    /**
	 * Tests the PhysicalEntitySetUtil.getPhysicalEntitySetPathways() method.
     *
     * @throws Exception All Exceptions.
     */
    public void testPhysicalEntitySetPathwayQuery() throws Exception {

		// populate internal family table
		savePathwayFamilyMembership();

		long[] pathwayRecordIDs = { 108, 298, 5 }; // must be decending order of membership
		long[] physicalEntityRecordIDs = { 222, 224, 220, 223, 225, 418, 96 };
		executeAndCheckQuery(physicalEntityRecordIDs, pathwayRecordIDs);
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
	 * calls PhysicalEntitySetUtil.getPhysicalEntitySetPathways()
	 * and checks result
	 *
	 * @param physicalEntityRecordIDs long[]
	 * @param pathwayRecordIDs long[]
	 */
	private void executeAndCheckQuery(long[] physicalEntityRecordIDs, long[] pathwayRecordIDs)
		throws Exception {

		// perform query
		long[] queryResults =
			PhysicalEntitySetUtil.getPhysicalEntitySetPathways(physicalEntityRecordIDs);

		// check pathwayRecordIDs size against queryResults size
		Assert.assertEquals(pathwayRecordIDs.length, queryResults.length);

		// check pathwayRecordIDs values
		// against queryResult values in decending ranked order
		int lc = -1;
		for (long queryResult : queryResults) {
			Assert.assertEquals(queryResult, pathwayRecordIDs[++lc]);
		}
	}

    /**
     * Saves Family Membership information for pathways only.
     *
     * @throws Exception (db access error)
     */
    private void savePathwayFamilyMembership () throws Exception {
		
        DaoInternalLink internalLinker = new DaoInternalLink();
        DaoInternalFamily daoFamily = new DaoInternalFamily();
        DaoCPath daoCPath = DaoCPath.getInstance();
		DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
		ArrayList<CPathRecord> cPathRecordList = daoCPath.getAllRecords(CPathRecordType.PATHWAY);

		// iterate over pathway record list
        for (CPathRecord record : cPathRecordList) {

            BioPaxRecordSummary pathwaySummary =
                BioPaxRecordUtil.createBioPaxRecordSummary(record);

			if (pathwaySummary.getName() == null ||
				pathwaySummary.getName().length() == 0) {
				if (CPathConstants.CPATH_DO_ASSERT) {
					assert (record.getType() == CPathRecordType.INTERACTION) :
					"*** PopulateInternalFamilyLookupTable: Pathway or Physical Entity Record has no name ***";
				}
				pathwaySummary.setName(CPathRecord.NA_STRING);
			}
			if (pathwaySummary.getOrganism() == null ||
				pathwaySummary.getOrganism().length() == 0) {
				pathwaySummary.setOrganism(CPathRecord.NA_STRING);
			}

			//  Store membership info for pathways only.
			ArrayList<Long> idList = internalLinker.getAllDescendents(record.getId());
			for (Long descendentID : idList) {
				CPathRecord descendentRecord = daoCPath.getRecordById(descendentID.longValue());
					BioPaxRecordSummary descendentSummary =
						BioPaxRecordUtil.createBioPaxRecordSummary(descendentRecord);
					if (descendentSummary.getName() == null ||
						descendentSummary.getName().length() == 0) {
						descendentSummary.setName(CPathRecord.NA_STRING);
					}
					daoFamily.addRecord(record.getId(), pathwaySummary.getName(), CPathRecordType.PATHWAY,
										daoSnapshot.getDatabaseSnapshot(record.getSnapshotId()),
										record.getNcbiTaxonomyId(), pathwaySummary.getOrganism(),
										descendentRecord.getId(), descendentSummary.getName(),
										descendentRecord.getType());
			}
        }
    }
}
