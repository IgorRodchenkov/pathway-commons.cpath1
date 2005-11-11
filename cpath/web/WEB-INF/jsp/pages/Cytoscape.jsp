<%@ page import="org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE,
        "cPath::Cytoscape PlugIn"); %>

<jsp:include page="../global/header.jsp" flush="true" />

<% // render the following content if we are in psi mode %>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI){ %>
<div id="apphead">
    <h2>cPath Cytoscape PlugIn</h2>
</div>

<h3>About Cytocape</h3>

<TABLE><TR><TD>
Cytoscape is an open source bioinformatics software platform for
visualizing molecular interaction networks and integrating these
interactions with gene expression profiles and other state data.
(Find out more at:  <A HREF="http://cytoscape.org/">cytoscape.org</A>).
</TD></TR></TABLE>

<div class="h3">
    <h3>About the cPath PlugIn</h3>
</div>

<TABLE><TR><TD>
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
</TD></TR></TABLE>

<h3>Download cPath PlugIn Beta 2</h3>
<TABLE><TR><TD>
    <UL>
    <LI>View the
    <A HREF="http://cbio.mskcc.org/cytoscape/cpath/beta2/README.txt">README.txt</A>
    File (includes Installation Instructions and Release Notes).
    <LI><A HREF="http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.tar.gz">
    Download Beta2 Version tar.gz</A> [2.7 MB]
    <LI><A HREF="http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.zip">
    Download Beta2 Version .zip</A> [2.7 MB]
    </UL>
</TD></TR></TABLE>

<h3>Bugs / Feature Requests</h3>

<TABLE><TR><TD>
If you encounter a bug with this PlugIn, or have a feature suggestion, we
encourage you to use the
<A HREF="http://www.cbio.mskcc.org/cytoscape/bugs">Cytoscape Bug Tracker</A>.
<P>
If you log a bug, we will automatically email you when the bug is resolved.
</div>
<% // render the following content if we are in psi mode %>
<% } else { %>
<div id="apphead">
    <h2>Ped Viewer Cytoscape PlugIn</h2>
</div>
<% } %>
</TD></TR></TABLE>

<h3>Contacts</h3>
<TABLE><TR><TD>
Sander Group, Computational Biology Center<BR>
Memorial Sloan-Kettering Cancer Center, New York City<BR>
<P>
For any questions concerning this PlugIn, please contact:
<P>
Gary Bader:  baderg AT mskcc.org
<BR>
Ethan Cerami:  cerami AT cbio.mskcc.org
<P>
This software is made available under the LGPL (Lesser General Public License).
<P>
This product includes software developed by the
<A HREF="http://www.apache.org">Apache Software Foundation</A>
</TD></TR></TABLE>
<jsp:include page="../global/footer.jsp" flush="true" />