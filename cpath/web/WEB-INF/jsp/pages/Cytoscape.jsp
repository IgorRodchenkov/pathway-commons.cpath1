<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Cytoscape PlugIn"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<% // render the following content if we are in psi mode %>
<div>

<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI){ %>
<a href="jsp/images/plugin/cpath/cpath_plugin.png">
<img align=right title="Click to View Full Image"
    src="jsp/images/plugin/cpath/cpath_plugin_thumb.png" alt="Screenshot of Cytoscape cPath PlugIn" border="0"/>
</a>
<h1>Cytoscape cPath PlugIn</h1>
<p>
<a href="http://cytoscape.org/">Cytoscape</a>
is an open source bioinformatics software platform for visualizing molecular interaction networks
and integrating these interactions with gene expression profiles and other state data.
</p>
<p>
The cPath PlugIn (which is automatically bundled with Cytoscape 2.4) enables Cytoscape
users to directly query, retrieve and visualize interactions retrieved from the cPath database.

<h1>How to use the Cytoscape cPath PlugIn</h1>
<ul>
    <li>Download and install the latest version of Cytoscape from the main <a href="http://cytoscape.org/">Cytoscape web site</a>.</li>
    <li>From the main Cytoscape menu, select:  File &rarr; New &rarr; Network &rarr; Construct network using cPath.
    <img title="Click to View Full Image"
    src="jsp/images/plugin/cpath/cpath_howto.png" alt="Creating a network from cPath" border="0"/></li>
    <li>Enter your search criteria, e.g. "p53".  Then, click the "Search" button.</li>
</ul>

<% // render the following content if we are in cellmap mode %>
<%} else { %>
<h1>View Human Affymetrix gene expression data on Cancer Cell Map 
pathways</h1>
<ol>
<li><b><a href="jsp/cytoscape/cy1.jnlp">Click to start the Expression 
Viewer software</a></b> (requires <a href="http://java.sun.com/docs/books/tutorial/information/javawebstart.html">Java Web Start</a>)</li>
<li>Select the Cancer Cell Map pathway you want to view</li>
<li>Load your Human Affymetrix gene expression data. Note: this requires 
your data to conform to a specific, but easy to create format. <a
href="jsp/cytoscape/expressionData/sample.pvals">See a sample</a> (right click to download and be sure to save with .pvals extension).</li>
<li><a href="jsp/cytoscape/README.html">More details...</a></li>
</ol>

<table width="576">
<tr><td>
<a href="jsp/images/plugin/expression_viewer.png">
<img title="Click to View Full Image"
     alt="Cytoscape Screenshot" src="jsp/images/plugin/expression_viewer_thumb.png" border="0"/>
</a>
</td></tr>
<tr><td>
<small>(screenshot depicts Cytoscape running with the Expression Viewer Plugin.
  The KitReceptor pathway has been loaded and overlaid with expression data.  Red circles
 represent highly upregulated genes - an indication of unusually high mRNA levels)</small>
</td></tr>
</table>
<br/>
<h1>About the Cytoscape and the Expression Viewer Cytoscape Plugin</h1>
<p>
Cytoscape is an open source bioinformatics software platform for
visualizing molecular interaction networks and integrating these
interactions with gene expression profiles and other state data.
(Find out more at:  <a href="http://cytoscape.org/">cytoscape.org</a>).
</p>
<p>Cytoscape includes a built-in plugin framework for adding new features
and functionality.  The Expression Viewer plugin enables Cytoscape users
to visualize expression data in the context of cPath Pathways.
</p>

<p><b>Note:</b> The Expression Viewer plugin currently only works with
Cytoscape 2.2, or later. It will not work with earlier versions of 
Cytoscape.
</p>
<% } %>

<h1>Bugs / Feature Requests</h1>
<p>
If you encounter a bug with this PlugIn, or have a feature suggestion, we
encourage you to use the
<a href="http://www.cbio.mskcc.org/cytoscape/bugs">Cytoscape Bug Tracker</a>.
</p>
<p>
If you log a bug, we will automatically email you when the bug is resolved.
</p>

<h1>Contacts</h1>
<p>
<a href="http://cbio.mskcc.org/people/info/ethan_cerami.html">Ethan Cerami</a><br/>
<a href="http://cbio.mskcc.org/people/info/benjamin_gross.html">Benjamin Gross</a>
<br>
Computational Biology Center
<br/>Memorial Sloan-Kettering Cancer Center, New York City<br/>
</p>
<p>
<h1>License / Credits</h1>
This software is made available under the LGPL (Lesser General Public License).
</p>
<p>This product includes software developed by the
<a href="http://www.apache.org">Apache Software Foundation</a>
</p>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />