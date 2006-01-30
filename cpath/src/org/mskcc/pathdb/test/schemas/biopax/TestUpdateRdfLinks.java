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
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.schemas.biopax.BioPaxUtil;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.schemas.biopax.UpdateRdfLinks;
import org.mskcc.pathdb.sql.assembly.CPathIdFilter;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Tests the UpdateRdfLinks Class.
 *
 * @author Ethan Cerami
 */
public class TestUpdateRdfLinks extends TestCase {
    private ProgressMonitor pMonitor = new ProgressMonitor();

    /**
     * Tests the UpdateRdfLinks Class.
     *
     * @throws Exception All Errors.
     */
    public void testUpdateRdfLinks() throws Exception {
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample1.owl");
        BioPaxUtil util = new BioPaxUtil(file, pMonitor);
        ArrayList pathwayList = util.getPathwayList();

        //  Create a Sample ID Map
        HashMap idMap = new HashMap();
        idMap.put("pathway50", new Long(100));
        idMap.put("catalysis43", new Long(1));
        idMap.put("biochemicalReaction37", new Long(2));
        idMap.put("biochemicalReaction6", new Long(3));
        idMap.put("catalysis5", new Long(4));

        //  Before:  we have the following RDF ID
        Element pathway = (Element) pathwayList.get(0);
        String pathwayId = pathway.getAttributeValue(RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);
        assertEquals("pathway50", pathwayId);

        //  Before:  we have the following links
        XPath xpath = XPath.newInstance("//@rdf:resource");
        xpath.addNamespace("rdf", RdfConstants.RDF_NAMESPACE_URI);
        List links = xpath.selectNodes(pathway);
        this.validateLink(links, 0, "#catalysis43");
        this.validateLink(links, 1, "#biochemicalReaction37");
        this.validateLink(links, 2, "#biochemicalReaction6");
        this.validateLink(links, 3, "#catalysis5");
        this.validateLink(links, 4, "#catalysis43");
        this.validateLink(links, 5, "#biochemicalReaction37");

        //  Update the Links
        UpdateRdfLinks linker = new UpdateRdfLinks();
        linker.updateInternalLinks(pathwayList, idMap,
                CPathIdFilter.CPATH_PREFIX);

        //  After:  we have the following RDF ID
        pathwayId = pathway.getAttributeValue(RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);
        assertEquals("CPATH-100", pathwayId);

        //  After:  we have the following links
        xpath = XPath.newInstance("//@rdf:resource");
        xpath.addNamespace("rdf", RdfConstants.RDF_NAMESPACE_URI);
        links = xpath.selectNodes(pathway);
        this.validateLink(links, 0, "#CPATH-1");
        this.validateLink(links, 1, "#CPATH-2");
        this.validateLink(links, 2, "#CPATH-3");
        this.validateLink(links, 3, "#CPATH-4");
        this.validateLink(links, 4, "#CPATH-1");
        this.validateLink(links, 5, "#CPATH-2");

        //  After:  Get All Internal Links for cPath ID:  100
        long[] internalLinks = linker.getInternalLinks(100);
        assertEquals(4, internalLinks.length);
        boolean matches[] = new boolean[4];
        for (int i = 0; i < internalLinks.length; i++) {
            if (internalLinks[i] == 1) {
                matches[0] = true;
            } else if (internalLinks[i] == 2) {
                matches[1] = true;
            } else if (internalLinks[i] == 3) {
                matches[2] = true;
            } else if (internalLinks[i] == 4) {
                matches[3] = true;
            }
        }
        for (int i = 0; i < matches.length; i++) {
            assertTrue(matches[i]);
        }
    }

    private void validateLink(List links, int index, String expectedValue) {
        Attribute link = (Attribute) links.get(index);
        assertEquals(expectedValue, link.getValue());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can update RDF links/resources";
    }
}