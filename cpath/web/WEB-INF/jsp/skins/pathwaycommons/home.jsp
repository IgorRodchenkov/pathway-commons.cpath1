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
<li><img src="jsp/images/new.jpg" alt="New!"/>&nbsp;&nbsp;February 4, 2011:
   <ul>
     <li>Added MetaCyc data set (December 7, 2010 Version 14.6).</li>
     <li>BioGRID data set (December 15, 2010 Version 3.1.72).</li>
     <li>HumanCyc data set (October 7, 2010 Version 14.6).</li>
     <li>IntAct data set (December 15, 2010).</li>
     <li>Mint data set (December 21, 2010).</li>
     <li>Nature PID (September 16, 2010).</li>
     <li>Reactome data set (December 17, 2010 Version 35).</li>
  </ul>
</li>
<li>September 7, 2010:
  <ul>
    <li>BioGRID data set (July 31, 2010 Velsion 30.0.67).</li>
	<li>HPRD data set (April 13, 2010 Version 9).</li>
	<li>HumanCyc data set (June 16, 2010 Version 14.1).</li>
	<li>IntAct data set (August 8, 2010).</li>
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

<h4>Citing Pathway Commons:</h4>
<p><b>To cite the Pathway Commons Project:</b>
Cerami et al. Pathway Commons, a web resource for biological pathway data. Nucl. Acids Res. (2010)
<a href="http://nar.oxfordjournals.org/content/early/2010/11/10/nar.gkq1039.abstract">doi: 10.1093/nar/gkq1039</a></p>
<b>To cite the cPath Software:</b>
Cerami et al. cPath: open source software for collecting, storing, and querying biological pathways. BMC Bioinformatics. (2006)
<a href="http://www.biomedcentral.com/1471-2105/7/497/abstract">doi:10.1186/1471-2105-7-497</a></p> 
</p>
</td>
</tr>
</table>
