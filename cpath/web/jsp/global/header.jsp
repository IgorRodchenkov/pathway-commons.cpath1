<%@ page import="java.util.ArrayList"%>

<!--  Title Bar -->
<!--  Includes:
      1.  HTML Header
      2.  Main Header
      3.  Main Navigation Elements
-->

<%
    ArrayList navTextList = new ArrayList();
    ArrayList navURLList = new ArrayList();
    navTextList.add("Help");
    navURLList.add("/ds/dataservice");
    navTextList.add("Admin");
    navURLList.add("/ds/jsp/pages/Admin.jsp");
    navTextList.add("News");
    navURLList.add("");
    navTextList.add("About");
    navURLList.add("");
    navTextList.add("Documents");
    navURLList.add("");
    navTextList.add("Links");
    navURLList.add("");
%>

<html>
    <head>
        <title>CPath Database</title>
        <link rel="stylesheet" href="/ds/jsp/css/style.css" type="text/css">
    </head>
    <body bgcolor="#ccccff" marginwidth="0" marginheight="0"
        leftmargin="0" topmargin="0" text="#ffffff">

<div id="TitleBar">
    <br><br>
    <table cellpadding="2" cellspacing="2" border="0" width="90%">
        <tr>
            <td valign="Top" align="Right" bgcolor="#9999cc" NOWRAP>
            <big><big>
            <b>
            <font color="#ffffff">cPath:</font>
            <font color="#333366">CBio Pathways Database</font>
            </b>
            </big></big>
            <br>
        </td>
    </tr>
    <tr bgcolor="#333366">
        <td valign="Top" align="Right" >
        <table>
        <tr>
        <% for (int i=0; i<navTextList.size(); i++) {
            String navText = (String) navTextList.get(i);
            String navURL = (String) navURLList.get(i);
        %>
            <td valign="Top" NOWRAP>
                <div align="Right">
                <a href="<%= navURL %>" class="HereSideBarLinks"> <%= navText %> </a>
                </div>
            </td>
            <td>&nbsp;</td>
        <% } %>
        </tr>
        </table>
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