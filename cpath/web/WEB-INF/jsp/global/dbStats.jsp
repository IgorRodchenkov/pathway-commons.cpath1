<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.util.XssFilter,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 org.mskcc.pathdb.xdebug.XDebug,
                 java.text.DecimalFormat,
                 java.text.NumberFormat"%>

<%
try {
    DaoCPath dao = new DaoCPath();
    int numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
    int numPhysicalEntities = dao.getNumEntities
            (CPathRecordType.PHYSICAL_ENTITY);
    OrganismStats orgStats = new OrganismStats();
    NumberFormat formatter = new DecimalFormat("#,###,###");

%>
<div class="h3">
    <h3>Database Stats</h3>
</div>
<TABLE>
    <TR>
        <TD>Number of Interactions:</TD>
        <TD><%= formatter.format(numInteractions) %></TD>
    </TR>
    <TR>
        <TD>Number of Interactors:</TD>
        <TD><%= formatter.format(numPhysicalEntities) %></TD>
    </TR>
    <TR>
        <TD>Number of Organisms:</TD>
        <TD><%= formatter.format(orgStats.getOrganismsSortedByName().size()) %></TD>
    </TR>

</TABLE>
<% } catch (Exception e) {
    out.println(e.toString());
} %>