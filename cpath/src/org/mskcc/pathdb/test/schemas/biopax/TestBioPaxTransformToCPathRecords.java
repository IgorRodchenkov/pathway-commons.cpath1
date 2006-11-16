// $Id: TestBioPaxTransformToCPathRecords.java,v 1.9 2006-11-16 15:45:31 cerami Exp $
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
package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxUtil;
import org.mskcc.pathdb.schemas.biopax.TransformBioPaxToCPathRecords;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.jdom.Element;

import java.io.FileReader;
import java.util.ArrayList;

/**
 * Tests the BioPaxTransformToCPathRecords Class.
 *
 * @author Ethan Cerami
 */
public class TestBioPaxTransformToCPathRecords extends TestCase {

    /**
     * Tests BioPAX Transformation.
     *
     * @throws Exception All Errors.
     */
    public void testTransformation() throws Exception {
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample1.owl");
        ProgressMonitor pMonitor = new ProgressMonitor();
        BioPaxUtil util = new BioPaxUtil(file, false, pMonitor);

        //  Try with a Pathway
        TransformBioPaxToCPathRecords transformer = new TransformBioPaxToCPathRecords();
        Element pathway = util.getPathway(0);

        //  Validate the cPath Record
        CPathRecord record = transformer.createCPathRecord(pathway);
        assertEquals("glycolysis", record.getName());
        assertEquals("Glycolysis Pathway", record.getDescription());
        assertEquals(562, record.getNcbiTaxonomyId());
        assertEquals(XmlRecordType.BIO_PAX, record.getXmlType());
        assertEquals(CPathRecordType.PATHWAY, record.getType());
        assertEquals(BioPaxConstants.PATHWAY, record.getSpecificType());

        //  Validate the RDF ID
        String id = transformer.getRdfId(pathway);
        assertEquals("pathway50", id);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can transform BioPAX records into cPath records";
    }
}
