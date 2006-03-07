// $Id: TestExternalReferenceUtil.java,v 1.12 2006-03-07 16:11:38 cerami Exp $
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
package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.util.ExternalReferenceUtil;
import org.mskcc.pathdb.util.cache.EhCache;

/**
 * Tests the ExternalReferenceUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestExternalReferenceUtil extends TestCase {
    private String testName;

    /**
     * Tests the Filter Method.
     *
     * @throws Exception All Exceptions.
     */
    public void testFilterReferences() throws Exception {
        EhCache.resetAllCaches();
        testName = "Test Filtering of Reference List";
        ExternalReference refs[] = new ExternalReference[7];
        refs[0] = new ExternalReference("SwissProt", "P25300");
        refs[1] = new ExternalReference("GO", "ABCD");
        refs[2] = new ExternalReference("InterPro", "InterPro");
        refs[3] = new ExternalReference("InterPro", "ABCD");
        refs[4] = new ExternalReference("PDB", "ABCD");
        refs[5] = new ExternalReference("Unigene", "ABCD");
        refs[6] = new ExternalReference("REF_SEQ PROTEIN", "ABCD");
        ExternalReference filteredRefs[] =
                ExternalReferenceUtil.filterByReferenceType(refs,
                        ReferenceType.PROTEIN_UNIFICATION);
        //  Before filtering, we have 7 references.
        assertEquals(7, refs.length);

        //  After filtering, we have 2 references.
        assertEquals(2, filteredRefs.length);
    }

    /**
     * Tests the createUnifiedList method.
     * @throws Exception All Exceptions.
     */
    public void testUnionMethod() throws Exception {
        EhCache.resetAllCaches();
        testName = "Test Union of References";
        ExternalReference refs1[] = new ExternalReference[2];
        refs1[0] = new ExternalReference("SwissProt", "P25300");
        refs1[1] = new ExternalReference("GO", "ABCD");

        ExternalReference refs2[] = new ExternalReference[2];
        refs2[0] = new ExternalReference("InterPro", "ABCD");
        refs2[1] = new ExternalReference("UniGene", "ABCD");

        ExternalReference union[] =
                ExternalReferenceUtil.createUnifiedList(refs1, refs2);
        assertEquals(4, union.length);
        ExternalReference ref4 = union[3];
        assertEquals("UniGene", ref4.getDatabase());
    }

    /**
     * Tests the RemoveDuplicates Method.
     * 
     * @throws Exception All Exceptions.
     */
    public void testRemoveDuplicates() throws Exception {
        EhCache.resetAllCaches();
        testName = "Test Removal of Duplicate References";
        ExternalReference refs[] = new ExternalReference[4];
        refs[0] = new ExternalReference("SwissProt", "P25300");
        refs[1] = new ExternalReference("GO", "ABCD");
        refs[2] = new ExternalReference("SwissProt", "P25300");
        refs[3] = new ExternalReference("SwissProt", "ABCD");
        refs = ExternalReferenceUtil.removeDuplicates(refs);
        assertEquals(3, refs.length);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the External Reference Utility Class:  " + testName;
    }
}
