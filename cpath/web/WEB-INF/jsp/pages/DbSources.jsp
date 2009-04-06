<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Database Sources"); %>
<% boolean renderForHomepage = false; %>
<jsp:include page="../global/redesign/header.jsp" flush="true" />
<cbio:dataSourceListTable renderForHomepage="<%= renderForHomepage %>"/>
<p>&nbsp;</p>
<p>&nbsp;</p>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />