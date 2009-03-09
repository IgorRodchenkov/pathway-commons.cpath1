<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
String entityValue = "ALL_ENTITY_TYPE";
String entityName = ProtocolRequest.ARG_ENTITY_TYPE;
String dataSourceName = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME;
String dataSourceValue = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL;

%>
<table cellspacing=0>
<tr valign="top">
<td width="60%" valign="top">

<div class="large_search_box">
<h1>Search <%= webUIBean.getApplicationName() %>:</h1>
<p>
<jsp:include page="../../global/redesign/homePageSearchBox.jsp" flush="true" />
</p>
<p>To get started, enter a gene name, gene identifier or pathway name in the text box above.
For example: <a href="webservice.do?version=2.0&q=BRCA1&format=html&cmd=get_by_keyword&<%= entityName %>=<%= entityValue %>&<%= dataSourceName %>=<%= dataSourceValue %>">BRCA1</a>,
<a href="webservice.do?version=2.0&q=P38398&format=html&cmd=get_by_keyword&<%= entityName %>=<%= entityValue %>&<%= dataSourceName %>=<%= dataSourceValue %>">P38398</a>
or  <a href="webservice.do?version=2.0&q=mtor&format=html&cmd=get_by_keyword&<%= entityName %>=<%= entityValue %>&<%= dataSourceName %>=<%= dataSourceValue %>">mTOR</a>.
</p>
<p>To restrict your search to specific data sources or specific organisms, update your
<a href="filter.do">global filter settings</a>.</p>
</div>
<h4>Using Pathway Commons:</h4>
<p><b>Biologists:</b>
Browse and search pathways across multiple valuable public pathway databases.
</p>
<p><b>Computational biologists:</b>
Download an integrated set of pathways in BioPAX format for global analysis.</p>
<p><b>Software developers:</b>
Build software on top of Pathway Commons using our <a href="webservice.do?cmd=help">web service API</a>.
Download and install the <a href="http://cbio.mskcc.org/dev_site/cpath/">cPath software</a> to
create a local mirror.</p>


<h2>What's New:</h2>
<ul>
<li><img src="jsp/images/new.jpg" alt="New!"/>&nbsp;&nbsp;March 1, 2009:
    <ul>
        <li>BioGRID data set added to repository (January 28, 2009 Version 2.0.49).</li>
        <li>Latest Reactome data set (December 17, 2008 Version 27).</li>
        <li>Latest HumanCyc data set (October 15, 2008 Version 12.5).</li>
        <li>Neighborhood maps added to protein pages.</li>
        <li>Numerous bug fixes and performance improvements.</li>
    </ul>
</li>
<li>July 24, 2008:
    <ul>
        <li>Latest Reactome data set (June 30, 2008 Version 25).</li>
        <li>All human, mouse and rat proteins are now annotated with UniProt functional annotation.</li>
        <li>Improved search support for gene symbols.</li>
        <li>Stable links now available for linking out to protein pages.</li>
        <li>Numerous bug fixes and performance improvements.</li>
    </ul>
</li>
<li>April 1, 2008: New features for visualizing pathways using Cytoscape [<a href="cytoscape.do">Details</a>].
<li>February 1, 2008:  New data sources, including:  HPRD, IntAct, and MINT.
Improved <a href="webservice.do?cmd=help">web service API</a>.</li>
</ul>

</td>
<td width=20>&nbsp;</td>
<td valign="top">

<h4>Current Data Sources:</h4>

<%= webUIBean.getApplicationName() %> currently contains the following data sources:</p>
<cbio:dataSourceListTable/>
<div class="home_page_box">
<jsp:include page="../../global/redesign/dbStatsMini.jsp" flush="true" />
</div>
<p>Integration of additional data sources is planned in the near future.  For a comprehensive directory
of interaction and pathway databases, please refer to <a href="http://www.pathguide.org">Pathguide</a>.</p>

</p>
</td>
</tr>
</table>
