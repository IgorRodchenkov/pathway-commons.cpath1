<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
String entityValue =  "pathway";
String entityName = GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME;
String dataSourceName = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME;
String dataSourceValue = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL;

%>
<table cellspacing=20>
<tr valign="top">
<td width="60%">
<div>
Pathway Commons is a convenient point of access to biological pathway information collected
from public pathway databases, which you can browse or search. Pathways include biochemical reactions,
complex assembly, transport and catalysis events, and physical interactions involving proteins, DNA,
RNA, small molecules and complexes. <a href="about.do">more...</a>
</div>
<div class="large_search_box">
<h1>Search <%= webUIBean.getApplicationName() %>:</h1>
<p>
<jsp:include page="../../global/redesign/homePageSearchBox.jsp" flush="true" />
</p>
<p>To get started, enter a gene name, gene identifier or pathway name in the text box above.
For example: <a href="webservice.do?version=1.0&q=p53&format=html&cmd=get_by_keyword&<%= entityName %>=<%= entityValue %>&<%= dataSourceName %>=<%= dataSourceValue %>">p53</a>,
<a href="webservice.do?version=1.0&q=P38398&format=html&cmd=get_by_keyword&<%= entityName %>=<%= entityValue %>&<%= dataSourceName %>=<%= dataSourceValue %>">P38398</a>
or  <a href="webservice.do?version=1.0&q=mtor&format=html&cmd=get_by_keyword&<%= entityName %>=<%= entityValue %>&<%= dataSourceName %>=<%= dataSourceValue %>">mTOR</a>.
</p>
<p>To restrict your search to specific data sources or specific organisms, update your
<a href="filter.do">global filter settings</a>.</p>
</div>
<p><b>Biologists:</b>
Browse and search pathways across multiple valuable public pathway databases.
</p>
<p><b>Computational biologists:</b>
Download an integrated set of pathways in BioPAX format for global analysis.</p>
<p><b>Software developers:</b>
Build software on top of Pathway Commons using our soon-to-be released web service API.
Download and install the <a href="http://cbio.mskcc.org/dev_site/cpath/">cPath software</a> to
create a local mirror.</p>
</td>
<td valign="top">
<%= webUIBean.getApplicationName() %> currently contains the following data sources:</p>
<cbio:dataSourceListTable/>
<div class="home_page_box">
<jsp:include page="../../global/redesign/dbStatsMini.jsp" flush="true" />
</div>
<p>Integration of additional data sources is planned in the near future.  For a comprehensive directory
of interaction and pathway databases, please refer to <a href="http://www.pathguide.org">Pathguide</a>.</p>
</td>
</tr>
</table>
