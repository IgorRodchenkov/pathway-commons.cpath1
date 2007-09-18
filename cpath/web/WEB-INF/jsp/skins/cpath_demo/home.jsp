<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion1"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
String entityValue =  "pathway";
String entityName = ProtocolRequest.ARG_ENTITY_TYPE;
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
%>

<table>
<tr valign="top">
<td width="60%">
<div>
This is a demonstration site, which illustrates the use of cPath software.
cPath is a database and software suite for storing, visualizing, and
analyzing biological pathways.
<a href="about.do">more...</a>
</div>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) { %>
    <div class="large_search_box">
    <h1>Search <%= webUIBean.getApplicationName() %>:</h1>
    <form name="searchbox" action="webservice2.do" method="get">
    <input type="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="<%= CPathUIConfig.getWebUIBean().getWebApiVersion() %>"/>
    <input type="text" name="<%= ProtocolRequest.ARG_QUERY %>" size="15"/>
    <input type="submit" value="Search"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_FORMAT %>" value="<%= ProtocolConstants.FORMAT_HTML %>"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
        size="25" value='<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>'/>
    <input type="hidden" name="<%= entityName %>" value="<%= entityValue %>"/>
    </form>
    <p>To get started, enter a gene name or identifier in the text box above.</p>
    <p>To restrict your search to specific data sources or specific organisms, update your
    <a href="filter.do">global filter settings</a>.</p>
    </div>
<% } else { %>
    <img src="jsp/images/ismb_poster_2005.png" alt="Image of cPath ISMB 2005 Poster"/>
    <br/>Download cPath Poster from
    <a href="http://www.iscb.org/ismb2005/">ISMB 2005</a>.
    <br/>
    <a href="http://cbio.mskcc.org/dev_site/cpath_poster_2005.pdf">PDF Format</a> [9.5 MB]
<% } %>

<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) { %>
<div class="home_page_box">
<p>
<%= webUIBean.getApplicationName() %> currently contains the following data sources:</p>
<cbio:dataSourceListTable/>
</div>
<% } %>
</td>
<td valign="top">
    <div class="home_page_box">
    <jsp:include page="../../global/redesign/dbStatsMini.jsp" flush="true" />
    </div>

    <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) { %>
        <img src="jsp/images/ismb_poster_2005.png" alt="Image of cPath ISMB 2005 Poster"/>
        <br/>Download cPath Poster from
        <a href="http://www.iscb.org/ismb2005/">ISMB 2005</a>.
        <br/>
        <a href="http://cbio.mskcc.org/dev_site/cpath_poster_2005.pdf">PDF Format</a> [9.5 MB]
    <% } %>
    <p>This site currently contains PSI-MI Level 1
    data from March, 2007, available from
    <A href="http://www.ebi.ac.uk/intact/site/index.jsf">IntAct</A> and
    <A href="http://mint.bio.uniroma2.it/mint/Welcome.do">MINT</A>.</p>
</td>
</tr>
</table>
