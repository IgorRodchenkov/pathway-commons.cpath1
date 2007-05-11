<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ page import="org.mskcc.pathdb.taglib.ReactomeCommentUtil"%>
<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.model.*"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="org.mskcc.pathdb.taglib.ReferenceUtil"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.*"%>
<%@ page import="org.mskcc.pathdb.xdebug.XDebugUtil"%>
<%@ page import="org.mskcc.pathdb.action.ShowBioPaxRecord2"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
//  Extract data from user request
String id = request.getParameter(ShowBioPaxRecord2.ID_PARAMETER);

//  Extract data from attributes

//  The BioPAX Record Summary
BioPaxRecordSummary bpSummary = (BioPaxRecordSummary) request.getAttribute(ShowBioPaxRecord2.BP_SUMMARY);

//  The Entity Summary (applies to interaction records only)
EntitySummary entitySummary = (EntitySummary) request.getAttribute(ShowBioPaxRecord2.ENTITY_SUMMARY);

//  External References
HashMap<String,Reference> referenceMap = (HashMap<String,Reference>)
	request.getAttribute(ShowBioPaxRecord2.EXTERNAL_LINKS);

//  Children / Parent Types (for creation of tabs)
ArrayList typesList = (ArrayList) request.getAttribute(ShowBioPaxRecord2.TYPES_LIST);

//  Set page title
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, bpSummary.getName());

// Separate PubMed links from other links
ReferenceUtil refUtil = new ReferenceUtil();
ArrayList masterList = refUtil.categorize(bpSummary);
ArrayList<ExternalLinkRecord> referenceLinks = (ArrayList<ExternalLinkRecord>) masterList.get(0);
ArrayList<ExternalLinkRecord> nonReferenceLinks = (ArrayList<ExternalLinkRecord>) masterList.get(1);

// server name
final String CYTOSCAPE_HTTP_SERVER = "127.0.0.1:27182";
String serverName = (String)request.getServerName();
serverName = (serverName.indexOf("pathwaycommons") != -1) ? serverName : serverName + ":8080";

// cytoscape link
String urlForCytoscapeLink = ((StringBuffer)request.getRequestURL()).toString();
urlForCytoscapeLink = urlForCytoscapeLink.substring(7); // remove "http://" from string
urlForCytoscapeLink = urlForCytoscapeLink.replace("record2.do", "webservice.do");
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<%
boolean showTabs = false;
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
boolean debugMode = XDebugUtil.xdebugIsEnabled(request);
BioPaxTabs bpPlainEnglish = new BioPaxTabs();
%>
<script type="text/javascript">

/////////////////////////////
//  Set up Tabs
/////////////////////////////

