<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
request.setAttribute("advancedSearch", "true");
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "FAQ");
%>

<jsp:include page="../global/header.jsp" flush="true" />

<% String faqFile = CPathUIConfig.getPath("faq.jsp"); %>
<div id="content">
<jsp:include page="<%=faqFile%>" flush="true"/>
</div>
<jsp:include page="../global/footer.jsp" flush="true" />