package org.mskcc.pathdb.query.batch;

import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

import java.util.ArrayList;

/**
 * Batch Query to find all pathways for all specified genes/proteins.
 *
 * @author Ethan Cerami
 */
public class PathwayBatchQuery {

    /**
     * Given a list of Physical Entity IDs, get the complete set of all linked pathways.
     *
     * @param ids       Array of External Ids.
     * @param dbTerm    External Database Term, e.g. UNIPROT, ENTREZ_GENE, etc.
     * @return ArrayList of PhysicalEntityWithPathwayList Objects.
     */
    public ArrayList<PhysicalEntityWithPathwayList> executeBatchQuery (String ids[],
            String dbTerm) throws DaoException, BioPaxRecordSummaryException {
        DaoCPath daoCPath = DaoCPath.getInstance();

        //  Look up External Database
        DaoExternalDb daoExternalDb = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord = daoExternalDb.getRecordByTerm(dbTerm);

        if (dbRecord == null) {
            throw new IllegalArgumentException("External Database is unknown:  "
                + dbTerm);
        }

        DaoExternalLink daoExternalLinker = DaoExternalLink.getInstance();
        DaoInternalFamily daoInternalFamily = new DaoInternalFamily();
        ArrayList<PhysicalEntityWithPathwayList> responseList =
                new ArrayList<PhysicalEntityWithPathwayList>();

        //  Iterate through all external IDs
        for (int i=0; i<ids.length; i++) {

            //  Get all cPath Records that match external IDs
            //  Note that each external ID could map to multiple physical entities.
            //  For example, an Entrez Gene ID could map to multiple splice-variant proteins.
            ArrayList externalLinkList = daoExternalLinker.getRecordByDbAndLinkedToId
                    (dbRecord.getId(), ids[i]);

            ArrayList <BioPaxRecordSummary> pathwayList = new ArrayList <BioPaxRecordSummary>();
            //  Iterate through all physical entities
            for (int j=0; j<externalLinkList.size(); j++) {

                //  Retrieve physical entity
                ExternalLinkRecord externalLinkRecord =
                        (ExternalLinkRecord) externalLinkList.get(j);
                CPathRecord record = daoCPath.getRecordById(externalLinkRecord.getCpathId());

                //  Get all pathways this entity participates in
                long pathwayIds[] = daoInternalFamily.getAncestorIds(record.getId(),
                        CPathRecordType.PATHWAY);

                //  Retrieve summary details regarding all pathways
                for (int k=0; k<pathwayIds.length; k++) {
                    CPathRecord pathwayRecord = daoCPath.getRecordById(pathwayIds[k]);
                    BioPaxRecordSummary pathwaySummary =
                            BioPaxRecordUtil.createBioPaxRecordSummary(pathwayRecord);
                    pathwayList.add(pathwaySummary);
                }
            }
            //  Store the response data
            PhysicalEntityWithPathwayList peData = new PhysicalEntityWithPathwayList();
            peData.setExternalId(ids[i]);
            peData.setExternalDb(dbRecord);
            peData.setPathwayList(pathwayList);
            responseList.add(peData);
        }
        return responseList;
    }

    /**
     * Given a List of PhysicalEntityWithPathwayList Objects, outputs a tab-delimited
     * summary of all data.
     *
     * @param responseList  ArrayList of PhysicalEntityWithPathwayList Objects.
     * @return tab-delimited summary of all data.
     * @throws DaoException
     */
    public String outputTabDelimitedText
            (ArrayList<PhysicalEntityWithPathwayList> responseList) throws DaoException {
        StringBuffer out = new StringBuffer();
        outputHeader(out);
        for (int i=0; i<responseList.size(); i++) {
            PhysicalEntityWithPathwayList peData = responseList.get(i);
            ArrayList <BioPaxRecordSummary> pathwayList = peData.getPathwayList();
            if (pathwayList.size() == 0) {
                out.append(peData.getExternalDb().getMasterTerm());
                out.append(":" + peData.getExternalId());
                out.append("\tNO_DATA\n");
            } else {
                for (int j=0; j < pathwayList.size(); j++) {
                    BioPaxRecordSummary pathwaySummary = pathwayList.get(j);
                    out.append(peData.getExternalDb().getMasterTerm());
                    out.append(":" + peData.getExternalId());
                    out.append("\t" + pathwaySummary.getName());
                    out.append("\t" +
                            pathwaySummary.getExternalDatabaseSnapshotRecord().
                            getExternalDatabase().getMasterTerm());
                    out.append("\t" + pathwaySummary.getRecordID() + "\n");
                }
            }
        }
        return out.toString();
    }

    private static void outputHeader (StringBuffer out) {
        out.append ("Database:ID\t");
        out.append ("Pathway_Name\t");
        out.append ("Pathway_Database_Name\t");
        out.append ("Internal_ID\n");
    }
}

