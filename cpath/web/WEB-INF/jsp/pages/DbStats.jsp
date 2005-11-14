<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
    String title = "cPath::Database Stats and Information";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title); %>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="content">
<jsp:include page="../global/dbStats.jsp" flush="true" />
<cbio:importTable />
</div>

<jsp:include page="../global/footer.jsp" flush="true" />