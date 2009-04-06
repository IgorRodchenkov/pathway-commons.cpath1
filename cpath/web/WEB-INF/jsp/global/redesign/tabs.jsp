<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 java.util.ArrayList,
                 org.mskcc.pathdb.sql.assembly.XmlAssembly,
                 java.io.PrintWriter,
                 java.io.StringWriter,
                 org.mskcc.dataservices.util.PropertyManager,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstantsVersion1,
                 org.mskcc.pathdb.action.ToggleSearchOptions,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%
    String url = "";
    String baseAction = (String) request.getAttribute
            (BaseAction.ATTRIBUTE_SERVLET_NAME);
    String searchResultsPage = (String) request.getAttribute
            (BaseAction.PAGE_IS_SEARCH_RESULT);
    if (baseAction != null) {
        StringBuffer uri = new StringBuffer (
            (String) request.getAttribute
            (BaseAction.ATTRIBUTE_SERVLET_NAME));
        uri = new StringBuffer(uri.substring(1));
        url = uri.toString();
    }
	String referer = (String)request.getAttribute(BaseAction.REFERER);
	if (referer == null){
		referer = new String();
	}
	String queryString = request.getQueryString();
	if (queryString == null){
		queryString = "";
	}

    ArrayList tabUrls = new ArrayList();
    ArrayList tabNames = new ArrayList();
    ArrayList tabActive = new ArrayList();

	// get WebUIBean
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();

    tabNames.add("Home");
    String homeUrl = "home.do";
    tabUrls.add(homeUrl);
    if (url.equals(homeUrl) || url.equals("toggleSearchOptions.do")
		|| referer.equals(BaseAction.FORWARD_HOME)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    if (webUIBean.getDisplayBrowseByPathwayTab()) {
        tabNames.add("Pathways");
        String browsePathwayUrl = "record.do";
        tabUrls.add(browsePathwayUrl);
        if (url.startsWith(browsePathwayUrl) &&
			queryString.indexOf("id=") == -1) {
            tabActive.add (Boolean.TRUE);
        } else {
            tabActive.add (Boolean.FALSE);
        }
    }

    if (webUIBean.getDisplayFilterTab()) {
        tabNames.add("Filter");
        String filterUrl = "filter.do";
        tabUrls.add(filterUrl);
        if (url.startsWith(filterUrl)) {
            tabActive.add (Boolean.TRUE);
        } else {
            tabActive.add (Boolean.FALSE);
        }
    }

    if (webUIBean.getDisplayBrowseByOrganismTab()) {
    	tabNames.add("Organisms");
	    String browseUrl = "browse.do";
    	tabUrls.add(browseUrl);
    	if (url.equals(browseUrl) &&
			!referer.equals(BaseAction.FORWARD_HOME)) {
        	tabActive.add (Boolean.TRUE);
	    } else {
    	    tabActive.add (Boolean.FALSE);
	    }
	}

    tabNames.add("DB Sources");
    String browseUrl = "dbSources.do";
    tabUrls.add(browseUrl);
    if (url.equals(browseUrl) &&
        !referer.equals(BaseAction.FORWARD_HOME)) {
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

    if (webUIBean.getDisplayWebServiceTab()) {
	   tabNames.add("Web Service");
	   String webServiceUrl = "webservice.do?cmd=help";
		tabUrls.add(webServiceUrl);
		if (url.equals("webservice.do") && searchResultsPage == null) {
		    tabActive.add (Boolean.TRUE);
		} else {
		    tabActive.add (Boolean.FALSE);
		}
	}

    tabNames.add("About");
    String aboutUrl = "about.do";
    tabUrls.add(aboutUrl);
    if (url.equals(aboutUrl)) {
        tabActive.add (Boolean.TRUE);
    } else {
        tabActive.add (Boolean.FALSE);
    }

    String isAdmin = (String) request.getAttribute(BaseAction.PAGE_IS_ADMIN);
    String host = request.getRemoteHost();
    if (isAdmin != null
            || (host != null && host.indexOf("mskcc.org") > -1)
            || (host != null && host.indexOf("localhost") > -1)
            || (host != null && host.indexOf("127.0.0.1") >-1)) {
        tabNames.add("Admin");
        String adminUrl = "adminHome.do";
        tabUrls.add(adminUrl);
        if (url.equals(adminUrl) || isAdmin != null) {
            tabActive.add (Boolean.TRUE);
        } else {
            tabActive.add (Boolean.FALSE);
        }
    }

    if (searchResultsPage != null) {
        tabNames.add("Results");
        tabUrls.add("");
        tabActive.add(Boolean.TRUE);
    }
%>

<div id="navigation">
<ul>
<% for (int i=0; i<tabNames.size(); i++) {
            String tabUrl = (String) tabUrls.get(i);
            String tabName = (String) tabNames.get(i);
            Boolean active = (Boolean) tabActive.get(i);
            if (active.booleanValue()) {
                out.println("<li class=\"selected\"><a>" + tabName + "</a></li>");
            } else {
                if (tabUrl.length() > 0) {
                    out.println("<li>" + "<a href=\""+tabUrl+"\">" +
                        tabName + "</a></li>");
                } else {
                    out.println("<li>" + tabName + "</li>");
                }
            }
        }
        %>
</ul>
</div>
