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
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;


/**
 * Command Line Utility to Export BioPAX Pathways & Interactions.
 *
 * @author Benjamn Gross
 */
public class ExportBioPAX {

    private ExportFileUtil exportFileUtil;
	private List<Long> processedRecordIDs;

    /**
     * Constructor.
     *
     * @param exportFileUtil Export File Util Object.
     */
    public ExportBioPAX(ExportFileUtil exportFileUtil) {

		// init members
        this.exportFileUtil = exportFileUtil;
		processedRecordIDs = new ArrayList<Long>();
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

		// bail if we have already processed this record
		if (processedRecordIDs.contains(record.getId())) {
			return;
		}

        //  get the database term
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        long snapshotId = record.getSnapshotId();
        ExternalDatabaseSnapshotRecord snapshotRecord =
                daoSnapshot.getDatabaseSnapshot(snapshotId);
        String dbTerm = snapshotRecord.getExternalDatabase().getMasterTerm();

		// get biopax xml string
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly(record, 1, XmlAssemblyFactory.XML_FULL, new XDebug());
		String xmlString = assembly.getXmlString();

		// dump the biopax
		exportFileUtil.appendToSpeciesFile(xmlString, record.getNcbiTaxonomyId(), ExportFileUtil.BIOPAX_OUTPUT);
		exportFileUtil.appendToDataSourceFile (xmlString, dbTerm, ExportFileUtil.BIOPAX_OUTPUT);

		// save id of record we have just processed - to prevent processing in future
		saveProcessedRecordIDs(record);
	}

	/**
	 * To prevent dumps of duplicate pathways & interactions, we keep track
	 * of already processed cpath record ids.  Given a pathway or interaction
	 * record, we add id to processedRecordIDs list and then iterate over all descendents.
	 * If descendent is pathway or interaction we recursively call routine.  If id is already
	 * in list, we exit routine.  This should mirror how BioPaXAssembly constructs a full xml
	 * file from given cpath id.
	 *
	 * @param record CPathRecord
	 * @throws DaoException
	 */
	void saveProcessedRecordIDs(CPathRecord record) throws DaoException {

		// prevent infinite looping
		if (processedRecordIDs.contains(record.getId())) {
			return;
		}

		// add ourself to processed record list
		processedRecordIDs.add(record.getId());

		// add descendent pathways and interactions
        DaoCPath daoCPath = DaoCPath.getInstance();
		DaoInternalLink internalLinker = new DaoInternalLink();
		ArrayList internalLinks = internalLinker.getTargetsWithLookUp(record.getId());
		for (int i = 0; i < internalLinks.size(); i++) {
			CPathRecord descendentRecord = (CPathRecord) internalLinks.get(i);
			// we only are concerned about pathways and interactions
			if (descendentRecord.getType() == CPathRecordType.PATHWAY ||
				descendentRecord.getType() == CPathRecordType.INTERACTION) {
				saveProcessedRecordIDs(descendentRecord);
			}
		}
	}
}
