// $Id: TestIndexer.java,v 1.20 2006-06-09 19:22:04 cerami Exp $
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
package org.mskcc.pathdb.test.lucene;

import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.lucene.*;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.util.xml.XmlStripper;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;

/**
 * Tests the StoreXmlToIndexer and the QueryIndexer Classes.
 *
 * @author Ethan Cerami
 */
public class TestIndexer extends TestCase {
    private String testName;

    /**
     * Tests the Full Text Indexer.
     *
     * @throws Exception All Exceptions.
     */
    public void testIndexer() throws Exception {
        testName = "Test the Lucene Indexer";
        XDebug xdebug = new XDebug();
        DaoCPath dao = DaoCPath.getInstance();
        CPathRecord record = dao.getRecordById(4);
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (record, 1, XmlAssemblyFactory.XML_FULL, xdebug);
        ItemToIndex item = IndexFactory.createItemToIndex(4, assembly);
        LuceneWriter indexWriter = new LuceneWriter(true);
        indexWriter.addRecord(item);
        indexWriter.closeWriter();
        indexWriter.optimize();

        queryInteraction("chaperonin");
        queryInteraction("interactor:chaperonin");
        queryInteraction("P06139");
        queryInteraction("interactor:P06139");
        queryInteraction("Escherichia coli");
        queryInteraction("organism:Escherichia coli");
        queryInteraction("organism:562");
        queryInteraction("Genetic");
        queryInteraction("\"MI:0045\"");
        queryInteraction("experiment_type:\"MI:0045\"");
        queryInteraction("experiment_type:Genetic");
        queryInteraction("pmid:11821039");
        queryInteraction("database:DIP");
    }

    /**
     * Validates Query.
     */
    private void queryInteraction(String terms) throws QueryException,
            IOException {
        LuceneReader indexReader = new LuceneReader();
        try {
            Hits hits = indexReader.executeQuery(terms);
            assertEquals(1, hits.length());
            Document doc = hits.doc(0);
            Field id = doc.getField(LuceneConfig.FIELD_CPATH_ID);
            assertEquals("4", id.stringValue());
        } finally {
            //  Make sure to always close the IndexReader Object.
            indexReader.close();
        }
    }

    /**
     * Tests the XML Stripper Utility.
     *
     * @throws Exception All Exceptions.
     */
    public void testStripper() throws Exception {
        testName = "Test the XML Markup Stripper";
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_mi/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);

        //  Test the XML Stripper.
        String text = XmlStripper.stripTags(xml, true);
        int index = text.indexOf("classical two hybrid");
        assertTrue(index > 0);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the Lucene Index/Query functionality:  " + testName;
    }
}
