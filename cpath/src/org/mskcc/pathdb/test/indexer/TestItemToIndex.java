package org.mskcc.pathdb.test.indexer;

import junit.framework.TestCase;
import org.apache.lucene.document.Field;
import org.mskcc.pathdb.lucene.IndexFactory;
import org.mskcc.pathdb.lucene.ItemToIndex;
import org.mskcc.pathdb.lucene.LuceneIndexer;
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
     * @throws Exception All Exceptions.
     */
    public void testInteraction() throws Exception {
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (4, 1, xdebug);
        ItemToIndex item = IndexFactory.createItemToIndex (4, assembly);

        assertTrue (item instanceof PsiInteractionToIndex);
        int numFields = item.getNumFields();
        assertEquals (7, numFields);

        String fieldNames[] = {
            PsiInteractionToIndex.FIELD_INTERACTOR,
            PsiInteractionToIndex.FIELD_ORGANISM,
            PsiInteractionToIndex.FIELD_PMID,
            PsiInteractionToIndex.FIELD_INTERACTION_TYPE,
            PsiInteractionToIndex.FIELD_DATABASE,
            LuceneIndexer.FIELD_ALL,
            LuceneIndexer.FIELD_CPATH_ID
        };
        String fieldValues[] = {
            "60 kDa chaperonin (Protein Cpn60) (groEL protein) (AMS) DIP "
                + "339N PIR BVECGL SwissProt P06139 Entrez GI 7429025 RefSeq "
                + "NP_313151 major prion PrP-Sc protein precursor DIP 1081N "
                + "PIR UJHYIH Entrez GI 2144854",
            "562 Escherichia coli 10036 Mesocricetus auratus",

            "pubmed 11821039 pubmed 9174345 pubmed 9174345 pubmed 10587438 "
            + "pubmed 10089390",

            "Genetic PSI MI:0045 x-ray crystallography PSI MI:0114 x-ray "
            + "crystallography PSI MI:0114 x-ray crystallography PSI MI:0114 "
            + "x-ray crystallography PSI MI:0114",

            "DIP 61E",

            "1 1 2 60 kDa chaperonin (Protein Cpn60) (groEL protein) (AMS) "
            + "DIP 339N PIR BVECGL SwissProt P06139 Entrez GI 7429025 RefSeq "
            + "NP_313151 562 Escherichia coli 3 major prion PrP-Sc protein "
            + "precursor DIP 1081N PIR UJHYIH Entrez GI 2144854 10036 "
            + "Mesocricetus auratus DIP_22043X pubmed 11821039 Genetic PSI "
            + "MI:0045 DIP_22120X pubmed 9174345 x-ray crystallography PSI "
            + "MI:0114 DIP_70X pubmed 9174345 x-ray crystallography PSI "
            + "MI:0114 DIP_22018X pubmed 10587438 x-ray crystallography "
            + "PSI MI:0114 DIP_22044X pubmed 10089390 x-ray crystallography "
            + "PSI MI:0114 2 3 DIP 61E",

            "4"
        };
        for (int i=0; i<numFields; i++) {
            Field field = item.getField(i);
            String name = field.name();
            String value = field.stringValue();
            //  System.out.println("Field:  " + name);
            //  System.out.println(".....:  " + value);
            assertEquals (name, fieldNames[i]);
            assertTrue (value.startsWith(fieldValues[i]));
        }
    }
}