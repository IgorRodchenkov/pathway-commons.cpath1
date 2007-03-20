<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
 <%-- Displays Query / Web Service Help Page --%>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Web Service API"); %>
<jsp:include page="../global/redesign/header.jsp" flush="true" />
<div>
<jsp:include page="../global/redesign/help.jsp" flush="true" />
</div>    
<jsp:include page="../global/redesign/footer.jsp" flush="true" />