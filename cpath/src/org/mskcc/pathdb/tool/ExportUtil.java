package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.model.ExternalLinkRecord;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Export Utility Class.
 */
public class ExportUtil {
    public static final int GSEA_OUTPUT = 1;
    public static final int PC_OUTPUT = 2;
    public static final int SIF_OUTPUT = 3;
    public static final int TAB_DELIM_OUTPUT = 4;

    /**
     * Initializes output directories.  This method creates a structure like so:
     * - xxxx
     * ---- by_species
     * ---- by_source
     */
    public static File initDir (File baseDir, String targetDir) {
        File newDir = new File (baseDir, targetDir);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        File bySpeciesDir = ExportUtil.getBySpeciesDir (newDir);
        if (!bySpeciesDir.exists()) {
            bySpeciesDir.mkdir();
        }
        File byDataSourceDir = ExportUtil.getBySourceDir (newDir);
        if (!byDataSourceDir.exists()) {
            byDataSourceDir.mkdir();
        }
        return newDir;
    }

    /**
     * Gets the by_species directory.
     */
    public static File getBySpeciesDir (File baseDir) {
        return new File (baseDir, "by_species");
    }

    /**
     * Gets the by_source directory.
     */
    public static File getBySourceDir (File baseDir) {
        return new File (baseDir, "by_source");
    }

    /**
     * Gets the file extension for the specified outputFormat.
     * @param outputFormat  Output Format Index.
     * @return file extension, e.g. ".txt";
     */
    public static String getFileExtension (int outputFormat) {
        if (outputFormat == ExportUtil.GSEA_OUTPUT) {
            return ".gmt";
        } else if (outputFormat == ExportUtil.PC_OUTPUT) {
            return ".txt";
        } else if (outputFormat == ExportUtil.SIF_OUTPUT) {
            return ".sif";
        } else {
            return ".txt";
        }
    }

    /**
     * XRef Look up.
     */
    public static HashMap<String, String> getXRefMap(long cpathId) throws DaoException {
        HashMap <String, String> xrefMap = new HashMap <String, String> ();
        DaoExternalLink daoExternalLink = DaoExternalLink.getInstance();
        ArrayList<ExternalLinkRecord> xrefList = daoExternalLink.getRecordsByCPathId(cpathId);
        for (ExternalLinkRecord xref : xrefList) {
            String dbMasterTerm = xref.getExternalDatabase().getMasterTerm();
            String xrefId = xref.getLinkedToId();
            xrefMap.put(dbMasterTerm, xrefId);
        }
        return xrefMap;
    }
}
