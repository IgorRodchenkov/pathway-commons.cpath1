<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
    request.setAttribute(BaseAction.PAGE_IS_ADMIN, "true");
%>

<jsp:include page="../global/header.jsp" flush="true" />

<cbio:diagnosticsTable />

<jsp:include page="../global/footer.jsp" flush="true" />
