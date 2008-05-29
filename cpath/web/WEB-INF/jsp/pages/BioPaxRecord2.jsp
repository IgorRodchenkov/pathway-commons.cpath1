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
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion1"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion2"%>
<%@ page import="org.mskcc.pathdb.taglib.ReferenceUtil"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.*"%>
<%@ page import="org.mskcc.pathdb.xdebug.XDebugUtil"%>
<%@ page import="org.mskcc.pathdb.action.ShowBioPaxRecord2"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Collections"%>
<%@ page import="org.mskcc.pathdb.util.ExternalDatabaseConstants" %>
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

// cytoscape link
String urlForCytoscapeLink = (String) request.getAttribute("request_url");
urlForCytoscapeLink = urlForCytoscapeLink.substring(7); // remove "http://" from string
urlForCytoscapeLink = urlForCytoscapeLink.replace("record2.do", "webservice.do");

// data source parameter string  to network neighborhood map
String encodedDataSourceParameter = "";
GlobalFilterSettings filterSettings =
	(GlobalFilterSettings)request.getSession().getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
DaoExternalDbSnapshot daoSnapShot = new DaoExternalDbSnapshot();
if (filterSettings != null) {
	for (Long snapshotID : (Set<Long>)filterSettings.getSnapshotIdSet()) {
		 ExternalDatabaseSnapshotRecord record = daoSnapShot.getDatabaseSnapshot(snapshotID);
		 encodedDataSourceParameter += record.getExternalDatabase().getMasterTerm() + ",";
	}
	// snip off last ' '
	encodedDataSourceParameter = encodedDataSourceParameter.replaceAll(",$", "");
	// encode
	encodedDataSourceParameter = URLEncoder.encode(encodedDataSourceParameter, "UTF-8");
}

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
        elements[3] = document.getElementById(id + "_evidence");
        elements[4] = document.getElementById(id + "_availability");
        elements[5] = document.getElementById(id + "_remaining_participants");
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
            toggleImage.innerHTML = "less...";
        } else {
            YAHOO.util.Dom.setStyle(commentRemainder, 'display', 'none');
            toggleImage.innerHTML = "more...";
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
if (!bpSummary.getName().equals(bpSummary.getLabel())) { %>
    <h1><%= bpSummary.getName() %></h1>
<% } %>
<%
    if (entitySummary != null && entitySummary instanceof InteractionSummary) {
        String entitySummaryStr = InteractionSummaryUtils.createInteractionSummaryStringTruncated
                ((InteractionSummary) entitySummary);
        if (entitySummaryStr != null && entitySummaryStr.length() > 0) {
            out.println("<p><b>Summary:</b>&nbsp;&nbsp;" + entitySummaryStr + "</p>");
        }
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
                commentHtml.append("<p>");
                commentHtml.append(paragraphs[j]);
                if (paragraphs.length > 1 || comments.length > 1
                    || referenceLinks.size() > 0
                    || (bpSummary.getAvailability() != null && bpSummary.getAvailability().length() > 0)) {
                    commentHtml.append("<a title='Toggle Comments / References' onClick='toggleComments()'>");
                    commentHtml.append("<span id='toggleCommentImage' class='toggle_details_text'>");
                    commentHtml.append("more...</span></a>");
                }
                commentHtml.append("</p>\n\r");
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

<noscript>
<div class="user_message">
In order to view complete details regarding this record, please
enable Javascript support within your web browser.
</div>
</noscript>

<% } else { %>
    <div class="user_message">No pathway or interaction information available for this record.</div>
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
        out.println ("<div class=\"box\">");
        out.println("<h3>Data Source:</h3>");
        ExternalDatabaseRecord dbRecord =
                bpSummary.getExternalDatabaseSnapshotRecord().getExternalDatabase();
        out.println("<ul><li>");
        out.println(DbSnapshotInfo.getDbSnapshotHtml
                (bpSummary.getExternalDatabaseSnapshotRecord().getId()));
        out.println("</li></ul>");
        if (dbRecord.getIconFileExtension() != null) {
            out.println("<div class='data_source_logo'><img src='jsp/images/database/"
                    + "db_" + dbRecord.getId() + "." + dbRecord.getIconFileExtension() + "'/></div>");
        }
        out.println("</div>");
    }

    //  Output organism details
    if (bpSummary.getOrganism() != null) {
        out.println ("<div class=\"box\">");
        out.println("<h3>Organism:</h3>");
        out.println("<ul><li>" + bpSummary.getOrganism() + "</li></ul>");
        out.println ("</div>");
    }

    //  Output Gene Symbol
    ArrayList<String> geneSymbols = new ArrayList<String>();
    for (int i=0; i<nonReferenceLinks.size(); i++) {
        ExternalLinkRecord link = nonReferenceLinks.get(i);
        ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
        String dbId = link.getLinkedToId();
        if (dbRecord.getMasterTerm().equals(ExternalDatabaseConstants.GENE_SYMBOL)) {
            geneSymbols.add(dbId);
        }
    }
    if (geneSymbols.size() > 0) {
        out.println ("<div class=\"box\">");
        out.println("<h3>Gene Symbol:</h3>");
        out.println("<ul>");
        for (String geneSymbol: geneSymbols) {
            out.println("<li>" + geneSymbol + "</li>");
        }
        out.println("</ul>");
        out.println("</div>");
    }


    //  Output synonyms
    if (bpSummary.getSynonyms() != null && bpSummary.getSynonyms().size() > 0) {
        out.println ("<div class=\"box\">");
        out.println("<h3>Synonyms:</h3>");
        out.println("<ul>");
        for (int i=0; i<bpSummary.getSynonyms().size(); i++) {
            String synonym = (String) bpSummary.getSynonyms().get(i);
            out.println("<li>" + synonym + "</li>");
        }
        out.println("</ul>");
        out.println("</div>");
    }

    //  Output external links
    String acStableLinkId = null;
    if (nonReferenceLinks.size() > 0) {
		HashMap<String, String> linksMap = new HashMap<String,String>();
        for (int i=0; i<nonReferenceLinks.size(); i++) {
            ExternalLinkRecord link = nonReferenceLinks.get(i);
            ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
            String dbId = link.getLinkedToId();
            if (dbRecord.getMasterTerm().equals(ExternalDatabaseConstants.UNIPROT)
                    && acStableLinkId == null) {
                acStableLinkId = dbId;
            }
            String linkStr = dbRecord.getName() + ": " + dbId;
            String uri = link.getWebLink();
            if (uri != null && uri.length() > 0) {
                //  Hide GO Links for now [Temporary]
                if (!dbId.startsWith("GO")) {
                    linksMap.put(linkStr, new String("<a href=\"" + uri + "\">" + linkStr + "</a>"));
                }
            }
        }
        if (linksMap.keySet().size() > 0) {
            out.println ("<div class=\" box\">");
            out.println("<h3>Links:</h3>");
            out.println("<ul>");
            // output goes here
            ArrayList<String> linksMapKeys = new ArrayList<String>();
            linksMapKeys.addAll(linksMap.keySet());
            Collections.sort(linksMapKeys);
            for (String key : linksMapKeys) {
                out.println("<li>");
                out.println(linksMap.get(key));
                out.println("</li>");
            }
            out.println("</ul>");
            out.println("</div>");
        }
    }

    //  Output Cytoscape Links
    boolean pathwayType = (bpSummary.getType() != null &&
						   bpSummary.getType().equalsIgnoreCase(CPathRecordType.PATHWAY.toString()));
    if (webUIBean.getWantCytoscape() && (!pathwayType || (pathwayType && showTabs))) {
        out.println ("<div class=\"box\">");
        out.println("<h3>Cytoscape:</h3>");
		if (pathwayType) {
			out.println("<P><img src='jsp/images/cytoscape.png' align='ABSMIDDLE'>&nbsp;<a href=\"http://" + CYTOSCAPE_HTTP_SERVER + "/" +
						urlForCytoscapeLink +
						"?" + ProtocolRequest.ARG_VERSION + "=" + ProtocolConstantsVersion2.VERSION_2 +
						"&" + ProtocolRequest.ARG_COMMAND + "=" + ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID +
						"&" + ProtocolRequest.ARG_OUTPUT + "=" + ProtocolConstantsVersion1.FORMAT_BIO_PAX +
						"&" + ProtocolRequest.ARG_QUERY + "=" + id +
						"&" + ProtocolRequest.ARG_DATA_SOURCE + "=" + encodedDataSourceParameter +
                        "\"" +
                        " id=\"" + id +"\"" +
						" onclick=\"appRequest(this.href, this.id, " + "'" + ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID + "', " + "'empty_title', '" + encodedDataSourceParameter + "'); return false;\"" +
						">View in Cytoscape</a>");
			//out.println("<a href=\"cytoscape.do\">(help)</a></P>");
		}
		else {
			String encodedNeighborhoodTitle = URLEncoder.encode("Neighborhood: " + bpSummary.getLabel(), "UTF-8");
			out.println("<P><img src='jsp/images/cytoscape.png' align='ABSMIDDLE'>&nbsp;<a href=\"http://" + CYTOSCAPE_HTTP_SERVER + "/" +
						urlForCytoscapeLink +
						"?" + ProtocolRequest.ARG_VERSION + "=" + ProtocolConstantsVersion2.VERSION_2 +
						"&" + ProtocolRequest.ARG_COMMAND + "=" + ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS +
						"&" + ProtocolRequest.ARG_OUTPUT + "=" + ProtocolConstantsVersion1.FORMAT_BIO_PAX +
						"&" + ProtocolRequest.ARG_QUERY + "=" + id + 
						"&" + ProtocolRequest.ARG_DATA_SOURCE + "=" + encodedDataSourceParameter +
						"&" + ProtocolRequest.ARG_NEIGHBORHOOD_TITLE + "=" + encodedNeighborhoodTitle + "\"" +
						" id=\"" + id +"\"" +
						" onclick=\"appRequest(this.href, this.id, " + "'" + ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS + "', '" + encodedNeighborhoodTitle + "', '" + encodedDataSourceParameter + "'); return false;\"" +
						">View in Cytoscape</a>");
			//out.println("<a href=\"cytoscape.do\">(help)</a></P>");
		}
        out.println("</P>");
        out.println("</div>");
    }

	//  Output BioPAX Links
    if (bpSummary.getType() != null && bpSummary.getType().equalsIgnoreCase
            (CPathRecordType.PATHWAY.toString())) {
            ProtocolRequest pRequest = new ProtocolRequest();
            pRequest.setVersion(ProtocolConstantsVersion2.VERSION_2);
            pRequest.setCommand(ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID);
            pRequest.setQuery(Long.toString(bpSummary.getRecordID()));
            pRequest.setOutput(ProtocolConstantsVersion1.FORMAT_BIO_PAX);
            out.println ("<div class=\"box\">");
            out.println("<h3>Download:</h3>");
            out.println("<P>&nbsp;<img src='jsp/images/xml_doc.gif' align='ABSMIDDLE'>&nbsp;&nbsp;<a href='downloadBioPax.do?id=" + bpSummary.getRecordID() + "'>"
                + "Download BioPAX" + "</a></P>");
            out.println("</div>");
    }

    //  Output XML_ABBREV Link (Debug Mode)
    if (debugMode) {
        out.println ("<div class=\"box\">");
        out.println("<h3>Debug:</h3>");
        String xmlAbbrevUrl = "record.do?format=xml_abbrev&id=" + bpSummary.getRecordID();
        out.println("<ul>");
        out.println("<li><a href=\"" + xmlAbbrevUrl + "\">XML Content (Abbrev)</a></li>");
        out.println("</ul>");
    }
%>
<jsp:include page="../global/redesign/currentFilterSettings.jsp" flush="true" />
<%
    if (acStableLinkId != null) {
        out.println ("<div class=\"box\">");
        out.println("<p><b><a href='stable.do?db="
                + ExternalDatabaseConstants.UNIPROT + "&id=" + acStableLinkId
                + "'>Stable link for this page</a></b></p>");
        out.println ("</div>");
    }
%>

</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
