package org.mskcc.pathdb.test.lucene;

import junit.framework.TestCase;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.pathdb.lucene.PsiInteractorExtractor;
import org.mskcc.pathdb.model.ProteinWithWeight;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tests the PsiInteractorExtractor.
 *
 * @author Ethan Cerami
 */
public class TestPsiInteractorExtractor extends TestCase {

    /**
     * Tests the Interactor Extractor.
     *
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
        ProteinWithWeight proteinWithWeight = (ProteinWithWeight)
                iterator.next();
        ProteinInteractorType protein = proteinWithWeight.getProtein();
        assertEquals("2", protein.getId());
    }
}
