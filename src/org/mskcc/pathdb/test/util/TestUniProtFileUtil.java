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
        File file = new File ("testData/uniprot/uniprot_test_human.dat");
        File target = UniProtFileUtil.getOrganismSpecificFileName(file, "uniprot_ac", "txt");
        assertEquals (target.getName(), "uniprot_ac_human.txt");
        try {
            file = new File ("testData/uniprot/uniprot.dat");
            target = UniProtFileUtil.getOrganismSpecificFileName(file, "entrez_gene", "txt");
            fail ("Illegal Argument Exception should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
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
