<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collections"%>
<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.model.Organism"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.lucene.OrganismStats"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Filter"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" /> 

<%
	// some contants
    int NUM_TOP_SPECIES_TO_DISPLAY = 10;
    String ORGANISM_TR = "ORGANISM_TR_";
    String ORGANISM_CB = "ORGANISM_CB_";
    String ALL_ORGANISMS_FILTER_VALUE = String.valueOf(GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE);

    // setup some globals
    String referer = request.getHeader("Referer");
    if (referer != null &&
		(referer.indexOf("record2.do") > 0 ||
		 (referer.indexOf("webservice.do") > 0 && referer.indexOf("webservice.do?cmd=help") == -1))) {
		session.setAttribute("Referer", referer);
    }
    else {
		String filterURL = null;
		if (referer != null) {
	        String debug = (referer.indexOf("debug=1") > 0) ? "?debug=1" : "";
	        filterURL = referer.substring(0, referer.lastIndexOf('/')) + "/filter.do" + debug;
		}
	    session.setAttribute("Referer", filterURL);
	}
    GlobalFilterSettings settings = (GlobalFilterSettings)
            session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
    if (settings == null) {
        settings = new GlobalFilterSettings();
        session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, settings);
    }

    // entire organism list - sorted by name
    OrganismStats orgStats = OrganismStats.getInstance();
    List<Organism> allOrganismsList = orgStats.getListSortedByName();

    // construct organism list into string used by autocomplete box
    boolean organismSelected = false; // used to check "All organisms" check box below
    String organismListStr = "";
    for (Organism organism : allOrganismsList) {
	    organismListStr += "\"" + organism.getSpeciesName() + "\", ";
		if (settings.isOrganismSelected(organism.getTaxonomyId())) {
			organismSelected = true;
		}
	}
    // zap off ',' at end of organism list
    organismListStr = organismListStr.replaceAll("\\, $", "");

    // top XX organisms
    ArrayList<Organism> topOrganisms = orgStats.getListSortedByNumEntities();
    List<String> topOrganismsString = new ArrayList<String>();
    if (topOrganisms.size() > 0) {
		topOrganisms = (ArrayList)topOrganisms.clone();
		Collections.reverse(topOrganisms);
		int maxRecords = (NUM_TOP_SPECIES_TO_DISPLAY < topOrganisms.size()) ? NUM_TOP_SPECIES_TO_DISPLAY : topOrganisms.size();
		for (int i = 0; i < maxRecords; i++) {
			Organism organism = topOrganisms.get(i);
			topOrganismsString.add(organism.getSpeciesName());
		}
	}
%>

<script type="text/javascript">

	// create hashmap of organism name to tax id
	var javascriptOrganismMap = new Array();
	<%
		for (Organism organism : allOrganismsList) {
	%>
	        javascriptOrganismMap['<%= organism.getSpeciesName()%>'] = <%= organism.getTaxonomyId()%>;
	<%
		}
    %>

	//
	// method to process autocomplete button press
	//
	function processOrganismInput(organismInputID) {
		var organismInputElement = document.getElementById(organismInputID);
		if (organismInputElement.value == "") {
			return;
		}
		var organismID = javascriptOrganismMap[organismInputElement.value];
		var organismTableRow = document.getElementById("<%= ORGANISM_TR%>" + organismID);
		YAHOO.util.Dom.setStyle(organismTableRow, 'display', "table-row");
		organismInputElement.value = "";
		organismCheckBoxClicked(organismID, true);
	}

    //
    // method called when all organism checkbox is clicked
    //
    function allOrganismsCheckBoxClicked() {
		var allOrganismCheckBox = document.getElementById("<%= ORGANISM_CB%>" + "<%= ALL_ORGANISMS_FILTER_VALUE%>");
		if (allOrganismCheckBox.checked == true) {
			// uncheck all other organism checkboxes
			var index;
			for (index in javascriptOrganismMap) {
				var organismID = javascriptOrganismMap[index];
				var organismCheckBox = document.getElementById("<%= ORGANISM_CB%>" + organismID);
				organismCheckBox.checked = false;
			}
		}
    }

    //
    // method called when any checkbox but all organism is clicked
    //
    function organismCheckBoxClicked(organismID, set) {
		var organismCheckBox = document.getElementById("<%= ORGANISM_CB%>" + organismID);
		if (organismCheckBox.checked == true || set == true) {
			// uncheck all organism checkbox
			var allOrganismsCheckBox = document.getElementById("<%= ORGANISM_CB%>" + "<%= ALL_ORGANISMS_FILTER_VALUE%>");
			allOrganismsCheckBox.checked = false;
			if (set == true) {
				organismCheckBox.checked = true;
			}
		}
    }
</script>

