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
    private boolean mustUseGeneSymbols;
    private final static String TAB = "\t";
    private static final int BLOCK_SIZE = 1000;
    private HashMap<String, FileWriter> fileWriters = new HashMap <String, FileWriter>();

    /**
     * Constructor.
     *
     * @param pMonitor Progress Monitor.
     */
    public DumpGsea(ProgressMonitor pMonitor, boolean mustUseGeneSymbols,
            File outDir) throws IOException {
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
        File organismDir = new File (outDir, "by_species");
        if (!organismDir.exists()) {
            organismDir.mkdir();
        }
        File sourceDir = new File (outDir, "by_source");
        if (!sourceDir.exists()) {
            sourceDir.mkdir();
        }
    }

    /**
     * Dumps the Pathway Record in the GSEA GMT File Format.
     */
    private void dumpPathwayRecord(CPathRecord record)
            throws DaoException, AssemblyException, IOException {
        DaoCPath dao = DaoCPath.getInstance();

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
        for (long descendentId : descendentIds) {
            if (mustUseGeneSymbols) {
                String geneSymbol = getGeneSymbol (descendentId);
                if (geneSymbol != null) {
                    line.append (geneSymbol + TAB);
                }
            } else {
                CPathRecord participant = dao.getRecordById(descendentId);
                line.append (participant.getName() + TAB);
            }
        }
        line.append ("\n");
        appendToOrganismFile (line.toString(), record.getNcbiTaxonomyId());
        appendToDataSourceFile (line.toString(), dbTerm);
    }

    /**
     * Appends to a Data Source File.
     */
    private void appendToDataSourceFile (String line, String dbTerm)
        throws IOException {
        FileWriter writer = fileWriters.get(dbTerm);
        if (writer == null) {
            File sourceDir = new File (outDir, "by_source");
            writer = new FileWriter (new File (sourceDir, dbTerm.toLowerCase() + ".gmt"));
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
            File organismDir = new File (outDir, "by_species");
            writer = new FileWriter (new File (organismDir, speciesName.toLowerCase() + ".gmt"));
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
        DumpGsea dumper = new DumpGsea(pMonitor, true, outDir);
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