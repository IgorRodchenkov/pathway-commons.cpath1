<%@ page import="java.text.DecimalFormat,
                 java.text.NumberFormat"%>
<%@ page import="org.mskcc.pathdb.sql.util.DatabaseStats" %>

<%
DatabaseStats dbStats = DatabaseStats.getInstance();
NumberFormat formatter = new DecimalFormat("#,###,###");
%>
<h1>Database Stats</h1>

<table>

    <tr>
        <td>Number of Pathways:</td>
        <td><%= formatter.format(dbStats.getNumPathways()) %></td>
    </tr>
    <tr>
        <td>Number of Interactions:</td>
        <td><%= formatter.format(dbStats.getNumInteractions()) %></td>
    </tr>
    <tr>
        <td>Number of Physical Entities:</td>
        <td><%= formatter.format(dbStats.getNumPhysicalEntities()) %></td>
    </tr>
    <tr>
        <td>Number of Organisms:</td>
        <td><%= formatter.format(dbStats.getNumOrganisms()) %></td>
    </tr>
</table>
