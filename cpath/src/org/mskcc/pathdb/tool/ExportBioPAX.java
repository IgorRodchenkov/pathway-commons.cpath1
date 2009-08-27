// package
package org.mskcc.pathdb.tool;

// imports
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoSourceTracker;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.IOException;


/**
 * Command Line Utility to Export BioPAX Pathways & Interactions.
 *
 * @author Benjamn Gross
 */
public class ExportBioPAX {

    private ExportFileUtil exportFileUtil;

    /**
     * Constructor.
     *
     * @param exportFileUtil Export File Util Object.
     */
    public ExportBioPAX(ExportFileUtil exportFileUtil) {

		// init members
        this.exportFileUtil = exportFileUtil;
    }

    /**
     * Exports the specified interaction record.
     * @param record        CPath Interaction Record.
     * @throws DaoException         Database Error.
     * @throws AssemblyException    XML Assembly Error.
     * @throws IOException          IO Error.
     */
    public void exportRecord(CPathRecord record)
            throws DaoException, AssemblyException, IOException {

		// save type of record
		boolean recordTypePE = (record.getType() == CPathRecordType.PHYSICAL_ENTITY);

		// get biopax xml string
		int mode = (recordTypePE) ?
			XmlAssemblyFactory.XML_FULL : XmlAssemblyFactory.XML_ABBREV;
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly(record, 1, mode, new XDebug());
		String xmlString = assembly.getXmlString();

        //  dump biopax to data source file
		ArrayList<Long> snapshotIDs = new ArrayList<Long>();
		if (recordTypePE) {
            DaoSourceTracker daoSourceTracker = new DaoSourceTracker();
			ArrayList<CPathRecord> records = daoSourceTracker.getSourceRecords(record.getId());
			for (CPathRecord sourceRecord : records) {
				snapshotIDs.add(sourceRecord.getId());
			}
		}
		else {
			snapshotIDs.add(record.getSnapshotId());
		}
		for (Long snapshotID : snapshotIDs) {
			DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
			ExternalDatabaseSnapshotRecord snapshotRecord =
				daoSnapshot.getDatabaseSnapshot(snapshotID);
			if (snapshotRecord != null) {
				String dbTerm = snapshotRecord.getExternalDatabase().getMasterTerm();
				if (dbTerm != null && dbTerm.length() > 0) {
					exportFileUtil.appendToDataSourceFile (xmlString, dbTerm, ExportFileUtil.BIOPAX_OUTPUT);
				}
			}
		}

		// dump biopax to species file
		HashSet<Integer> ncbiTaxonomyIDs = getTaxIDs(record);
		for (Integer taxID : ncbiTaxonomyIDs) {
			exportFileUtil.appendToSpeciesFile(xmlString, taxID, ExportFileUtil.BIOPAX_OUTPUT);
		}
	}

	/**
	 * Given a record ID, returns a set of tax ids.
	 *
	 * @param record CPathRecord
	 * @return HashSet<Integer>
	 */
	HashSet<Integer> getTaxIDs(CPathRecord record) throws DaoException {

		// hashset to return
		HashSet<Integer> toReturn = new HashSet<Integer>();

		// if we have pathway
		if (record.getType() == CPathRecordType.PATHWAY) {
			toReturn.add(record.getNcbiTaxonomyId());
		}
		// PE or interaction
		else {
			// grab children
			ExportUtil.getNCBITaxonomyIDs(record, toReturn, new ArrayList<Long>());
			// grab tax ids of all fellow interaction participants and parent pathways
			appendFellowParticipantTaxIDs(record, toReturn, new ArrayList<Long>());
		}

		// outta here
		return toReturn;
	}

	/**
	 * Given a interaction or complex gathers tax ids from fellow participants.
	 *
	 * @param record CPathRecord
	 * @param ncbiTaxonomyIDs HashSet<Integer>
	 * @parma recIDs ArrayList<Long>
	 */
	void appendFellowParticipantTaxIDs(CPathRecord record, HashSet<Integer> ncbiTaxonomyIDs, ArrayList<Long> recIDs) throws DaoException {

		// prevent infinite looping
		if (recIDs.contains(record.getId())) {
			return;
		}
		else {
			recIDs.add(record.getId());
		}

		// interate over ancestors of this record and get tax ids
		DaoCPath daoCPath = DaoCPath.getInstance();
		DaoInternalLink internalLinker = new DaoInternalLink();
		ArrayList internalLinks = internalLinker.getSources(record.getId());
		for (int lc = 0; lc < internalLinks.size(); lc++) {
			InternalLinkRecord link = (InternalLinkRecord)internalLinks.get(lc);
			CPathRecord sourceRecord = daoCPath.getRecordById(link.getSourceId());
			if (sourceRecord.getType() == CPathRecordType.PATHWAY) {
				ncbiTaxonomyIDs.add(sourceRecord.getNcbiTaxonomyId());
			}
			else if (sourceRecord.getType() == CPathRecordType.INTERACTION ||
					 sourceRecord.getSpecificType().contains("complex")) {
				// get tax id of all descendents
				ExportUtil.getNCBITaxonomyIDs(sourceRecord, ncbiTaxonomyIDs, new ArrayList<Long>());
				// go up another level
				appendFellowParticipantTaxIDs(sourceRecord, ncbiTaxonomyIDs, recIDs);
			}
		}
	}
}
