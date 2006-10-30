<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="java.text.Format"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoImport"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.ImportRecord"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// get bean
    String snapshotIdStr = request.getParameter("snapshot_id");
    long snapshotId = -1;
    try {
        snapshotId = Long.parseLong(snapshotIdStr);
    } catch (NumberFormatException e) {
    }
    ExternalDatabaseSnapshotRecord snapshotRecord = null;
    if (snapshotId >= 0) {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        snapshotRecord = dao.getDatabaseSnapshot(snapshotId);
        WebUIBean webUIBean = CPathUIConfig.getWebUIBean();

        // title
        String title = webUIBean.getApplicationName() + "::"
                + snapshotRecord.getExternalDatabase().getName() + "Data Source Details";
        request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
    }

%>

<jsp:include page="../global/header.jsp" flush="true" />

<% if (snapshotId >= 0) { %>

<div id="content">
<h1>Data Source Details</h1>
</div>

<TABLE CELLSPACING=5 CELLPADDING=0>
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

    <% if (snapshotRecord.getSnapshotVersion() != null) { %>
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
    ArrayList list = daoImport.getImportRecordsBySnapshotId(snapshotId);
%>


<div id="content">
<h1>Original Data Files from <%= snapshotRecord.getExternalDatabase().getName()%></h1>
</div>
<TABLE CELLSPACING=5 CELLPADDING=5>
    <tr BGCOLOR=#CCCCCC>
        <td>File Name</td>
        <td>Record Type</td>
        <td>Date imported</td>
        <td>Download</td>
    </tr>
    <% for (int i=0; i<list.size(); i++) {
        out.println("<TR>");
        ImportRecord importRecord = (ImportRecord) list.get(i);
        out.println("<TD>");
        out.println(importRecord.getDescription());
        out.println("</TD>");
        out.println("<TD>");
        out.println(importRecord.getXmlType());
        out.println("</TD>");
        out.println("<TD>");
        date = formatter.format(importRecord.getCreateTime());
        out.println(date);
        out.println("</TD>");
        out.println("<TD>");
        out.println("<FORM ACTION=downloadSource.do>");
        out.println("<INPUT TYPE=HIDDEN NAME='source_id' VALUE='"
                + importRecord.getImportId() + "'>");
        out.println("<INPUT TYPE=SUBMIT VALUE='Download "
            + importRecord.getDescription() + ".gz file'>");
        out.println("</FORM>");
        out.println("</TD>");
        out.println("</TR>");
    }
    %>
</table>
<% } %>

<jsp:include page="../global/footer.jsp" flush="true" />