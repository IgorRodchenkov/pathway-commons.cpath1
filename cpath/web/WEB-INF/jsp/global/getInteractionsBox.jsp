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

<SCRIPT language="JavaScript">
function toggleSearchOptions()
{
  document.searchbox.action="toggleSearchOptions.do";
  return document.searchbox.submit();
}
</SCRIPT>

<div id="search" class="toolgroup">
    <FORM name="searchbox" ACTION="webservice.do" METHOD="GET">
    <div class="label">
        <strong>Search</strong>
    </div>
    <div class="body">
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
        <NOBR>
        <INPUT TYPE="TEXT" name="<%= ProtocolRequest.ARG_QUERY %>"
            SIZE="15" VALUE='<%= searchTerm %>'/>

        <INPUT TYPE="SUBMIT" value="Go"/>
        </NOBR>
        <INPUT TYPE="HIDDEN" name="<%= ProtocolRequest.ARG_FORMAT %>"
            value="html"/>

        <% try { %>
        <P>Filter by Organism:
        <BR>
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

        <% if (searchOptionsFlag != null
                && searchOptionsFlag.booleanValue() == true) { %>
        <P>Filter By Specific Field:
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
                <%= getByExperimentType %>>Experimental Evidence</OPTION>
            <OPTION
                VALUE="<%= ProtocolConstants.COMMAND_GET_BY_PMID %>"
                <%= getByPmid %>>Pub Med ID</OPTION>
            <OPTION
                VALUE="<%= ProtocolConstants.COMMAND_GET_BY_DATABASE %>"
                <%= getByDb %>>Database Source</OPTION>
        </SELECT>
        <% } %>
        <P>
        Example Searches:
        <BR>- dna repair
        <BR>- dna AND repair
        <BR>- "dna repair"
        <P>
        <span style="text-align:right">
            <% if (searchOptionsFlag != null
                    && searchOptionsFlag.booleanValue() == true) { %>
                <A TITLE="Hide Search Options"
                    HREF="javascript:toggleSearchOptions();">[Hide Options...]</A>
            <% } else { %>
            <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
                value="<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>"/>
            <A TITLE="Show Search Options"
                    HREF="javascript:toggleSearchOptions();">[Show Options...]</A>
            <% } %>
        </span>

        </FORM>
    </div>
</div>