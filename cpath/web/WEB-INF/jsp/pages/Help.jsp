<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
 <%-- Displays Query / Web Service Help Page --%>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath Help"); %>
<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<jsp:include page="../global/help.jsp" flush="true" />
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />