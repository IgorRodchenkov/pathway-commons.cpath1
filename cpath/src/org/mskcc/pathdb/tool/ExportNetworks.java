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
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import com.hp.hpl.jena.shared.JenaException;

/**
 * Command Line Utility to Export Interaction Networks.
 *
 * @author Ethan Cerami.
 */
public class ExportNetworks {
    private ExportFileUtil exportFileUtil;
    private ProgressMonitor pMonitor;
	private HashMap<String, String> processedSIFs;
	private HashMap<String, String> processedInteractors;
    private final static String TAB = "\t";

    /**
     * Constructor.
     *
     * @param exportFileUtil Export File Util Object.
	 * @param pMonitor ProgressMonitor
     */
    public ExportNetworks(ExportFileUtil exportFileUtil, ProgressMonitor pMonitor) {
        this.exportFileUtil = exportFileUtil;
        this.pMonitor = pMonitor;
		processedSIFs = new HashMap<String, String>();
		processedInteractors = new HashMap<String, String>();
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
            ArrayList <Interaction> interactionList = convertToInteractionList (dbTerm,
                    record.getId(), sif);

            for (Interaction interaction:  interactionList) {
				ArrayList<String> finalSIFs = getFinalSIFs(interaction, ExportFileUtil.SIF_OUTPUT);
				// more than one sif string can come out of single cpath interaction record
				// for each sif string, check and see if we have already created similiar sif
				if (processedSIFs.containsKey(finalSIFs.get(0))) {
					continue;
				}
                exportRecord(record, dbTerm, interaction, finalSIFs, ExportFileUtil.SIF_OUTPUT);
                exportRecord(record, dbTerm, interaction, getFinalSIFs(interaction, ExportFileUtil.TAB_DELIM_EDGE_OUTPUT), ExportFileUtil.TAB_DELIM_EDGE_OUTPUT);
				exportRecord(record, dbTerm, interaction, getFinalSIFs(interaction, ExportFileUtil.TAB_DELIM_NODE_OUTPUT), ExportFileUtil.TAB_DELIM_NODE_OUTPUT);
				processedSIFs.put(finalSIFs.get(0), "");
            }
        } catch (JenaException e) {
            pMonitor.logWarning("Got JenaException:  " + e.getMessage() + ".  Occurred "
                + " while getting SIF for interaction:  " + record.getId() + ", Data Source:  "
                + dbTerm);
        }
    }

	/**
	 * Given an interaction generates array of strings that gets dumped into files.  Only TAB_DELIM_NODE_OUTPUT should 
	 * generate list of 2 strings all other output format should generate list of 1 string.
	 *
	 * @param interaction Interaction
	 * @param outputFormat int
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getFinalSIFs(Interaction interaction, int outputFormat) throws DaoException {

		// list to return
		ArrayList<String> toReturn = new ArrayList<String>();

		if (outputFormat == ExportFileUtil.SIF_OUTPUT) {
			StringBuffer finalSif = new StringBuffer();
			finalSif.append(interaction.getGeneA() + TAB);
			finalSif.append(interaction.getInteractionType() + TAB);
			finalSif.append(interaction.getGeneB());
			finalSif.append("\n");
			toReturn.add(finalSif.toString());
		}
		else if (outputFormat == ExportFileUtil.TAB_DELIM_EDGE_OUTPUT) {
			StringBuffer finalSif = new StringBuffer();
			finalSif.append(interaction.getCPathRecordA().getId() + TAB);
			finalSif.append(interaction.getInteractionType() + TAB);
			finalSif.append(interaction.getCPathRecordB().getId() + TAB);
			finalSif.append(interaction.getGeneA() + TAB);
			finalSif.append(interaction.getGeneB() + TAB);
			finalSif.append(interaction.getDbSource());
			finalSif.append("\n");
			toReturn.add(finalSif.toString());
		}
		else if (outputFormat == ExportFileUtil.TAB_DELIM_NODE_OUTPUT) {
			for (int lc = 0; lc < 2; lc++) {
				StringBuffer finalSif = new StringBuffer();
				long geneID = (lc == 0) ? interaction.getCPathRecordA().getId() : interaction.getCPathRecordB().getId();
				String geneSymbol = (lc == 0) ? interaction.getGeneA() : interaction.getGeneB();
				HashMap<String, String> xrefMap = ExportUtil.getXRefMap(geneID);
				String entrezGeneId = xrefMap.get(ExternalDatabaseConstants.ENTREZ_GENE);
				String uniprotAccession = xrefMap.get(ExternalDatabaseConstants.UNIPROT);
				finalSif.append(geneID + TAB);
				finalSif.append(geneSymbol + TAB);
				finalSif.append(ExportUtil.getXRef(uniprotAccession) + TAB);
				finalSif.append(ExportUtil.getXRef(entrezGeneId));
				finalSif.append("\n");
				toReturn.add(finalSif.toString());
			}
		}

		// outta here
		return toReturn;
	}

	/**
	 * This routine exports records to data / species files.
	 * We are making sure not to export duplicate entries to SIF_OUTPUT & TAB_DELIM_EDGE_OUTPUT in exportInteractionRecord().
	 * In this routine we make sure we don't export duplicate entries to TAB_DELIM_NODE_OUTPUT.  This is a bit tedious since we have
	 * to export to multiple data source and species files.  So, we need to take into account the dbTerm and/or taxID respectively.
	 *
	 * @param record CPathRecord
	 * @param dbTerm String
	 * @param interaction Interaction
	 * @param finalSifs ArrayList<String>
	 * @param outputFormat int
	 */
    private void exportRecord(CPathRecord record, String dbTerm, Interaction interaction, ArrayList<String> finalSifs, int outputFormat)
            throws IOException, DaoException {

		for (String finalSif : finalSifs) {

			// export to data file
			String key = dbTerm + TAB + finalSif;
			boolean exportToDataFile = ((outputFormat != ExportFileUtil.TAB_DELIM_NODE_OUTPUT) ||
										(outputFormat == ExportFileUtil.TAB_DELIM_NODE_OUTPUT && !processedInteractors.containsKey(key)));
			if (exportToDataFile) {
				exportFileUtil.appendToDataSourceFile(finalSif, dbTerm, outputFormat);
				if (outputFormat == ExportFileUtil.TAB_DELIM_NODE_OUTPUT) {
					processedInteractors.put(key, "");
				}
			}
			// export to species specific file(s)
			ArrayList<Integer> ncbiTaxonomyIDs = new ArrayList<Integer>();
			ExportUtil.getNCBITaxonomyIDs(record, ncbiTaxonomyIDs, new ArrayList<Long>());
			for (Integer taxID : ncbiTaxonomyIDs) {
				key = taxID + TAB + finalSif;
				boolean exportToSpeciesFile = ((outputFormat != ExportFileUtil.TAB_DELIM_NODE_OUTPUT) ||
											   (outputFormat == ExportFileUtil.TAB_DELIM_NODE_OUTPUT && !processedInteractors.containsKey(key)));
				if (exportToSpeciesFile) {
					exportFileUtil.appendToSpeciesFile(finalSif, taxID, outputFormat);
					if (outputFormat == ExportFileUtil.TAB_DELIM_NODE_OUTPUT) {
						processedInteractors.put(key, "");
					}
				}
			}
		}
    }

    private ArrayList <Interaction> convertToInteractionList (String dbSource, long interactionId,
            String sif) throws DaoException, IOException {
        ArrayList <Interaction> interactionList = new ArrayList <Interaction>();
        DaoCPath daoCPath = DaoCPath.getInstance();
        String lines[] = sif.split("\\n");
        for (int i=0; i<lines.length; i++) {
            String line = lines[i];
            if (line.length() > 0) {
                String parts[] = lines[i].split("\\s");
                String id0Str = parts[0];
                String intxnType = parts[1];
                String id1Str = parts[2];

                int idA = Integer.parseInt(id0Str);
                int idB = Integer.parseInt(id1Str);
                String gene0 = getGeneSymbol(idA);
                String gene1 = getGeneSymbol(idB);
                if (gene0 != null && gene1 != null) {
                    CPathRecord recordA = daoCPath.getRecordById(idA);
                    CPathRecord recordB = daoCPath.getRecordById(idB);
                    Interaction interaction = new Interaction (interactionId, dbSource);
                    interaction.setCPathRecordA(recordA);
                    interaction.setCPathRecordB(recordB);
                    interaction.setGeneA(gene0);
                    interaction.setGeneB(gene1);
                    interaction.setInteractionType(intxnType.toUpperCase());
                    interactionList.add(interaction);
                }
            }
        }
        return interactionList;
    }

    private String getGeneSymbol(long cpathId) throws DaoException {
        HashMap<String, String> xrefMap = ExportUtil.getXRefMap(cpathId);
		String toReturn = xrefMap.get(ExternalDatabaseConstants.GENE_SYMBOL);
        return (toReturn != null) ? toReturn.trim().toUpperCase() : null;
    }
}

class Interaction {
    private String geneA;
    private String geneB;
    private String interactionType;
    private String dbSource;
    private long cPathId;
    private CPathRecord recordA;
    private CPathRecord recordB;

    Interaction (long cPathId, String dbSource) {
        this.cPathId = cPathId;
        this.dbSource = dbSource;
    }

    public String getGeneA() {
        return geneA;
    }

    public void setGeneA(String geneA) {
        this.geneA = geneA;
    }

    public String getGeneB() {
        return geneB;
    }

    public void setGeneB(String geneB) {
        this.geneB = geneB;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public String getDbSource() {
        return dbSource;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }

    public long getCPathId() {
        return cPathId;
    }

    public void setCPathId(long cPathId) {
        this.cPathId = cPathId;
    }

    public CPathRecord getCPathRecordA() {
        return recordA;
    }

    public void setCPathRecordA(CPathRecord record) {
        this.recordA = record;
    }

    public CPathRecord getCPathRecordB() {
        return recordB;
    }

    public void setCPathRecordB (CPathRecord record) {
        this.recordB = record;
    }
}
