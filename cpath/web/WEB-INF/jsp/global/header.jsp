<%@ page import="java.net.URL,
                 org.mskcc.pathdb.action.QueryAction,
                 org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%
    String pageCommand = (String) request.getAttribute("advancedSearch");
    String autoUpdate = (String) request.getAttribute("autoUpdate");
    String title = (String) request.getAttribute(BaseAction.ATTRIBUTE_TITLE);
    if (title == null) {
        title = "cPath";
    }
%>
<html>
    <head>
        <title><%= title %></title>
        <link rel="stylesheet" href="jsp/css/style.css" type="text/css">

        <STYLE TYPE="TEXT/CSS">
        .hide {
	        display:none;
        }
        </STYLE>

        <% if (autoUpdate != null) { %>
        <meta http-equiv="refresh" content="10;url=adminHome.do">
        <% } %>

        <script src="jsp/javascript/cpath.js" LANGUAGE="JAVASCRIPT"
        TYPE="TEXT/JAVASCRIPT">
        </script>
    </head>
    <% if (pageCommand != null) { %>
        <body bgcolor="#ccccff" marginwidth="0" marginheight="0"
            leftmargin="0" topmargin="0" text="#ffffff" onLoad="updateAdvancedSearchBox()">
    <% } else { %>
        <body bgcolor="#ccccff" marginwidth="0" marginheight="0"
            leftmargin="0" topmargin="0" text="#ffffff">
    <% } %>
    <div id="TitleBar">
    <br><br>
    <table cellpadding="2" cellspacing="0" border="0" width="90%">
        <tr>
            <td colspan=3 valign="Top" align="Right" bgcolor="#9999cc" NOWRAP>
            <big><big>
            <b>
            <font color="#ffffff"><bean:message key="cpath.name"/>:</font>
            <font color="#333366"><bean:message key="cpath.tagline"/></font>
            </b>
            </big></big>
            <br>
        </td>
    </tr>
    <tr bgcolor="#333366">
        <td width="140"></td>
        <td>
            <jsp:include page="userMessage.jsp" flush="true" />
        </td>
        <td valign="Top" align="Right" >
            <jsp:include page="navBar.jsp" flush="true" />
        </td>
    </tr>
    </table>
</div>

<div id="ContentTop">
        <table width="85%" cellpadding="0" cellspacing="2">
        <tr>
        <td bgcolor="#ffcc00" width="1%">
        <table width="1%" cellpadding="10" cellspacing="0"><tr><td width="1%">
        <br></td><tr>
        </table>
        </td>
        <td bgcolor="#666699">
        <table width="100%" cellpadding="10" cellspacing="0">
        <tr><td bgcolor="#666699">&nbsp;<br><br><br><br><br><br><br></td></tr>
        </table>
        </td>
        </tr>
        </table>
</div>

<div id="ContentPane">

    <table width="85%" cellpadding="0" cellspacing="2">
    <tr>
    <td bgcolor="#ffcc00" width="1%">

    <table width="1%" cellpadding="10" cellspacing="0">
    <tr><td width="1%">
    <br></td></tr>
    </table>
    </td>
    <td bgcolor="#666699">