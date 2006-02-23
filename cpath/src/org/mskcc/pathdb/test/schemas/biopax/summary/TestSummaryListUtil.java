// $Id: TestSummaryListUtil.java,v 1.7 2006-02-23 22:15:47 cerami Exp $
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
package org.mskcc.pathdb.test.schemas.biopax.summary;

import junit.framework.TestCase;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummary;
import org.mskcc.pathdb.schemas.biopax.summary.SummaryListUtil;

import java.util.ArrayList;

/**
 * Tests the SummaryListUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestSummaryListUtil extends TestCase {

    /**
     * Tests the SummaryListUtil Class.
     *
     * @throws Exception All Exceptions
     */
    public void testSummaryList() throws Exception {
        SummaryListUtil util = new SummaryListUtil(108,
                SummaryListUtil.MODE_GET_PARENTS);
        ArrayList list = util.getSummaryList();
        EntitySummary summary = (EntitySummary) list.get(0);
        String currentType = summary.getSpecificType();
        for (int i = 0; i < list.size(); i++) {
            summary = (EntitySummary) list.get(i);

            //  Verify that list is sorted by specific type, in ascending order.
            int compare = summary.getSpecificType().compareTo(currentType);
            assertTrue(compare >= 0);
            currentType = summary.getSpecificType();
        }
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can retrieve a list of Interaction "
            + " Summary objects";
    }
}
