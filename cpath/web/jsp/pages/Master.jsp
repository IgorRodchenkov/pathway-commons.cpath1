<%@ taglib uri="/WEB-INF/cbio-taglib.tld" prefix="cbio" %>
<%@ page import="java.util.ArrayList,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolException"%>

<jsp:include page="../global/header.jsp" flush="true" />
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute("protocol_request");
    ArrayList interactions = (ArrayList) request.getAttribute("interactions");
    ProtocolException exception = (ProtocolException)
            request.getAttribute("exception");
%>

<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />

<% if (exception != null) { %>
    <jsp:include page="../global/error.jsp" flush="true" />
<% } else if (interactions != null) { %>
    <cbio:interactionTable interactions="<%= interactions %>"
        uid="<%= protocolRequest.getUid() %>"/>
<% } else { %>
    <jsp:include page="../global/help.jsp" flush="true" />
<% } %>

<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
