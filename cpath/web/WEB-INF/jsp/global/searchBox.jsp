<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.action.ToggleSearchOptions,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism"%>
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

<form name="searchbox" action="webservice.do" method="get">
    <input type="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
    <input type="text" name="<%= ProtocolRequest.ARG_QUERY %>" size="15" value='<%= searchTerm %>'/>
    <input type="submit" value="Search"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_FORMAT %>" value="<%= ProtocolConstants.FORMAT_HTML %>"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
        size="25" value='<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>'/>

    <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) { %>
        <% try { %>
            <select name="<%= ProtocolRequest.ARG_ORGANISM %>">
            <option value="">All Organisms</option>
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
<p>
  <a href="javascript:void(0);" onmouseover="return overlib('<DIV CLASS=popup><DIV CLASS=popup_text><UL><LI><B>dna repair</B>: Finds all records that contain the words dna OR repair.<LI><B>dna AND repair:</B>  Finds all records the contain the words dna AND repair.<LI><B>dna NOT repair</B>:  Finds all records that contain the word dna, but do not contain the word repair.<LI><B>&quot;dna repair&quot;</B>: Finds all records containing the exact text: &quot;dna repair&quot;.<LI><B>regulat*</B>:  Finds all records beginning with the wildcard: regulat. This will match against records, such as regulate, regulatory, etc.</LI></UL></DIV></DIV>', FULLHTML, FOLLOWMOUSE, 0);" onmouseout="return nd();">Search Examples</a>