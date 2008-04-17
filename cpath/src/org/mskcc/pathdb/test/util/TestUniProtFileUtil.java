package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.pathdb.util.file.UniProtFileUtil;

import java.io.File;

/**
 * Tests the UniProtFileUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestUniProtFileUtil extends TestCase {

    /**
     * Tests the UniProt File Util.
     */
    public void testUnitProtFileUtil () {
        File file = new File ("uniprot_sprot_human.dat");
        String target = UniProtFileUtil.getOrganismSpecificFileName(file, "entrez_gene");
        try {
            file = new File ("uniprot.dat");
            target = UniProtFileUtil.getOrganismSpecificFileName(file, "entrez_gene");
            fail ("Illegal Argument Exception should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
        assertEquals (target, "entrez_gene_human.txt");
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the UniProt File Utility Class";
    }
}
