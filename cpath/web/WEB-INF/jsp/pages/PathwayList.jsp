<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.model.CPathRecord,
                 java.util.ArrayList,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 java.util.HashMap,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%
    String title = "cPath:: Browse Pathways";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>

<jsp:include page="../global/header.jsp" flush="true" />

<div id="content">
<h1>Browse Pathways</h1>
<jsp:include page="./PathwayListTable.jsp" flush="true" />
</div>
<jsp:include page="../global/footer.jsp" flush="true" />