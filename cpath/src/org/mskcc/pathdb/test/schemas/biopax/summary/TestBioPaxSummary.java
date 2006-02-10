// $Id: TestBioPaxSummary.java,v 1.2 2006-02-10 22:49:12 grossb Exp $
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
import java.util.List;
import junit.framework.TestCase;

import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

/**
 * Tests the BioPaxRecordUtil - BioPaxSummary gathering methods.
 *
 * @author Benjamin Gross.
 */
public class TestBioPaxSummary extends TestCase {

    /**
	 * Tests the BioPaxRecordUtil - BioPaxSummary gathering methods.
     *
     * @throws Exception All Exceptions.
     */
    public void testBioPaxSummaryGathering() throws Exception {

		BioPaxRecordSummary biopaxRecordSummary;

		// test biopax record summary creation
		IllegalArgumentException exception = null;
		try{
			biopaxRecordSummary = BioPaxRecordUtil.createBioPaxRecordSummary(null);
		}
		catch (IllegalArgumentException e){
			exception = e;
		}
		assertTrue(exception != null);

		// test BioPaxRecordSummaryUtils null pointer argument
		exception = null;
		try {
			String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(null);
		}
		catch(IllegalArgumentException e){
			exception = e;
		}
		assertTrue(exception != null);

		// get a real biopaxRecordSummary
        DaoCPath cPath = DaoCPath.getInstance();
        CPathRecord cPathRecord = cPath.getRecordById(15);
		biopaxRecordSummary = BioPaxRecordUtil.createBioPaxRecordSummary(cPathRecord);

		// header
		String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(biopaxRecordSummary);
		assertEquals("GLK (Protein) from Escherichia coli", header);
		// synonyms
		String synonyms = BioPaxRecordSummaryUtils.getBioPaxRecordSynonymString(biopaxRecordSummary);
		assertEquals("Glucose kinase GLK_ECOLI", synonyms);
		// data source
		String dataSource = BioPaxRecordSummaryUtils.getBioPaxRecordDataSourceString(biopaxRecordSummary);
		assertEquals("Swiss-Prot/TrEMBL", dataSource );
 		// availability
		String availability = BioPaxRecordSummaryUtils.getBioPaxRecordAvailabilityString(biopaxRecordSummary);
		assertEquals("see http://www.amaze.ulb.ac.be/", availability );
		// external links
		List links = biopaxRecordSummary.getExternalLinks();
		assertTrue(links == null);
		// comment
		String comment = BioPaxRecordSummaryUtils.getBioPaxRecordCommentString(biopaxRecordSummary);
		assertTrue(comment.startsWith("This example is meant to provide an illustration"));
	}

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test BioPaxSummary Code";
    }
}
