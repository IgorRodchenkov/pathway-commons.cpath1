<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.action.BioPaxParentChild"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.*"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
int start = 0;
String startIndex = request.getParameter("startIndex");
if (startIndex != null) {
    start = Integer.parseInt(startIndex);
}
start++;

String id = request.getParameter("id");
if (id == null) {
    out.println("param id is null");
}
String command = request.getParameter("command");
if (command == null) {
    out.println("param command is null");
}
String type = request.getParameter("type");
if (type == null) {
    out.println("param type is null");
}
String showHeader = request.getParameter("showHeader");
boolean headerFlag = true;
if (showHeader != null && showHeader.equals("false")) {
    headerFlag = false;
}

int max = BioPaxParentChild.MAX_RECORDS;

int total = 9999;
String totalNumRecords = request.getParameter("totalNumRecords");
if (totalNumRecords != null) {
    total = Integer.parseInt(totalNumRecords);
}

int stop = start + max -1;

if (stop > total) {
    stop = total;
}

boolean showPathwayRoots = false;
if (type != null && type.equals(BioPaxParentChild.GET_PATHWAY_ROOTS)) {
    showPathwayRoots = true;
}
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
<table width=100%>
<%
ArrayList summaryList = (ArrayList) request.getAttribute("SUMMARY_LIST");
ArrayList bpSummaryList = (ArrayList) request.getAttribute("BP_SUMMARY_LIST");
if (summaryList != null && summaryList.size() > 0) {
    Object object = summaryList.get(0);
    int index = start;

    if (object != null && object instanceof EntitySummary) {

        for (int i = 0; i < summaryList.size(); i++) {
            if (i%2 ==0) {
                out.println("<tr>");
            } else {
                out.println("<tr bgcolor=#EEEEEE>");
            }
            EntitySummary entitySummary = (EntitySummary) summaryList.get(i);
            BioPaxRecordSummary bpSummary = (BioPaxRecordSummary) bpSummaryList.get(i);
            String uri = "record2.do?id=" + entitySummary.getRecordID();
            if (entitySummary instanceof InteractionSummary) {
                out.println("<td width='20%'>" + index + ". <a href=\""
                            + uri + "\">View Details</a></td>");
                InteractionSummary interactionSummary =
                        (InteractionSummary) entitySummary;
                String interactionString =
                        InteractionSummaryUtils.createInteractionSummaryString
                                (interactionSummary);
                if (interactionString != null) {
                    out.println("<td><div class='entity_summary'>"+ interactionString + "</div>");
                    if (bpSummary.getComment() != null) {
                        out.println("<P><div class='data_source'>"
                                + bpSummary.getComment() + "</div>");
                    }
                    out.println("</td>");
                }
            } else {
                out.println("<td>");
                if (entitySummary != null) {
                    out.println(index + ". <a href=\"" + uri + "\">");
                    out.println(entitySummary.getName());
                    out.println("</a>");
                }
                if (bpSummary.getComment() != null) {
                    out.println("<P><div class='data_source'>"
                            + bpSummary.getComment() + "</div>");
                }
                out.println("</td>");
            }
            out.println("<td><div class='data_source'>");
            out.println(DbSnapshotInfo.getDbSnapshotHtml(entitySummary.getSnapshotId()));
            out.println("</div>");
            out.println("</td>");
            out.println("</tr>");
            index++;
        }
    } else if (object instanceof BioPaxRecordSummary) {
        if (showPathwayRoots) {
            for (int i = 0; i < summaryList.size(); i++) {
                if (i%2 ==0) {
                    out.println("<tr>");
                } else {
                    out.println("<tr bgcolor=#EEEEEE>");
                }
                BioPaxRecordSummary bpSummary = (BioPaxRecordSummary) summaryList.get(i);
                out.println("<td>" + index + ".  ");
                String organism = bpSummary.getOrganism();
                out.println("<A HREF='record2.do?id=" + bpSummary.getRecordID()
                    + "'>" + bpSummary.getName() + "</A>");
                out.println("</td><td>");
                if (organism != null) {
                    out.println(organism);
                }
                out.println("</td>");
                out.println("<td><div class='data_source'>");
                out.println(DbSnapshotInfo.getDbSnapshotHtml
                        (bpSummary.getExternalDatabaseSnapshotRecord().getId()));
                out.println("</div>");
                out.println("</td>");
                out.println("</tr>");
                index++;
            }
        } else {
            for (int i = 0; i < summaryList.size(); i++) {
                if (i%2 ==0) {
                    out.println("<tr>");
                } else {
                    out.println("<tr bgcolor=#EEEEEE>");
                }
                BioPaxRecordSummary bpSummary = (BioPaxRecordSummary) summaryList.get(i);
                out.println("<td>" + index + ".  ");
                String moleculeLink = BioPaxRecordSummaryUtils.createEntityLink(bpSummary, 200);
                out.println(moleculeLink);
                out.println("</td>");
                index++;
            }
        }
    }
}
%>
</table>
</div>
<jsp:include page="../global/xdebug.jsp" flush="true" />