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
package org.mskcc.pathdb.test.lucene;

import junit.framework.TestCase;
import org.apache.lucene.document.Field;
import org.mskcc.pathdb.lucene.IndexFactory;
import org.mskcc.pathdb.lucene.ItemToIndex;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.lucene.PsiInteractionToIndex;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * Tests the ItemToIndex factory and classes.
 *
 * @author Ethan Cerami
 */
public class TestItemToIndex extends TestCase {

    /**
     * Tests the ItemToIndex Factory and an Interactor Item.
     *
     * @throws Exception All Exceptions.
     */
    public void testInteraction() throws Exception {
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (4, 1, xdebug);

        ItemToIndex item = IndexFactory.createItemToIndex(4, assembly);

        assertTrue(item instanceof PsiInteractionToIndex);
        int numFields = item.getNumFields();
        assertEquals(8, numFields);

        String fieldNames[] = {
            PsiInteractionToIndex.FIELD_INTERACTOR,
            LuceneConfig.FIELD_INTERACTOR_ID,
            PsiInteractionToIndex.FIELD_ORGANISM,
            PsiInteractionToIndex.FIELD_PMID,
            PsiInteractionToIndex.FIELD_EXPERIMENT_TYPE,
            PsiInteractionToIndex.FIELD_DATABASE,
            LuceneConfig.FIELD_ALL,
            LuceneConfig.FIELD_INTERACTION_ID,
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
//          System.out.println("Field:  " + name);
//          System.out.println("Actual:  " + value);
//          System.out.println("Expected:  " + fieldValues[i]);
            assertEquals(name, fieldNames[i]);
            assertTrue(value.startsWith(fieldValues[i]));
        }
    }
}