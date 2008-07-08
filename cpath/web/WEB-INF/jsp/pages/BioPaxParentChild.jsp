<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.action.BioPaxParentChild"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.*"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalLink"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoException"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.mskcc.pathdb.taglib.ReactomeCommentUtil"%>
<%@ page import="org.mskcc.pathdb.taglib.ReferenceUtil"%>
<%@ page import="org.mskcc.pathdb.taglib.EvidenceUtil"%>
<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ page import="org.mskcc.pathdb.model.*"%>
<%@ page import="java.io.IOException"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
int start = 0;
String startIndex = request.getParameter(BioPaxParentChild.START_INDEX_PARAMETER);
if (startIndex != null) {
    start = Integer.parseInt(startIndex);
}
start++;
String type = request.getParameter(BioPaxParentChild.TYPE_PARAMETER);
String showHeader = request.getParameter("showHeader");
boolean headerFlag = true;
if (showHeader != null && showHeader.equals("false")) {
    headerFlag = false;
}

int NUM_PREVIEW_INTERACTION_PARTICIPANTS = 2;
int max = BioPaxParentChild.MAX_RECORDS;

int total = 9999;
String totalNumRecords = request.getParameter(BioPaxParentChild.TOTAL_NUM_RECORDS_PARAMETER);
if (totalNumRecords != null) {
    total = Integer.parseInt(totalNumRecords);
}

int stop = start + max -1;
if (stop > total) {
    stop = total;
}
HashMap<String, Reference> referenceMap =
        (HashMap<String,Reference>)request.getAttribute(BioPaxParentChild.KEY_PMID_MAP);

boolean debugMode = false;
String xdebugSession = (String) session.getAttribute
        (AdminWebLogging.WEB_LOGGING);
String xdebugParameter = request.getParameter(AdminWebLogging.WEB_LOGGING);
if (xdebugSession != null || xdebugParameter != null) {
    debugMode = true;
}

DetailsTracker detailsTracker = new DetailsTracker();

//  The BioPAX Record Summary for the protein page we are on
BioPaxRecordSummary proteinPageBpSummary =
	(BioPaxRecordSummary) request.getAttribute(BioPaxParentChild.BP_SUMMARY_OBJECT_PARAMETER);
%>

<% if (headerFlag) { %>
<table WIDTH=100% CELLSPACING=0 CELLPADDING=4>
    <tr bgcolor=#DDDDDD>
        <td>Showing
        <span id="<%= "start_"+type%>"><%= start %></span>-<span id="<%= "stop_"+type%>"><%= stop %></span> of
        <span id="<%= "total_"+type%>"><%= total %></span>
        </td>
        <td align=right>
        <%
            String nextStyle = "none";
            if (stop < total) {
                nextStyle = "inline";
            }
            String prevStyle = "none";
            if (start > 1) {
                prevStyle = "inline";
            }
        %>
        <span style="display:<%= prevStyle %>" class="button" id="prev_<%= type %>" onClick="getPreviousData('<%= type %>')">&lt; Previous</span>
        &nbsp;
        <span style="display:<%= nextStyle %>" class="button" id="next_<%= type %>" onClick="getNextData('<%= type %>')">Next &gt;</span>
        </td>
    </tr>
</table>
<% } %>

<div id="content_<%= type%>">
<table width=100% CELLSPACING=0 CELLPADDING=4>
<%
ArrayList <BioPaxRecordSummary>  bpSummaryList = (ArrayList<BioPaxRecordSummary>)
        request.getAttribute(BioPaxParentChild.KEY_BP_SUMMARY_LIST);
HashMap <Long, EntitySummary> interactionSummaryMap = (HashMap <Long, EntitySummary>)
        request.getAttribute(BioPaxParentChild.KEY_INTERACTION_SUMMARY_MAP);
HashMap <Long, ArrayList> parentInteractionMap = (HashMap <Long, ArrayList>)
        request.getAttribute(BioPaxParentChild.KEY_INTERACTION_PARENTS_SUMMARY_MAP);

