package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.util.tool.ConsoleUtil;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

/**
 * Command Line Utility to Dump all Pathways to the Broad
 * GSEA GMT: Gene Matrix Transposed file format (*.gmt) Format.
 * <p/>
 * Format is described at:
 * http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats
 */
public class DumpGsea {
    private ProgressMonitor pMonitor;
    private File outDir;
    private File bySpeciesDir;
    private File bySourceDir;
    private boolean mustUseGeneSymbols;
    private final static String TAB = "\t";
    private static final int BLOCK_SIZE = 1000;
    private HashMap<String, FileWriter> fileWriters = new HashMap <String, FileWriter>();

    /**
     * Constructor.
     *
     * @param pMonitor Progress Monitor.
     */
    public DumpGsea(ProgressMonitor pMonitor, File outDir) throws IOException {
        this.pMonitor = pMonitor;
        this.outDir = outDir;
        this.mustUseGeneSymbols = mustUseGeneSymbols;
        ToolInit.initProps();
    }

    /**
     * Dump the reactions to the specified text file.
     *
     * @throws java.io.IOException IO Error.
     * @throws org.mskcc.pathdb.sql.dao.DaoException
     *                             Database Error.
     * @throws org.mskcc.pathdb.sql.assembly.AssemblyException
     *                             XML/SIF Assembly Error.
     */
    public void dump() throws IOException, DaoException, AssemblyException {
        DaoCPath dao = DaoCPath.getInstance();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CPathRecord record = null;
        long maxIterId = dao.getMaxCpathID();
        pMonitor.setMaxValue((int) maxIterId);

        //  Initialize output directories
        initDirs();

        //  Iterate through all cPath Records
        try {
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

                        //  Only dump pathway records
                        if (record.getType() == CPathRecordType.PATHWAY) {
                            dumpPathwayRecord(record);
                        }
                    }
                } catch (Exception e1) {
                    throw new DaoException(e1);
                }
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        } finally {
            Collection <FileWriter> fds = fileWriters.values();
            for (FileWriter fileWriter:  fds) {
                fileWriter.close();
            }
        }
    }

    private void initDirs() throws IOException {
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        File gseaDir = new File (outDir, "gsea");
        if (!gseaDir.exists()) {
            gseaDir.mkdir();
        }
        bySpeciesDir = new File (gseaDir, "by_species");
        if (!bySpeciesDir.exists()) {
            bySpeciesDir.mkdir();
        }
        bySourceDir = new File (gseaDir, "by_source");
        if (!bySourceDir.exists()) {
            bySourceDir.mkdir();
        }
    }

    /**
     * Dumps the Pathway Record in the GSEA GMT File Format.
     */
    private void dumpPathwayRecord(CPathRecord record)
            throws DaoException, AssemblyException, IOException {
        //  Gets the Database Term
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        long snapshotId = record.getSnapshotId();
        ExternalDatabaseSnapshotRecord snapshotRecord =
                daoSnapshot.getDatabaseSnapshot(snapshotId);
        String dbTerm = snapshotRecord.getExternalDatabase().getMasterTerm();

        //  Gets all participants
        DaoInternalFamily daoInternalFamily = new DaoInternalFamily();
        StringBuffer line = new StringBuffer();
        line.append (record.getName() + TAB);
        line.append (dbTerm + TAB);
        long[] descendentIds = daoInternalFamily.getDescendentIds(record.getId(),
                CPathRecordType.PHYSICAL_ENTITY);

        //  Dumps all participants
        int numParticipantsOutput = 0;
        for (long descendentId : descendentIds) {
            String geneSymbol = getGeneSymbol (descendentId);
            if (geneSymbol != null) {
                numParticipantsOutput++;
                line.append (geneSymbol + TAB);
            }
        }
        line.append ("\n");
        if (numParticipantsOutput > 0) {
            appendToOrganismFile (line.toString(), record.getNcbiTaxonomyId());
            appendToDataSourceFile (line.toString(), dbTerm);
        }
    }

    /**
     * Appends to a Data Source File.
     */
    private void appendToDataSourceFile (String line, String dbTerm)
        throws IOException {
        FileWriter writer = fileWriters.get(dbTerm);
        if (writer == null) {
            writer = new FileWriter (new File (bySourceDir, dbTerm.toLowerCase() + ".gmt"));
            fileWriters.put(dbTerm, writer);
        }
        writer.write(line);
    }

    /**
     * Appends to an Organism File.
     */
    private void appendToOrganismFile (String line, int ncbiTaxonomyId)
            throws IOException, DaoException {
        FileWriter writer = fileWriters.get(Integer.toString(ncbiTaxonomyId));
        if (writer == null) {
            DaoOrganism daoOrganism = new DaoOrganism();
            Organism organism = daoOrganism.getOrganismByTaxonomyId(ncbiTaxonomyId);
            String speciesName = organism.getSpeciesName().replaceAll(" ", "_");
            writer = new FileWriter (new File (bySpeciesDir, speciesName.toLowerCase() + ".gmt"));
            fileWriters.put(Integer.toString(ncbiTaxonomyId), writer);
        }
        writer.write(line);
    }

    private String getGeneSymbol(long cpathId) throws DaoException {
        DaoExternalLink daoExternalLink = DaoExternalLink.getInstance();
        ArrayList<ExternalLinkRecord> xrefList =
                daoExternalLink.getRecordsByCPathId(cpathId);
        for (ExternalLinkRecord xref : xrefList) {
            if (xref.getExternalDatabase().getMasterTerm().equals
                    (ExternalDatabaseConstants.GENE_SYMBOL)) {
                return xref.getLinkedToId();
            }
        }
        return null;
    }

    /**
     * Command Line Usage.
     *
     * @param args Must include UniProt File Name.
     * @throws java.io.IOException IO Error.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("command line usage:  dumpGsea.pl <output_dir>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File outDir = new File(args[0]);
        System.out.println("Writing out to:  " + outDir.getAbsolutePath());
        DumpGsea dumper = new DumpGsea(pMonitor, outDir);
        dumper.dump();

        ArrayList<String> warningList = pMonitor.getWarningList();
        System.out.println("Total number of warning messages:  " + warningList.size());
        int i = 1;
        for (String warning : warningList) {
            System.out.println("Warning #" + i + ":  " + warning);
            i++;
        }
    }
}