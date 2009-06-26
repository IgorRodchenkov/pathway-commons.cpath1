<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
				 org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%
    WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
    String uri = (String) request.getAttribute("servlet_name");
    String searchTerm = new String("");
    String taxId = new String("");
    ProtocolRequest pRequest = (ProtocolRequest) request.getAttribute
            (BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    if (pRequest != null) {
        if (pRequest.getQuery() != null) {
            searchTerm = pRequest.getQuery();
        }
        taxId = pRequest.getOrganism();
    }
    String dataSourceName = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME;
    String dataSourceValue = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL;
%>
<%  boolean showSearchBox = true;
    if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX && uri != null  && uri.endsWith("home.do")) {
        showSearchBox = false;
    }
    if (showSearchBox) { %>
<div id="searchbar">
<%
    String recordType = request.getParameter(ProtocolRequest.ARG_RECORD_TYPE);
    if (recordType == null) {
        recordType = GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS;
    }
%>

<script>
  var tabs = ["pathway_tab_tab", "protein_tab_tab"];

  //  Highlight the specified search bar tab
  function showSearchBarTab(tab){
    // first make sure all the tabs are hidden
    for(i=0; i < tabs.length; i++){
      var obj_tab = document.getElementById(tabs[i]);
      obj_tab.className = 'search_bar_tab_inactive';
    }

    // show the tab we're interested in
    var obj = document.getElementById(tab);
    var obj_tab = document.getElementById(tab + "_tab");
    obj_tab.className = 'search_bar_tab_active';

    //  Update the hidden record type parameter
    var record_type = document.getElementById("<%= ProtocolRequest.ARG_RECORD_TYPE %>");
    if (tab == "pathway_tab") {
        record_type.value = "<%= GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS %>"
    } else {
        record_type.value = "<%= GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PHYSICAL_ENTITIES %>"
    }

    //  Set Focus to the Text Box
    var textBox = document.getElementById("<%= ProtocolRequest.ARG_QUERY %>")
    textBox.focus();
      
    return true;
  }
</script>

<div class="search_bar_box">
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) {

    //  Highlight the Correct Tab, depending on the Search Just Done.
    String pathwayTabClass = "search_bar_tab_inactive";
    String proteinTabClass = "search_bar_tab_inactive";
    if (recordType.equals(GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS)) {
        pathwayTabClass = "search_bar_tab_active";
    } else {
        proteinTabClass = "search_bar_tab_active";
    }

%>
    <div class="search_bar_tabs">
        <a href="#" id="pathway_tab_tab" class="<%= pathwayTabClass %>" onclick="showSearchBarTab('pathway_tab')">Find Pathways</a>
        <a href="#" id="protein_tab_tab" class="<%= proteinTabClass %>" onclick="showSearchBarTab('protein_tab')">Find Proteins</a>
    </div>
<% } %>
<form name="searchbox" action="webservice.do" method="get">
    <input type="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="<%= webUIBean.getWebApiVersion() %>"/>
    <input type="text" id="<%= ProtocolRequest.ARG_QUERY %>" name="<%= ProtocolRequest.ARG_QUERY %>" size="30" value='<%= searchTerm %>'/>
    <input type="submit" id="searchbutton" value="Search"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_FORMAT %>" value="<%= ProtocolConstants.FORMAT_HTML %>"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
        size="25" value='<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>'/>

    <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) { %>
        <input type="hidden" name="<%= dataSourceName %>" value="<%= dataSourceValue %>"/>
        <input type="hidden" id="<%= ProtocolRequest.ARG_RECORD_TYPE %>" name="<%= ProtocolRequest.ARG_RECORD_TYPE %>" value="<%= recordType %>"/>
    <% } %>

    <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) { %>
        <% try { %>
            <select name="<%= ProtocolRequest.ARG_ORGANISM %>">
            <option value="">All Organisms&nbsp;&nbsp;</option>
        <%
            OrganismStats orgStats = OrganismStats.getInstance();
            ArrayList organisms = orgStats.getListSortedByName();
        %>
        <% for (int i=0; i<organisms.size(); i++) {
            Organism organism = (Organism) organisms.get(i);
            String currentTaxId = Integer.toString(organism.getTaxonomyId());
            String species = organism.getSpeciesName();
            String speciesShort = species;
            if (species.length() > 12) {
                speciesShort = new String (species.substring(0,12) + "...");
            }
        %>
            <% if (currentTaxId.equals(taxId)) { %>
                <option title='<%= species %>'
                    value="<%= organism.getTaxonomyId()%>" selected>
                <%= speciesShort %></option>
            <% } else { %>
                <OPTION TITLE='<%= species %>' VALUE="<%= organism.getTaxonomyId()%>">
                <%= speciesShort %></option>
            <% } %>
        <% } %>
        </select>
        <% } catch (Exception e) { %>
        </select>
        <% } %>
    <% } %>
</form>
</div>
</div>
<% } else { %>
<div id="tagline">
<%= webUIBean.getTagLine() %>
</div>
<% } %>
