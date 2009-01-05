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
 * Command Line Utility to Export Interaction Networks.
 *
 * @author Ethan Cerami.
 */
public class ExportNetworks {
    private ExportFileUtil exportFileUtil;
    private ProgressMonitor pMonitor;
    private final static String TAB = "\t";

    /**
     * Constructor.
     *
     * @param exportFileUtil Export File Util Object.
     */
    public ExportNetworks(ExportFileUtil exportFileUtil, ProgressMonitor pMonitor) {
        this.exportFileUtil = exportFileUtil;
        this.pMonitor = pMonitor;
    }

    /**
     * Exports the specified interaction record.
     * @param record        CPath Interaction Record.
     * @throws DaoException         Database Error.
     * @throws AssemblyException    XML Assembly Error.
     * @throws IOException          IO Error.
     */
    public void exportInteractionRecord(CPathRecord record)
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
            exportFileUtil.appendToDataSourceFile(finalSif, dbTerm, ExportFileUtil.SIF_OUTPUT);
            exportFileUtil.appendToSpeciesFile(finalSif, record.getNcbiTaxonomyId(),
                    ExportFileUtil.TAB_DELIM_OUTPUT);
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
}
