<%@ page import="java.net.URL,
                 org.mskcc.pathdb.action.QueryAction,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.util.CPathConstants"%>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%
    String title = (String) request.getAttribute(BaseAction.ATTRIBUTE_TITLE);
    if (title == null) {
        title = "cPath";
    }
    String style = request.getParameter(BaseAction.ATTRIBUTE_STYLE);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><%= title %></title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="jsp/css/style.css" type="text/css"/>
    <link rel="stylesheet" href="jsp/css/tigris.css" type="text/css"/>
    <link rel="stylesheet" href="jsp/css/inst.css" type="text/css"/>
    <% if (style != null && style.equals(BaseAction.ATTRIBUTE_STYLE_PRINT)) { %>
        <link rel="stylesheet" href="jsp/css/print.css" type="text/css"/>
    <% } %>

    <%--  Set Auto-Update for Admin Page --%>
    <%
        String autoUpdate = (String) request.getAttribute
            (BaseAction.PAGE_AUTO_UPDATE);
        if (autoUpdate != null) { %>
        <meta http-equiv="refresh" content="10;url=adminHome.do">
        <% }
    %>

    <%-- Include cPath JavaScript module --%>
    <script src="jsp/javascript/cpath.js" LANGUAGE="JAVASCRIPT"
        TYPE="TEXT/JAVASCRIPT">
    </script>

    <%-- Explicitly Set Character Encoding
    Helps prevent against Cross-site scripting attacks:
    See http://www.cert.org/tech_tips/malicious_code_mitigation.html.
    --%>
    <META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>

<body marginwidth="0" marginheight="0" class="composite">

<div id="banner">
    <table border="0" cellspacing="0" cellpadding="8" width="100%">
        <tr>
            <td>
                <h1>cPath:  Cancer Pathway Database</h1>
                <small>Memorial Sloan-Kettering
                Cancer Center</small>
            </td>
            <td>
                <div align="right" id="login">
                Version:  <%= CPathConstants.VERSION %>

                <jsp:include page="../global/printFriendlyLink.jsp" flush="true" />
                </div>
            </td>
        </tr>
    </table>
</div>

<jsp:include page="../global/tabs.jsp" flush="true" />

<!-- Start Main Table -->
<table border="0" cellspacing="0" cellpadding="4" width="100%" id="main">
    <tr valign="top">

        <jsp:include page="../global/leftColumn.jsp" flush="true" />

        <!-- Start Body Column -->
        <td>
            <!-- Start Div:  bodycol/projecthome -->
            <div id="bodycol">
                <!-- Start Div:  app -->
                <div id="projecthome" class="app">
                    <jsp:include page="../global/userMessage.jsp" flush="true" />