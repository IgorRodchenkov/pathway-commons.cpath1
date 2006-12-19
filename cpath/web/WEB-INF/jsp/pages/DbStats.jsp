<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Database Stats and Information"); %>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="content">
<jsp:include page="../global/dbStats.jsp" flush="true" />
<cbio:importTable />
</div>

<jsp:include page="../global/footer.jsp" flush="true" />