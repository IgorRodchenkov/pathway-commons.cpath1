<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="java.text.Format"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoImport"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.ImportRecord"%>
<%@ page import="org.mskcc.pathdb.model.CPathRecord"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// get bean
    ExternalDatabaseSnapshotRecord snapshotRecord = (ExternalDatabaseSnapshotRecord)
            request.getAttribute("SNAPSHOT_RECORD");
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, snapshotRecord.getExternalDatabase().getName());
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<h1>Data Source Details</h1>

<table>
    <tr>
        <td><b>Name:</b></td>
        <td><%= snapshotRecord.getExternalDatabase().getName() %></td>
    </tr>

    <tr>
        <td><b>Description:</b></td>
        <td><%= snapshotRecord.getExternalDatabase().getDescription() %></td>
    </tr>

    <% if (snapshotRecord.getExternalDatabase().getHomePageUrl() != null) { %>
    <tr>
        <td><b>Web Site:</b></td>
        <td><a href="<%= snapshotRecord.getExternalDatabase().getHomePageUrl()%>">
            <%= snapshotRecord.getExternalDatabase().getHomePageUrl()%></A>
        </td>
    </tr>
    <% } %>

    <% if (snapshotRecord.getSnapshotVersion() != null
            && ! snapshotRecord.getSnapshotVersion().equals(CPathRecord.NA_STRING)) { %>
    <tr>
        <td><b>Release Version:</b></td>
        <td><%= snapshotRecord.getSnapshotVersion()%></td>
    </tr>
    <% } %>

    <tr>
        <td><b>Release Date:</b></td>
        <%
            Format formatter = new SimpleDateFormat("dd-MMM-yy");
            String date = formatter.format(snapshotRecord.getSnapshotDate());
        %>
        <td><%= date %></td>
    </tr>

</table>

<%
    DaoImport daoImport = new DaoImport();
    ArrayList list = daoImport.getImportRecordsBySnapshotId(snapshotRecord.getId());
%>
<p>&nbsp;</p>
<h1>Original Data Files from <%= snapshotRecord.getExternalDatabase().getName()%></h1>
<table>
    <tr>
        <th>File Name</th>
        <th>Record Type</th>
        <th>Date imported</th>
        <th>Download</th>
    </tr>
    <% for (int i=0; i<list.size(); i++) {
        out.println("<tr>");
        ImportRecord importRecord = (ImportRecord) list.get(i);
        out.println("<td>");
        out.println(importRecord.getDescription());
        out.println("</td>");
        out.println("<td>");
        out.println(importRecord.getXmlType());
        out.println("</td>");
        out.println("<td>");
        date = formatter.format(importRecord.getCreateTime());
        out.println(date);
        out.println("</td>");
        out.println("<td align='right'>");
        out.println("<form action='downloadSource.do'>");
        out.println("<input type='hidden' name='source_id' value='"
                + importRecord.getImportId() + "'>");
        out.println("<input type='submit' value='Download BioPAX'>");
        out.println("</form>");
        out.println("</td>");
        out.println("</tr>");
    }
    %>
</table>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />