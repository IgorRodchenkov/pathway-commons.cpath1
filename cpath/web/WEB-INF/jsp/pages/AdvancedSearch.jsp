<%@ page import="org.mskcc.pathdb.controller.ProtocolException,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
request.setAttribute("advancedSearch", "true");
%>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath Advanced Search"); %>

<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<jsp:include page="../global/advanced_search.jsp" flush="true" />
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />