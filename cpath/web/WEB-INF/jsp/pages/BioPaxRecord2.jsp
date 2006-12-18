<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.TypeCount"%>
<%@ page import="org.mskcc.pathdb.model.BioPaxTabs"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
ArrayList typesList = (ArrayList) request.getAttribute("TYPES_LIST");
BioPaxTabs bpPlainEnglish = new BioPaxTabs();
String id = request.getParameter("id");
String name = (String) request.getAttribute("NAME");
boolean showTabs = false;
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>

<link rel="stylesheet" href="jsp/css/style.css" type="text/css"/>
<link rel="stylesheet" href="jsp/css/tigris.css" type="text/css"/>
<link rel="stylesheet" href="jsp/css/inst.css" type="text/css"/>

<!-- Yahoo  UI Dependencies -->
<!-- core CSS -->
<link rel="stylesheet" type="text/css" href="jsp/yui/build/tabview/assets/tabs.css">
<link type="text/css" rel="stylesheet" href="jsp/yui/build/logger/assets/logger.css">

<!-- optional skin for border tabs -->
<link rel="stylesheet" type="text/css" href="jsp/yui/build/tabview/assets/border_tabs.css">

<script type="text/javascript" src="jsp/yui/build/yahoo/yahoo.js"></script>
<script type="text/javascript" src="jsp/yui/build/dom/dom.js"></script>
<script type="text/javascript" src="jsp/yui/build/event/event.js"></script>
<script type="text/javascript" src="jsp/yui/build/logger/logger.js"></script>

<script type="text/javascript" src="jsp/yui/build/animation/animation.js" ></script>
<script type="text/javascript" src="jsp/yui/build/connection/connection.js" ></script>
<script type="text/javascript" src="jsp/yui/build/container/container.js"></script>
<link type="text/css" rel="stylesheet" href="jsp/yui/build/container/assets/container.css">

<!-- OPTIONAL: Connection (required for dynamic loading of data) -->
<script type="text/javascript" src="jsp/yui/build/connection/connection.js"></script>

<!-- Source file -->
<script type="text/javascript" src="jsp/yui/build/tabview/tabview.js"></script>

<script type="text/javascript" src="jsp/javascript/overlib/overlib.js"><!-- overLIB (c) Erik Bosrup --></script>

<style type="text/css">
#demo {
    width:710px; /* arbitrary width */
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


<h1><%= name %></h1>
<% if (showTabs) { %>
<div id="doc">
</div>
<a class="button" id="toggle_all_details_button" onClick='toggleAllDetails()'>Show All Details</a>
<% } %>


<script type="text/javascript">
var myLogReader = new YAHOO.widget.LogReader();
</script>

<jsp:include page="../global/xdebug.jsp" flush="true" />
</body>
</html>