<%@ page import="org.mskcc.pathdb.controller.ProtocolException,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute("protocol_request");
%>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath Search Results"); %>

<jsp:include page="../global/header.jsp" flush="true" />

<cbio:searchResultsTable protocolRequest="<%= protocolRequest %>"/>

<jsp:include page="../global/footer.jsp" flush="true" />
