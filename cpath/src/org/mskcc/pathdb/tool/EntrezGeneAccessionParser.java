package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.file.FileUtil;

import java.io.*;
import java.util.ArrayList;

/**
 * Utility class for parsing Entrez Gene Accession files
 * and converting into appropriate ID Mapping files.
 *
 * @author Ethan Cerami.
 */
public class EntrezGeneAccessionParser {
    private int numMappingRecords = 0;
    private ProgressMonitor pMonitor;

    /**
     * Empty Arg Constructor.
     */
    public EntrezGeneAccessionParser() {
    }

    /**
     * Constructor.
     *
     * @param pMonitor  Progress Monitor.
     */
    public EntrezGeneAccessionParser(ProgressMonitor pMonitor) {
        this.pMonitor = pMonitor;
    }

    /**
     * Parses a UniProt File and creates two ID mapping files:
     * one for UniProt Accession Numbers --> Entrez Gene Ids; and
     * one for RefSeq IDs --> Entrez Gene Ids.
     *
     * @param gene2AccessionFile        Gene2Accession File.
     * @param acOutFile                 File to store UniProt AC --> Entrez Gene ID mappings.
     * @param refSeqOutFile             File to store RefSeq --> Entrez Gene ID mappings.
     * @return                          Number of unique ID mapping records created.
     * @throws IOException              Error Reading File.
     */
    public int createIdMappingFiles(File gene2AccessionFile, File acOutFile,
            File refSeqOutFile) throws IOException {
        FileReader reader= null;
        FileWriter acWriter = null;
        FileWriter refSeqWriter = null;
        try {
            reader = new FileReader (gene2AccessionFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();

            acWriter = new FileWriter(acOutFile);
            acWriter.write("ENTREZ_GENE\tUNIPROT\n");
            refSeqWriter = new FileWriter(refSeqOutFile);
            refSeqWriter.write("ENTREZ_GENE\tREF_SEQ\n");

            ArrayList<String> acList;
            ArrayList <String> entrezGeneList;
            ArrayList <String> refSeqList;
            while (line != null) {
                if (pMonitor != null) {
                    pMonitor.incrementCurValue();
                    ConsoleUtil.showProgress(pMonitor);
                }
                String cols[] = line.split("\t");
                String ncbiOrganismTaxId = cols[0];
                if (ncbiOrganismTaxId.equalsIgnoreCase("9606")) {
                    String entrezGeneId = cols[1];
                    String proteinAc = cols[5];

                    acList = new ArrayList<String>();
                    entrezGeneList = new ArrayList<String>();
                    refSeqList = new ArrayList<String>();

                    entrezGeneList.add(entrezGeneId);
                    if (proteinAc.contains("_")) {
                        refSeqList.add(proteinAc);
                    } else {
                        acList.add(proteinAc);
                    }
                    createMapping(acList, entrezGeneList, refSeqList, acWriter, refSeqWriter);
                }
                line = bufferedReader.readLine();
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (acWriter != null) {
                acWriter.close();
            }
            if (refSeqWriter != null) {
                refSeqWriter.close();
            }
        }
        return numMappingRecords;
    }

    /**
     * Command Line Usage.
     * @param args          Must include UniProt File Name.
     * @throws IOException  IO Error.
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println ("command line usage:  entrez_gene.pl <gene2accesion file>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File uniProtFile = new File (args[0]);
        File acOutFile = new File(uniProtFile.getParentFile(), "uniprot_2_entrez_gene_id.txt");
        File refSeqOutFile = new File (uniProtFile.getParentFile(), "refseq_2_entrez_gene_id.txt");

        System.out.println ("Reading data from:  " + uniProtFile.getAbsolutePath());
        int numLines = FileUtil.getNumLines(uniProtFile);
        System.out.println (" --> total number of lines:  " + numLines);
        pMonitor.setMaxValue(numLines);
        System.out.println ("Writing out to:  " + acOutFile.getAbsolutePath());
        System.out.println ("Writing out to:  " + refSeqOutFile.getAbsolutePath());
        UniProtParser parser = new UniProtParser(pMonitor);
        int numRecords = parser.createIdMappingFiles(uniProtFile, acOutFile, refSeqOutFile);
        System.out.println ("Total number of id mappings created:  " + numRecords);
    }

    private void createMapping(ArrayList <String> acList, ArrayList <String> entrezGeneList,
        ArrayList <String> refSeqList, FileWriter acWriter, FileWriter refSeqWriter)
            throws IOException {
        for (String ac:  acList) {
            for (String entrezGeneId:  entrezGeneList) {
                if (!ac.equalsIgnoreCase("-")) {
                    ac = stripOutVersionNumber (ac);
                    acWriter.write (entrezGeneId + "\t" + ac + "\n");
                    numMappingRecords++;
                }
            }
        }
        for (String refSeqId:  refSeqList) {
            for (String entrezGeneId:  entrezGeneList) {
                if (!refSeqId.equalsIgnoreCase("-")) {
                    refSeqId = stripOutVersionNumber(refSeqId);
                    refSeqWriter.write (entrezGeneId + "\t" + refSeqId + "\n");
                    numMappingRecords++;
                }
            }
        }
    }

    private String stripOutVersionNumber(String id) {
        if (id.contains(".")) {
            String parts[] = id.split("\\.");
            id = parts[0];
        }
        return id;
    }
}
