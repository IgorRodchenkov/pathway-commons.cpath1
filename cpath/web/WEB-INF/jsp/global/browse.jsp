<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.util.XssFilter,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 org.mskcc.pathdb.xdebug.XDebug"%>
<%
    try {
    ProtocolRequest pRequest = new ProtocolRequest();
    pRequest.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
    pRequest.setFormat(ProtocolConstants.FORMAT_HTML);
    OrganismStats orgStats = new OrganismStats();
    ArrayList orgList = orgStats.getOrganismsSortedByNumInteractions();
%>

<div id="dbstats" class="toolgroup">
    <div class="label">
        <strong>Quick Browse</strong>
    </div>

    <div class="body">

        <%
        int endIndex = Math.min(orgList.size(), 10);
        for (int i=0; i < endIndex; i++) {
            Organism organism = (Organism) orgList.get(i);
            pRequest.setOrganism(Integer.toString(organism.getTaxonomyId()));
            String uri = pRequest.getUri();
        %>
        <div>
            <A HREF='<%= uri %>'><%= organism.getSpeciesName()%></A>
        </div>
        <% } %>
        <div>
        <A HREF="browse.do">View All Organisms...</A>
        </div>
    </div>
</div>
<% } catch (Exception e) {
    //  Ignore Exception here;  it probably indicates that the database is down.
} %>