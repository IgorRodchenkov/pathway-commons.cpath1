<%@ page import="java.net.URLEncoder,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.action.ToggleSearchOptions"%>

<%
    String searchTerm = new String("");
    String taxId = new String("");
    String command = new String ("");
    ProtocolRequest pRequest = (ProtocolRequest) request.getAttribute
            (BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    if (pRequest != null) {
        if (pRequest.getQuery() != null) {
            searchTerm = pRequest.getQuery();
        }
        command = pRequest.getCommand();
        taxId = pRequest.getOrganism();
    }
    Boolean searchOptionsFlag = (Boolean)
        session.getAttribute(ToggleSearchOptions.SESSION_SEARCH_OPTIONS_FLAG);
%>
<div id="search" class="toolgroup">
    <div class="label">
        <strong>Search</strong>
    </div>
    <div class="body">
        <FORM ACTION="webservice.do" METHOD="GET">
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
        <INPUT TYPE="TEXT" name="<%= ProtocolRequest.ARG_QUERY %>"
            SIZE="20" VALUE='<%= searchTerm %>'/>
        <INPUT TYPE="HIDDEN" name="<%= ProtocolRequest.ARG_FORMAT %>"
            value="html"/>

        <% if (searchOptionsFlag != null
                && searchOptionsFlag.booleanValue() == true) { %>
        Filter By Specific Field:
        <BR>
            <%
                String getByKeyword = "";
                String getByInteractor = "";
                String getByExperimentType = "";
                String getByPmid = "";
                String getByDb = "";
            %>
            <%
                if (command.equals(ProtocolConstants.COMMAND_GET_BY_KEYWORD))  {
                    getByKeyword = new String("SELECTED");
                } else if (command.equals
                        (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME_XREF))  {
                    getByInteractor = new String("SELECTED");
                } else if (command.equals
                        (ProtocolConstants.COMMAND_GET_BY_EXPERIMENT_TYPE))  {
                    getByExperimentType = new String("SELECTED");
                } else if (command.equals(ProtocolConstants.COMMAND_GET_BY_PMID))  {
                    getByPmid = new String("SELECTED");
                } else if (command.equals
                        (ProtocolConstants.COMMAND_GET_BY_DATABASE))  {
                    getByDb = new String("SELECTED");
                }
            %>
            <SELECT NAME="<%= ProtocolRequest.ARG_COMMAND %>">
            <OPTION
                VALUE="<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>"
                <%= getByKeyword %>>All Fields</OPTION>
            <OPTION
                VALUE="<%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME_XREF %>"
                <%= getByInteractor %>>Interactor</OPTION>
            <OPTION
                VALUE="<%= ProtocolConstants.COMMAND_GET_BY_EXPERIMENT_TYPE %>"
                <%= getByExperimentType %>>Experiment Type</OPTION>
            <OPTION
                VALUE="<%= ProtocolConstants.COMMAND_GET_BY_PMID %>"
                <%= getByPmid %>>Pub Med ID</OPTION>
            <OPTION
                VALUE="<%= ProtocolConstants.COMMAND_GET_BY_DATABASE %>"
                <%= getByDb %>>Database Source</OPTION>
        </SELECT>
        <% } %>

        <P>Filter by Organism:
        <BR>
        <SELECT NAME="<%= ProtocolRequest.ARG_ORGANISM %>">
            <OPTION VALUE="">All Organisms</OPTION>
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

        <% if (searchOptionsFlag != null
                && searchOptionsFlag.booleanValue() == true) { %>
        <P>
        <INPUT TYPE="SUBMIT" value="Go"/>
        <P>
            <A HREF="toggleSearchOptions.do">Hide Field Specific Filter...</A>
        </FORM>
        <% } else { %>
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
            value="<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>"/>
        <P>
        <INPUT TYPE="SUBMIT" value="Go"/>
        <P>
        <A HREF="toggleSearchOptions.do">Show Field Specific Filter...</A>
        </FORM>
        <% } %>
    </div>
</div>