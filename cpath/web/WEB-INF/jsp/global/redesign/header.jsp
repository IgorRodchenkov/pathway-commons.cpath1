<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%
    String initFile = "../" + CPathUIConfig.getPath("init.jsp");
    String headerFile = "../" + CPathUIConfig.getPath("header.jsp");
%>

<jsp:include page="<%=initFile%>" flush="true"/>

<%
    // get WebUIBean
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();

	// title
    String title = (String) request.getAttribute(BaseAction.ATTRIBUTE_TITLE);
    if (title == null) {
        title = webUIBean.getApplicationName();
    } else {
        title = webUIBean.getApplicationName() + "::" + title;
    }

	// setup some other configurable UI elements
    String isAdminPage = (String) request.getAttribute(BaseAction.PAGE_IS_ADMIN);
%>
<head>
    <title><%= title %></title>
    <jsp:include page="../../global/stylesAndScripts.jsp" flush="true" />
    <%--  Set Auto-Update for Admin Page --%>
    <%
        String autoUpdate = (String) request.getAttribute
            (BaseAction.PAGE_AUTO_UPDATE);
        if (autoUpdate != null) { %>
        <meta http-equiv="refresh" content="10;url=adminHome.do">
        <% }
    %>

    <%-- Explicitly Set Character Encoding
    Helps prevent against Cross-site scripting attacks:
    See http://www.cert.org/tech_tips/malicious_code_mitigation.html.
    --%>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
</head>

<body>
<!-- For OverLib PopUp Boxes -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
String url = "";
String baseAction = (String) request.getAttribute
        (BaseAction.ATTRIBUTE_SERVLET_NAME);
if (baseAction != null) {
    StringBuffer uri = new StringBuffer (
        (String) request.getAttribute
        (BaseAction.ATTRIBUTE_SERVLET_NAME));
    uri = new StringBuffer(uri.substring(1));
    url = uri.toString();
}
%>

<div id="container" >

	<div id="header">
        <div id="header_content">
        <jsp:include page="<%=headerFile%>" flush="true"/>
        </div>
        <jsp:include page="searchBox.jsp" flush="true" />
    </div>

<!-- Navigation Tabs -->
<jsp:include page="tabs.jsp" flush="true" />

<div id="content">

<jsp:include page="../../global/userMessage.jsp" flush="true" />    