<%@ page import="java.net.URLEncoder,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolConstants,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.action.BaseAction"%>

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
<div id="search" class="toolgroup">
    <div class="label">
        <strong>Search</strong>
    </div>
    <div class="body">
        <FORM ACTION="webservice.do" METHOD="GET">
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
            value="<%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME %>"/>
                    <INPUT TYPE="TEXT" name="<%= ProtocolRequest.ARG_QUERY %>"
                        SIZE="20" VALUE="<%= searchTerm %>"/>
                    <INPUT TYPE="HIDDEN" name="<%= ProtocolRequest.ARG_FORMAT %>"
                        value="html"/>
        <P>Organism:
        <BR>
        <SELECT NAME="<%= ProtocolRequest.ARG_ORGANISM %>">
            <OPTION VALUE="">All</OPTION>
        <%
            DaoOrganism dao = new DaoOrganism ();
            ArrayList organisms = dao.getAllOrganisms();
        %>
        <% for (int i=0; i<organisms.size(); i++) {
            Organism organism = (Organism) organisms.get(i);
            String currentTaxId = Integer.toString(organism.getTaxonomyId());
        %>
            <% if (currentTaxId.equals(taxId)) { %>
                <OPTION VALUE="<%= organism.getTaxonomyId()%>" SELECTED>
                <%= organism.getSpeciesName()%></OPTION>
            <% } else { %>
                <OPTION VALUE="<%= organism.getTaxonomyId()%>">
                <%= organism.getSpeciesName()%></OPTION>
            <% } %>
        <% } %>
        </SELECT>
        <P>
        <INPUT TYPE="SUBMIT" value="Go"/>
        </FORM>
    </div>
</div>