package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Command Line Utility to Dump Gene Sets.
 *
 * This classes supports two data formats:
 * 
 * 1)  GSEA GMT: Gene Matrix Transposed file format (*.gmt) Format.
 * Format is described at:
 * http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats
 *
 * 2)  Pathway Commons Gene Set format:  Similar to the GSEA GMT format, except that all
 * participants are micro-encoded with multiple identifiers. For example, each participant
 * is specified as: CPATH_ID:RECORD_TYPE:NAME:UNIPROT_ACCESION:GENE_SYMBOL:ENTREZ_GENE_ID.
 *
 * It also creates the a directory structure like so:
 *
 * - snapshots
 * ---- gsea
 * ------- by_species
 * ------- by_source
 * ---- gene_sets
 * ------- by_species
 * ------- by source
 */
public class ExportGeneSets {
    private ProgressMonitor pMonitor;
    private File outDir;
    private File gseaDir;
    private File pcDir;
    private final static String TAB = "\t";
    private final static String COLON = ":";
    private final static String NA = "NA";
    private static final int BLOCK_SIZE = 1000;

    //  HashMap that will contain multiple open file writers
    private HashMap<String, FileWriter> fileWriters = new HashMap <String, FileWriter>();

    /**
     * Constructor.
     *
     * @param pMonitor Progress Monitor.
     */
    public ExportGeneSets(ProgressMonitor pMonitor, File outDir) throws IOException {
        this.pMonitor = pMonitor;
        this.outDir = outDir;
        ToolInit.initProps();
    }

    /**
     * Initializes the Output Directories. This method creates a structure like so:
     * - gsea
     * ---- by_species
     * ---- by_source
     * - gene_sets
     * ---- by_species
     * ---- by source
     * 
     * @throws IOException IO Errors.
     */
    private void initDirs(File outDir) throws IOException {
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        gseaDir = ExportUtil.initDir (outDir, "gsea");
        pcDir = ExportUtil.initDir (outDir, "gene_sets");
    }

    /**
     * Dumps the Pathway Record in the specified file format.
     */
    public void dumpPathwayRecord(CPathRecord record)
            throws DaoException, IOException {

        //  Gets the Database Term
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        long snapshotId = record.getSnapshotId();
        ExternalDatabaseSnapshotRecord snapshotRecord =
                daoSnapshot.getDatabaseSnapshot(snapshotId);
        String dbTerm = snapshotRecord.getExternalDatabase().getMasterTerm();

        DaoInternalFamily daoInternalFamily = new DaoInternalFamily();
        long[] descendentIds = daoInternalFamily.getDescendentIds(record.getId(),
                CPathRecordType.PHYSICAL_ENTITY);

        ArrayList <CPathRecord> cpathRecordList = new ArrayList <CPathRecord>();

        DaoCPath daoCPath = DaoCPath.getInstance();
        //  Get XRefs for all Participants
        ArrayList <HashMap <String, String>> xrefList =
                new ArrayList <HashMap <String, String>>();
        for (long descendentId : descendentIds) {
            HashMap <String, String> xrefMap = ExportUtil.getXRefMap (descendentId);
            cpathRecordList.add(daoCPath.getRecordById(descendentId));
            xrefList.add (xrefMap);
        }

        //  Dump to both file formats.
        outputGeneSet(record, dbTerm, cpathRecordList, xrefList, ExportUtil.GSEA_OUTPUT);
        outputGeneSet(record, dbTerm, cpathRecordList, xrefList, ExportUtil.PC_OUTPUT);
    }

