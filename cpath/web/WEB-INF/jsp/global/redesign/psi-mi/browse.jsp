<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 java.util.ArrayList,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 java.util.Collections"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants" %>
<%
    try {
        ProtocolRequest pRequest = new ProtocolRequest();
        pRequest.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
        pRequest.setFormat(ProtocolConstants.FORMAT_HTML);
        OrganismStats orgStats = OrganismStats.getInstance();
        ArrayList orgList = orgStats.getListSortedByNumEntities();

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