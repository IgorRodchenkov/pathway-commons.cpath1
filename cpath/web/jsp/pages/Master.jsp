<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "Error.jsp" %>

<%@ page import="java.util.ArrayList,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolException,
                 org.mskcc.pathdb.controller.CPathController"%>
<%
    String showHelp = (String) request.getAttribute(CPathController.SHOW_HELP);
%>

<jsp:include page="../global/header.jsp" flush="true" />
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute("protocol_request");
    ArrayList interactions = (ArrayList) request.getAttribute("interactions");
    ArrayList searchResults = (ArrayList) request.getAttribute
            ("textSearchResults");
    ProtocolException exception = (ProtocolException)
            request.getAttribute("exception");
%>
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<% if (exception != null) { %>
    <cbio:errorMessage throwable="<%= exception %>"/>
<%
} else if (interactions != null) { %>
    <cbio:interactionTable interactions="<%= interactions %>"
        uid="<%= protocolRequest.getUid() %>"/>
<% } else if (searchResults != null) { %>
    <cbio:searchResultsTable searchResults="<%= searchResults %>"
        uid="<%= protocolRequest.getUid() %>"/>
<% } else if (showHelp != null) { %>
    <jsp:include page="../global/help.jsp" flush="true" />
<% } else { %>
    <jsp:include page="../global/home.jsp" flush="true" />
<% } %>
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
