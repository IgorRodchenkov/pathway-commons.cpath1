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

<jsp:include page="./PathwayListTable.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />