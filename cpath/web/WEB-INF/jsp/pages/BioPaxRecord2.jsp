<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.TypeCount"%>
<%@ page import="org.mskcc.pathdb.model.BioPaxTabs"%>
<%@ page import="org.mskcc.pathdb.model.Reference"%>
<%@ page import="org.mskcc.pathdb.model.ExternalLinkRecord"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseRecord"%>
<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary"%>
<%@ page import="org.mskcc.pathdb.taglib.ReactomeCommentUtil"%>
<%@ page import="java.util.StringTokenizer"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
    String initFile = CPathUIConfig.getPath("init.jsp");
    String headerFile = CPathUIConfig.getPath("header.jsp");
%>

<jsp:include page="<%=initFile%>" flush="true"/>

<%
ArrayList typesList = (ArrayList) request.getAttribute("TYPES_LIST");
BioPaxTabs bpPlainEnglish = new BioPaxTabs();
String id = request.getParameter("id");
BioPaxRecordSummary bpSummary = (BioPaxRecordSummary) request.getAttribute("BP_SUMMARY");
HashMap<String,Reference> externalLinks = (HashMap<String,Reference>)request.getAttribute("EXTERNAL_LINKS");
boolean showTabs = false;
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<jsp:include page="../global/stylesAndScripts.jsp" flush="true" />

<style type="text/css">
#demo {
    width:100%; /* arbitrary width */
}

#demo .yui-content {
    padding:1em;
}

#demo .loading {
    background-image:url("jsp/images/loading.gif");
    background-position:center center;
    background-repeat:no-repeat;
}

#demo .loading * {
    display:none;
}
</style>

<style type="text/css">
    .button {
        font-size:85%;
        color:blue;
        text-decoration:underline;
        cursor:pointer;
    }
</style>

<script type="text/javascript">

/////////////////////////////
//  Set up Tabs
/////////////////////////////

YAHOO.example.init = function() {
    var tabView = new YAHOO.widget.TabView({id: 'demo'});

    //  Create one tab per type
    <% for (int i=0; i<typesList.size(); i++) {
        TypeCount typeCount = (TypeCount) typesList.get(i);
        String plain = (String) bpPlainEnglish.getTabLabel(typeCount.getCommand(),
        typeCount.getType());
        if (plain == null) {
            plain = typeCount.getType();
        }
        String tabLabel = plain + " (" + typeCount.getCount() + ")";
        String tabActive = "false";
        if (i==0) {
            tabActive = "true";
        }
        String dataUrl = "table.do?id=" + id + "&command=" + typeCount.getCommand() + "&type="
            + typeCount.getType() + "&totalNumRecords=" + typeCount.getCount();
        if (typeCount.getCount() > 0) {
            showTabs = true;
            %>
                tabView.addTab(new YAHOO.widget.Tab({
                    label: '<%= tabLabel %>',
                    dataSrc: '<%= dataUrl %>',
                    active: <%= tabActive %>,
                    cacheData: true /* only load once */
                }));
            <% } %>
    <% } %>

    //  When ready, create tabs within div=doc
    YAHOO.util.Event.onContentReady('doc', function() {
        tabView.appendTo('doc');
    });

};

YAHOO.example.init();
</script>

