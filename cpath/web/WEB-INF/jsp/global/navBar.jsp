<%@ page import="java.util.ArrayList"%>

<%
    ArrayList navTextList = new ArrayList();
    ArrayList navURLList = new ArrayList();
    navTextList.add("Home");
    navURLList.add("home.do");
    navTextList.add("Web Service API");
    navURLList.add("webservice.do?cmd=help");
    navTextList.add("Admin");
    navURLList.add("adminHome.do");
%>

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