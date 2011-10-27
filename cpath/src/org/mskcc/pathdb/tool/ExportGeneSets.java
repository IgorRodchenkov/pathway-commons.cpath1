package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.sql.query.GetNeighborsCommand;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Command Line Utility to Export Gene Sets.
 * <p/>
 * This classes supports two data formats:
 * <p/>
 * 1) ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT: Gene Matrix Transposed file format (*.gmt) Format.
 * Format is described at:
 * http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats
 * <p/>
 * 2) ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT:  Same as above, except we export Entre Gene IDs,
 * instead of gene symbols.
 * <p/>
 * 3) ExportFileUtil.PC_OUTPUT:  Similar to the GSEA GMT format, except that all
 * participants are micro-encoded with multiple identifiers. For example, each participant
 * is specified as: CPATH_ID:RECORD_TYPE:NAME:UNIPROT_ACCESSION:GENE_SYMBOL:ENTREZ_GENE_ID.
 * <p/>
 * It also creates the a directory structure like so:
 * <p/>
 * - snapshots
 * ---- gsea
 * ------- by_species
 * ------- by_source
 * ---- gene_sets
 * ------- by_species
 * ------- by source
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class ExportGeneSets {
    private final static String TAB = "\t";
    private final static String COLON = ":";
    private final static String COMMA = ",";
	private final static String NOT_SPECIFIED = "NOT_SPECIFIED";
    private HashMap<String, Integer> processedRecords;

    /**
     * Constructor.
     */
    public ExportGeneSets() {
        processedRecords = new HashMap<String, Integer>();
    }

    /**
     * Exports the Pathway Record in all File Formats.
     *
     * @param record CPath Record.
     */
    public void exportPathwayRecordAllFormats(CPathRecord record, ExportFileUtil exportFileUtil)
            throws DaoException, IOException {

        // this processed record count
        int count = (processedRecords.containsKey(record.getName())) ?
                processedRecords.get(record.getName()) : 0;

        if (count > 0) {
            record.setName(record.getName() + " - " + count + 1);
        }

        //  Gets the Database Term
        String dbMasterTerm = getMasterDbTerm(record);
        ArrayList<CPathRecord> participantRecordList = getParticipants(record);

        //  Get XRefs for all Participants
        ArrayList<HashMap<String, Set<String>>> xrefList = getParticipantXRefs(participantRecordList);

        //  Dump to all file formats.
        exportGeneSet(record, dbMasterTerm, participantRecordList, xrefList, exportFileUtil,
                ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT, null);
        exportGeneSet(record, dbMasterTerm, participantRecordList, xrefList, exportFileUtil,
                ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT, null);
        exportGeneSet(record, dbMasterTerm, participantRecordList, xrefList, exportFileUtil,
                ExportFileUtil.PC_OUTPUT, null);

        // add to processed list
        processedRecords.put(record.getName(), ++count);
    }

    /**
     * Exports the Pathway Record in the Specified Output Format.
     *
     * @param record             CPath Record.
     * @param exportOutputFormat Must be set to ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT,
     *                           ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT, ExportFileUtil.GSEA_OUTPUT or ExportFileUtil.PC_OUTPUT.
     * @param outputId           External IDs to output.  Only applicable if exportOutputFormat is set to ExportFileUtil.GSEA_OUTPUT.
     *                           Can be null.  Can be set to, e.g. External Database Constants.  If exportOutputFormat is set to
     *                           ExportFileUtil.GSEA_OUTPUT and this is null, the method outputs cPath IDs.
     */
    public String exportRecord(CPathRecord record, int exportOutputFormat, String outputId)
            throws DaoException, IOException {

        //  Gets the Database Term
        String dbMasterTerm = getMasterDbTerm(record);
        ArrayList<CPathRecord> participantRecordList = getParticipants(record);

        //  Get XRefs for all Participants
        ArrayList<HashMap<String, Set<String>>> xrefList = getParticipantXRefs(participantRecordList);

        String exportText = exportGeneSetToText(record, dbMasterTerm, participantRecordList, xrefList,
                exportOutputFormat, outputId);
        return exportText;
    }

    /**
     * Gets the Master Database Term.
     *
     * @param record CPath Record.
     * @return Master Term
     * @throws DaoException Database Error.
     */
    private String getMasterDbTerm(CPathRecord record) throws DaoException {
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        long snapshotId = record.getSnapshotId();
        ExternalDatabaseSnapshotRecord snapshotRecord =
                daoSnapshot.getDatabaseSnapshot(snapshotId);
        if (snapshotRecord != null) {
            return snapshotRecord.getExternalDatabase().getMasterTerm();
        } else {
            return "NA";
        }
    }

    /**
     * Gets Participants in Specified Record.
     *
     * @param record CPath Record.
     * @return ArrayList of CPath Records.
     * @throws DaoException Database Error.
     */
    private ArrayList<CPathRecord> getParticipants(CPathRecord record) throws DaoException {
        ArrayList<CPathRecord> participantRecordList = new ArrayList<CPathRecord>();
        DaoInternalFamily daoInternalFamily = new DaoInternalFamily();
        long[] descendentIds = daoInternalFamily.getDescendentIds(record.getId(),
                CPathRecordType.PHYSICAL_ENTITY);

        DaoCPath daoCPath = DaoCPath.getInstance();
        //  If nothing is found in the descendent IDs table, we may have a complex or interaction.
        //  In that case, try the DaoInternalLink.getAllDescendents method.
        if (descendentIds.length == 0) {
            DaoInternalLink daoInternalLink = new DaoInternalLink();
            ArrayList descIdList = daoInternalLink.getAllDescendents(record.getId());
            for (int i = 0; i < descIdList.size(); i++) {
                long descendentId = (Long) descIdList.get(i);
                participantRecordList.add(daoCPath.getRecordById(descendentId));
            }
        } else {
            for (long descendentId : descendentIds) {
                participantRecordList.add(daoCPath.getRecordById(descendentId));
            }
        }
        return participantRecordList;
    }

    /**
     * Gets the Particpant XRefs.
     *
     * @param participantRecordList List of CPath Records.
     * @return Participant XRefs.
     * @throws DaoException Database Error.
     */
    private ArrayList<HashMap<String, Set<String>>> getParticipantXRefs(ArrayList<CPathRecord> participantRecordList)
            throws DaoException {
        ArrayList<HashMap<String, Set<String>>> xrefList =
                new ArrayList<HashMap<String, Set<String>>>();
        for (CPathRecord participantRecord : participantRecordList) {
            HashMap<String, Set<String>> xrefMap = ExportUtil.getXRefMap(participantRecord.getId());
            xrefList.add(xrefMap);
        }
        return xrefList;
    }

    /**
     * Actual Output of the Gene Set.
     */
    private void exportGeneSet(CPathRecord record, String dbTerm, ArrayList<CPathRecord>
            cpathRecordList, ArrayList<HashMap<String, Set<String>>> xrefList, ExportFileUtil exportFileUtil,
                               int outputFormat, String outputId)
            throws IOException, DaoException {
        String text = exportGeneSetToText(record, dbTerm, cpathRecordList, xrefList,
                outputFormat, outputId);

        //  Append to the correct output files
        if (text != null) {
			System.out.println("Writing to species and data source files");
			System.out.println("record tax id: " + record.getNcbiTaxonomyId());
			System.out.println("db term: " + dbTerm);
			// some insurance for HUMANCYC - big hack
			int taxID = record.getNcbiTaxonomyId();
			if (taxID == CPathRecord.TAXONOMY_NOT_SPECIFIED && dbTerm.contains("HUMANCYC")) {
				taxID = 9606;
			}
			exportFileUtil.appendToSpeciesFile(text, taxID, outputFormat);
            exportFileUtil.appendToDataSourceFile(text, dbTerm, outputFormat);
        }
    }

    /**
     * Output Gene Set.
     *
     * @param record           CPath Record
     * @param dbTermMasterTerm Database Master Term
     * @param participantsList CPath Records for All Participants
     * @param xrefList         XRefs for all Participants
     * @param outputFormat     Output Format.
     * @return Text Output.
     * @throws IOException  IO Error.
     * @throws DaoException Database Error.
     */
    private String exportGeneSetToText(CPathRecord record, String dbTermMasterTerm, ArrayList<CPathRecord>
            participantsList, ArrayList<HashMap<String, Set<String>>> xrefList, int outputFormat, String outputId)
            throws IOException, DaoException {

        Set<String> outputSet = null;
        StringBuffer line = new StringBuffer();
        line.append(record.getName() + TAB);
        line.append(dbTermMasterTerm + TAB);

		boolean skipCrossSpeciesCheck = skipCrossSpeciesCheck(record, participantsList);

        for (int i = 0; i < participantsList.size(); i++) {
            CPathRecord participantRecord = participantsList.get(i);

			// see method definition for this boolean
			if (!skipCrossSpeciesCheck) {
				// per spec, GSEA & PC should not contain cross-species genes
				// - only check for proteins (skip small molecules)
				if (participantRecord.getSpecificType().equals(BioPaxConstants.PROTEIN)) {
					if (participantRecord.getNcbiTaxonomyId() != record.getNcbiTaxonomyId()) {
						continue;
					}
				}
			}

            long descendentId = participantRecord.getId();
            HashMap<String, Set<String>> xrefMap = xrefList.get(i);
            Set<String> geneSymbols = xrefMap.get(ExternalDatabaseConstants.GENE_SYMBOL);
            Set<String> entrezGeneIds = xrefMap.get(ExternalDatabaseConstants.ENTREZ_GENE);
            Set<String> uniprotAccessions = xrefMap.get(ExternalDatabaseConstants.UNIPROT);

            //  These three file formats only support the output of Genes / Proteins
            if (outputFormat == ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT ||
                    outputFormat == ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT ||
                    outputFormat == ExportFileUtil.GSEA_OUTPUT) {
                // create an output set collection if necessary
                outputSet = (outputSet == null) ? new HashSet<String>() : outputSet;
                if (participantRecord.getSpecificType().equals(BioPaxConstants.PROTEIN)) {
                    if (outputFormat == ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT) {
                        outputExternalId(outputSet, geneSymbols);
                    } else if (outputFormat == ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT) {
                        outputExternalId(outputSet, entrezGeneIds);
                    } else if (outputFormat == ExportFileUtil.GSEA_OUTPUT) {
						Set<String> externalIds = new HashSet<String>();
                        String externalId = Long.toString(descendentId);
						externalIds.add(externalId);
                        if (outputId != null && outputId.trim().length() > 0 &&
                                !outputId.equals(ExternalDatabaseConstants.INTERNAL_DATABASE)) {
                            externalIds = xrefMap.get(outputId);
                        }
                        outputExternalId(outputSet, externalIds);
                    }
                }
            } else if (outputFormat == ExportFileUtil.PC_OUTPUT) {
				if (participantRecord.getSpecificType().equals(BioPaxConstants.PROTEIN)) {
					line.append(descendentId + COLON);
					line.append(participantRecord.getSpecificType() + COLON);
					String name = participantRecord.getName();
					if (name != null) {
						//  Replace all : with -, so that we don't mess up the default delimiter.
						name = name.replaceAll(":", "-");
						line.append(participantRecord.getName() + COLON);
					} else {
						line.append(GetNeighborsCommand.NOT_SPECIFIED + COLON);
					}
                    if (uniprotAccessions != null) {
                        for (String uniprotAccession : uniprotAccessions) {
                            line.append(ExportUtil.getXRef(uniprotAccession) + COMMA);
                        }
						// replace last COMMA with COLON
						line = line.replace(line.lastIndexOf(COMMA), line.lastIndexOf(COMMA)+1, COLON);
                    } else {
						line.append(GetNeighborsCommand.NOT_SPECIFIED + COLON);
					}
                    if (geneSymbols != null) {
                        for (String geneSymbol : geneSymbols) {
                            line.append(ExportUtil.getXRef(geneSymbol) + COMMA);
                        }
						// replace last COMMA with COLON
						line = line.replace(line.lastIndexOf(COMMA), line.lastIndexOf(COMMA)+1, COLON);
                    } else {
						line.append(GetNeighborsCommand.NOT_SPECIFIED + COLON);
					}
                    if (entrezGeneIds != null) {
                        for (String entrezGeneId : entrezGeneIds) {
                            line.append(ExportUtil.getXRef(entrezGeneId) + COMMA);
                        }
						// remove last COMMA
						line = line.deleteCharAt(line.lastIndexOf(COMMA));
                    } else {
						line.append(GetNeighborsCommand.NOT_SPECIFIED);
					}
					line.append(TAB);
				}
            }
        }

        if (outputSet != null) {
            outputExternalId(line, outputSet);
        }

        line.append("\n");
        return line.toString();
    }

    // use this to prevent duplicate id's in output across participants
    private void outputExternalId(Set<String> outputSet, Set<String> geneSymbols) {
        if (geneSymbols != null && geneSymbols.size() > 0) {
			for (String geneSymbol : geneSymbols) {
				outputSet.add(geneSymbol);
			}
        }
    }

    private void outputExternalId(StringBuffer line, Set<String> geneSymbols) {
        if (geneSymbols != null && geneSymbols.size() > 0) {
			for (String geneSymbol : geneSymbols) {
				line.append(geneSymbol + TAB);
			}
        } else {
            line.append(GetNeighborsCommand.NOT_SPECIFIED + TAB);
        }
    }

	/**
	 * Helper function used when parent record taxid = -9999.  If all participant
	 * records have a taxid that are equal, assume parent record taxid equals
	 * participants taxid.  This was motivated by humancyc pathways. taxid of pathway
	 * is not assigned, but protein records are assigned.   -9999 != 9606, therefore
	 * participants were not getting exported.
	 *
	 * @param parent CPathRecord
	 * @param participantsList ArrayList<CPathRecord>
	 * @return boolean
	 */
	private boolean skipCrossSpeciesCheck(CPathRecord parent, ArrayList<CPathRecord> participantsList) {

		boolean toReturn = true;

		if (parent.getType() == CPathRecordType.PATHWAY &&
			parent.getNcbiTaxonomyId() == CPathRecord.TAXONOMY_NOT_SPECIFIED) {
			int taxID = CPathRecord.TAXONOMY_NOT_SPECIFIED;
			for (CPathRecord participant : participantsList) {
				if (participant.getSpecificType().equals(BioPaxConstants.PROTEIN)) {
					// set taxID - may as well use the first ID we encounter -
					// we assume taxID of protein(s) are specified
					if (taxID == CPathRecord.TAXONOMY_NOT_SPECIFIED) {
						taxID = participant.getNcbiTaxonomyId();
					}
					// check that this participant's tax id equals taxID
					if (participant.getNcbiTaxonomyId() != taxID) {
						toReturn = false;
						break;
					}
				}
					
			}
		}
		else {
			toReturn = false;
		}

		// outta here
		return toReturn;
	}
}