<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%
    String headerFile = "../" + CPathUIConfig.getPath("header.jsp");
%>

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
    <jsp:include page="stylesAndScripts.jsp" flush="true" />
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

    <style type="text/css" xml:space="preserve">
        #demo .loading {
        background-image:url("jsp/images/loading.gif");
        background-position:center center;
        background-repeat:no-repeat;
        min-height:200px;
        }
        #demo .yui-content {
        padding:1em;
        min-height:200px;
        }
        #demo .yui-nav li {
        margin-top:0px;
        margin-bottom:5px;
        background-color:#F3F3F3;
        }
        #demo .yui-nav .selected {
            font-weight:bold;
        }
        #demo .yui-nav .selected a{

        }
        #demo .yui-nav {
        background-color:#FFFFFF;
        padding-top:4px;
        padding-left:4px;
        border:1px solid #467aa7;
        margin-right:0px;
        }
        #demo .yui-nav li a {
          border:1px solid #467aa7;
        }
        #demo .yui-content {
           border:1px solid #467aa7;
        }
    </style>
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

<jsp:include page="userMessage.jsp" flush="true" />

<% 
int webMode = CPathUIConfig.getWebMode();

if (webMode == CPathUIConfig.WEB_MODE_PSI_MI) { %>
    <jsp:include page="psi-mi/psiMiLeftColumn.jsp" flush="true" />
    <div class="splitcontentright">
<% } %>