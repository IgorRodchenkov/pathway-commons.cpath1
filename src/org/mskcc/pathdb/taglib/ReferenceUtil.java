package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.Reference;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoReference;
import org.mskcc.pathdb.util.CPathConstants;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Reference Utility Class.
 *
 * @author Ethan Cerami
 */
public class ReferenceUtil {

    /**
     * Creates a HashMap of Reference Objects, keyed by PMID.
     *
     * @param bpSummaryList List of BioPaxRecordSummary Objects
     * @return HashMap<String,Reference>
     * @throws org.mskcc.pathdb.sql.dao.DaoException
     */
    public HashMap<String, Reference> getReferenceMap
            (ArrayList<BioPaxRecordSummary> bpSummaryList, XDebug xdebug)
        throws DaoException {

        // hashset to return
        HashMap<String,Reference> referenceMap = new HashMap<String,Reference>();

        for (BioPaxRecordSummary bpSummary:  bpSummaryList) {
            // iterate over ExternalLinkRecord from bpSummary
            DaoReference daoReference = new DaoReference();
            if (bpSummary != null && bpSummary.getExternalLinks() != null) {
                List<ExternalLinkRecord> externalLinkRecords = bpSummary.getExternalLinks();
                for (ExternalLinkRecord externalLinkRecord : externalLinkRecords) {

                    // get the linked to id
                    String linkedToId = externalLinkRecord.getLinkedToId();

                    // get external database record
                    ExternalDatabaseRecord dbRecord = externalLinkRecord.getExternalDatabase();

                    // get the reference object
                    xdebug.logMsg (this, "Getting Reference for:  " + linkedToId);
                    Reference reference = daoReference.getRecord(linkedToId, dbRecord.getId());
                    if (CPathConstants.CPATH_DO_ASSERT) {
                        assert (reference != null) :
                        "ShowBioPaxRecord2.setExternalLinks(), reference object is null";
                    }
                    if (reference == null) {
                        xdebug.logMsg(this, "Could not find any reference info.");
                    } else {
                        xdebug.logMsg (this, "Found reference info:  " + reference.getTitle());
                    }

                    if (reference == null) continue;

                    // add reference string to proper list
                    referenceMap.put(linkedToId, reference);
                }
            }
        }

        // outta here
        return referenceMap;
    }

    /**
     * Gets Reference Html.
     * @param referenceLinks ArrayList of Reference Links.
     * @return HTML String
     */
    public String getReferenceHtml (ArrayList<ExternalLinkRecord> referenceLinks,
            HashMap<String, Reference> referenceMap) {
        StringBuffer html = new StringBuffer();
        // iterate over list of ExternalLinkRecords
		if (referenceLinks.size() > 0) {
		    html.append("<p><b>References:</b></p>\n\r");
            html.append("<ul>\n\r");
            for (ExternalLinkRecord externalLinkRecord : referenceLinks) {
                Reference reference = referenceMap.get(externalLinkRecord.getLinkedToId());
                html.append("<li>");
                String uri = (externalLinkRecord.getWebLink() == null) ? "" :
                    externalLinkRecord.getWebLink();
                String database = (reference.getDatabase() == null) ? "" :
                    reference.getDatabase();
                uri = (uri == null) ? "" : uri;
                html.append(reference.getReferenceString() + " " +
                            "[<a href=\"" + uri + "\">" + database + "</a>]");
                html.append("</li>\n\r");
            }
            html.append("</ul>\n\r");
			html.append("<br>");
        }
        return html.toString();
    }

    /**
     * Split XRefs into references v. non-references.
     * @param bpSummary BioPaxRecordSummary Object.
     * @return ArrayList with two items:  (0):  reference links, (1):  non-reference links
     */
    public ArrayList categorize(BioPaxRecordSummary bpSummary) {
        ArrayList<ExternalLinkRecord> referenceLinks = new ArrayList<ExternalLinkRecord>();
        ArrayList<ExternalLinkRecord> nonReferenceLinks = new ArrayList<ExternalLinkRecord>();
        if (bpSummary.getExternalLinks() != null) {
            for (int i=0; i<bpSummary.getExternalLinks().size(); i++) {
                ExternalLinkRecord link = (ExternalLinkRecord) bpSummary.getExternalLinks().get(i);
                String dbName = link.getExternalDatabase().getName();
                if (dbName.equalsIgnoreCase("PUBMED")) {
                    referenceLinks.add(link);
                } else {
                    nonReferenceLinks.add(link);
                }
            }
        }
        ArrayList masterList = new ArrayList();
        masterList.add(referenceLinks);
        masterList.add(nonReferenceLinks);
        return masterList;
    }
}