<script type="text/javascript">
    /////////////////////////////
    //  Set up Pagination within Tabs
    /////////////////////////////

    //  indicates connection in progress
    var connectionInProgress = false;

    //  page size
    var hitsPerPage = 10;

    //  current tab type
    var currentType;

    //  will contain the current index value in each tab
    var indexArray = new Array();

    //  will contain the total number of records in each tab
    var totalsArray = new Array();

    //  will contain URLs for each each tab
    var urlArray = new Array();
    <%
    for (int i=0; i<typesList.size(); i++) {
        TypeCount typeCount = (TypeCount) typesList.get(i);
        String url = "table.do?id=" + id + "&command="+typeCount.getCommand()+"&type="
                + typeCount.getType() + "&totalNumRecords=" + typeCount.getCount()
                + "&showHeader=false&startIndex=";
    %>
    indexArray['<%= typeCount.getType()%>'] = 0;
    urlArray['<%= typeCount.getType()%>'] = "<%= url%>";
    totalsArray['<%= typeCount.getType()%>'] = <%= typeCount.getCount() %>;
    <% } %>

    var callback = {
        success: function(o) {
            YAHOO.log("Connection Success", "info");
            var start = indexArray[currentType] + 1;
            YAHOO.log("Setting start value to:  " + start, "info");

            //  calculate stop value
            var stop = indexArray[currentType] + hitsPerPage;
            if (stop > totalsArray[currentType]) {
                stop = totalsArray[currentType];
            }

            YAHOO.log("Setting stop value to:  " + stop, "info");
            var startSpan = document.getElementById("start_" + currentType);
            startSpan.innerHTML = start;
            var stopSpan = document.getElementById("stop_" + currentType);
            stopSpan.innerHTML = stop;

            var content = document.getElementById("content_" + currentType);
            content.innerHTML = o.responseText;

            //  Conditionally show/hide previous button
            if (start > 1) {
                YAHOO.log("Enabling previous button", "info");
                YAHOO.util.Dom.setStyle("prev_"+currentType, 'display', "inline");
            } else {
                YAHOO.log("Disabling previous button", "info");
                YAHOO.util.Dom.setStyle("prev_"+currentType, 'display', "none");
            }

            //  Conditionally show/hide next button
            if (stop == totalsArray[currentType]) {
                YAHOO.log("Disabling next button", "info");
                YAHOO.util.Dom.setStyle("next_"+currentType, 'display', "none");
            } else {
                YAHOO.log("Enabling next button", "info");
                YAHOO.util.Dom.setStyle("next_"+currentType, 'display', "inline");
            }

            //  Conditionally show/hide all details
            showHideAllDetails();

            connectionInProgress = false;
        },
        failure: function(o) {
            YAHOO.log("Connection Failure:  " + o.statusText, "error");
        }
    }

    function getData (type) {
        var content = document.getElementById("content_" + type);
        content.innerHTML = "<img src='jsp/images/loading.gif'>";
        var url = urlArray[type] + indexArray[type];
        YAHOO.log("Getting data from:  " + url, "info");
        YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
    }

    function getNextData(type) {
        if (connectionInProgress) {
            YAHOO.log("Ignoring request for next data.  Connection currenting in progress",
                    "warn");
        } else {
            connectionInProgress = true;
            indexArray[type] = indexArray[type] + hitsPerPage;
            currentType = type;
            YAHOO.log ("Clicked next on tab:  " + type + ", index is set to:  " + indexArray[type],
                    "info");
            getData(type);
        }
    }

    function getPreviousData(type) {
        if (connectionInProgress) {
            YAHOO.log("Ignoring request for previous data.  Connection currenting in progress",
                    "warn");
        } else {
            connectionInProgress = true;
            indexArray[type] = indexArray[type] - hitsPerPage;
            currentType = type;
            YAHOO.log ("Clicked previous on tab:  " + type + ", index is set to:  " + indexArray[type],
                    "info");
            getData(type);
        }
    }

    var showAllDetails = false;
    var detailsMap = new Array();

    //  Dynamically shows/hides all details.
    function showHideAllDetails() {
        var elements = YAHOO.util.Dom.getElementsByClassName('details', 'div');
        if (showAllDetails == false ) {
            YAHOO.util.Dom.setStyle(elements, 'display', 'none');
        } else {
            YAHOO.util.Dom.setStyle(elements, 'display', 'inline');
        }
    }

    //  Toggles the showing/hiding of all details
    function toggleAllDetails() {
        var buttonContent = document.getElementById("toggle_all_details_button");
        showAllDetails = !showAllDetails;
        YAHOO.log ("User toggled showing of all details:  " + showAllDetails,
                "info");
        if (showAllDetails == true ) {
            buttonContent.innerHTML = "Hide All Details";
        } else {
            buttonContent.innerHTML = "Show All Details";
        }
        showHideAllDetails();
    }

    //  Toggles details on single row
    function toggleDetails (id) {
        YAHOO.log ("Toggling row with cPathID:  " + id);
        var elements = new Array();
        elements[0] = document.getElementById(id + "_comment");
        elements[1] = document.getElementById(id + "_organism");
        elements[2] = document.getElementById(id + "_source");
        var current = YAHOO.util.Dom.getStyle(elements[0], 'display');
        YAHOO.log ("Current Display Style is set to:  " + current);
        if (current == false || current == "none") {
            YAHOO.util.Dom.setStyle(elements, 'display', 'inline');
        } else {
            YAHOO.util.Dom.setStyle(elements, 'display', 'none');
        }
    }