<div>
<h1>Restrict my search results to the following data sources:</h1>

<form action="storeFilters.do">
    <table cellpadding="0" cellspacing="5">
    <%
    DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
    ArrayList list = dao.getAllNetworkDatabaseSnapshots();

    // process records
    if (list.size() == 0) {
        out.println("<tr>");
        out.println("<td>No Data Sources Available</td>");
        out.println("</td>");
    } else {
        for (int i = 0; i < list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            out.println("<tr><td>");
            out.println("<input type=\"checkbox\" name=\"SNAPSHOT_ID\" value=\""
                + snapshotRecord.getId() + "\"");
            if (settings.isSnapshotSelected(snapshotRecord.getId())) {
                out.print(" checked=\"checked\"");
            }
            out.println("/>");
            out.println("&nbsp;&nbsp;" +
                    DbSnapshotInfo.getDbSnapshotHtml(snapshotRecord.getId()));
            out.println("</td></tr>");
        }
    }
    %>
    </table>

<h1>Restrict my search results to the following organisms:</h1>
    <table cellpadding="0" cellspacing="5">
        <%
            if (allOrganismsList.size() == 0) {
                out.println("<tr><td>No Organism Data Available</td><tr>");
		    }
            else {
				// all organism radio button
				String checked = (organismSelected) ? "" : " checked=\"checked\"";
				String orgTrID = ORGANISM_TR + ALL_ORGANISMS_FILTER_VALUE;
				String orgCbID = ORGANISM_CB + ALL_ORGANISMS_FILTER_VALUE;
				out.println("<tr id=\"" + orgTrID + "\"" + " style=\"display:table-row;\">");
				out.println("<td><input type=\"checkbox\" id=\"" + orgCbID + "\"" + " name=\"ORGANISM_TAXONOMY_ID\" value=\"" +
							ALL_ORGANISMS_FILTER_VALUE + "\"" + " onclick=\"allOrganismsCheckBoxClicked()" + "\"" + checked + "/>&nbsp;&nbsp;All organisms</td>");
				out.println("</tr>");
				// interate through all the other organisms, top XX organisms and user choosen organisms are display, all others hidden
				for (Organism organism : allOrganismsList) {
					boolean isSelected = settings.isOrganismSelected(organism.getTaxonomyId());
					orgTrID = ORGANISM_TR + organism.getTaxonomyId();
					orgCbID = ORGANISM_CB + organism.getTaxonomyId();
					checked = (isSelected) ? " checked=\"checked\"" : "";
					String styleStr = (isSelected || topOrganismsString.contains(organism.getSpeciesName())) ? "style=\"display:table-row;\"" : "style=\"display:none;\"";
					out.println("<tr id=\"" + orgTrID + "\" "  + styleStr + ">");
					out.println("<td><input type=\"checkbox\" id=\"" + orgCbID + "\"" + " name=\"ORGANISM_TAXONOMY_ID\" value=\"" +
								organism.getTaxonomyId() + "\"" + " onclick=\"organismCheckBoxClicked('" + organism.getTaxonomyId() + "', false)\"" + checked + "/>&nbsp;&nbsp;" + organism.getSpeciesName() + "</td>");
					out.println("</tr>");
				}
			}
	    %>
		<tr>
			<td>
			<h1>Search for organism to add to filter list:</h1>
			</td>
		</tr>
		<tr>
			<td>
		        <div class="yui-skin-sam">
		            <input id="ORGANISM_INPUT" type="text"> 
                    <input id="ORGANISM_INPUT_BUTTON" type="button" onclick="processOrganismInput('ORGANISM_INPUT')" value="Add Organism"/>
		            <div id="ORGANISM_CONTAINER"></div>
	            </div>
	            <script type="text/javascript">
		            ////////////////////////////////////
		            // setup organism auto-complete box
		            ////////////////////////////////////
                    var organismList = [<%= organismListStr%>]; 
		            var organismArray = new YAHOO.widget.DS_JSArray(organismList); 
		            var myAutoComp = new YAHOO.widget.AutoComplete("ORGANISM_INPUT","ORGANISM_CONTAINER", organismArray); 
                    myAutoComp.queryDelay = 0;
                    myAutoComp.forceSelection = true;
                    myAutoComp.prehighlightClassName = "yui-ac-prehighlight";
                    myAutoComp.useShadow = true;
                    myAutoComp.minQueryLength = 0;
                </script>
    			</td>
		</tr>
    </table>

<table cellpadding="0" cellspacing="5">
    <tr>
        <td>
            <b>Once set, your filter settings will remain in effect,
            and persistent for all subsequent searches.</b>
        </td>
    </tr>
    <tr>
        <td>
            <input type="submit" value="Set Global Filters"/>
        </td>
    </tr>
</table>

</form>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
