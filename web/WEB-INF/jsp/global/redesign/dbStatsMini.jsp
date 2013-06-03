<%@ page import="java.text.DecimalFormat,
                 java.text.NumberFormat"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.sql.util.DatabaseStats" %>

<%
DatabaseStats dbStats = DatabaseStats.getInstance();
NumberFormat formatter = new DecimalFormat("#,###,###");
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
%>
<b><%= webUIBean.getApplicationName()%> Quick Stats:</b>
<table>
    <% if (dbStats.getNumPathways() > 0)  { %>
    <tr>
        <td>Number of Pathways:</td>
        <td><%= formatter.format(dbStats.getNumPathways()) %></td>
    </tr>
    <% } %>
    <tr>
        <td>Number of Interactions:</td>
        <td><%= formatter.format(dbStats.getNumInteractions()) %></td>
    </tr>
    <tr>
        <td>Number of Physical Entities:</td>
        <td><%= formatter.format(dbStats.getNumPhysicalEntitiesPathwayOrInteractionOnly()) %></td>
    </tr>
    <tr>
        <td>Number of Organisms:</td>
        <td><%= formatter.format(dbStats.getNumOrganisms()) %></td>
    </tr>
</table>
