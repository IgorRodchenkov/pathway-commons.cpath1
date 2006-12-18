<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.action.BioPaxParentChild"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.*"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoException"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.mskcc.pathdb.model.CPathRecord"%>
<%@ page import="org.mskcc.pathdb.taglib.ReactomeCommentUtil"%>
<%@ page import="org.mskcc.pathdb.model.CPathRecordType"%>
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

int index = start;
for (int i = 0; i < bpSummaryList.size(); i++) {
    out.println(getStartRow(i));
    BioPaxRecordSummary bpSummary = bpSummaryList.get(i);
    out.println("<td>");
    out.println(index + ". ");
    if (interactionSummaryMap.containsKey(bpSummary.getRecordID())) {
        EntitySummary entitySummary = interactionSummaryMap.get(bpSummary.getRecordID());
        if (entitySummary instanceof InteractionSummary) {
            InteractionSummary interactionSummary = (InteractionSummary) entitySummary;
            String interactionString = InteractionSummaryUtils.createInteractionSummaryString
                (interactionSummary);
            if (interactionString != null) {
                out.println("<span class='entity_summary'>" + interactionString + "</span>");
            }
            out.println(getBioPaxDetailsHtml(bpSummary));
        }
    } else {
        out.println(getBioPaxRecordHtml(bpSummary));
    }
    out.println("</tr>");
    index++;
}
%>

<%!
private String getInspectorButtonHtml (long cPathId) {
    return "<td align=right><a style='cursor:pointer' "
        + "title='Toggle Record Details' onClick=\"toggleDetails('cpath_" + cPathId
        + "')\"><img src='jsp/images/inspect_small.png'></a></td>";
}

private String getDetailsHtml (long cPathId, String label, String html) {
    return ("<div id='cpath_" + cPathId + "_" + label +"' class='details'>"
        + html + "</div>");
}

private String getStartRow (int i) {
    if (i%2 ==0) {
        return ("<tr valign=top>");
    } else {
        return("<tr valign=top bgcolor=#EEEEEE>");
    }
}

private String getBioPaxRecordHtml(BioPaxRecordSummary bpSummary) throws DaoException {
    StringBuffer buf = new StringBuffer();
    if (bpSummary.getCPathRecord().getType() == CPathRecordType.PHYSICAL_ENTITY) {
        String entityLink = BioPaxRecordSummaryUtils.createEntityLink(bpSummary, 200);
        buf.append(entityLink);
    } else {
        buf.append ("<a href='record2.do?id=" + bpSummary.getRecordID() + "'>"
            + bpSummary.getName() + "</a>");
    }
    buf.append(getBioPaxDetailsHtml (bpSummary));
    return buf.toString();
}

private String getBioPaxDetailsHtml (BioPaxRecordSummary bpSummary) throws DaoException {
    StringBuffer buf = new StringBuffer();
    boolean hasDetails = false;
    if (bpSummary.getComment() != null) {
        String comment = ReactomeCommentUtil.massageComment(bpSummary.getComment());
        buf.append(getDetailsHtml(bpSummary.getRecordID(), "comment", "<P><B>Summary:</B>&nbsp;" + comment));
        hasDetails = true;
    } else {
        buf.append(getDetailsHtml(bpSummary.getRecordID(), "comment", ""));
    }
    String organism = bpSummary.getOrganism();
    if (organism != null) {
        buf.append(getDetailsHtml (bpSummary.getRecordID(), "organism", "<P><B>Organism:</B>&nbsp;" + organism));
        hasDetails = true;
    } else {
        buf.append(getDetailsHtml(bpSummary.getRecordID(), "organism", ""));
    }
    if (bpSummary.getCPathRecord() != null) {
        CPathRecord record = bpSummary.getCPathRecord();
        if (record.getSnapshotId() > 0) {
            buf.append(getDetailsHtml (bpSummary.getRecordID(), "source",
                "<P><B>Data Source:</B>&nbsp;" + DbSnapshotInfo.getDbSnapshotHtml (record.getSnapshotId())));
            hasDetails = true;
        } else {
            buf.append(getDetailsHtml(bpSummary.getRecordID(), "source", ""));
        }
    }
    buf.append("</td>");
    if (hasDetails) {
        buf.append(getInspectorButtonHtml(bpSummary.getRecordID()));
    }
    return buf.toString();
}
%>
</table>
</div>
<jsp:include page="../global/xdebug.jsp" flush="true" />