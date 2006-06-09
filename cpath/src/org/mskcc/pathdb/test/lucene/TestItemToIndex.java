// $Id: TestItemToIndex.java,v 1.19 2006-06-09 19:22:04 cerami Exp $
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
import org.apache.lucene.document.Field;
import org.mskcc.pathdb.lucene.*;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * Tests the ItemToIndex factory and classes.
 *
 * @author Ethan Cerami
 */
public class TestItemToIndex extends TestCase {
    private String testName;

    /**
     * Tests the ItemToIndex Factory and a PSI-MI Record.
     *
     * @throws Exception All Exceptions.
     */
    public void testPsiRecord() throws Exception {
        testName = "Test PSI-MI Record";
        XDebug xdebug = new XDebug();
        DaoCPath dao = DaoCPath.getInstance();
        CPathRecord record = dao.getRecordById(4);
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (record, 1, XmlAssemblyFactory.XML_FULL, xdebug);

        ItemToIndex item = IndexFactory.createItemToIndex(4, assembly);

        assertTrue(item instanceof PsiInteractionToIndex);
        int numFields = item.getNumFields();
        assertEquals(8, numFields);

        String fieldNames[] = {
                PsiInteractionToIndex.FIELD_INTERACTOR,
                LuceneConfig.FIELD_INTERACTOR_ID,
                LuceneConfig.FIELD_ORGANISM,
                PsiInteractionToIndex.FIELD_PMID,
                PsiInteractionToIndex.FIELD_EXPERIMENT_TYPE,
                PsiInteractionToIndex.FIELD_DATABASE,
                LuceneConfig.FIELD_ALL,
                LuceneConfig.FIELD_CPATH_ID,
        };
        String fieldValues[] = {
                "60 kDa chaperonin (Protein Cpn60) (groEL protein) (AMS) DIP "
                        + "339N PIR BVECGL SwissProt P06139 Entrez GI 7429025 RefSeq "
                        + "NP_313151 major prion PrP-Sc protein precursor DIP 1081N "
                        + "PIR UJHYIH Entrez GI 2144854",

                "2 3",

                "562 Escherichia coli 10036 Mesocricetus auratus",

                "pubmed 11821039 pubmed 9174345 pubmed 9174345 pubmed 10587438 "
                        + "pubmed 10089390",

                "Genetic PSI MI:0045 x-ray crystallography PSI MI:0114 x-ray "
                        + "crystallography PSI MI:0114 x-ray crystallography PSI "
                        + "MI:0114 x-ray crystallography PSI MI:0114",

                "DIP 61E",

                "2 60 kDa chaperonin (Protein Cpn60) (groEL protein) (AMS) "
                        + "DIP 339N PIR BVECGL SwissProt P06139 Entrez GI 7429025 "
                        + "RefSeq "
                        + "NP_313151 562 Escherichia coli 3 major prion PrP-Sc protein "
                        + "precursor DIP 1081N PIR UJHYIH Entrez GI 2144854 10036 "
                        + "Mesocricetus auratus DIP_22043X pubmed 11821039 Genetic PSI "
                        + "MI:0045 DIP_22120X pubmed 9174345 x-ray crystallography PSI "
                        + "MI:0114 DIP_70X pubmed 9174345 x-ray crystallography PSI "
                        + "MI:0114 DIP_22018X pubmed 10587438 x-ray crystallography "
                        + "PSI MI:0114 DIP_22044X pubmed 10089390 x-ray "
                        + "crystallography PSI MI:0114 2 3 DIP 61E",

                "4"
        };
        for (int i = 0; i < numFields; i++) {
            Field field = item.getField(i);
            String name = field.name();
            String value = field.stringValue();
            assertEquals(name, fieldNames[i]);
            assertTrue(value.startsWith(fieldValues[i]));
        }
    }

    /**
     * Tests the ItemToIndex Factory and the a BioPAX Record.
     *
     * @throws Exception All Errors.
     */
    public void testBioPaxRecord() throws Exception {
        testName = "Test BioPAX Record";
        XDebug xdebug = new XDebug();

        //  Get the Glycolysis Pathway
        DaoCPath dao = DaoCPath.getInstance();
        CPathRecord record = dao.getRecordById(5);
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (record, 1, XmlAssemblyFactory.XML_FULL, xdebug);
        ItemToIndex item = IndexFactory.createItemToIndex(record.getId(),
                assembly);

        //  Validate that this is actually a BioPAX Record
        assertTrue(item instanceof BioPaxToIndex);

        //  Validate Number of Fields
        int numFields = item.getNumFields();
        assertEquals(4, numFields);

        Field allField = item.getField(0);
        Field idField = item.getField(1);
        Field nameField = item.getField(2);
        Field organismField = item.getField(3);

        //  Validate Individual Fields
        assertTrue(allField.stringValue().startsWith
                ("This pathway is freely available to all users and may be"));
        assertEquals("5", idField.stringValue());
        assertEquals("glycolysis I", nameField.stringValue().trim());
        assertTrue(organismField.stringValue().startsWith
                ("Escherichia coli K-12 83333"));
    }

    /**
     * Tests Removal of cPath IDs.
     *
     * @throws Exception All Exceptions
     */
    public void testRemovalOfcPathIds() throws Exception {
        testName = "Test Remove of cPath Ids Method";
        String before = "TESTING CPATH 123 CBIO CPATH 432 APPLE";
        String after = BioPaxToIndex.removecPathIds(before);
        assertEquals("TESTING CBIO APPLE", after);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can index BioPAX/PSI-MI Records in Lucene:  "
                + testName;
    }
}
