<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "404 Not Found"); %>

<jsp:include page="../global/header.jsp" flush="true" />

<%
    String request_uri = (String) request.getAttribute
            ("javax.servlet.error.request_uri");
%>

<div class="errormessage">
<p><strong>404:  Not Found</strong></p>
The requested URL:  <B><%= request_uri %></B> was
    not found on this server.
</div>

<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<jsp:include page="../global/footer.jsp" flush="true" />