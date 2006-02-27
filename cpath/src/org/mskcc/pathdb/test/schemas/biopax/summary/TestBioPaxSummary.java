// $Id: TestBioPaxSummary.java,v 1.7 2006-02-27 22:04:12 grossb Exp $
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

import java.util.List;

import junit.framework.TestCase;

import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;

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
        try {
            biopaxRecordSummary = BioPaxRecordUtil.createBioPaxRecordSummary(null);
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        assertTrue(exception != null);

        // test BioPaxRecordSummaryUtils null pointer argument
        exception = null;
        try {
            String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(null);
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        assertTrue(exception != null);

        // get a real biopaxRecordSummary
        DaoCPath cPath = DaoCPath.getInstance();
        CPathRecord cPathRecord = cPath.getRecordById(418);
        biopaxRecordSummary = BioPaxRecordUtil.createBioPaxRecordSummary(cPathRecord);

        // header
        String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(biopaxRecordSummary);
        assertEquals("AR (Protein) from Homo sapiens", header);
        // synonyms
        List synonymList = biopaxRecordSummary.getSynonyms();
        assertTrue(synonymList != null);
        assertTrue(synonymList.size() == 10);
        assertEquals((String) synonymList.get(0), "NR3C4");
        assertEquals((String) synonymList.get(1), "AR");
        assertEquals((String) synonymList.get(2), "SBMA");
        assertEquals((String) synonymList.get(3), "SMAX1");
        assertEquals((String) synonymList.get(4), "Dihydrotestosterone receptor");
        assertEquals((String) synonymList.get(5), "HUMARA");
        assertEquals((String) synonymList.get(6), "KD");
        assertEquals((String) synonymList.get(7), "AIS");
        assertEquals((String) synonymList.get(8), "DHTR");
        assertEquals((String) synonymList.get(9), "TFM");
        // data source
        String dataSource =
                BioPaxRecordSummaryUtils.getBioPaxRecordDataSourceString(biopaxRecordSummary);
        assertTrue(dataSource == null);
        // availability
        String availability =
                BioPaxRecordSummaryUtils.getBioPaxRecordAvailabilityString(biopaxRecordSummary);
        assertTrue(availability == null);
        // external links
        List links = biopaxRecordSummary.getExternalLinks();
        assertTrue(links.size() == 4);
        // comment
        String comment = BioPaxRecordSummaryUtils.getBioPaxRecordCommentString(biopaxRecordSummary);
        assertEquals("Description of AR", comment);
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
