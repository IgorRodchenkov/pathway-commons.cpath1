package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;

import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Command Line Utility to Export Gene Sets.
 *
 * This classes supports two data formats:
 * 
 * 1) ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT: Gene Matrix Transposed file format (*.gmt) Format.
 * Format is described at:
 * http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats
 *
 * 2) ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT:  Same as above, except we export Entre Gene IDs,
 * instead of gene symbols.
 *
 * 3) ExportFileUtil.PC_OUTPUT:  Similar to the GSEA GMT format, except that all
 * participants are micro-encoded with multiple identifiers. For example, each participant
 * is specified as: CPATH_ID:RECORD_TYPE:NAME:UNIPROT_ACCESSION:GENE_SYMBOL:ENTREZ_GENE_ID.
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
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class ExportGeneSets {
    private final static String TAB = "\t";
    private final static String COLON = ":";
    private final static String NA = "NOT_SPECIFIED";
	private HashMap<String, Integer> processedRecords;

    /**
     * Constructor.
     */
    public ExportGeneSets () {
		processedRecords = new HashMap<String, Integer>();
    }

    /**
     * Exports the Pathway Record in all File Formats.
     * @param record CPath Record.
     */
    public void exportPathwayRecordAllFormats(CPathRecord record, ExportFileUtil exportFileUtil)
            throws DaoException, IOException {

		// this processed record count
		int count = (processedRecords.containsKey(record.getName())) ?
			processedRecords.get(record.getName()) : 0;

		if (count > 0) {
			record.setName(record.getName() + " - " + count+1);
		}

        //  Gets the Database Term
        String dbMasterTerm = getMasterDbTerm (record);
        ArrayList <CPathRecord> participantRecordList = getParticipants (record);

        //  Get XRefs for all Participants
        ArrayList<HashMap<String, String>> xrefList = getParticipantXRefs(participantRecordList);

        //  Dump to all file formats.
        exportGeneSet(record, dbMasterTerm, participantRecordList, xrefList, exportFileUtil,
                ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT);
        exportGeneSet(record, dbMasterTerm, participantRecordList, xrefList, exportFileUtil,
                ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT);
        exportGeneSet(record, dbMasterTerm, participantRecordList, xrefList, exportFileUtil,
                ExportFileUtil.PC_OUTPUT);

		// add to processed list
		processedRecords.put(record.getName(), ++count);
    }

    /**
     * Exports the Pathway Record in the Specified Output Format.
     * 
     * @param record CPath Record.
     */
    public String exportPathwayRecord (CPathRecord record, int exportOutputFormat)
            throws DaoException, IOException {

        //  Gets the Database Term
        String dbMasterTerm = getMasterDbTerm (record);
        ArrayList <CPathRecord> participantRecordList = getParticipants (record);

        //  Get XRefs for all Participants
        ArrayList<HashMap<String, String>> xrefList = getParticipantXRefs(participantRecordList);

        String exportText = exportGeneSetToText (record, dbMasterTerm, participantRecordList, xrefList,
                exportOutputFormat);
        return exportText;
    }

    /**
     * Gets the Master Database Term.
     * @param record CPath Record.
     * @return Master Term
     * @throws DaoException Database Error.
     */
    private String getMasterDbTerm (CPathRecord record) throws DaoException {
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
     * @param record    CPath Record.
     * @return          ArrayList of CPath Records.
     * @throws DaoException Database Error.
     */
    private ArrayList <CPathRecord> getParticipants(CPathRecord record) throws DaoException {
        ArrayList <CPathRecord> participantRecordList = new ArrayList <CPathRecord>();
        DaoInternalFamily daoInternalFamily = new DaoInternalFamily();
        long[] descendentIds = daoInternalFamily.getDescendentIds(record.getId(),
                CPathRecordType.PHYSICAL_ENTITY);
        DaoCPath daoCPath = DaoCPath.getInstance();
        for (long descendentId : descendentIds) {
            participantRecordList.add(daoCPath.getRecordById(descendentId));
        }
        return participantRecordList;
    }

    /**
     * Gets the Particpant XRefs.
     * @param participantRecordList List of CPath Records.
     * @return Participant XRefs.
     * @throws DaoException Database Error.
     */
    private ArrayList<HashMap<String, String>> getParticipantXRefs(ArrayList<CPathRecord> participantRecordList)
            throws DaoException {
        ArrayList <HashMap <String, String>> xrefList =
                new ArrayList <HashMap <String, String>>();
        for (CPathRecord participantRecord : participantRecordList) {
            HashMap <String, String> xrefMap = ExportUtil.getXRefMap (participantRecord.getId());
            xrefList.add (xrefMap);
        }
        return xrefList;
    }

    /**
     * Actual Output of the Gene Set.
     */
    private void exportGeneSet(CPathRecord record, String dbTerm, ArrayList <CPathRecord>
            cpathRecordList, ArrayList<HashMap<String, String>> xrefList, ExportFileUtil exportFileUtil,
           int outputFormat)
            throws IOException, DaoException {
        String text = exportGeneSetToText (record, dbTerm, cpathRecordList, xrefList,
                outputFormat);

        //  Append to the correct output files
        if (text != null) {
            exportFileUtil.appendToSpeciesFile(text, record.getNcbiTaxonomyId(),
                    outputFormat);
            exportFileUtil.appendToDataSourceFile (text, dbTerm, outputFormat);
        }
    }

    /**
     * Output Gene Set.
     * @param record                CPath Record
     * @param dbTermMasterTerm      Database Master Term
     * @param participantsList      CPath Records for All Participants
     * @param xrefList              XRefs for all Participants
     * @param outputFormat          Output Format.
     * @return Text Output.
     * @throws IOException          IO Error.
     * @throws DaoException         Database Error.
     */
    private String exportGeneSetToText(CPathRecord record, String dbTermMasterTerm, ArrayList <CPathRecord>
            participantsList, ArrayList<HashMap<String, String>> xrefList, int outputFormat)
            throws IOException, DaoException {
        StringBuffer line = new StringBuffer();
        line.append (record.getName() + TAB);
        line.append (dbTermMasterTerm + TAB);

        int numParticipantsOutput = 0;
        for (int i=0; i < participantsList.size(); i++) {
            CPathRecord participantRecord = participantsList.get(i);
			// per spec, GSEA & PC should not contain cross-species genes
			if (participantRecord.getNcbiTaxonomyId() != record.getNcbiTaxonomyId()) {
                continue;
			}
            long descendentId = participantRecord.getId();
            HashMap <String, String> xrefMap = xrefList.get(i);
            String geneSymbol = xrefMap.get(ExternalDatabaseConstants.GENE_SYMBOL);
            String entrezGeneId = xrefMap.get(ExternalDatabaseConstants.ENTREZ_GENE);
            String uniprotAccession = xrefMap.get(ExternalDatabaseConstants.UNIPROT);
            if (outputFormat == ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT) {
                if (geneSymbol != null) {
                    numParticipantsOutput++;
                    line.append (geneSymbol + TAB);
                }
			}
			else if (outputFormat == ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT) {
				if (entrezGeneId != null) {
                    numParticipantsOutput++;
                    line.append (entrezGeneId + TAB);
                }
            }
			else if (outputFormat == ExportFileUtil.PC_OUTPUT) {
                numParticipantsOutput++;
                line.append (descendentId + COLON);
                line.append (participantRecord.getSpecificType() + COLON);
                String name = participantRecord.getName();
                if (name != null) {
                    //  Replace all : with -, so that we don't mess up the default delimiter.
                    name = name.replaceAll(":", "-");
                    line.append (participantRecord.getName() + COLON);
                } else {
                    line.append (NA + COLON);
                }
                line.append (ExportUtil.getXRef (uniprotAccession) + COLON);
                line.append (ExportUtil.getXRef (geneSymbol) + COLON);
                line.append (ExportUtil.getXRef (entrezGeneId));
                line.append (TAB);
            }
        }
        line.append ("\n");
        if (numParticipantsOutput > 0) {
            return line.toString();
        } else {
            return line.toString();
        }
    }
}