int index = start;
for (int i = 0; i < bpSummaryList.size(); i++) {
    out.println(getStartRow(i));
    BioPaxRecordSummary bpSummary = bpSummaryList.get(i);
    out.println("<td>");
    out.println(index + ". ");
    if (debugMode) {
        out.println("[<a href='record2.do?debug=1&id=" + bpSummary.getRecordID()
            + "'>" + bpSummary.getRecordID() + "</a>]");
        out.println("[<a href='record.do?format=xml_abbrev&id=" + bpSummary.getRecordID()
            + "'>BP Abbrev</a>]");
    }
    if (interactionSummaryMap.containsKey(bpSummary.getRecordID())) {
        EntitySummary entitySummary = interactionSummaryMap.get(bpSummary.getRecordID());
        if (entitySummary instanceof InteractionSummary) {
            InteractionSummary interactionSummary = (InteractionSummary) entitySummary;
            List<String> interactionSummaryStringList = InteractionSummaryUtils.createInteractionSummaryStringList
                (interactionSummary, proteinPageBpSummary);
            if (interactionSummaryStringList.size() > 0) {
                out.println("<span class='entity_summary'>");
				int interactionSummaryStringListSize = interactionSummaryStringList.size();
				int num_participants_to_show = (interactionSummary.getSpecificType().equals("physicalInteraction") &&
												interactionSummaryStringListSize > NUM_PREVIEW_INTERACTION_PARTICIPANTS)
					? NUM_PREVIEW_INTERACTION_PARTICIPANTS : interactionSummaryStringListSize;
				for (int lc = 0; lc < num_participants_to_show; lc++) {
					out.println(interactionSummaryStringList.get(lc));
				}
				int remainding_particpants_to_show = (interactionSummary.getSpecificType().equals("physicalInteraction") &&
													  num_participants_to_show < interactionSummaryStringListSize) ?
													  (interactionSummaryStringListSize - NUM_PREVIEW_INTERACTION_PARTICIPANTS) : 0;
				if (remainding_particpants_to_show > 0) {
					out.println("(and " + String.valueOf(remainding_particpants_to_show) + " other participants)");
				}
            }
            outputParentInteractions(interactionSummary, parentInteractionMap,
                    interactionSummaryMap, out, debugMode);
            out.println("</span>");
            out.println("</td>");
            out.println(getBioPaxDetailsHtml(bpSummary, referenceMap, i, detailsTracker, interactionSummary,
											 interactionSummaryStringList, NUM_PREVIEW_INTERACTION_PARTICIPANTS));
        } else {
            out.println(getBioPaxRecordHtml(bpSummary, referenceMap, i, detailsTracker));
        }
    } else {
        out.println(getBioPaxRecordHtml(bpSummary, referenceMap, i, detailsTracker));
    }
    out.println("</tr>");
    index++;
}
%>

