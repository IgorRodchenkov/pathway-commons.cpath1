<%@ page import="org.mskcc.pathdb.controller.ProtocolException"%>
<%@ page isErrorPage = "true" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<jsp:include page="../global/header.jsp" flush="true" />

<% if (exception != null)  { %>
    <cbio:errorMessage throwable="<%= exception %>"/>
<% } else {
    Throwable throwable = (Throwable) request.getAttribute("exception");
%>
    <cbio:errorMessage throwable="<%= throwable %>"/>
<% }%>

<jsp:include page="../global/footer.jsp" flush="true" />
