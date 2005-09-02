/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;
import org.jdom.Element;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxElementFilter;
import org.mskcc.pathdb.schemas.biopax.BioPaxUtil;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests the BioPaxElement Filter Class.
 *
 * @author Ethan Cerami
 */
public class TestBioPaxElementFilter extends TestCase {

    /**
     * Tests the BioPAX Element Filter.
     *
     * @throws Exception All Errors.
     */
    public void testBioPaxElementFilter() throws Exception {
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample1.owl");
        BioPaxUtil util = new BioPaxUtil(file, new ProgressMonitor());

        ArrayList pathwayList = util.getPathwayList();
        assertEquals(1, pathwayList.size());

        Element pathway = (Element) pathwayList.get(0);
        List children = pathway.getChildren();
        assertEquals(14, children.size());

        BioPaxElementFilter.retainCoreElementsOnly(pathway);
        children = pathway.getChildren();
        assertEquals(7, children.size());
        for (int i = 0; i < children.size(); i++) {
            Element e = (Element) children.get(i);
            String name = e.getName();
            switch (i) {
                case 0:
                    assertEquals(BioPaxConstants.COMMENT_ELEMENT, name);
                    break;
                case 1:
                    assertEquals(BioPaxConstants.XREF_ELEMENT, name);
                    break;
                case 2:
                    assertEquals(BioPaxConstants.XREF_ELEMENT, name);
                    break;
                case 3:
                    assertEquals(BioPaxConstants.ORGANISM_ELEMENT, name);
                    break;
                case 4:
                    assertEquals(BioPaxConstants.NAME_ELEMENT, name);
                    break;
                case 5:
                    assertEquals(BioPaxConstants.SHORT_NAME_ELEMENT, name);
                    break;
                case 6:
                    assertEquals(BioPaxConstants.XREF_ELEMENT, name);
                    break;

            }
        }
    }

    /**
     * Gets Test Description.
     *
     * @return Description.
     */
    public String getName() {
        return "Test that we can filter BioPAX Documents for core "
                + "elements only";
    }
}
