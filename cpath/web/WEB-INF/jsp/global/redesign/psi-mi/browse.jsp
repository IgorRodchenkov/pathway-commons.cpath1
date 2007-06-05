<%@ page import="org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.util.security.XssFilter,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstantsVersion1,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 org.mskcc.pathdb.xdebug.XDebug,
                 java.util.Collections"%>
<%
    try {
    ProtocolRequest pRequest = new ProtocolRequest();
    pRequest.setCommand(ProtocolConstantsVersion1.COMMAND_GET_BY_KEYWORD);
    pRequest.setFormat(ProtocolConstantsVersion1.FORMAT_HTML);
    OrganismStats orgStats = new OrganismStats();
    ArrayList orgList = orgStats.getOrganismsSortedByNumInteractions();

    //  Clone the ArrayList Locally and Reverse Sort
    orgList = (ArrayList) orgList.clone();
    Collections.reverse(orgList);
%>

<h3>Quick Browse</h3>
<ul>
        <%
        int endIndex = Math.min(orgList.size(), 10);
        for (int i=0; i < endIndex; i++) {
            Organism organism = (Organism) orgList.get(i);
            pRequest.setOrganism(Integer.toString(organism.getTaxonomyId()));
            String uri = pRequest.getUri();
        %>
        <li>
            <a
                TITLE='View All Records for Organism: <%= organism.getSpeciesName().trim() %>'
                HREF='<%= uri %>'><%= organism.getSpeciesName()%></a>
        </li>
        <% } %>
        <li>
        <A HREF="browse.do">View All Organisms...</A>
        </li>
</ul>
<% } catch (Exception e) {
    //  Ignore Exception here;  it probably indicates that the database is down.
} %>