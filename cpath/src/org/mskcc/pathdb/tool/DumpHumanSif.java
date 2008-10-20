package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.util.PropertyManager;
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
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * Command Line Utility to Dump all Manually Curated Human Interactions in SIF-Like
 * output file format.
 */
public class DumpHumanSif {
    private ProgressMonitor pMonitor;
    private File outFile;
    private HashSet curatedDbSet = new HashSet();
    private final static String TAB = "\t";

    /**
     * Constructor.
     *
     * @param pMonitor Progress Monitor.
     */
    public DumpHumanSif(ProgressMonitor pMonitor, File outFile) throws IOException {
        this.pMonitor = pMonitor;
        this.outFile = outFile;
        curatedDbSet.add("REACTOME");
        curatedDbSet.add("CELL_MAP");
        curatedDbSet.add("NCI_NATURE");
        curatedDbSet.add("HPRD");
        initProps();
    }

    /**
     * Initializes the build properties.
     * @throws IOException IO Error.
     */
    private void initProps() throws IOException {
        EhCache.initCache();
        EhCache.resetAllCaches();

        //  Load build.properties
        String cpathHome = System.getProperty(Admin.CPATH_HOME);
        String separator = System.getProperty("file.separator");
        Properties buildProps = new Properties();
        buildProps.load(new FileInputStream(cpathHome
                + separator + "build.properties"));

        String dbUser = buildProps.getProperty("db.user");
        String dbPwd = buildProps.getProperty("db.password");
        String dbName = buildProps.getProperty("db.name");
        String dbHost = buildProps.getProperty("db.host");

        PropertyManager propertyManager = PropertyManager.getInstance();
        propertyManager.setProperty(PropertyManager.DB_USER, dbUser);
        propertyManager.setProperty(PropertyManager.DB_PASSWORD, dbPwd);
        propertyManager.setProperty(CPathConstants.PROPERTY_MYSQL_DATABASE,
                dbName);
        propertyManager.setProperty(PropertyManager.DB_LOCATION, dbHost);
    }

    /**
     * Dump the reactions to the specified text file.
     * @throws IOException          IO Error.
     * @throws DaoException         Database Error.
     * @throws AssemblyException    XML/SIF Assembly Error.
     */
    public void dump() throws IOException, DaoException, AssemblyException {
        FileWriter fileWriter = new FileWriter(outFile);
        try {
            DaoCPath dao = DaoCPath.getInstance();
            DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
            ArrayList<CPathRecord> recordList = dao.getAllRecords(CPathRecordType.INTERACTION);
            pMonitor.setMaxValue(recordList.size());
            for (CPathRecord record : recordList) {
                long snapshotId = record.getSnapshotId();
                ExternalDatabaseSnapshotRecord snapshotRecord =
                        daoSnapshot.getDatabaseSnapshot(snapshotId);
                String dbTerm = snapshotRecord.getExternalDatabase().getMasterTerm();

                // If this is a curated db, dump the SIF.  otherwise, skip
                if (curatedDbSet.contains(dbTerm)) {
                    long ids[] = new long[1];
                    ids[0] = record.getId();
                    XmlAssembly xmlAssembly = XmlAssemblyFactory.createXmlAssembly(ids,
                            XmlRecordType.BIO_PAX, 1, XmlAssemblyFactory.XML_FULL, true,
                            new XDebug());
                    // determine binary interaction assembly type
                    BinaryInteractionAssemblyFactory.AssemblyType binaryInteractionAssemblyType =
                            BinaryInteractionAssemblyFactory.AssemblyType.SIF;

                    // contruct rule types
                    List<String> binaryInteractionRuleTypes = BinaryInteractionUtil.getRuleTypes();

                    // get binary interaction assembly
                    BinaryInteractionAssembly sifAssembly =
                            BinaryInteractionAssemblyFactory.createAssembly
                                    (binaryInteractionAssemblyType, binaryInteractionRuleTypes,
                                            xmlAssembly.getXmlString());
                    String sif = sifAssembly.getBinaryInteractionString();
                    convertIdsToGeneSymbols(dbTerm, record.getId(), sif, fileWriter);
                }
                if (pMonitor != null) {
                    pMonitor.incrementCurValue();
                    ConsoleUtil.showProgress(pMonitor);
                }
            }
        } finally {
            fileWriter.flush();
            fileWriter.close();
        }
    }

    private void convertIdsToGeneSymbols(String dbSource, long interactionId,
            String sif, FileWriter fileWriter) throws DaoException, IOException {
        String lines[] = sif.split("\\n");
        DaoCPath dao = DaoCPath.getInstance();
        for (int i=0; i<lines.length; i++) {
            String line = lines[i];
            if (line.length() > 0) {
                String parts[] = lines[i].split("\\s");
                String id0 = parts[0];
                String intxnType = parts[1];
                String id1 = parts[2];
                CPathRecord record0 = dao.getRecordById(Integer.parseInt(id0));
                CPathRecord record1 = dao.getRecordById(Integer.parseInt(id1));
                // Only include human-human interactions
                if (record0.getNcbiTaxonomyId() == 9606 && record1.getNcbiTaxonomyId() == 9606) {
                    String gene0 = getGeneSymbol(Integer.parseInt(id0));
                    String gene1 = getGeneSymbol(Integer.parseInt(id1));
                    //  Only dump, if we have gene symbols for both
                    if (gene0 != null && gene1 != null) {
                        fileWriter.write(gene0 + TAB);
                        fileWriter.write(intxnType + TAB);
                        fileWriter.write(gene1 + TAB);
                        fileWriter.write(dbSource + TAB);
                        fileWriter.write(interactionId + "\n");
                    }
                }
            }
        }
    }

    private String getGeneSymbol(long cpathId) throws DaoException {
        DaoExternalLink daoExternalLink = DaoExternalLink.getInstance();
        ArrayList <ExternalLinkRecord> xrefList =
                daoExternalLink.getRecordsByCPathId(cpathId);
        for (ExternalLinkRecord xref:  xrefList) {
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
     * @throws IOException IO Error.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("command line usage:  dumpHumanSif.pl <output.txt>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File file = new File(args[0]);
        System.out.println("Writing out to:  " + file.getAbsoluteFile());
        DumpHumanSif dumper = new DumpHumanSif(pMonitor, file);
        dumper.dump();
    }
}
