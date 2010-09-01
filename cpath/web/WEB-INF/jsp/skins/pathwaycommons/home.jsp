<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
%>
<table cellspacing=0>
<tr valign="top">
<td width="60%" valign="top">

<div class="large_search_box">
<h1>Search <%= webUIBean.getApplicationName() %>:</h1>
<p>
<jsp:include page="../../global/redesign/homePageSearchBox.jsp" flush="true" />
</p>

</div>
<h2>What's New:</h2>
<ul>
<li><img src="jsp/images/new.jpg" alt="New!"/>&nbsp;&nbsp;September 7, 2010:
    <ul>
     <li>BioGRID data set (July 31, 2010 Version 30.0.67).</li>
      <li>HPRD data set (April 13, 2010 Version 9).</li>
      <li>HumanCyc data set (June 16, 2010 Version 14.1).</li>
      <li>IntAct data set (August 8, 2010 Version 3.1, r14760).</li>
      <li>MINT data set (July 28, 2010).</li>
      <li>NCI/Nature Pathway Interaction Database (August 10, 2010).</li>
      <li>Reactome data set (June 18, 2010 Version 33).</li>
  </ul>
</li>
<li>October 15, 2009:
    <ul>
        <li>Improved search functionality.</li>
    </ul>
</li>
<li>July 2, 2009:
    <ul>
        <li><a href="<%= webUIBean.getSnapshotDownloadBaseURL() %>">Batch Download</a> of all Pathway Commons data in multiple file formats is now available.</li>
        <li>Systems Biology Center New York - IMID data set (December 17, 2008 Version 27).</li>
        <li>Latest Reactome data set (June 24, 2009 Version 29).</li>
        <li>Latest HumanCyc data set (June 22, 2009 Version 13.1).</li>
        <li>All yeast proteins are now annotated with UniProt functional annotation.</li>
    </ul>
</li>
<li>March 1, 2009:
    <ul>
        <li>BioGRID data set (January 28, 2009 Version 2.0.49).</li>
        <li>Latest Reactome data set (December 17, 2008 Version 27).</li>
        <li>Latest HumanCyc data set (October 15, 2008 Version 12.5).</li>
        <li>Neighborhood maps added to protein pages.</li>
    </ul>
</li>
<li>July 24, 2008:
    <ul>
        <li>Latest Reactome data set (June 30, 2008 Version 25).</li>
        <li>All human, mouse and rat proteins are now annotated with UniProt functional annotation.</li>
        <li>Improved search support for gene symbols.</li>
        <li>Stable links now available for linking out to protein pages.</li>
    </ul>
</li>
<li>April 1, 2008: New features for visualizing pathways using Cytoscape [<a href="cytoscape.do">Details</a>].
<li>February 1, 2008:  New data sources, including:  HPRD, IntAct, and MINT.
Improved <a href="webservice.do?cmd=help">web service API</a>.</li>
</ul>

</td>
<td width=20>&nbsp;</td>
<td valign="top">

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


<h4>Current Data Sources:</h4>

<%= webUIBean.getApplicationName() %> currently contains the following data sources (<a href="<%= webUIBean.getSnapshotDownloadBaseURL() %>">batch download</a>):</p>
<% boolean renderForHomepage = true; %>
<cbio:dataSourceListTable renderForHomepage="<%= renderForHomepage %>"/>
<div class="home_page_box">
<jsp:include page="../../global/redesign/dbStatsMini.jsp" flush="true" />
</div>
<p>Integration of additional data sources is planned in the near future.  For a comprehensive directory
of interaction and pathway databases, please refer to <a href="http://www.pathguide.org">Pathguide</a>.</p>

</p>
</td>
</tr>
</table>
