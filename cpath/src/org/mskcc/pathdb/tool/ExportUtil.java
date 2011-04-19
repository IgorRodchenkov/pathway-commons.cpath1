package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.query.GetNeighborsCommand;

import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Export Utility Class.
 */
public class ExportUtil {

    /**
     * XRef Look up.
     */
    public static HashMap<String, Set<String>> getXRefMap(long cpathId) throws DaoException {
        HashMap<String, Set<String>> xrefMap = new HashMap<String, Set<String>>();
        DaoExternalLink daoExternalLink = DaoExternalLink.getInstance();
        ArrayList<ExternalLinkRecord> xrefList = daoExternalLink.getRecordsByCPathId(cpathId);
        for (ExternalLinkRecord xref : xrefList) {
            String dbMasterTerm = xref.getExternalDatabase().getMasterTerm();
			Set<String> dbSet = ((xrefMap.get(dbMasterTerm)) == null) ? new HashSet<String>() : xrefMap.get(dbMasterTerm);
            String xrefId = xref.getLinkedToId();
			dbSet.add(xrefId);
            xrefMap.put(dbMasterTerm, dbSet);
        }
        return xrefMap;
    }

    public static String getXRef (String id) {
        if (id == null) {
            return GetNeighborsCommand.NOT_SPECIFIED;
        } else {
            return id.trim().toUpperCase();
        }
    }

	/**
	 * NCBI Taxonomy ID lookup.
	 *
	 * @param record CPathRecord
	 * @param taxIDs HashSet<Integer>
	 * @param recIDs ArrayList<Long>
     * @throws DaoException         Database Error.
	 */
	public static void getNCBITaxonomyIDs(CPathRecord record, HashSet<Integer> taxIDs, ArrayList<Long> recIDs)
		throws DaoException {

		// prevent infinite looping
		if (recIDs.contains(record.getId())) {
			return;
		}
		else {
			recIDs.add(record.getId());
		}

		// get records tax id
		int ncbiTaxonomyID = record.getNcbiTaxonomyId();

		// we need to iterate over all participants and get list of organism ids
		if (ncbiTaxonomyID == CPathRecord.TAXONOMY_NOT_SPECIFIED) {
			DaoCPath daoCPath = DaoCPath.getInstance();
			DaoInternalLink internalLinker = new DaoInternalLink();
			ArrayList internalLinks = internalLinker.getTargetsWithLookUp(record.getId());
			for (int i = 0; i < internalLinks.size(); i++) {
				CPathRecord descendentRecord = (CPathRecord) internalLinks.get(i);
				if (descendentRecord.getType() == CPathRecordType.PATHWAY ||
					descendentRecord.getType() == CPathRecordType.INTERACTION ||
					descendentRecord.getSpecificType().contains("complex")) {
					ExportUtil.getNCBITaxonomyIDs(descendentRecord, taxIDs, recIDs);
				}
				else {
					int taxID = descendentRecord.getNcbiTaxonomyId();
					if (!taxIDs.contains(taxID) && organismFromPathwayOrInteraction(taxID)) {
						taxIDs.add(taxID);
					}
				}
			}
		}
		// add rec tax id directly and bail
		else {
			if (!taxIDs.contains(ncbiTaxonomyID) && organismFromPathwayOrInteraction(ncbiTaxonomyID)) {
				taxIDs.add(ncbiTaxonomyID);
			}
		}
	}

	private static boolean organismFromPathwayOrInteraction(int taxID) throws DaoException {
		DaoOrganism dao = new DaoOrganism();
		Organism organism = dao.getOrganismByTaxonomyId(taxID);
		return (organism == null) ? false : organism.fromPathwayOrInteraction();
	}
}
