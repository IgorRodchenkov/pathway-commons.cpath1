package org.mskcc.pathdb.test.indexer;

import junit.framework.TestCase;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.pathdb.lucene.PsiInteractorExtractor;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.Iterator;
import java.util.ArrayList;

/**
 * Tests the PsiInteractorExtractor.
 *
 * @author Ethan Cerami
 */
public class TestPsiInteractorExtractor extends TestCase {

    /**
     * Tests the Interactor Extractor.
     * @throws Exception All Exceptions.
     */
    public void testExtractor() throws Exception {
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (4, 1, xdebug);
        EntrySet entrySet = (EntrySet) assembly.getXmlObject();
        PsiInteractorExtractor interactorExtractor = new PsiInteractorExtractor
                (entrySet, "chaperonin +interaction_type:Genetic",
                        new XDebug());
        ArrayList proteins = interactorExtractor.getSortedInteractors();

        assertEquals(1, proteins.size());
        Iterator iterator = proteins.iterator();
        ProteinInteractorType protein = (ProteinInteractorType) iterator.next();
        assertEquals("2", protein.getId());
    }
}
