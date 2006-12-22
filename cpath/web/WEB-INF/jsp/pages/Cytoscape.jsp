<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Cytoscape PlugIn"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<% // render the following content if we are in psi mode %>
<div>

<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI){ %>

<h1>About the cPath PlugIn</h1>

Cytoscape includes a built-in PlugIn framework for adding new features and
functionality.  The cPath PlugIn enables Cytoscape users to directly query,
retrieve and visualize interactions retrieved from the cPath database.

<p><b>Note:</b>  The cPath PlugIn currently only works with
Cytoscape 2.1, or later. It will not work with earlier versions of Cytoscape.

<p>A sample screenshot of the cPath PlugIn Beta2 in action is shown below:

<p>
<a href="jsp/images/plugin/plugin_beta2.png">
<img title="Click to View Full Image"
    src="jsp/images/plugin/plugin_beta2_thumb.png" alt="Screenshot of Cytoscape PlugIn" border="0"/>
</A>

<h1>Download cPath PlugIn Beta 2</h1>
<ul>
    <li>View the
    <a href="http://cbio.mskcc.org/cytoscape/cpath/beta2/README.txt">README.txt</a>
    File (includes Installation Instructions and Release Notes).</li>
    <li><a href="http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.tar.gz">
    Download Beta2 Version tar.gz</a> [2.7 MB]</li>
    <li><a href="http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.zip">
    Download Beta2 Version .zip</a> [2.7 MB]</li>
</ul>

<h1>About Cytoscape</h1>

Cytoscape is an open source bioinformatics software platform for
visualizing molecular interaction networks and integrating these
interactions with gene expression profiles and other state data.
(Find out more at:  <a href="http://cytoscape.org/">cytoscape.org</a>).

<% // render the following content if we are in psi mode %>
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
Sander Group, Computational Biology Center<br/>
Memorial Sloan-Kettering Cancer Center, New York City<br/>
</p>
<p>
For any questions concerning this PlugIn, please contact:
</p>
<img src="jsp/images/emailimage.jpg" alt="Email image" border="0"/>
<p>
This software is made available under the LGPL (Lesser General Public License).
</p>
<p>This product includes software developed by the
<a href="http://www.apache.org">Apache Software Foundation</a>
</p>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />