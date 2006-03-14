<%@ page import="org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Cytoscape PlugIn";
	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>

<jsp:include page="../global/header.jsp" flush="true" />

<% // render the following content if we are in psi mode %>
<div id="content">

<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI){ %>

<h1>About the cPath PlugIn</h1>

Cytoscape includes a built-in PlugIn framework for adding new features and
functionality.  The cPath PlugIn enables Cytoscape users to directly query,
retrieve and visualize interactions retrieved from the cPath database.

<p><B>Note:</B>  The cPath PlugIn currently only works with
Cytoscape 2.1, or later. It will not work with earlier versions of Cytoscape.

<P>A sample screenshot of the cPath PlugIn Beta2 in action is shown below:

<P>
<A HREF="jsp/images/plugin/plugin_beta2.png">
<IMG TITLE="Click to View Full Image"
    SRC="jsp/images/plugin/plugin_beta2_thumb.png" BORDER=0>
</A>

<h1>Download cPath PlugIn Beta 2</h1>
<UL>
    <LI>View the
    <A HREF="http://cbio.mskcc.org/cytoscape/cpath/beta2/README.txt">README.txt</A>
    File (includes Installation Instructions and Release Notes).
    <LI><A HREF="http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.tar.gz">
    Download Beta2 Version tar.gz</A> [2.7 MB]
    <LI><A HREF="http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.zip">
    Download Beta2 Version .zip</A> [2.7 MB]
</UL>

<h1>About Cytoscape</h1>

Cytoscape is an open source bioinformatics software platform for
visualizing molecular interaction networks and integrating these
interactions with gene expression profiles and other state data.
(Find out more at:  <A HREF="http://cytoscape.org/">cytoscape.org</A>).

<% // render the following content if we are in psi mode %>
<%} else { %>
<h1>View Human Affymetrix gene expression data on Cancer Cell Map 
pathways in 3 easy steps</h1>
<ol>
<li><b><a href="jsp/cytoscape/cy1.jnlp">Click to start the Expression 
Viewer software</a></b> (requires <a href="http://java.sun.com/docs/books/tutorial/information/javawebstart.html">Java Web Start</a>)
<li>Select the Cancer Cell Map pathway you want to view
<li>Load your Human Affymetrix gene expression data. Note: this requires 
your data to conform to a specific, but easy to create format. <A 
HREF="jsp/cytoscape/expressionData/sample.pvals">See a sample.</A>
</ol>

<a href="jsp/cytoscape/README.html"><h1>More details...</h1></a>

<h1>Screenshot</h1>
<A HREF="jsp/images/plugin/expression_viewer.png">
<IMG TITLE="Click to View Full Image"
     SRC="jsp/images/plugin/expression_viewer_thumb.png" BORDER=0>
</A>

<h1>About the Cytoscape and the Expression Viewer Cytoscape Plugin</h1>

Cytoscape is an open source bioinformatics software platform for
visualizing molecular interaction networks and integrating these
interactions with gene expression profiles and other state data.
(Find out more at:  <A HREF="http://cytoscape.org/">cytoscape.org</A>).

Cytoscape includes a built-in plugin framework for adding new features 
and functionality.  The Expression Viewer plugin enables Cytoscape users
to visualize expression data in the context of cPath Pathways.

<p><B>Note:</B> The Expression Viewer plugin currently only works with
Cytoscape 2.2, or later. It will not work with earlier versions of 
Cytoscape.
<% } %>

<h1>Bugs / Feature Requests</h1>

If you encounter a bug with this PlugIn, or have a feature suggestion, we
encourage you to use the
<A HREF="http://www.cbio.mskcc.org/cytoscape/bugs">Cytoscape Bug Tracker</A>.
<P>
If you log a bug, we will automatically email you when the bug is resolved.

<h1>Contacts</h1>
<P>
Sander Group, Computational Biology Center<BR>
Memorial Sloan-Kettering Cancer Center, New York City<BR>
</P>
<P>
For any questions concerning this PlugIn, please contact:
</P>
Gary Bader:  baderg AT mskcc.org
<BR/>
Ethan Cerami:  cerami AT cbio.mskcc.org
<BR/>
Benjamin Gross:  grossb AT cbio.mskcc.org
<P>
This software is made available under the LGPL (Lesser General Public License).
</P>
This product includes software developed by the
<A HREF="http://www.apache.org">Apache Software Foundation</A>
</div>
<jsp:include page="../global/footer.jsp" flush="true" />