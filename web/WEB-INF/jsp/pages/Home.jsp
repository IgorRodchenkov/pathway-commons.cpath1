<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// title
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Home");

	// referer string used in tabs
	request.setAttribute(BaseAction.REFERER, BaseAction.FORWARD_HOME);
    // get WebUIBean
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<% String homePageFile = CPathUIConfig.getPath("home.jsp"); %>

<% if (CPathUIConfig.isOnline()) { %>
    <jsp:include page="<%=homePageFile %>" flush="true"/>
<% } else { %>
    <div class="user_message">
    <%= webUIBean.getApplicationName() %> is currently down for a scheduled upgrade.
    Please check back in approximately 30 minutes.
    </div>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
<% } %>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />