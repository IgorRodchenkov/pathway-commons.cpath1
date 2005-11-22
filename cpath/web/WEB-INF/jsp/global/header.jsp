<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%
	// get WebUIBean
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();

	// title
    String title = (String) request.getAttribute(BaseAction.ATTRIBUTE_TITLE);
    if (title == null) {
        title = webUIBean.getApplicationName();
    }

	// setup some other configurable UI elements
	String homePageHeader = webUIBean.getHomePageHeader();
    String isAdminPage = (String) request.getAttribute(BaseAction.PAGE_IS_ADMIN);
%>
<head>
    <title><%= title %></title>
    <link rel="stylesheet" href="jsp/css/style.css" type="text/css"/>
    <link rel="stylesheet" href="jsp/css/tigris.css" type="text/css"/>
    <link rel="stylesheet" href="jsp/css/inst.css" type="text/css"/>

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

<body class="composite" onload="document.searchbox.q.focus();">

<!-- Header/Banner -->
<div id="page_header">
<table width="100%">
    <tr>
        <td valign="top" width="60%">
        <div id="page_title">
            <%= homePageHeader %>
        </div>
        </td>

        <td align="right" valign="top">
        <!-- Search Box -->
        <div id="search">
            <jsp:include page="../global/searchBox.jsp" flush="true" />
        </div>
        </td>
    </tr>
</table>
</div>

<!-- Navigation Tabs -->
<jsp:include page="../global/tabs.jsp" flush="true" />

<!-- Start Main Table -->
<table border="0" cellspacing="0" cellpadding="4" width="100%" id="main">
    <tr valign="top">
	    <%
			// only show left hand column on admin page or
			// if mode is psi and we are on psi_mi_search.jsp
			if (isAdminPage != null  ||
	           ((title.indexOf("::Search Results") > -1) &&
			    CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI)){
	    %>
            <jsp:include page="../global/leftColumn.jsp" flush="true" />
		<% } %>
        <!-- Start Body Column -->
        <td valign="top">
            <!-- Start Div:  bodycol/projecthome -->
            <div id="bodycol">
                <!-- Start Div:  app -->
                <div id="projecthome" class="app">
                    <jsp:include page="../global/userMessage.jsp" flush="true" />