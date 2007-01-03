<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// title
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Home");

	// referer string used in tabs
	request.setAttribute(BaseAction.REFERER, BaseAction.FORWARD_HOME);
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<% String homePageFile = CPathUIConfig.getPath("home.jsp"); %>
<jsp:include page="<%=homePageFile %>" flush="true"/>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
