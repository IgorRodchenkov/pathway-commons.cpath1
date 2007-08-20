<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.protocol.ProtocolConstantsVersion1,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
				 org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%
    WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
    String uri = (String) request.getAttribute("javax.servlet.forward.servlet_path");
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
    String entityValue = "ALL_ENTITY_TYPE";
    String entityName = GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME;
    String dataSourceName = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME;
    String dataSourceValue = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL;
%>
<%  boolean showSearchBox = true;
    if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX && uri != null  && uri.endsWith("home.do")) {
        showSearchBox = false;
    }
    if (showSearchBox) { %>
<div id="searchbar">
<form name="searchbox" action="webservice.do" method="get">
    <input type="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="<%= webUIBean.getWebApiVersion() %>"/>
    <input type="text" name="<%= ProtocolRequest.ARG_QUERY %>" size="15" value='<%= searchTerm %>'/>
    <input type="submit" id="searchbutton" value="Search"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_FORMAT %>" value="<%= ProtocolConstantsVersion1.FORMAT_HTML %>"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
        size="25" value='<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>'/>
    <input type="hidden" name="<%= dataSourceName %>" value="<%= dataSourceValue %>"/>
	<input type="hidden" name="<%= entityName %>" value="<%= entityValue %>"/>

    <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) { %>
        <% try { %>
            <select name="<%= ProtocolRequest.ARG_ORGANISM %>">
            <option value="">All Organisms&nbsp;&nbsp;</option>
        <%
            OrganismStats orgStats = new OrganismStats();
            ArrayList organisms = orgStats.getOrganismsSortedByName();
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
<% } %>
