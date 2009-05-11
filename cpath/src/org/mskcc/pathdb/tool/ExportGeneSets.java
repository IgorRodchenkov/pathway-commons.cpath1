package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Command Line Utility to Export Gene Sets.
 *
 * This classes supports two data formats:
 * 
 * 1)  GSEA GMT: Gene Matrix Transposed file format (*.gmt) Format.
 * Format is described at:
 * http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats
 *
 * 2)  Pathway Commons Gene Set format:  Similar to the GSEA GMT format, except that all
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
 */
public class ExportGeneSets {
    private final static String TAB = "\t";
    private final static String COLON = ":";
    private final static String NA = "NOT_SPECIFIED";
    private ExportFileUtil exportFileUtil;
	private HashMap<String, Integer> processedRecords;

    public ExportGeneSets (ExportFileUtil exportFileUtil) {
        this.exportFileUtil = exportFileUtil;
		processedRecords = new HashMap<String, Integer>();
    }

    /**
     * Dumps the Pathway Record in the specified file format.
     */
    public void exportPathwayRecord(CPathRecord record) throws DaoException, IOException {

		// this processed record count
		int count = (processedRecords.containsKey(record.getName())) ?
			processedRecords.get(record.getName()) : 0;

		if (count > 0) {
			record.setName(record.getName() + " - " + count+1);
		}

        //  Gets the Database Term
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        long snapshotId = record.getSnapshotId();
        ExternalDatabaseSnapshotRecord snapshotRecord =
                daoSnapshot.getDatabaseSnapshot(snapshotId);
        String dbTerm = snapshotRecord.getExternalDatabase().getMasterTerm();

        DaoInternalFamily daoInternalFamily = new DaoInternalFamily();
        long[] descendentIds = daoInternalFamily.getDescendentIds(record.getId(),
                CPathRecordType.PHYSICAL_ENTITY);

        ArrayList <CPathRecord> cpathRecordList = new ArrayList <CPathRecord>();

        DaoCPath daoCPath = DaoCPath.getInstance();
        //  Get XRefs for all Participants
        ArrayList <HashMap <String, String>> xrefList =
                new ArrayList <HashMap <String, String>>();
        for (long descendentId : descendentIds) {
            HashMap <String, String> xrefMap = ExportUtil.getXRefMap (descendentId);
            cpathRecordList.add(daoCPath.getRecordById(descendentId));
            xrefList.add (xrefMap);
        }

        //  Dump to both file formats.
        exportGeneSet(record, dbTerm, cpathRecordList, xrefList, ExportFileUtil.GSEA_OUTPUT);
        exportGeneSet(record, dbTerm, cpathRecordList, xrefList, ExportFileUtil.PC_OUTPUT);

		// add to processed list
		processedRecords.put(record.getName(), ++count);
    }

    /**
     * Actual Output of the Gene Set.
     */
    private void exportGeneSet(CPathRecord record, String dbTerm, ArrayList <CPathRecord>
            cpathRecordList, ArrayList<HashMap<String, String>> xrefList, int outputFormat)
            throws IOException, DaoException {
        StringBuffer line = new StringBuffer();
        line.append (record.getName() + TAB);
        line.append (dbTerm + TAB);

        int numParticipantsOutput = 0;
        for (int i=0; i < cpathRecordList.size(); i++) {
            CPathRecord participantRecord = cpathRecordList.get(i);
			// per spec, GSEA & PC does not support cross-species interactions
			if (participantRecord.getType() == CPathRecordType.INTERACTION) {
				HashSet<Integer> ncbiTaxonomyIDs = new HashSet<Integer>();
				ExportUtil.getNCBITaxonomyIDs(record, ncbiTaxonomyIDs, new ArrayList<Long>());
				if (ncbiTaxonomyIDs.size() > 1) {
					continue;
				}
			}
            long descendentId = participantRecord.getId();
            HashMap <String, String> xrefMap = xrefList.get(i);
            String geneSymbol = xrefMap.get(ExternalDatabaseConstants.GENE_SYMBOL);
            String entrezGeneId = xrefMap.get(ExternalDatabaseConstants.ENTREZ_GENE);
            String uniprotAccession = xrefMap.get(ExternalDatabaseConstants.UNIPROT);
            if (outputFormat == ExportFileUtil.GSEA_OUTPUT) {
                if (geneSymbol != null) {
                    numParticipantsOutput++;
                    line.append (geneSymbol + TAB);
                }
            } else {
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

        //  Append to the correct output files
        if (numParticipantsOutput > 0) {
            exportFileUtil.appendToSpeciesFile(line.toString(), record.getNcbiTaxonomyId(),
                    outputFormat);
            exportFileUtil.appendToDataSourceFile (line.toString(), dbTerm, outputFormat);
        }
    }
}