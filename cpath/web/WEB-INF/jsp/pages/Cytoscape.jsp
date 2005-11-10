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

<%
	// if we are in psi mode print the following content
    if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI){
        out.println("<div id=\"apphead\">");
        out.println("    <h2>cPath Cytoscape PlugIn</h2>");
        out.println("</div>");
        out.println("");
        out.println("<div class=\"h3\">");
        out.println("    <h3>About Cytocape</h3>");
        out.println("</div>");
        out.println("");
        out.println("<div>");
        out.println("Cytoscape is an open source bioinformatics software platform for");
        out.println("visualizing molecular interaction networks and integrating these");
        out.println("interactions with gene expression profiles and other state data.");
        out.println("(Find out more at:  <A HREF=\"http://cytoscape.org/\">cytoscape.org</A>).");
        out.println("<P>");
        out.println("</div>");
        out.println("");
        out.println("<div class=\"h3\">");
        out.println("    <h3>About the cPath PlugIn</h3>");
        out.println("</div>");
        out.println("");
        out.println("<div>");
        out.println("<div>");
        out.println("Cytoscape includes a built-in PlugIn framework for adding new features and");
        out.println("functionality.  The cPath PlugIn enables Cytoscape users to directly query,");
        out.println("retrieve and visualize interactions retrieved from the cPath database.");
        out.println("");
        out.println("<p><B>Note:</B>  The cPath PlugIn currently only works with");
        out.println("<A HREF=\"http://www.cytoscape.org/download.php?file=cyto2\">Cytoscape 2.0</A>.");
        out.println("It will not work with earlier versions of Cytoscape.");
        out.println("");
        out.println("<P>A sample screenshot of the cPath PlugIn Beta2 in action is shown below:");
        out.println("");
        out.println("<P>");
        out.println("<A HREF=\"jsp/images/plugin/plugin_beta2.png\">");
        out.println("<IMG TITLE=\"Click to View Full Image\"");
        out.println("    SRC=\"jsp/images/plugin/plugin_beta2_thumb.png\" BORDER=0>");
        out.println("</A>");
        out.println("</div>");
        out.println("");
        out.println("");
        out.println("<div class=\"h3\">");
        out.println("    <h3>Download cPath PlugIn Beta 2</h3>");
        out.println("</div>");
        out.println("<DIV>");
        out.println("    <UL>");
        out.println("    <LI>View the");
        out.println("    <A HREF=\"http://cbio.mskcc.org/cytoscape/cpath/beta2/README.txt\">README.txt</A>");
        out.println("    File (includes Installation Instructions and Release Notes).");
        out.println("    <LI><A HREF=\"http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.tar.gz\">");
        out.println("    Download Beta2 Version tar.gz</A> [2.7 MB]");
        out.println("    <LI><A HREF=\"http://cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.zip\">");
        out.println("    Download Beta2 Version .zip</A> [2.7 MB]");
        out.println("    </UL>");
        out.println("</DIV>");
        out.println("");
        out.println("<div class=\"h3\">");
        out.println("    <h3>Bugs / Feature Requests</h3>");
        out.println("</div>");
        out.println("<div>");
        out.println("If you encounter a bug with this PlugIn, or have a feature suggestion, we");
        out.println("encourage you to use the");
        out.println("<A HREF=\"http://www.cbio.mskcc.org/cytoscape/bugs\">Cytoscape Bug Tracker</A>.");
        out.println("<P>");
        out.println("If you log a bug, we will automatically email you when the bug is resolved.");
        out.println("</div>");
        out.println("");
        out.println("<div class=\"h3\">");
        out.println("    <h3>Contacts</h3>");
        out.println("</div>");
        out.println("<div>");
        out.println("<P>");
        out.println("Sander Group, Computational Biology Center<BR>");
        out.println("Memorial Sloan-Kettering Cancer Center, New York City<BR>");
        out.println("<P>");
        out.println("For any questions concerning this PlugIn, please contact:");
        out.println("<P>");
        out.println("Gary Bader:  baderg AT mskcc.org");
        out.println("<BR>");
        out.println("Ethan Cerami:  cerami AT cbio.mskcc.org");
        out.println("<P>");
        out.println("This software is made available under the LGPL (Lesser General Public License).");
        out.println("<P>");
        out.println("This product includes software developed by the");
        out.println("<A HREF=\"http://www.apache.org\">Apache Software Foundation</A>");
        out.println("</div>");
    }
	// we are in biopax mode, print the following content
	else{
        out.println("<div id=\"apphead\">");
        out.println("    <h2>cPath Cytoscape PlugIn (BioPax)</h2>");
        out.println("</div>");
    }
%>
<jsp:include page="../global/footer.jsp" flush="true" />