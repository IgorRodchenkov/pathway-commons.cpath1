<%@ page isErrorPage = "true" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<% if (exception != null)  { %>
    <cbio:errorMessage throwable="<%= exception %>"/>
<% } else {
    Throwable throwable = (Throwable) request.getAttribute("exception");
%>
    <cbio:errorMessage throwable="<%= throwable %>"/>
<% }%>
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
