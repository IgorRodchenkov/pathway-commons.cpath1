// $Id: TestPopulateReferenceTableTask.java,v 1.4 2006-12-22 18:02:34 grossb Exp $
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
package org.mskcc.pathdb.test.references;

// imports
import org.mskcc.pathdb.model.Reference;
import org.mskcc.pathdb.sql.dao.DaoReference;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.task.PopulateReferenceTableTask;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Tests select PopulateReferenceTableTask class methods.
 *
 * @author Benjamin Gross
 */
public class TestPopulateReferenceTableTask extends TestCase {

	// some statics for testing
	private final Object[] PUBMED_TEST_RECORD_ONE = { "11748933",
													  "PubMed", "2001",
													  "Is cryopreservation a homogeneous process? " +
													  "Ultrastructure and motility of untreated, prefreezing, " +
													  "and postthawed spermatozoa of Diplodus puntazzo (Cetti).",
													  new Integer(8), "Abelli, L", "Cryobiology 42(4):244-55" };
	private final Object[] PUBMED_TEST_RECORD_TWO = { "11700088",
													  "PubMed", "2001", "Proton MRI of (13)C distribution by J " +
													  "and chemical shift editing.",
													  new Integer(6), "Carpinelli, G", "Journal of magnetic resonance (San Diego, Calif. : 1997) 153(1):117-23" };

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(TestPopulateReferenceTableTask.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
		return "TestPopulateReferenceTableTask";
    }

    /**
	 * Tests PopulateReferenceTableTask.parsePubMedData().
     *
     * @throws Exception All Exceptions.
     */
    public void testParsePubMedData() throws Exception {

		// populate list of pubmed ids to fetch
		ArrayList<String> recordsToFetch = new ArrayList<String>();
		recordsToFetch.add((String)PUBMED_TEST_RECORD_ONE[0]);
		recordsToFetch.add((String)PUBMED_TEST_RECORD_TWO[0]);

		// fetch the records from the ncbi
		PopulateReferenceTableTask task = new PopulateReferenceTableTask(true, new XDebug());
		task.processPubMedBatch(recordsToFetch);

		// verify the fetched/data
		Reference ref;
		DaoReference daoReference = new DaoReference();
		DaoExternalDb daoExternalDb = new DaoExternalDb();
		ExternalDatabaseRecord dbRecord = daoExternalDb.getRecordByName("PubMed");
		ref = daoReference.getRecord((String)PUBMED_TEST_RECORD_ONE[0], dbRecord.getId());
		verifyPubMed(ref, PUBMED_TEST_RECORD_ONE);
		ref = daoReference.getRecord((String)PUBMED_TEST_RECORD_TWO[0], dbRecord.getId());
		verifyPubMed(ref, PUBMED_TEST_RECORD_TWO);
	}

	/**
	 * Verify data fetched from NCBI.
	 */
	private void verifyPubMed(Reference ref, Object[] pubMedRecord) {

		// id
		Assert.assertEquals(ref.getId(), ((String)pubMedRecord[0]));
		// database
		Assert.assertEquals(ref.getDatabase(), (String)pubMedRecord[1]);
		// year
		Assert.assertEquals(ref.getYear(), (String)pubMedRecord[2]);
		// title
		Assert.assertEquals(ref.getTitle(), (String)pubMedRecord[3]);

		// authors
		String[] authors = ref.getAuthors();
		Assert.assertEquals(authors.length, ((Integer)pubMedRecord[4]).intValue());
		Assert.assertEquals(authors[2], (String)pubMedRecord[5]);
		// source
		Assert.assertEquals(ref.getSource(), (String)pubMedRecord[6]);
    }
}
