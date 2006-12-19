<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Diagnostics"); %>

<jsp:include page="../global/header.jsp" flush="true" />

<cbio:diagnosticsTable />

<jsp:include page="../global/footer.jsp" flush="true" />
