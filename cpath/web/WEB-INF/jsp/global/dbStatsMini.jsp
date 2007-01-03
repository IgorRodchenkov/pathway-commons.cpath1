<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType,
                 java.text.DecimalFormat,
                 java.text.NumberFormat"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoOrganism"%>

<%
try {
    DaoCPath dao = DaoCPath.getInstance();
    int numPathways = dao.getNumEntities(CPathRecordType.PATHWAY);
    int numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
    int numPhysicalEntities = dao.getNumEntities
            (CPathRecordType.PHYSICAL_ENTITY);
    NumberFormat formatter = new DecimalFormat("#,###,###");
    WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
    DaoOrganism daoOrganism = new DaoOrganism();
    int numOrganisms = daoOrganism.getAllOrganisms().size();

%>
<b><%= webUIBean.getApplicationName()%> Quick Stats:</b>
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
        <td><%= formatter.format(numOrganisms) %></td>
    </tr>
</table>
<% } catch (Exception e) {
} %>