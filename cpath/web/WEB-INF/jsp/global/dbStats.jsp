<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.util.security.XssFilter,
                 org.mskcc.pathdb.lucene.OrganismStats,
                 org.mskcc.pathdb.xdebug.XDebug,
                 java.text.DecimalFormat,
                 java.text.NumberFormat"%>

<%
try {
    DaoCPath dao = DaoCPath.getInstance();
    int numPathways = dao.getNumEntities(CPathRecordType.PATHWAY);
    int numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
    int numPhysicalEntities = dao.getNumEntities
            (CPathRecordType.PHYSICAL_ENTITY);
    OrganismStats orgStats = new OrganismStats();
    NumberFormat formatter = new DecimalFormat("#,###,###");

%>
<h2>Database Stats</h2>

<table>

    <tr>
        <td>Number of Pathways:</td>
        <td><%= formatter.format(numPathways) %></td>
    </tr>
    <tr>
        <td>Number of Interactions:</td>
        <td><%= formatter.format(numInteractions) %></td>
    </tr>
    <tr>
        <td>Number of Physical Entities:</td>
        <td><%= formatter.format(numPhysicalEntities) %></td>
    </tr>
    <tr>
        <td>Number of Organisms:</td>
        <td>
            <% try {
                out.println(formatter.format(orgStats.getOrganismsSortedByName().size()));
            } catch (Exception e) {
                out.println("Cannot determine organism data:  " + e.getMessage());
            }
            %>
        </td>
    </tr>

</table>
<% } catch (Exception e) {
    out.println(e.toString());
} %>
