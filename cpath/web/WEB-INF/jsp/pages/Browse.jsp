<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
    String title = "cPath::Browse By Organism";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title); %>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="apphead">
    <h2>Browse By Organism</h2>
</div>
<cbio:organismTable />

<jsp:include page="../global/footer.jsp" flush="true" />