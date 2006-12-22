<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page isErrorPage = "true" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<jsp:include page="../global/redesign/header.jsp" flush="true" />

<% if (exception != null)  { %>
    <cbio:errorMessage throwable="<%= exception %>"/>
<% } else {
    Throwable throwable = (Throwable) request.getAttribute
            (BaseAction.ATTRIBUTE_EXCEPTION);
%>
    <cbio:errorMessage throwable="<%= throwable %>"/>
<% }%>
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<jsp:include page="../global/redesign/footer.jsp" flush="true" />