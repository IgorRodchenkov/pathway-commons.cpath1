<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Administration::View Logs"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<jsp:include page="../global/redesign/adminLeft.jsp" flush="true" />
<div class="splitcontentright">
<cbio:logTable />
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
