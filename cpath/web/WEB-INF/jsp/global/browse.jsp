<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.util.XssFilter,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolConstants,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism"%>
<%
    ProtocolRequest pRequest = new ProtocolRequest();
    pRequest.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
    pRequest.setFormat(ProtocolConstants.FORMAT_HTML);
%>

<div id="dbstats" class="toolgroup">
    <div class="label">
        <strong>Browse Interactions by Organism</strong>
    </div>

    <div class="body">
        <%
            DaoOrganism dao = new DaoOrganism ();
            ArrayList organisms = dao.getAllOrganisms();
        %>
        <% for (int i=0; i<organisms.size(); i++) {
            Organism organism = (Organism) organisms.get(i);
        %>
        <div>
            <%
                pRequest.setOrganism(Integer.toString(organism.getTaxonomyId()));
                String uri = pRequest.getUri();
            %>
            <A HREF='<%= uri %>'><%= organism.getSpeciesName()%></A>
        </div>
        <% } %>
        <div>
        </div>
    </div>
</div>