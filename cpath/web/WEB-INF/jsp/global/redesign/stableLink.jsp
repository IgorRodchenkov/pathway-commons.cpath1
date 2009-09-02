<%@ page import="org.mskcc.pathdb.action.BaseAction" %>
<%@ page import="org.mskcc.pathdb.util.UrlUtil" %>
<%@ page import="org.mskcc.pathdb.util.ExternalDatabaseConstants" %>
<%
    String acStableLinkId = request.getParameter("acStableLinkId");
    String peName = request.getParameter("peName");
    String currentUrl = (String) request.getAttribute(BaseAction.ATTRIBUTE_URL_BEFORE_FORWARDING);
    String stableUrl = null;
    String href = null;
    if (!acStableLinkId.equals("null") && !peName.equals("null")) {
        stableUrl = UrlUtil.rewriteUrl(currentUrl, "stable.do") +
            "?db=" + ExternalDatabaseConstants.UNIPROT + "&id=" + acStableLinkId;
        href = "<a href='" + stableUrl + "'>" + peName + "</a>";
    } else {
        stableUrl = "Stable links are not currently available to pathways.";
        href = stableUrl;
    }

%>

<script>
		YAHOO.namespace("cpath.container");
		YAHOO.util.Event.onContentReady('stableLinkPanel', function() {
            YAHOO.log("Initializing stable link panel", "info");
            YAHOO.cpath.container.stableLinkPanel =
				new YAHOO.widget.Panel("stableLinkPanel", { width:"320px",
                    visible:false, constraintoviewport:true } );
			YAHOO.cpath.container.stableLinkPanel.render();
			YAHOO.util.Event.addListener("link_to_page", "click",
				YAHOO.cpath.container.stableLinkPanel.show, YAHOO.cpath.container.stableLinkPanel, true);
	    });
</script>

<ul>
    <li><a href="javascript:void(0);" id="link_to_page">Link to this page</a>
    <div id="stableLinkPanel">
        <div class="hd">Link to this page</div>
        <div class="bd">Paste link in email or IM:
            <div class="url_box">
            <form>
            <input type="text" size=40 value="<%= stableUrl %>"></input>
            </form>
            </div>
        </div>
        <div class="bd">Paste HTML to embed in website:
            <div class="url_box">
            <form>
            <input type="text" size=40 value="<%= href %>"></input>
            </form>
            </div>
        </div>        
    </div>
    </li>
</ul>