<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Unauthorized access");
%>
<jsp:include page="../global/redesign/header.jsp" flush="true" />

<div>
<h1>Unauthorized Access</h1>
This page is only available to authorized users.
</div>

<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<jsp:include page="../global/redesign/footer.jsp" flush="true" />