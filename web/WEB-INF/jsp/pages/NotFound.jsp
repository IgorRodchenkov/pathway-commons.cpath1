<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "404 Not Found"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<%
    String request_uri = (String) request.getAttribute
            ("javax.servlet.error.request_uri");
%>

<div>
<h1>404:  Not Found</h1>
<p>The requested URL:  <b><%= request_uri %></b> was not found on this server.</p>
</div>

<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<jsp:include page="../global/redesign/footer.jsp" flush="true" />