package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.util.file.UniProtFileUtil;
import org.mskcc.pathdb.util.file.FileUtil;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.*;
import java.util.ArrayList;

/**
 * Utility class for parsing UniProt Flat Text Files, and converting into appropriate
 * ID Mapping files.
 *
 * @author Ethan Cerami.
 */
public class UniProtParser {
    private int numMappingRecords = 0;
    private ProgressMonitor pMonitor;
    private final static String GENE_ID = "DR   GeneID;";
    private final static String REF_SEQ_ID = "DR   RefSeq;";

    /**
     * Empty Arg Constructor.
     */
    public UniProtParser() {
    }

    /**
     * Constructor.
     *
     * @param pMonitor  Progress Monitor.
     */
    public UniProtParser (ProgressMonitor pMonitor) {
        this.pMonitor = pMonitor;
    }

    /**
     * Parses a UniProt File and creates two ID mapping files:
     * one for UniProt Accession Numbers --> Entrez Gene Ids; and
     * one for RefSeq IDs --> Entrez Gene Ids.
     *
     * @param uniProtFile       UniProt Flat File.
     * @param acOutFile         File to store UniProt AC --> Entrez Gene ID mappings.
     * @param refSeqOutFile     File to store RefSeq --> Entrez Gene ID mappings.
     * @return                  Number of unique ID mapping records created.
     * @throws IOException      Error Reading File.
     */
    public int createIdMappingFiles(File uniProtFile, File acOutFile,
            File refSeqOutFile) throws IOException {
        FileReader reader= null;
        FileWriter acWriter = null;
        FileWriter refSeqWriter = null;
        try {
            reader = new FileReader (uniProtFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();

            acWriter = new FileWriter(acOutFile);
            acWriter.write("ENTREZ_GENE\tUNIPROT\n");
            refSeqWriter = new FileWriter(refSeqOutFile);
            refSeqWriter.write("ENTREZ_GENE\tREF_SEQ\n");

            ArrayList<String> acList = new ArrayList<String>();
            ArrayList <String> entrezGeneList = new ArrayList<String>();
            ArrayList <String> refSeqList = new ArrayList<String>();
            while (line != null) {
                if (pMonitor != null) {
                    pMonitor.incrementCurValue();
                    ConsoleUtil.showProgress(pMonitor);
                }
                if (line.startsWith("ID")) {
                    createMapping(acList, entrezGeneList, refSeqList, acWriter, refSeqWriter);
                    acList = new ArrayList<String>();
                    entrezGeneList = new ArrayList<String>();
                    refSeqList = new ArrayList<String>();
                } else if (line.startsWith("AC")) {
                    line = line.substring(5);
                    String parts[] = line.split(";");
                    for (String part:  parts) {
                        part = part.trim();
                        acList.add(part);
                    }
                } else if (line.startsWith(GENE_ID)) {
                    extractXref(line, GENE_ID, entrezGeneList);
                } else if (line.startsWith(REF_SEQ_ID)) {
                    extractXref(line, REF_SEQ_ID, refSeqList);
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
            System.out.println ("command line usage:  uniprot2ids.pl <uniprot_file.dat>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File uniProtFile = new File (args[0]);
        File acOutFile = UniProtFileUtil.getOrganismSpecificFileName(uniProtFile, "uniprot_ac",
                "txt");
        File refSeqOutFile = UniProtFileUtil.getOrganismSpecificFileName(uniProtFile, "refseq",
                "txt");

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

    private void extractXref(String line, String match, ArrayList<String> idList) {
        line = line.substring(match.length());
        line = line.replaceAll("-.", "");
        String parts[] = line.split(";");
        for (String part:  parts) {
            part = part.trim();
            if (part.length() > 0) {
                idList.add(part);
            }
        }
    }

    private void createMapping(ArrayList <String> acList, ArrayList <String> entrezGeneList,
        ArrayList <String> refSeqList, FileWriter acWriter, FileWriter refSeqWriter)
            throws IOException {
        for (String ac:  acList) {
            for (String entrezGeneId:  entrezGeneList) {
                acWriter.write (entrezGeneId + "\t" + ac + "\n");
                numMappingRecords++;
            }
        }
        for (String refSeqId:  refSeqList) {
            for (String entrezGeneId:  entrezGeneList) {
                if (refSeqId.contains(".")) {
                    String parts[] = refSeqId.split("\\.");
                    refSeqId = parts[0];
                }
                refSeqWriter.write (entrezGeneId + "\t" + refSeqId + "\n");
                numMappingRecords++;
            }
        }
    }
}