<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.model.CPathRecord,
                 java.util.ArrayList,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 java.util.HashMap,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%
    String title = "cPath:: Browse by Pathway";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
    ArrayList records = (ArrayList) request.getAttribute("RECORDS");

    ProtocolRequest pathwayListRequest = new ProtocolRequest();
    pathwayListRequest.setCommand
            (ProtocolConstants.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST);
    pathwayListRequest.setFormat(ProtocolConstants.FORMAT_BIO_PAX);
    String pathwayListRequestUrl = pathwayListRequest.getUri();
%>

<jsp:include page="../global/header.jsp" flush="true" />

<table border='0' cellspacing='0' cellpadding='0' width='100%'>
    <tr>
        <td width="50%">
            <div id="apphead">
            <h2>Browse By Pathway</h2>
            </div>
        </td>
        <td width="50%" align="right">
            <IMG SRC="jsp/images/xml_doc.gif">&nbsp;
            <A HREF="<%= pathwayListRequestUrl %>">Get Pathway List in BioPAX Format</A>
        </td>
    </tr>
</table>

<div id='axial' class='h3'>
<h3>Pathway Information</h3>
</div>

<table border='0' cellspacing='2' cellpadding='3' width='100%'>
<TR>
<TH>Pathway</TH>
<TH>Organism</TH>
</TR>
<%
    DaoOrganism daoOrganism = new DaoOrganism();
    HashMap organismMap = daoOrganism.getAllOrganismsMap();
    for (int i=0; i<records.size(); i++) {
        CPathRecord rec = (CPathRecord) records.get(i);
        if (i % 2 == 0) {
            out.println("<tr class='a'>");
        } else {
            out.println("<tr class='b'>");
        }
        String uri = "record.do?id=" + rec.getId();
        out.println("<TD><A HREF=\"" + uri + "\">"
                + rec.getName() + "</A>");
        out.println("</TD>");

        Organism organism = (Organism) organismMap.get(Integer.toString
                (rec.getNcbiTaxonomyId()));
        if (organism != null) {
            out.println("<TD>");
            if (organism.getSpeciesName() != null) {
                out.println (organism.getSpeciesName());
            }
            out.println("</TD>");
        }
        out.println("</TR>");
    }
%>
</TABLE>

<jsp:include page="../global/footer.jsp" flush="true" />