<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "Error.jsp" %>

<%@ page import="java.util.ArrayList,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolException,
                 org.mskcc.pathdb.controller.CPathController,
                 org.mskcc.pathdb.controller.ProtocolConstants"%>

<jsp:include page="../global/header.jsp" flush="true" />
<%
    String pageCommand = request.getParameter(CPathController.PAGE_COMMAND);
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute("protocol_request");
    ArrayList interactions = (ArrayList) request.getAttribute("interactions");
    String doFullTextSearch = (String) request.getAttribute("doFullTextSearch");
    ProtocolException exception = (ProtocolException)
            request.getAttribute("exception");
%>
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<P>
<% if (exception != null) { %>
    <cbio:errorMessage throwable="<%= exception %>"/>
<%
} else if (interactions != null) { %>
    <cbio:interactionTable interactions="<%= interactions %>"
        uid="<%= protocolRequest.getQuery() %>"/>
<% } else if (doFullTextSearch != null) { %>
    <cbio:searchResultsTable protocolRequest="<%= protocolRequest %>"/>
<% } else if (protocolRequest != null  &&
        protocolRequest.equals(ProtocolConstants.COMMAND_HELP)) { %>
    <jsp:include page="../global/help.jsp" flush="true" />
<% } else if (pageCommand != null && pageCommand.equals(CPathController.SHOW_ADVANCED_SEARCH)) { %>
    <jsp:include page="../global/advanced_search.jsp" flush="true" />
<% } else { %>
    <jsp:include page="../global/home.jsp" flush="true" />
<% } %>
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
