package org.mskcc.pathdb.test.schemas.biopax;

import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssemblyFactory;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssembly;
import org.mskcc.pathdb.schemas.binary_interaction.util.BinaryInteractionUtil;
import junit.framework.TestCase;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 *  Tests the Binary Interaction Assembly Factory.
 */
public class TestBinaryInteractionAssemblyFactory extends TestCase {

    /**
     * Tests the Binary Interaction Assembly Factory.
     * @throws IOException IO Errors.
     */
    public void testBinaryAssembly() throws IOException {
        FileReader reader = new FileReader
                ("testData/biopax/biopax-example-proteomics-protein-interaction.owl");
        //        FileReader reader = new FileReader
        //                ("testData/biopax/Reactome_excerpt.owl");
        //        FileReader reader = new FileReader
        //                ("testData/biopax/biopax-example-short-pathway.owl");
        BufferedReader buf = new BufferedReader (reader);
        StringBuffer lineBuffer = new StringBuffer();
        String line = buf.readLine();
        while (line != null) {
            lineBuffer.append(line);
            line = buf.readLine();
        }

        List<String> rulesList =  BinaryInteractionUtil.getRuleTypes();
        BinaryInteractionAssembly binaryAssembly = BinaryInteractionAssemblyFactory.createAssembly
                (BinaryInteractionAssemblyFactory.AssemblyType.SIF, rulesList,
                        lineBuffer.toString());
        String sifString = binaryAssembly.getBinaryInteractionString();
        System.out.println(sifString);
        int index = sifString.indexOf("protein2\tParticipates.Interaction\tprotein1");
        assertTrue (index > -1);
    }
}
