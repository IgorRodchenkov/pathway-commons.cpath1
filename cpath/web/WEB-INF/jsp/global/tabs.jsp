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
    if (url.equals(homeUrl) || url.equals("toggleSearchOptions.do")
        || url.equals("disclaimer.do")) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("Browse By Organism");
    String browseUrl = "browse.do";
    tabUrls.add(browseUrl);
    if (url.equals(browseUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("Database Stats");
    String dbStatsUrl = "dbStats.do";
    tabUrls.add(dbStatsUrl);
    if (url.equals(dbStatsUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("FAQ");
    String advancedSearchUrl = "faq.do";
    tabUrls.add(advancedSearchUrl);
    if (url.equals(advancedSearchUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("Web Service API");
    String webServiceUrl = "webservice.do?cmd=help";
    tabUrls.add(webServiceUrl);
    if (url.equals("webservice.do") && xmlAssemblyTemp == null) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    tabNames.add("Cytoscape PlugIn");
    String cytoscapeUrl = "cytoscape.do";
    tabUrls.add(cytoscapeUrl);
    if (url.equals(cytoscapeUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    String isAdmin = (String) request.getAttribute(BaseAction.PAGE_IS_ADMIN);
    String host = request.getRemoteHost();
    if (isAdmin != null
            || (host != null && host.indexOf("mskcc.org") > -1)
            || (host != null && host.indexOf("localhost") > -1)) {
        tabNames.add("Administration");
        String adminUrl = "adminHome.do";
        tabUrls.add(adminUrl);
        if (url.equals(adminUrl) || isAdmin != null) {
            tabActive.add (Boolean.TRUE);
        } else {
            tabActive.add (Boolean.FALSE);
        }
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