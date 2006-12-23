<%@ page isErrorPage = "true" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<% if (exception != null)  { %>
    <cbio:errorMessage throwable="<%= exception %>"/>
<% } else {
    Throwable throwable = (Throwable) request.getAttribute("exception");
%>
    <cbio:errorMessage throwable="<%= throwable %>"/>
<% }%>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />
