<%@ page import="java.net.URLEncoder,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.action.ToggleSearchOptions,
                 org.mskcc.pathdb.xdebug.XDebug,
                 org.mskcc.pathdb.lucene.OrganismStats"%>

<%
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
%>

<div id="search" class="searchbox">
    <FORM name="searchbox" ACTION="webservice.do" METHOD="GET">
    <div class="body">
        &nbsp;&nbsp;&nbsp;
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
        <NOBR>
        <INPUT TYPE="TEXT" name="<%= ProtocolRequest.ARG_QUERY %>"
            SIZE="15" VALUE='<%= searchTerm %>'/>

        <INPUT TYPE="SUBMIT" value="Search"/>
        </NOBR>
        <INPUT TYPE="HIDDEN" name="<%= ProtocolRequest.ARG_FORMAT %>"
            value="<%= ProtocolConstants.FORMAT_HTML %>"/>
        <INPUT TYPE="HIDDEN" name="<%= ProtocolRequest.ARG_COMMAND %>"
            value="<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>"/>

        <% try { %>
        &nbsp;Filter by Organism:
        &nbsp;
        <SELECT NAME="<%= ProtocolRequest.ARG_ORGANISM %>">
            <OPTION VALUE="">All Organisms</OPTION>
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
                <OPTION TITLE='<%= species %>'
                    VALUE="<%= organism.getTaxonomyId()%>" SELECTED>
                <%= speciesShort %></OPTION>
            <% } else { %>
                <OPTION TITLE='<%= species %>' VALUE="<%= organism.getTaxonomyId()%>">
                <%= speciesShort %></OPTION>
            <% } %>
        <% } %>
        </SELECT>
        <% } catch (Exception e) { %>
            </SELECT>
        <% } %>
        </FORM>
    </div>
</div>