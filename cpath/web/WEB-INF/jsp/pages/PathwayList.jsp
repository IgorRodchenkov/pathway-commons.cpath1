<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.model.CPathRecord,
                 java.util.ArrayList"%>
<%
    String title = "cPath:: Browse by Pathway";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
    ArrayList records = (ArrayList) request.getAttribute("RECORDS");
%>

<jsp:include page="../global/header.jsp" flush="true" />

<div id="apphead">
    <h2>Browse By Pathway</h2>
</div>

<div id='axial' class='h3'>
<h3>Pathway Information</h3>
</div>

<table border='0' cellspacing='2' cellpadding='3' width='100%'>
<TR>
<TH>Pathway</TH>
</TR>
<%
    for (int i=0; i<records.size(); i++) {
        CPathRecord rec = (CPathRecord) records.get(i);
        if (i % 2 == 0) {
            out.println("<tr class='a'>");
        } else {
            out.println("<tr class='b'>");
        }
        String uri = "bb_web.do?id=" + rec.getId();
        out.println("<TD><A HREF=\"" + uri + "\">"
                + rec.getName() + "</A>");
        out.println("</TD></TR>");
    }
%>
</TABLE>

<jsp:include page="../global/footer.jsp" flush="true" />