<%@ page import="org.mskcc.pathdb.controller.ProtocolRequest,
                 java.util.ArrayList,
                 org.mskcc.pathdb.controller.ProtocolConstants,
                 org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath Search Results"); %>
<jsp:include page="../global/header.jsp" flush="true" />
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute("protocol_request");
    ArrayList interactions = (ArrayList) request.getAttribute("interactions");
%>
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<P>
<cbio:interactionTable interactions="<%= interactions %>"
        protocolRequest="<%= protocolRequest %>"/>
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
