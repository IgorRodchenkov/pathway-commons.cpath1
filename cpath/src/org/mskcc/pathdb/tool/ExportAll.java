package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.tool.ConsoleUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Master utility tool to export all cPath data.
 *
 * @author Ethan Cerami
 */
public class ExportAll {
    private ProgressMonitor pMonitor;
    private File exportDir;
    private static final int BLOCK_SIZE = 1000;

    /**
     * Constructor.
     *
     * @param pMonitor Progress Monitor.
     */
    public ExportAll(ProgressMonitor pMonitor, File exportDir) throws IOException {
        this.pMonitor = pMonitor;
        this.exportDir = exportDir;
        ToolInit.initProps();
    }

    /**
     * Gene Set Dump.
     */
    public void exportAll() throws DaoException, IOException, SQLException, AssemblyException {
        DaoCPath dao = DaoCPath.getInstance();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CPathRecord record = null;
        long maxIterId = dao.getMaxCpathID();
        pMonitor.setMaxValue((int) maxIterId);

        // Initialize output directories and exporters
        ExportFileUtil exportUtil = new ExportFileUtil(exportDir);
        ExportGeneSets exportGeneSets = new ExportGeneSets(exportUtil);
        ExportNetworks exportInteractions = new ExportNetworks (exportUtil, pMonitor);
		ExportBioPAX exportBioPAX = new ExportBioPAX(exportUtil);

        //  Iterate through all cPath Records in blocks
        try {
            for (int id = 0; id <= maxIterId; id = id + BLOCK_SIZE + 1) {
                // setup start/end id to fetch
                long startId = id;
                long endId = id + BLOCK_SIZE;
                if (endId > maxIterId) endId = maxIterId;

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

                    if (record.getType() == CPathRecordType.PATHWAY) {
                        exportGeneSets.exportPathwayRecord(record);
                    } else if (record.getType() == CPathRecordType.INTERACTION) {
                        exportInteractions.exportInteractionRecord(record);
                    }

					// export biopax
					if (record.getType() != CPathRecordType.PHYSICAL_ENTITY) {
						exportBioPAX.exportRecord(record);
					}
                }
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        } finally {
            exportUtil.closeAllOpenFileDescriptors();
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Command Line Usage.
     *
     * @param args Must include UniProt File Name.
     * @throws java.io.IOException IO Error.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("command line usage:  exportAll.pl <output_dir>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File outDir = new File(args[0]);
        System.out.println("Writing out to:  " + outDir.getAbsolutePath());
        ExportAll exporter = new ExportAll(pMonitor, outDir);
        exporter.exportAll();

        ArrayList<String> warningList = pMonitor.getWarningList();
        System.out.println("Total number of warning messages:  " + warningList.size());
        int i = 1;
        for (String warning : warningList) {
            System.out.println("Warning #" + i + ":  " + warning);
            i++;
        }
    }
}