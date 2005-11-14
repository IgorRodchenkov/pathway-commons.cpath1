<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
    String title = "cPath::Browse By Organism";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title); %>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="content">
<h1>Browse by Organism</h1>
<cbio:organismTable />
</div>

<jsp:include page="../global/footer.jsp" flush="true" />