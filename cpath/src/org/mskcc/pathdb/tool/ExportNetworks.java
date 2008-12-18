package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssembly;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssemblyFactory;
import org.mskcc.pathdb.schemas.binary_interaction.util.BinaryInteractionUtil;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.hp.hpl.jena.shared.JenaException;

/**
 * Command Line Utility to Dump Interaction Networks.
 *
 */
public class ExportNetworks {
    private ProgressMonitor pMonitor;
    private File outDir;
    private final static String TAB = "\t";
    private static final int BLOCK_SIZE = 1000;
    private File sifDir;
    private File tabDelimDir;
    private static final int SIF = 1;
    private static final int TAB_DELIM = 2;

    //  HashMap that will contain multiple open file writers
    private HashMap<String, FileWriter> fileWriters = new HashMap <String, FileWriter>();

    /**
     * Constructor.
     *
     * @param pMonitor Progress Monitor.
     */
    public ExportNetworks(ProgressMonitor pMonitor, File outDir) throws IOException {
        this.pMonitor = pMonitor;
        this.outDir = outDir;
        ToolInit.initProps();
    }

    /**
     * Dump the reactions to the specified directory.
     * @throws IOException          IO Error.
     * @throws DaoException         Database Error.
     * @throws AssemblyException    XML/SIF Assembly Error.
     */
    public void dump() throws IOException, DaoException, AssemblyException {
        initDirs(outDir);
        try {
            DaoCPath dao = DaoCPath.getInstance();
            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            CPathRecord record = null;
            long maxIterId = dao.getMaxCpathID();
            pMonitor.setMaxValue((int)maxIterId);
            for (int id = 0; id <= maxIterId; id = id + BLOCK_SIZE + 1) {

                // setup start/end id to fetch
                long startId = id;
                long endId = id + BLOCK_SIZE;
                if (endId > maxIterId) endId = maxIterId;

                try {
                    con = JdbcUtil.getCPathConnection();
                    pstmt = con.prepareStatement("select * from cpath WHERE "
                            + " CPATH_ID BETWEEN " + startId + " and " + endId
                            + " order by CPATH_ID ");
                    rs = pstmt.executeQuery();

                    while (rs.next()) {
                        if (pMonitor != null) {
                            pMonitor.incrementCurValue();
                            ConsoleUtil.showProgress(pMonitor);
                        }
                        record = dao.extractRecord(rs);
                        if (record.getType() == CPathRecordType.INTERACTION) {
                            dumpInteractionRecord(record);
                        }
                    }
                } catch (Exception e1) {
                    throw new DaoException(e1);
                }
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        } finally {
            Collection<FileWriter> fds = fileWriters.values();
            for (FileWriter fileWriter:  fds) {
                fileWriter.close();
            }
        }
    }

    /**
     * Initializes the Output Directories. This method creates a structure like so:
     * - sif
     * ---- by_species
     * ---- by_source
     * - tab_delim_network
     * ---- by_species
     * ---- by source
     *
     * @throws IOException IO Errors.
     */
    private void initDirs(File outDir) throws IOException {
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        sifDir = ExportUtil.initDir (outDir, "sif");
        tabDelimDir = ExportUtil.initDir (outDir, "tab_delim_network");
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
        if (outputFormat == ExportUtil.SIF_OUTPUT) {
            baseDir = sifDir;
        } else {
            baseDir = tabDelimDir;
        }
        return baseDir;
    }

    private void dumpInteractionRecord(CPathRecord record)
            throws DaoException, AssemblyException, IOException {
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        long snapshotId = record.getSnapshotId();
        ExternalDatabaseSnapshotRecord snapshotRecord =
                daoSnapshot.getDatabaseSnapshot(snapshotId);
        String dbTerm = snapshotRecord.getExternalDatabase().getMasterTerm();

        // determine binary interaction assembly type
        long ids[] = new long[1];
        ids[0] = record.getId();
        XmlAssembly xmlAssembly = XmlAssemblyFactory.createXmlAssembly(ids,
                XmlRecordType.BIO_PAX, 1, XmlAssemblyFactory.XML_FULL, true,
                new XDebug());
        BinaryInteractionAssemblyFactory.AssemblyType binaryInteractionAssemblyType =
                BinaryInteractionAssemblyFactory.AssemblyType.SIF;

        // contruct rule types
        List<String> binaryInteractionRuleTypes = BinaryInteractionUtil.getRuleTypes();

        // get binary interaction assembly
        try {
            BinaryInteractionAssembly sifAssembly =
                    BinaryInteractionAssemblyFactory.createAssembly
                            (binaryInteractionAssemblyType, binaryInteractionRuleTypes,
                                    xmlAssembly.getXmlString());
            String sif = sifAssembly.getBinaryInteractionString();
            String finalSif = convertIdsToGeneSymbols(dbTerm, record.getId(), sif);
            appendToDataSourceFile(finalSif, dbTerm, ExportUtil.SIF_OUTPUT);
            appendToSpeciesFile(finalSif, record.getNcbiTaxonomyId(), ExportUtil.TAB_DELIM_OUTPUT);
        } catch (JenaException e) {
            pMonitor.logWarning("Got JenaException:  " + e.getMessage() + ".  Occurred "
                + " while getting SIF for interaction:  " + record.getId() + ", Data Source:  "
                + dbTerm);
        }
    }

    private String convertIdsToGeneSymbols(String dbSource, long interactionId,
            String sif) throws DaoException, IOException {
        StringBuffer buf = new StringBuffer();
        String lines[] = sif.split("\\n");
        for (int i=0; i<lines.length; i++) {
            String line = lines[i];
            if (line.length() > 0) {
                String parts[] = lines[i].split("\\s");
                String id0 = parts[0];
                String intxnType = parts[1];
                String id1 = parts[2];
                String gene0 = getGeneSymbol(Integer.parseInt(id0));
                String gene1 = getGeneSymbol(Integer.parseInt(id1));
                //  Only export if we have gene symbols for both participants
                if (gene0 != null && gene1 != null) {
                    buf.append(gene0 + TAB);
                    buf.append(intxnType + TAB);
                    buf.append(gene1 + TAB);
                    buf.append(dbSource + TAB);
                    buf.append(interactionId + "\n");
                }
            }
        }
        return buf.toString();
    }

    private String getGeneSymbol(long cpathId) throws DaoException {
        HashMap<String, String> xrefMap = ExportUtil.getXRefMap(cpathId);
        return xrefMap.get(ExternalDatabaseConstants.GENE_SYMBOL);
    }

    /**
     * Command Line Usage.
     *
     * @param args Must include UniProt File Name.
     * @throws IOException IO Error.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("command line usage:  dumpNetworks.pl <output_dir>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File outDir = new File(args[0]);
        System.out.println("Writing out to:  " + outDir.getAbsolutePath());
        ExportNetworks dumper = new ExportNetworks(pMonitor, outDir);
        dumper.dump();

        ArrayList <String> warningList = pMonitor.getWarningList();
        System.out.println ("Total number of warning messages:  " + warningList.size());
        int i = 1;
        for (String warning:  warningList) {
            System.out.println ("Warning #" + i + ":  " + warning);
            i++;
        }
    }
}