    /**
     * Actual Output of the Gene Set.
     */
    private void outputGeneSet(CPathRecord record, String dbTerm, ArrayList <CPathRecord>
            cpathRecordList, ArrayList<HashMap<String, String>> xrefList, int outputFormat)
            throws IOException, DaoException {
        StringBuffer line = new StringBuffer();
        line.append (record.getName() + TAB);
        line.append (dbTerm + TAB);

        int numParticipantsOutput = 0;
        for (int i=0; i < cpathRecordList.size(); i++) {
            CPathRecord participantRecord = cpathRecordList.get(i);
            long descendentId = participantRecord.getId();
            HashMap <String, String> xrefMap = xrefList.get(i);
            String geneSymbol = xrefMap.get(ExternalDatabaseConstants.GENE_SYMBOL);
            String entrezGeneId = xrefMap.get(ExternalDatabaseConstants.ENTREZ_GENE);
            String uniprotAccession = xrefMap.get(ExternalDatabaseConstants.UNIPROT);
            if (outputFormat == ExportUtil.GSEA_OUTPUT) {
                if (geneSymbol != null) {
                    numParticipantsOutput++;
                    line.append (geneSymbol + TAB);
                }
            } else {
                numParticipantsOutput++;
                line.append (descendentId + COLON);
                line.append (participantRecord.getSpecificType().toUpperCase() + COLON);
                String name = participantRecord.getName();
                if (name != null) {
                    //  Replace all : with -, so that we don't screw up a client parser
                    name = name.replaceAll(":", "-");
                    line.append (participantRecord.getName() + COLON);
                } else {
                    line.append (NA + COLON);
                }
                line.append (getXRef (uniprotAccession) + COLON);
                line.append (getXRef (geneSymbol) + COLON);
                line.append (getXRef (entrezGeneId));
                line.append (TAB);
            }
        }
        line.append ("\n");

        //  Append to the correct output files
        if (numParticipantsOutput > 0) {
            appendToSpeciesFile(line.toString(), record.getNcbiTaxonomyId(), outputFormat);
            appendToDataSourceFile (line.toString(), dbTerm, outputFormat);
        }
    }

    private String getXRef (String id) {
        if (id == null) {
            return NA;
        } else {
            return id;
        }
    }

    /**
     * Appends to a Data Source File.
     */
    private void appendToDataSourceFile (String line, String dbTerm, int outputFormat)
        throws IOException {
        String fdKey = dbTerm + outputFormat;
        String fileExtension = ExportUtil.getFileExtension (outputFormat);
        FileWriter writer = fileWriters.get(fdKey);
        File dir = ExportUtil.getBySourceDir (getFormatSpecificDir(outputFormat));
        if (writer == null) {
            writer = new FileWriter (new File (dir, dbTerm.toLowerCase() + fileExtension));
            fileWriters.put(fdKey, writer);
        }
        writer.write(line);
    }

    /**
     * Appends to a Speces File.
     */
    private void appendToSpeciesFile(String line, int ncbiTaxonomyId, int outputFormat)
            throws IOException, DaoException {
        String fdKey = Integer.toString(ncbiTaxonomyId) + outputFormat;
        String fileExtension = ExportUtil.getFileExtension (outputFormat);
        FileWriter writer = fileWriters.get(fdKey);
        File dir = ExportUtil.getBySpeciesDir (getFormatSpecificDir(outputFormat));
        if (writer == null) {
            DaoOrganism daoOrganism = new DaoOrganism();
            Organism organism = daoOrganism.getOrganismByTaxonomyId(ncbiTaxonomyId);
            String speciesName = organism.getSpeciesName().replaceAll(" ", "_");
            writer = new FileWriter (new File (dir, speciesName.toLowerCase() + fileExtension));
            fileWriters.put(fdKey, writer);
        }
        writer.write(line);
    }

    /**
     * Gets the format specific base directory.
     * @param outputFormat  Output Format.
     * @return Directory.
     */
    private File getFormatSpecificDir(int outputFormat) {
        File baseDir;
        if (outputFormat == ExportUtil.GSEA_OUTPUT) {
            baseDir = gseaDir;
        } else {
            baseDir = pcDir;
        }
        return baseDir;
    }

    /**
     * Command Line Usage.
     *
     * @param args Must include UniProt File Name.
     * @throws java.io.IOException IO Error.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("command line usage:  dumpGeneSets.pl <output_dir>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File outDir = new File(args[0]);
        System.out.println("Writing out to:  " + outDir.getAbsolutePath());
        ExportGeneSets dumper = new ExportGeneSets(pMonitor, outDir);
        dumper.dumpGeneSets();

        ArrayList<String> warningList = pMonitor.getWarningList();
        System.out.println("Total number of warning messages:  " + warningList.size());
        int i = 1;
        for (String warning : warningList) {
            System.out.println("Warning #" + i + ":  " + warning);
            i++;
        }
    }
}