<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.EntitySummary"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils"%>
<%@ page import="org.mskcc.pathdb.action.BioPaxParentChild"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<style type="text/css">
    .button {
        color:blue;
        text-decoration:underline;
        cursor:pointer;
    }
</style>

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
<table width=100% CELLSPACING=0 CELLPADDING=4>
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
        %>
        <span style="display:<%= nextStyle %>" class="button" id="next_<%= type %>" onClick="getNextData('<%= type %>')">Next &gt;</span>
        </td>
    </tr>
</table>
<% } %>

<div id="content_<%= type%>">
<table width="100%">
<%
ArrayList summaryList = (ArrayList) request.getAttribute("SUMMARY_LIST");

int index = start;
for (int i = 0; i < summaryList.size(); i++) {
    if (i%2 ==0) {
        out.println("<tr>");
    } else {
        out.println("<tr bgcolor=#EEEEEE>");
    }
    EntitySummary entitySummary = (EntitySummary) summaryList.get(i);
    if (entitySummary != null) {
        String uri = "record.do?id=" + entitySummary.getRecordID();
        out.println("<td width='20%'>" + index + ". <a href=\""
                + uri + "\">View Details</a></td>");
    }

    if (entitySummary instanceof InteractionSummary) {
        InteractionSummary interactionSummary =
                (InteractionSummary) entitySummary;
        String interactionString =
                InteractionSummaryUtils.createInteractionSummaryString
                        (interactionSummary);
        if (interactionString != null) {
            out.println("<td>"+ interactionString + "</td>");
        }
    } else {
        out.println("<td>");
        if (entitySummary != null) {
            out.println(entitySummary.getName() + "</td>");
        }
    }
    out.println("</tr>");
    index++;
}
%>
</table>
</div>
<jsp:include page="../global/xdebug.jsp" flush="true" />