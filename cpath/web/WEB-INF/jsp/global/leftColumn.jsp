<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.sql.assembly.XmlAssembly,
                 org.mskcc.pathdb.action.BaseAction,
                 java.util.ArrayList"%>
<%
    ProtocolRequest protocolRequest1 = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    ArrayList interactorList1 = (ArrayList)
            request.getAttribute(BaseAction.ATTRIBUTE_INTERACTOR_SET);
    String isAdminPage = (String) request.getAttribute(BaseAction.PAGE_IS_ADMIN);
%>

<!-- Start Left Column -->
<td id="leftcol" width="200">
    <div id="navcolumn">

        <jsp:include page="../global/getInteractionsBox.jsp" flush="true" />

        <% if (protocolRequest1 != null && interactorList1 != null) { %>
            <cbio:interactorTable interactorList="<%= interactorList1 %>"
                   protocolRequest="<%= protocolRequest1 %>"/>
        <% } %>

        <% if (isAdminPage != null) { %>
        <jsp:include page="../global/adminTasks.jsp" flush="true" />
        <% } else { %>
        <jsp:include page="../global/browse.jsp" flush="true" />

        <% if (protocolRequest1 == null) { %>
        <jsp:include page="../global/docs.jsp" flush="true" />
        <% } %>
        <jsp:include page="../global/howto.jsp" flush="true" />
        <% } %>
    </div>
</td>
<!-- End NavColumn -->