<%!
    private boolean hasDetails(BioPaxRecordSummary bpSummary,
            ArrayList<ExternalLinkRecord> referenceLinks, InteractionSummary interactionSummary,
            List<String> interactionSummaryStringList, int NUM_PREVIEW_INTERACTION_PARTICIPANTS) {
        boolean hasDetails = false;
        if      (  fieldExists(bpSummary.getComments())
                || fieldExists(bpSummary.getOrganism())
                || fieldExists (bpSummary.getAvailability())
                || referenceLinks.size() > 0
                || (interactionSummary != null &&
                    interactionSummary.getEvidence() != null &&
                    interactionSummary.getEvidence().size() > 0)
                || interactionSummaryStringList != null &&
                    interactionSummary.getSpecificType().equals("physicalInteraction") &&
                    (interactionSummaryStringList.size() - NUM_PREVIEW_INTERACTION_PARTICIPANTS > 0)) {
            hasDetails = true;
        }
        return hasDetails;
    }

    private boolean fieldExists (String field) {
        if (field != null && field.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean fieldExists (String[] fields) {
        if (fields != null && fields.length > 0) {
            if (fields[0].length() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Outputs parent interactions, e.g. controllers.
     */
    private void outputParentInteractions(InteractionSummary interactionSummary,
            HashMap<Long, ArrayList> parentInteractionMap,
            HashMap<Long, EntitySummary> interactionSummaryMap,
            JspWriter out, boolean debugMode) throws IOException {
        long cpathId = interactionSummary.getRecordID();

        //  First, get all parents of this interaction
        ArrayList parentRecords = parentInteractionMap.get(cpathId);
        if (parentRecords != null) {
            out.println("<UL>");
            for (int i = 0; i < parentRecords.size(); i++) {
                CPathRecord parentRecord = (CPathRecord) parentRecords.get(i);
                EntitySummary summary = interactionSummaryMap.get(parentRecord.getId());
                if (summary instanceof InteractionSummary) {
                    String summaryStr = InteractionSummaryUtils.createInteractionSummaryStringTruncated
                            ((InteractionSummary) summary);
                    out.println("<LI>");
                    if (debugMode) {
                        out.println("[<a href='record2.do?id=" + summary.getRecordID()
                                + "&debug=1'>" + summary.getRecordID() + "</a>]");
                    }
                    out.println(summaryStr + "</LI>");
                }
            }
            out.println("</UL>");
        }
    }

    private String getInspectorButtonHtml(long cPathId) {
        return "<td align=right><div class='toggle_details'><a "
                + "title='Toggle Record Details' onClick=\"toggleDetails('cpath_" + cPathId
                + "')\"><div id='cpath_" + cPathId + "_image' class='toggleImage'>"
                + "<img src='jsp/images/open.gif'/></div></a></td>";
    }

    private String getDetailsHtml(long cPathId, String label, String html) {
        return ("<div id='cpath_" + cPathId + "_" + label + "' class='details'>"
                + html + "</div>");
    }

    private String getStartRow(int i) {
        if (i % 2 == 0) {
            return ("<tr valign=top>");
        } else {
            return ("<tr valign=top bgcolor=#EEEEEE>");
        }
    }

    private String getBioPaxRecordHtml(BioPaxRecordSummary bpSummary,
            HashMap<String, Reference> referenceMap, int index, DetailsTracker detailsTracker)
            throws DaoException {
        StringBuffer buf = new StringBuffer();
        if (bpSummary.getCPathRecord() != null
                && bpSummary.getCPathRecord().getType() == CPathRecordType.PHYSICAL_ENTITY) {
            String entityLink = BioPaxRecordSummaryUtils.createEntityLink(bpSummary, 50);
            buf.append(entityLink);
        } else {
            buf.append("<a href='record2.do?id=" + bpSummary.getRecordID() + "'>"
                    + bpSummary.getLabel() + "</a>");
        }
        buf.append(getBioPaxDetailsHtml(bpSummary, referenceMap, index, detailsTracker, null, null, 0));
        return buf.toString();
    }

    private String getBioPaxDetailsHtml(BioPaxRecordSummary bpSummary,
            HashMap<String, Reference> referenceMap, int index,
            DetailsTracker detailsTracker, InteractionSummary interactionSummary,
            List<String> interactionSummaryStringList, int NUM_PREVIEW_INTERACTION_PARTICIPANTS)
            throws DaoException {
        ReferenceUtil refUtil = new ReferenceUtil();
        ArrayList masterList = refUtil.categorize(bpSummary);
        ArrayList<ExternalLinkRecord> referenceLinks =
                (ArrayList<ExternalLinkRecord>) masterList.get(0);

        StringBuffer buf = new StringBuffer();
        boolean hasDetails = hasDetails(bpSummary, referenceLinks, interactionSummary,
                interactionSummaryStringList, NUM_PREVIEW_INTERACTION_PARTICIPANTS);
        buf.append("<td>");
        if (bpSummary.getCPathRecord() != null) {
            CPathRecord record = bpSummary.getCPathRecord();
            if (record.getSnapshotId() > 0) {
                buf.append("<div class='data_source'> " + getDataSourceString(bpSummary, record) + "</div>");
            }
        }
        buf.append("</td>");
        if (hasDetails) {
            buf.append(getInspectorButtonHtml(bpSummary.getRecordID()));
            detailsTracker.incrementNumRecordsWithDetails();
        } else {
            buf.append("<td></td>");
        }
        buf.append("</tr>");
        buf.append(getStartRow(index));
        buf.append("<td colspan=3>");
        if (interactionSummaryStringList != null &&
                interactionSummary.getSpecificType().equals("physicalInteraction") &&
                (interactionSummaryStringList.size() - NUM_PREVIEW_INTERACTION_PARTICIPANTS > 0)) {
            String interactionSummaryListString = "";
            for (int lc = NUM_PREVIEW_INTERACTION_PARTICIPANTS; lc < interactionSummaryStringList.size(); lc++)
            {
                interactionSummaryListString += interactionSummaryStringList.get(lc);
            }
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "remaining_participants",
                    "<p><b>Additional Participants:</b></p>\n\r" + interactionSummaryListString));
        } else {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "remaining_participants", ""));
        }
        if (fieldExists(bpSummary.getComments())) {
            String comments[] = bpSummary.getComments();
            StringBuffer commentHtml = new StringBuffer();
            for (int i = 0; i < comments.length; i++) {
                commentHtml.append("<p>" + ReactomeCommentUtil.massageComment(comments[i])
                        + "</p>");
            }
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "comment",
                    commentHtml.toString()));
        } else {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "comment", "---"));
        }
        String organism = bpSummary.getOrganism();
        if (fieldExists(organism)) {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "organism",
                    "<p><b>Organism:</b>&nbsp;" + organism));
        } else {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "organism", "---"));
        }
        if (referenceLinks.size() > 0) {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "refs",
                    refUtil.getReferenceHtml(referenceLinks, referenceMap)));
        } else {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "refs", "---"));
        }
        if (interactionSummary != null) {
            List<Evidence> evidenceList = interactionSummary.getEvidence();
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "evidence",
                    EvidenceUtil.getEvidenceHtml(evidenceList)));
        } else {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "evidence", "---"));
        }
        if (fieldExists(bpSummary.getAvailability())) {
            String availabilityString = "<p><b>Availability:</b></p>\n" +
                    "<p>" + bpSummary.getAvailability() + "</p>\n<br>";
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "availability", availabilityString));
        } else {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "availability", "---"));
        }
        buf.append("</td></tr>");
        return buf.toString();
    }

    private String getDataSourceString(BioPaxRecordSummary bpSummary, CPathRecord record) throws
            DaoException {

        String toReturn = "";
        ExternalDatabaseSnapshotRecord snapshot = bpSummary.getExternalDatabaseSnapshotRecord();
        ExternalDatabaseRecord summaryDBRecord = snapshot.getExternalDatabase();

        ArrayList<ExternalLinkRecord> externalLinks = DaoExternalLink.getInstance().getRecordsByCPathId(record.getId());
        for (ExternalLinkRecord externalLink : externalLinks) {
            ExternalDatabaseRecord dbRecord = externalLink.getExternalDatabase();
            if (dbRecord.getId() == summaryDBRecord.getId()) {
                String uri = externalLink.getWebLink();
                toReturn = (uri != null && uri.length() > 0) ?
                        "<a href=\"" + uri + "\">" + dbRecord.getName() + "</a>" :
                        DbSnapshotInfo.getDbSnapshotHtmlAbbrev(record.getSnapshotId());
                break;
            }
        }

        // outta here
        return (toReturn.length() == 0) ?
                DbSnapshotInfo.getDbSnapshotHtmlAbbrev(record.getSnapshotId()) : toReturn;
    }
%>
</table>
</div>
<jsp:include page="../global/redesign/xdebug.jsp" flush="true" />

<%! class DetailsTracker {
    private int numRecordsWithDetails = 0;
    public void incrementNumRecordsWithDetails() {
        numRecordsWithDetails++;
    }
    public boolean detailsExist() {
        if (numRecordsWithDetails > 0) {
            return true;
        } else {
            return false;
        }
    }
}
%>