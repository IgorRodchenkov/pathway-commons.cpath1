package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.file.UniProtFileUtil;
import org.mskcc.pathdb.util.file.FileUtil;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level2.*;
import org.biopax.paxtools.impl.level2.Level2FactoryImpl;
import org.biopax.paxtools.io.simpleIO.SimpleExporter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for parsing UniProt Flat Text Files, and converting into appropriate
 * BioPAX Physical Entities.
  *
 * @author Ethan Cerami.
 */
public class UniProtToBioPax {
    private ProgressMonitor pMonitor;
    private static Level2Factory bpFactory = new Level2FactoryImpl();
    private static final int RECORDS_PER_BATCH = 1000;
    private int totalNumProteinsProcessed = 0;
    private int numProteinsInCurrentBatch = 0;
    private int batchNumber = 0;
    private Model bpModel;

    /**
     * Empty Arg Constructor.
     */
    public UniProtToBioPax() {
    }

    /**
     * Constructor.
     *
     * @param pMonitor  Progress Monitor.
     */
    public UniProtToBioPax(ProgressMonitor pMonitor) {
        this.pMonitor = pMonitor;
    }

    /**
     * Parses a UniProt File and converts to BioPAX.
     *
     * @param uniProtFile       UniProt Flat File.
     * @return                  Number of physical entity records created.
     * @throws java.io.IOException      Error Reading File.
     * @throws IllegalAccessException   BioPAX Output Error.
     * @throws InvocationTargetException BioPAX Output Error.
     */
    public int convertToBioPax(File uniProtFile) throws IOException,
            IllegalAccessException, InvocationTargetException {
        FileReader reader= null;

        try {
            reader = new FileReader (uniProtFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();

            protein currentProtein = null;
            bpModel = bpFactory.createModel();
            HashMap dataElements = new HashMap();
            numProteinsInCurrentBatch = 0;
            while (line != null) {
                if (pMonitor != null) {
                    pMonitor.incrementCurValue();
                    ConsoleUtil.showProgress(pMonitor);
                }
                if (line.startsWith ("//")) {
                    StringBuffer name = (StringBuffer) dataElements.get("DE");
                    StringBuffer id = (StringBuffer) dataElements.get("ID");
                    StringBuffer organismName = (StringBuffer) dataElements.get("OS");
                    StringBuffer organismTaxId = (StringBuffer) dataElements.get("OX");
                    StringBuffer comments = (StringBuffer) dataElements.get("CC");
                    StringBuffer geneName = (StringBuffer) dataElements.get("GN");
                    StringBuffer acNames = (StringBuffer) dataElements.get("AC");
                    StringBuffer xrefs = (StringBuffer) dataElements.get("DR");

                    currentProtein = bpFactory.createProtein();
                    String idParts[] = id.toString().split("\\s");
                    String shortName = idParts[0];
                    currentProtein.setSHORT_NAME(shortName);
                    currentProtein.setRDFId(shortName);

                    setNameAndSynonyms(currentProtein, name.toString());
                    setOrganism(organismName.toString(), organismTaxId.toString(),
                            currentProtein, bpModel);
                    String geneSyns = null;
                    if (geneName != null) {
                        geneSyns= setGeneSymbolAndSynonyms(geneName.toString(),
                                currentProtein, bpModel);
                    }
                    if (comments != null) {
                        setComments (comments.toString(), geneSyns, currentProtein);
                    }
                    setUniProtAccessionNumbers(acNames.toString(), currentProtein, bpModel);
                    if (xrefs != null) {
                        setXRefs (xrefs.toString(), currentProtein, bpModel);
                    }
                    bpModel.add(currentProtein);
                    dataElements = new HashMap();
                    numProteinsInCurrentBatch++;
                    
                    if (numProteinsInCurrentBatch >= RECORDS_PER_BATCH) {
                        streamToFile(uniProtFile);
                    }
                } else {
                    String key = line.substring (0, 2);
                    String data = line.substring(5);
                    if (data.startsWith("-------") ||
                            data.startsWith("Copyrighted") ||
                            data.startsWith("Distributed")) {
                        //  do nothing here...
                    } else {
                        if (dataElements.containsKey(key)) {
                            StringBuffer existingData = (StringBuffer) dataElements.get(key);
                            existingData.append (" " + data);
                        } else {
                            dataElements.put(key, new StringBuffer (data));
                        }
                    }
                }
                line = bufferedReader.readLine();
            }
            if (numProteinsInCurrentBatch > 0) {
                streamToFile(uniProtFile);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return totalNumProteinsProcessed;
    }

    private void setNameAndSynonyms (protein currentProtein, String deLine) {
        String parts[] = deLine.split("\\(");
        currentProtein.setNAME(parts[0].trim());
        if (parts.length > 1) {
            for (int i=1; i<parts.length; i++) {
                String syn = parts[i].trim();
                syn = syn.replaceAll("\\)", "");
                syn = syn.replaceAll("\\.", "");
                currentProtein.addSYNONYMS(syn);
            }
        }
    }

    /**
     * Streams BioPAX to a file via PaxTools.
     */
    private void streamToFile(File uniProtFile) throws IOException,
            IllegalAccessException, InvocationTargetException {
        File bpOutFile = UniProtFileUtil.getOrganismSpecificFileName(uniProtFile,
                "bp_" + batchNumber, "owl");
        FileOutputStream out = new FileOutputStream (bpOutFile);
        SimpleExporter exporter = new SimpleExporter(BioPAXLevel.L2);
        exporter.convertToOWL(bpModel, out);
        out.close();
        totalNumProteinsProcessed += numProteinsInCurrentBatch;
        numProteinsInCurrentBatch = 0;
        bpModel = bpFactory.createModel();
        batchNumber++;
    }

    /**
     * Sets the Current Organism Information.
     */
    private void setOrganism(String organismName, String organismTaxId,
            protein currentProtein, Model bpModel) {
        organismTaxId = organismTaxId.replaceAll(";", "");
        String parts[] = organismTaxId.split("=");
        String taxId = parts[1];
        parts = organismName.split("\\(");
        String name = parts[0].trim();
        Map<String, BioPAXElement> bpMap = bpModel.getIdMap();
        String rdfId = "BIO_SOURCE_NCBI_" + taxId;
        if (bpMap.containsKey(rdfId)) {
            bioSource currentBioSource = (bioSource) bpMap.get(rdfId);
            currentProtein.setORGANISM(currentBioSource);
        } else {
            bioSource currentBioSource = bpFactory.createBioSource();
            currentBioSource.setNAME(name);
            unificationXref taxonXref = bpFactory.createUnificationXref();
            taxonXref.setDB("NCBI_taxonomy");
            taxonXref.setID(taxId);
            taxonXref.setRDFId("TAXON_NCBI_" + taxId);
            currentBioSource.setTAXON_XREF(taxonXref);
            currentBioSource.setRDFId(rdfId);
            currentProtein.setORGANISM(currentBioSource);
            bpModel.add(currentBioSource);
            bpModel.add(taxonXref);
        }
    }

    /**
     * Sets Multiple Comments.
     */
    private void setComments (String comments, String geneSynonyms, protein currentProtein) {
        String commentParts[] = comments.split("-!- ");
        StringBuffer reducedComments = new StringBuffer();
        for (int i=0; i<commentParts.length; i++) {
            String currentComment = commentParts[i];
            //  Filter out the Interaction comments.
            //  We don't want these, as cPath itself will contain the interactions.
            if (!currentComment.startsWith("INTERACTION")) {
                currentComment = currentComment.replaceAll("     ", " ");
                reducedComments.append (currentComment);
            }
        }
        if (geneSynonyms != null && geneSynonyms.length() > 0) {
            reducedComments.append (" GENE SYNONYMS:" + geneSynonyms + ".");
        }
        if (reducedComments.length() > 0) {
            reducedComments.append (" COPYRIGHT:  Protein annotation is derived from the "
                    + "UniProt Consortium (http://www.uniprot.org/).  Distributed under "
                    + "the Creative Commons Attribution-NoDerivs License.");
        }
        HashSet <String> commentSet = new HashSet();
        commentSet.add(reducedComments.toString());
        currentProtein.setCOMMENT(commentSet);
    }

    /**
     * Sets UniProt Accession Numbers (can be 0, 1 or N).
     * However, we only take the 0th element, which is referred in UniProt as the
     * "Primary Accession Number".
     */
    private void setUniProtAccessionNumbers (String acNames, protein currentProtein,
            Model bpModel) {
        String acList[] = acNames.split(";");
        if (acList.length > 0) {
            String ac = acList[0].trim();
            setUnificationXRef(ExternalDatabaseConstants.UNIPROT, ac, currentProtein, bpModel);
        }
    }

    /**
     * Sets Multiple Types of XRefs, e.g. Entrez Gene ID and RefSeq.
     */
    private void setXRefs (String acNames, protein currentProtein,
            Model bpModel) {
        String xrefList[] = acNames.split("\\.");

        for (int i=0; i<xrefList.length; i++) {
            String xref = xrefList[i].trim();
            if (xref.startsWith("GeneID")) {
                xref = xref.replaceAll("; -.", "");
                String parts[] = xref.split(";");
                String entrezGeneId = parts[1];
                setRelationshipXRef(ExternalDatabaseConstants.ENTREZ_GENE,
                        entrezGeneId, currentProtein, bpModel);
            } else if (xref.startsWith("RefSeq")) {
                xref = xref.replaceAll("; -.", "");
                String parts[] = xref.split(";");
                String refSeqId = parts[1];
                if (refSeqId.contains(".")) {
                    parts = refSeqId.split("\\.");
                    refSeqId = parts[0];
                }
                setRelationshipXRef(ExternalDatabaseConstants.REF_SEQ,
                        refSeqId, currentProtein, bpModel);
            }
        }
    }

    /**
     * Sets the HUGO Gene Symbol and Synonyms.
     */
    private String setGeneSymbolAndSynonyms(String geneName, protein currentProtein,
            Model bpModel) {
        StringBuffer synBuffer = new StringBuffer();
        String parts[] = geneName.split(";");
        for (int i=0; i<parts.length; i++) {
            String subParts[] = parts[i].split("=");
            // Set HUGO Gene Name
            if (subParts[0].trim().equals("Name")) {
                geneName = subParts[1];
                setRelationshipXRef(ExternalDatabaseConstants.GENE_SYMBOL,
                        geneName, currentProtein, bpModel);
            } else if (subParts[0].trim().equals("Synonyms")) {
                String synList[] = subParts[1].split(",");
                for (int j=0; j<synList.length; j++) {
                    String currentSynonym = synList[j].trim();
                    synBuffer.append(" " + currentSynonym);
                }
            }
        }
        return synBuffer.toString();
    }

    /**
     * Sets Relationship XRefs.
     */
    private void setRelationshipXRef(String dbName, String id, protein currentProtein,
            Model bpModel) {
        id = id.trim();
        Map<String, BioPAXElement> bpMap = bpModel.getIdMap();
        String rdfId = dbName + "_" +  id;
        if (bpMap.containsKey(rdfId)) {
            relationshipXref rXRef = (relationshipXref) bpMap.get(rdfId);
            currentProtein.addXREF(rXRef);
        } else {
            relationshipXref rXRef = bpFactory.createRelationshipXref();
            rXRef.setRDFId(rdfId);
            rXRef.setDB(dbName);
            rXRef.setID(id);
            bpModel.add(rXRef);
            currentProtein.addXREF(rXRef);
        }
    }

    /**
     * Sets Unification XRefs.
     */
    private void setUnificationXRef(String dbName, String id, protein currentProtein,
            Model bpModel) {
        id = id.trim();
        Map<String, BioPAXElement> bpMap = bpModel.getIdMap();
        String rdfId = dbName + "_" +  id;
        if (bpMap.containsKey(rdfId)) {
            unificationXref rXRef = (unificationXref) bpMap.get(rdfId);
            currentProtein.addXREF(rXRef);
        } else {
            unificationXref rXRef = bpFactory.createUnificationXref();
            rXRef.setRDFId(rdfId);
            rXRef.setDB(dbName);
            rXRef.setID(id);
            bpModel.add(rXRef);
            currentProtein.addXREF(rXRef);
        }
    }

    /**
     * Command Line Usage.
     * @param args          Must include UniProt File Name.
     * @throws java.io.IOException  IO Error.
     * // TODO:  Verify that BioPAX created via this method can be imported into cPath.
     */
    public static void main(String[] args) throws IOException, IllegalAccessException,
            InvocationTargetException {
        if (args.length == 0) {
            System.out.println ("command line usage:  uniprot2biopax.pl <uniprot_file.dat>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File uniProtFile = new File (args[0]);
        System.out.println ("Reading data from:  " + uniProtFile.getAbsolutePath());
        int numLines = FileUtil.getNumLines(uniProtFile);
        System.out.println (" --> total number of lines:  " + numLines);
        pMonitor.setMaxValue(numLines);
        UniProtToBioPax parser = new UniProtToBioPax(pMonitor);
        int numRecords = parser.convertToBioPax(uniProtFile);
        System.out.println ("Total number of protein records processed:  " + numRecords);
        System.out.println ("All files written to:  "
                + uniProtFile.getParentFile().getAbsolutePath());
    }
}
