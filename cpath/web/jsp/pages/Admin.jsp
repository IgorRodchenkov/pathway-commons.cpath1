<%@ page import="org.mskcc.pathdb.sql.DatabaseImport,
                 org.mskcc.pathdb.model.ImportRecord"%>
<%
    String importID = request.getParameter("import_id");
    if (importID != null) {
        DatabaseImport dbImport = new DatabaseImport ();
        int id = Integer.parseInt(importID);
        ImportRecord record = dbImport.getImportRecordById(id);
        response.setContentType("text/plain");
        out.println(record.getData());
    } else { %>
    <%@ taglib uri="/WEB-INF/cbio-taglib.tld" prefix="cbio" %>
    <jsp:include page="../global/header.jsp" flush="true" />
    <cbio:importTable />
    <jsp:include page="../global/xdebug.jsp" flush="true" />
    <jsp:include page="../global/footer.jsp" flush="true" />
<% } %>