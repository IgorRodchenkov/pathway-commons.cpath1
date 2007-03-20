<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction,
                 java.util.ArrayList"%>
<%
    ProtocolRequest protocolRequest1 = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    ArrayList interactorList1 = (ArrayList)
            request.getAttribute(BaseAction.ATTRIBUTE_INTERACTOR_SET);
%>

<div class="splitcontentleft">
        <% if (protocolRequest1 != null && interactorList1 != null) { %>
            <cbio:interactorTable interactorList="<%= interactorList1 %>"
                   protocolRequest="<%= protocolRequest1 %>"/>
        <% } %>
        <jsp:include page="psi-mi/browse.jsp" flush="true" />
        <jsp:include page="psi-mi/community.jsp" flush="true" />
        <jsp:include page="psi-mi/docs.jsp" flush="true" />
        <jsp:include page="psi-mi/howto.jsp" flush="true" />
</div>
