<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 java.util.ArrayList,
                 org.mskcc.pathdb.sql.assembly.XmlAssembly"%>
<%
    StringBuffer uri = new StringBuffer (
            (String) request.getAttribute
            (BaseAction.ATTRIBUTE_SERVLET_NAME));
    uri = new StringBuffer(uri.substring(1));
    String url = uri.toString();

    XmlAssembly xmlAssemblyTemp = (XmlAssembly)
            request.getAttribute(BaseAction.ATTRIBUTE_XML_ASSEMBLY);

    ArrayList tabUrls = new ArrayList();
    ArrayList tabNames = new ArrayList();
    ArrayList tabActive = new ArrayList();

    tabNames.add("Home");
    String homeUrl = "home.do";
    tabUrls.add(homeUrl);
    if (url.equals(homeUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("Advanced Search");
    String advancedSearchUrl = "advancedSearch.do";
    tabUrls.add(advancedSearchUrl);
    if (url.equals(advancedSearchUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("Web Services API");
    String webServiceUrl = "webservice.do";
    tabUrls.add(webServiceUrl);
    if (url.equals(webServiceUrl) && xmlAssemblyTemp == null) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("Administration");
    String adminUrl = "adminHome.do";
    tabUrls.add(adminUrl);
    if (url.equals(adminUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    if (xmlAssemblyTemp != null) {
        tabNames.add("Search Results");
        tabUrls.add("");
        tabActive.add(Boolean.TRUE);
    }
%>

<div class="tabs" id="toptabs">
    <table cellpadding="4" cellspacing="0" border="0">
        <tr>
        <% for (int i=0; i<tabNames.size(); i++) {
            String tabUrl = (String) tabUrls.get(i);
            String tabName = (String) tabNames.get(i);
            Boolean active = (Boolean) tabActive.get(i);
            if (active.booleanValue()) {
                if (tabUrl.length() > 0) {
                    out.println("<th>" + "<a href='"+tabUrl+"'> " +
                        tabName + "</A></th>");
                } else {
                    out.println("<th>" + tabName + "</th>");
                }
            } else {
                if (tabUrl.length() > 0) {
                    out.println("<td>" + "<a href='"+tabUrl+"'> " +
                        tabName + "</A></td>");
                } else {
                    out.println("<td>" + tabName + "</td>");
                }
            }
        }
        %>
        </tr>
    </table>
</div>