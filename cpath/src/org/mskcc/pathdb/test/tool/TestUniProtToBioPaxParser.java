package org.mskcc.pathdb.test.tool;

import junit.framework.TestCase;
import org.mskcc.pathdb.tool.UniProtToBioPax;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level2.protein;
import org.biopax.paxtools.model.level2.bioSource;
import org.biopax.paxtools.model.level2.unificationXref;
import org.biopax.paxtools.model.level2.xref;
import org.biopax.paxtools.io.jena.JenaIOHandler;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * Tests the UniProtToBioPax Parser Class.
 *
 * @author Ethan Cerami.
 */
public class TestUniProtToBioPaxParser extends TestCase {

    /**
     * Tests the Parser.
     * @throws IllegalAccessException       BioPAX Write Error.
     * @throws IOException                  IO Error.
     * @throws InvocationTargetException    BioPAX Write Error.
     */
    public void testParser() throws IllegalAccessException, IOException, InvocationTargetException {
        UniProtToBioPax parser = new UniProtToBioPax();
        File inFile = new File ("testData/uniprot/uniprot_test_human.dat");
        int numRecords = parser.convertToBioPax(inFile);
        assertEquals (4, numRecords);
        File bpFile = new File ("testData/uniprot/bp_0_human.owl");
        FileInputStream inStream = new FileInputStream (bpFile);
        JenaIOHandler jenaIOHandler = new JenaIOHandler();
        Model model = jenaIOHandler.convertFromOWL(inStream);
        Map<String, BioPAXElement> bpMap = model.getIdMap();
        protein targetProtein = (protein) bpMap.get("1433B_HUMAN");
        assertEquals ("1433B_HUMAN", targetProtein.getSHORT_NAME());
        assertEquals ("14-3-3 protein beta/alpha", targetProtein.getNAME());
        bioSource organismSource = targetProtein.getORGANISM();
        assertEquals ("Homo sapiens", organismSource.getNAME());
        unificationXref taxonRef = organismSource.getTAXON_XREF();
        assertEquals ("9606", taxonRef.getID());
        assertEquals ("NCBI_taxonomy", taxonRef.getDB());

        Set <xref> xrefSet = targetProtein.getXREF();
        Iterator <xref> iteratorXref = xrefSet.iterator();
        int entrezGeneIdFound = 0;
        int hugoGeneSymbolFound = 0;
        int uniProtAcFound = 0;
        int refSeqIdFound = 0;
        while (iteratorXref.hasNext()) {
            xref currentXRef = iteratorXref.next();
            String db = currentXRef.getDB();
            String id = currentXRef.getID();
            entrezGeneIdFound += validateXRef("ENTREZ_GENE", db, "7529", id);
            hugoGeneSymbolFound += validateXRef("GENE_SYMBOL", db, "YWHAB", id);
            uniProtAcFound += validateXRef("UNIPROT", db, "P31946", id);
            refSeqIdFound += validateMultipleXRef("REF_SEQ", db, "NP_003395", "NP_647539", id);
        }
        assertTrue ("Failed to find Entrez Gene ID", entrezGeneIdFound == 1);
        assertTrue ("Failed to find HUGO Gene Symbol", hugoGeneSymbolFound == 1);
        assertTrue ("Failed to find UniProt Accession Number", uniProtAcFound == 1);
        assertTrue ("Failed to find RefSeq ID", refSeqIdFound ==2);

        Set<String> commentSet = targetProtein.getCOMMENT();
        Iterator iterator = commentSet.iterator();
        while (iterator.hasNext()) {
            String comment = (String) iterator.next();
            assertTrue (comment.startsWith("FUNCTION: Adapter protein implicated in the " +
                    "regulation of a large spectrum"));
        }

        //  Get another target protein
        targetProtein = (protein) bpMap.get("1433B_HUMAN");
        Set <String> synSet = targetProtein.getSYNONYMS();
        assertTrue ("Missing synonym", synSet.contains("Protein kinase C inhibitor protein 1"));
        assertTrue ("Missing synonym", synSet.contains("KCIP-1"));
        assertTrue ("Missing synonym", synSet.contains("Protein 1054"));

        targetProtein = (protein) bpMap.get("1433F_HUMAN");
        Iterator<String> commentIterator = targetProtein.getCOMMENT().iterator();
        while (commentIterator.hasNext()) {
            String comment = commentIterator.next();
            assertTrue (comment.contains("COPYRIGHT:  Protein annotation"));
            assertTrue (comment.contains("GENE SYNONYMS: YWHA1"));
        }
        bpFile.delete();
    }

    private int validateXRef(String expectedDb, String actualDb,
            String expectedId, String actualId) {
        if (actualDb.equals(expectedDb)) {
            assertEquals (expectedId, actualId);
            return 1;
        }
        return 0;
    }

    private int validateMultipleXRef(String expectedDb, String actualDb,
            String expectedId1, String expectedId2, String actualId) {
        if (actualDb.equals(expectedDb)) {
            if (actualId.equals(expectedId1) || actualId.equals(expectedId2)) {
                return 1;
            }
        }
        return 0;
    }
}