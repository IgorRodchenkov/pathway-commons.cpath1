<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Diagnostics"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<jsp:include page="../global/redesign/adminLeft.jsp" flush="true" />
<div class="splitcontentright">
<cbio:diagnosticsTable />
</div>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />
