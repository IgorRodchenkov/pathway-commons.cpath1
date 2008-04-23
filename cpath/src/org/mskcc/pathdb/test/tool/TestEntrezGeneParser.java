package org.mskcc.pathdb.test.tool;

import junit.framework.TestCase;
import org.mskcc.pathdb.tool.EntrezGeneAccessionParser;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Tests the Entrez Gene Parser.
 *
 * @author Ethan Cerami.
 */
public class TestEntrezGeneParser extends TestCase {

    public void testEntrezGeneParser() throws IOException {
        EntrezGeneAccessionParser parser = new EntrezGeneAccessionParser();
        File inFile = new File ("testData/entrez_gene/gene2accession_test.txt");
        File acOutFile = new File (inFile.getParentFile(), "ac_out.txt");
        File refSeqOutFile = new File (inFile.getParentFile(), "ref_seq_out.txt");
        parser.createIdMappingFiles(inFile, acOutFile, refSeqOutFile);

        //  Validate the AC Out File.
        FileReader acReader = new FileReader(acOutFile);
        BufferedReader buf = new BufferedReader (acReader);
        String line = buf.readLine();
        validateMapping(buf.readLine(), "1", "EAW72575");
        validateMapping(buf.readLine(), "1", "P04217");
        validateMapping(buf.readLine(), "1", "Q68CK0");
        validateMapping(buf.readLine(), "1", "Q7Z3U3");
        buf.close();

        //  Validate the RefSeq Out File.
        FileReader refSeqReader = new FileReader(refSeqOutFile);
        buf = new BufferedReader (refSeqReader);
        line = buf.readLine();
        validateMapping(buf.readLine(), "1", "NP_570602");
        validateMapping(buf.readLine(), "1", "NP_570602");

        buf.close();

        //  Don't forget to delete the temp files.
        //acOutFile.delete();
        //refSeqOutFile.delete();
    }

    private void validateMapping(String line, String left, String right) {
        String parts[] = line.split("\t");
        assertEquals (left, parts[0]);
        assertEquals (right, parts[1]);
    }
}