</script>
</head>
<body>

<body class="composite">

<!-- For OverLib PopUp Boxes -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<!-- Header/Banner -->
<div id="page_header">
<table width="100%">
    <tr>
        <td valign="top" width="60%">
        <div id="page_title">
            <jsp:include page="<%=headerFile%>" flush="true"/>
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

        <!-- Start Left Column -->
        <td id="leftcol" width="200">
            <div id="navcolumn">
                <div id="docs" class="toolgroup">
                    <div class="label">
                        <strong>Data Source</strong>
                    </div>

                    <div class="body">
                        <div>
                            Data source logo goes here...
                        </div>
                    </div>
                </div>
                <% if (bpSummary.getOrganism() != null) { %>
                <div id="docs" class="toolgroup">
                    <div class="label">
                        <strong>Organism</strong>
                    </div>

                    <div class="body">
                        <div>
                            <%= bpSummary.getOrganism() %>
                        </div>
                    </div>
                </div>
                <% } %>
                <div id="docs" class="toolgroup">
                    <div class="label">
                        <strong>Synonyms</strong>
                    </div>

                    <div class="body">
                        <div>
                            Synonyms go here...
                        </div>
                    </div>
                </div>
                <div id="docs" class="toolgroup">
                    <div class="label">
                        <strong>Links</strong>
                    </div>

                    <div class="body">
                        <div>
                            External links go here...
                        </div>
                    </div>
                </div>
                <div id="docs" class="toolgroup">
                    <div class="label">
                        <strong>Filter Settings</strong>
                    </div>

                    <div class="body">
                        <div>
                            Current filter settings go here...
                        </div>
                    </div>
                </div>
            </div>
        </td>

        <!-- Start Body Column -->
        <td valign="top">
            <!-- Start Div:  bodycol/projecthome -->
            <div id="bodycol">
                <!-- Start Div:  app -->
                <div id="projecthome" class="app">
                    <jsp:include page="../global/userMessage.jsp" flush="true" />

<%
String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(bpSummary);
%>
<h2><%= header %></h2>
<p>
<% if (bpSummary.getComment() != null) {
    out.println(ReactomeCommentUtil.massageComment(bpSummary.getComment()));
}%>
</p>
<%
		// iterate over list of ExternalLinkRecord
		List<ExternalLinkRecord> externalLinkRecords = bpSummary.getExternalLinks();
		if (externalLinks.size() > 0) {
		    out.println("<p><b>References</b></p>");
            out.println("<ul>");
        }
		for (ExternalLinkRecord externalLinkRecord : externalLinkRecords) {

			Reference reference = externalLinks.get(externalLinkRecord.getLinkedToId());
			if (reference == null) continue;

			if (reference.getDatabase().equalsIgnoreCase("PubMed")) {
			    ExternalDatabaseRecord dbRecord = externalLinkRecord.getExternalDatabase();	
			    out.println("<li>");
				String uri = (externalLinkRecord.getWebLink() == null) ? "" :
				    externalLinkRecord.getWebLink();
                String database = (reference.getDatabase() == null) ? "" :
				    reference.getDatabase();
				uri = (uri == null) ? "" : uri;
				out.println(reference.getReferenceString() + " " +
				            "[<A HREF=\"" + uri + "\">" + database + "</A>]");
                out.println("</li>");
			}
		}
		if (externalLinkRecords.size() > 0) {
            out.println("</ul>");
			out.println("<br>");
        }
%>
<% if (showTabs) { %>
<div id="doc">
</div>
<a class="button" id="toggle_all_details_button" onClick='toggleAllDetails()'>Show All Details</a>
<% } %>

<%
    String xdebugSession = (String) session.getAttribute
            (AdminWebLogging.WEB_LOGGING);
    String xdebugParameter = request.getParameter(AdminWebLogging.WEB_LOGGING);
    if (xdebugSession != null || xdebugParameter != null) {
%>
    <script type="text/javascript">
    var myLogReader = new YAHOO.widget.LogReader();
    </script>
<% } %>

<jsp:include page="../global/footer.jsp" flush="true" />