YAHOO.example.init = function() {
    var tabView = new YAHOO.widget.TabView({id: 'demo'});

    //  Create one tab per type
    <%
    boolean activeTab = true;
    for (int i=0; i<typesList.size(); i++) {
        TypeCount typeCount = (TypeCount) typesList.get(i);
        String plain = (String) bpPlainEnglish.getTabLabel(typeCount.getCommand(),
        typeCount.getType());
        if (plain == null) {
            plain = typeCount.getType();
        }
        String tabLabel = plain + " (" + typeCount.getCount() + ")";
        StringBuffer dataUrl = new StringBuffer("table.do?id=" + id + "&command=" + typeCount.getCommand() + "&type="
            + typeCount.getType() + "&totalNumRecords=" + typeCount.getCount());
        String xdebugParameter = request.getParameter(AdminWebLogging.WEB_LOGGING);
        if (xdebugParameter != null) {
            dataUrl.append("&" + AdminWebLogging.WEB_LOGGING + "="
                + xdebugParameter);
        }
        if (typeCount.getCount() > 0) {
            String tabActive;
            if (activeTab) {
                tabActive = "true";
                activeTab = false;
            } else {
                tabActive = "false";
            }
            showTabs = true;
            %>
                var tab<%= i%> = new YAHOO.widget.Tab({
                    label: '<%= tabLabel %>',
                    dataSrc: '<%= dataUrl.toString() %>',
                    active: <%= tabActive %>,
                    cacheData: true /* only load once */
                });
                tab<%= i%>.addListener('contentChange', handleContentChange);
                tabView.addTab (tab<%= i%>);
            <% } %>
    <% } %>

    //  Explicitly handle content changes
    function handleContentChange(e) {
        YAHOO.log("Tab content has been updated", "info");
        showHideAllDetails();
    }

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
        var images = YAHOO.util.Dom.getElementsByClassName('toggleImage', 'div');
        var html;
        if (showAllDetails == false ) {
            YAHOO.util.Dom.setStyle(elements, 'display', 'none');
            html = innerHTML = "<img src='jsp/images/open.gif'>";
        } else {
            YAHOO.util.Dom.setStyle(elements, 'display', 'inline');
            html = "<img src='jsp/images/close.gif'>"
        }
        for (i=0 ; i < images.length; i++) {
            images[i].innerHTML = html;
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
        elements[2] = document.getElementById(id + "_refs");
        var current = YAHOO.util.Dom.getStyle(elements[0], 'display');
        YAHOO.log ("Current Display Style is set to:  " + current);
        var toggleImage = document.getElementById (id + "_image");
        if (current == false || current == "none" || current == null) {
            YAHOO.util.Dom.setStyle(elements, 'display', 'inline');
            if (toggleImage != null) {
                toggleImage.innerHTML = "<img src='jsp/images/close.gif'>";
            }
        } else {
            YAHOO.util.Dom.setStyle(elements, 'display', 'none');
            if (toggleImage != null) {
                toggleImage.innerHTML = "<img src='jsp/images/open.gif'>";
            }
        }
    }

    var showAllComments = false;

    //  Toggles Comments
    function toggleComments() {
        var commentRemainder = document.getElementById("comment_remainder");
        var toggleImage = document.getElementById("toggleCommentImage");
        showAllComments = ! showAllComments;
        YAHOO.log ("Toggling comments, show all comments:  " + showAllComments);;
        if (showAllComments) {
            YAHOO.util.Dom.setStyle(commentRemainder, 'display', 'inline');
            toggleImage.innerHTML = "<img align=right src='jsp/images/close.gif'>";
        } else {
            YAHOO.util.Dom.setStyle(commentRemainder, 'display', 'none');
            toggleImage.innerHTML = "<img align=right src='jsp/images/open.gif'>";
        }
    }
</script>
<div class="splitcontentright">
<%
String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(bpSummary);
header = header.replaceAll("N/A", "");
%>
<h1><%= header %></h1>
<%
    if (entitySummary != null && entitySummary instanceof InteractionSummary) {
        String entitySummaryStr = InteractionSummaryUtils.createInteractionSummaryString
                ((InteractionSummary) entitySummary);
        out.println("<h2>" + entitySummaryStr + "</h2>");
    }
%>
<p>
<%
	//  Output comments
    boolean firstParagraph = false;
    if (bpSummary.getComments() != null) {
    String comments[] = bpSummary.getComments();
    StringBuffer commentHtml = new StringBuffer();
    for (int i=0; i<comments.length; i++) {
        String comment = ReactomeCommentUtil.massageComment(comments[i]);
        String paragraphs[] = comment.split("<p>");
        for (int j=0; j<paragraphs.length; j++) {
            if (firstParagraph == false) {
                commentHtml.append("<div class='comment_first'>");
                if (paragraphs.length > 1 || comments.length > 1
                    || referenceLinks.size() > 0
                    || (bpSummary.getAvailability() != null && bpSummary.getAvailability().length() > 0)) {
                    commentHtml.append("<a title='Toggle Comments / References' onClick='toggleComments()'>");
                    commentHtml.append("<span id='toggleCommentImage' class='toggle_details'>");
                    commentHtml.append("<img align=right src='jsp/images/open.gif'/></span></a>");
                }
                commentHtml.append("<p>");
                commentHtml.append(paragraphs[j] + "</p>\n\r");
                commentHtml.append("</div>\n\r");
                commentHtml.append("<div id='comment_remainder'>\n\r");
                firstParagraph = true;
            } else {
                commentHtml.append("<p>" + paragraphs[j] + "</p>\n\r");
            }
        }
    }
    out.println(commentHtml.toString());
}%>
</p>
<%
	//  Output Pub Med References
    if (referenceLinks.size() > 0) {
        out.println(refUtil.getReferenceHtml(referenceLinks, referenceMap));
    }

    //  Output Availability Info
    if (bpSummary.getAvailability() != null && bpSummary.getAvailability().length() > 0) {
        out.println("<p><b>Availability:</b></p>\n");
        out.println("<p>" + bpSummary.getAvailability() + "</p>");
    }
    if (firstParagraph == true) {
        out.println("</div>");
    }
%>

<% if (showTabs) { %>

<div id="doc">
</div>

<div id="toggle_div">
</div>

<script type="text/javascript">
        var toggleDiv = document.getElementById("toggle_div");
        toggleDiv.innerHTML = "<a class='button' id='toggle_all_details_button' onClick='toggleAllDetails()'>Show All Details</a>";
</script>
<noscript>
<div class="user_message">
In order to view complete details regarding this record, please
enable Javascript support within your web browser.
</div>
</noscript>

<% } else { %>
    <p>No additional details specified for this record.</p>
<% } %>
<%
    if (debugMode) {
%>
    <script type="text/javascript">
    var myLogReader = new YAHOO.widget.LogReader();
    </script>
<% } %>
<p>&nbsp;</p>
</div>
<div class="splitcontentleft">
<%
	//  Output data source details
    if (bpSummary.getExternalDatabaseSnapshotRecord() != null) {
        out.println("<h3>Data Source:</h3>");
        ExternalDatabaseRecord dbRecord =
                bpSummary.getExternalDatabaseSnapshotRecord().getExternalDatabase();
        out.println("<ul><li>");
        out.println(DbSnapshotInfo.getDbSnapshotHtml
                (bpSummary.getExternalDatabaseSnapshotRecord().getId()));
        out.println("</li></ul>");
        if (dbRecord.getIconFileExtension() != null) {
            out.println("<div class='data_source_logo'><img src='icon.do?id="
                    + dbRecord.getId() + "'/></div>");
        }
    }

    //  Output organism details
    if (bpSummary.getOrganism() != null) {
        out.println("<h3>Organism:</h3>");
        out.println("<ul><li>" + bpSummary.getOrganism() + "</li></ul>");
    }

    //  Output synonyms
    if (bpSummary.getSynonyms() != null && bpSummary.getSynonyms().size() > 0) {
        out.println("<h3>Synonyms:</h3>");
        out.println("<ul>");
        for (int i=0; i<bpSummary.getSynonyms().size(); i++) {
            String synonym = (String) bpSummary.getSynonyms().get(i);
            out.println("<li>" + synonym + "</li>");
        }
        out.println("</ul>");
    }

    //  Output external links
    if (nonReferenceLinks.size() > 0) {
        out.println("<h3>Links:</h3>");
        out.println("<ul>");
        for (int i=0; i<nonReferenceLinks.size(); i++) {
            ExternalLinkRecord link = nonReferenceLinks.get(i);
            ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
            String dbId = link.getLinkedToId();
            String linkStr = dbRecord.getName() + ": " + dbId;
            String uri = link.getWebLink();
            out.println("<li>");
            if (uri != null && uri.length() > 0) {
                out.println("<a href=\"" + uri + "\">" + linkStr + "</a>");
            } else {
                out.println(linkStr);
            }
            out.println("</li>");
        }
        out.println("</ul>");
    }

    //  Output Cytoscape Links
    if (webUIBean.getWantCytoscape()) {
		if (bpSummary.getType() != null && bpSummary.getType().equalsIgnoreCase
            (CPathRecordType.PATHWAY.toString())) {
					out.println("<h3>Cytoscape:</h3>");
				out.println("<ul><li>");
		//if (bpSummary.getType() != null && bpSummary.getType().equalsIgnoreCase
        //    (CPathRecordType.PATHWAY.toString())) {
			out.println("<a href=\"http://" + CYTOSCAPE_HTTP_SERVER + "/" +
						urlForCytoscapeLink +
						"?version=1.0&cmd=get_record_by_cpath_id&format=biopax&q=" +
						id + "\"" + " id=\"" +
						id +"\"" +
						" onclick=\"appRequest(this.href, this.id); return false;\"" +
						">View this pathway in Cytoscape</a>");
			out.println("<a href=\"cytoscape_readme.do\">(help)</a>");
		    out.println("</li></ul>");
		}
		//else {
		//	out.println("<span style=\"color:#467aa7;text-decoration:underline;\"" +
		//		">View network neighborhood map in Cytoscape</span>");
		//}
	}

	//  Output BioPAX Links
    if (bpSummary.getType() != null && bpSummary.getType().equalsIgnoreCase
            (CPathRecordType.PATHWAY.toString())) {
            ProtocolRequest pRequest = new ProtocolRequest();
            pRequest.setCommand(ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID);
            pRequest.setQuery(Long.toString(bpSummary.getRecordID()));
            pRequest.setFormat(ProtocolConstants.FORMAT_BIO_PAX);
            out.println("<h3>Download:</h3>");
            out.println("<ul><li><a href=\"" + pRequest.getUri() + "\">"
                + "Download in BioPAX Format" + "</a></li></ul>");
    }

	//  Output XML_ABBREV Link (Debug Mode)
    if (debugMode) {
        out.println("<h3>Debug:</h3>");
        String xmlAbbrevUrl = "record.do?format=xml_abbrev&id=" + bpSummary.getRecordID();
        out.println("<ul>");
        out.println("<li><a href=\"" + xmlAbbrevUrl + "\">XML Content (Abbrev)</a></li>");
        out.println("</ul>");
    }
%>
<jsp:include page="../global/redesign/currentFilterSettings.jsp" flush="true" />
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
