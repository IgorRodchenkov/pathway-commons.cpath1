<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 java.util.ArrayList"%>
<%
    StringBuffer uri = new StringBuffer (
            (String) request.getAttribute
            (BaseAction.ATTRIBUTE_SERVLET_NAME));
    uri = new StringBuffer(uri.substring(1));
    String url = uri.toString();
    ArrayList tabUrls = new ArrayList();
    tabUrls.add("home.do");
    tabUrls.add("advancedSearch.do");
    tabUrls.add("webservice.do?cmd=help");
    tabUrls.add("adminHome.do");

    ArrayList tabNames = new ArrayList();
    tabNames.add("Home");
    tabNames.add("Advanced Search");
    tabNames.add("Web Services API");
    tabNames.add("Administration");
%>

<div class="tabs" id="toptabs">
    <table cellpadding="4" cellspacing="0" border="0">
        <tr>
        <% for (int i=0; i<tabNames.size(); i++) {
            String tabUrl = (String) tabUrls.get(i);
            String tabName = (String) tabNames.get(i);
            if (tabUrl.startsWith(url)) {
                out.println("<th>" + "<a href='"+tabUrl+"'> " +
                    tabName + "</A></th>");
            } else {
                out.println("<td>" + "<a href='"+tabUrl+"'> " +
                    tabName + "</A></td>");
            }
        }
        %>
        </tr>
    </table>
</div>