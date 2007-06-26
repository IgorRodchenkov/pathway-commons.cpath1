<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.xdebug.XDebugUtil"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath"%>
<%@ page import="org.mskcc.pathdb.model.CPathRecord"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Download BioPAX Record");
boolean debugMode = XDebugUtil.xdebugIsEnabled(request);
String cPathId = request.getParameter("id");
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<script type="text/javascript">

var AjaxObject = {

	handleSuccess:function(o){
        YAHOO.log("Connection Success", "info");
        // This member handles the success response
		// and passes the response object o to AjaxObject's
		// processResult member.
		this.processResult(o);
	},

	handleFailure:function(o){
        YAHOO.log("Connection Failure", "info");
        // Failure handler
	},

	processResult:function(o){
        YAHOO.log("Processing result", "info");
        // This member is called by handleSuccess
        var content = document.getElementById("response");
		var size = o.responseText.length / 1024.0;
		sizeStr = size +'';
		parts = sizeStr.split(".");
		if (o.responseText.indexOf("<error>") == -1) {
    		content.innerHTML = "Document is Ready:  "
        		+"<a href='biopax.xml?cmd=get_record_by_cpath_id&version=2.0&output=biopax&q="
        		+"<%= cPathId %>'>BioPAX_ID_<%= cPathId%></a> [~" + parts[0] + " kb]";
		} else {
			content.innerHTML = "Sorry.  Unable to prepare BioPAX Assembly.  Check that you are using a valid ID.";
		}
    },

	startRequest:function() {
	   YAHOO.util.Connect.asyncRequest('GET',
               'biopax.xml?cmd=get_record_by_cpath_id&version=2.0&output=biopax&q=<%= cPathId %>', callback, null);
	}
};

/*
 * Define the callback object for success and failure
 * handlers as well as object scope.
 */
var callback =
{
	success:AjaxObject.handleSuccess,
	failure:AjaxObject.handleFailure,
	scope: AjaxObject
};

// Start the transaction.
AjaxObject.startRequest();

</script>

<%
    if (debugMode) {
%>
    <script type="text/javascript">
    var myLogReader = new YAHOO.widget.LogReader();
    </script>
<% } %>

<%
DaoCPath dao = DaoCPath.getInstance();
CPathRecord record = null;
if (cPathId != null) {
	try {
		record = dao.getRecordById(Long.parseLong(cPathId));
	} catch (NumberFormatException e) {
	}
}
%>

<h2>Download BioPAX Record:  
<% if (record != null) {
	out.println(record.getName());
}
%>
</h2>

<div id="response">
<img src='jsp/images/loading.gif'/>&nbsp;  Assembling BioPAX File...

</div>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />

