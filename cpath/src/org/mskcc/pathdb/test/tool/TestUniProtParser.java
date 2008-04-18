package org.mskcc.pathdb.test.tool;

import junit.framework.TestCase;
import org.mskcc.pathdb.tool.UniProtParser;
import org.mskcc.pathdb.util.file.UniProtFileUtil;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Tests the PaseUnitProt Class.
 *
 * @author Ethan Cerami.
 */
public class TestUniProtParser extends TestCase {

    /**
     * Tests the UniProt Parser.
     * @throws IOException IO Error.
     */
    public void testUniProtParser() throws IOException {
        UniProtParser parser = new UniProtParser();
        File inFile = new File ("testData/uniprot/uniprot_test_human.dat");
        File acOutFile = UniProtFileUtil.getOrganismSpecificFileName(inFile, "uniprot_ac");
        File refSeqOutFile = UniProtFileUtil.getOrganismSpecificFileName(inFile, "refseq");
        int numRecords = parser.createIdMappingFiles(inFile, acOutFile, refSeqOutFile);
        assertEquals (19, numRecords);

        //  Validate the AC Out File.
        FileReader acReader = new FileReader(acOutFile);
        BufferedReader buf = new BufferedReader (acReader);
        String line = buf.readLine();
        validateMapping(buf.readLine(), "7529", "P31946");
        validateMapping(buf.readLine(), "7531", "P62258");
        validateMapping(buf.readLine(), "7531", "P29360");
        validateMapping(buf.readLine(), "7531", "P42655");
        validateMapping(buf.readLine(), "7531", "Q53XZ5");
        validateMapping(buf.readLine(), "7531", "Q63631");
        validateMapping(buf.readLine(), "7531", "Q7M4R4");
        buf.close();

        //  Validate the RefSeq Out File.
        FileReader refSeqReader = new FileReader(refSeqOutFile);
        buf = new BufferedReader (refSeqReader);
        line = buf.readLine();
        validateMapping(buf.readLine(), "7529", "NP_003395");
        validateMapping(buf.readLine(), "7529", "NP_647539");
        validateMapping(buf.readLine(), "7531", "NP_006752");
        buf.close();

        //  Don't forget to delete the temp files.
        acOutFile.delete();
        refSeqOutFile.delete();
        
    }

    private void validateMapping(String line, String left, String right) {
        String parts[] = line.split("\t");
        assertEquals (left, parts[0]);
        assertEquals (right, parts[1]);
    }
}
