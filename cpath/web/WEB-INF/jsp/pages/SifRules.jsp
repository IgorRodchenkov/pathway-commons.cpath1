<%@ page import="org.mskcc.pathdb.action.BaseAction,org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.action.web_api.binary_interaction_mode.ExecuteBinaryInteraction" %>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest" %>
<%@ page import="java.util.List" %>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion2" %>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants" %>
<%@ page import="org.mskcc.pathdb.form.WebUIBean" %>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion1" %>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "SIF Interaction Rules");
List<String> binaryInteractionRules = ExecuteBinaryInteraction.getRuleTypesForDisplay();
WebUIBean webUiBean = CPathUIConfig.getWebUIBean();
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<div>
<h1>Exporting to the Simple Interaction Format (SIF)</h1>

<h2>Introduction:</h2>
By default, all interaction networks and pathways in <%= webUiBean.getApplicationName()%>  are
stored in the <a href="http://www.biopax.org">BioPAX</A> exchange format.
However, a number of <a href="webservice.do?cmd=help">web service commands</a>, including
<a href="webservice.do?cmd=help#get_neighbors"><%= ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS %></a> and
<a href="webservice.do?cmd=help#get_by_cpath_id"><%= ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID %></a>,
support the export of interaction networks to the Simple Interaction Format (SIF).  SIF was
originally created for use with <a href="http://www.cytoscape.org">Cytoscape</a>, the open source
bioinformatics software platform for visualizing molecular interaction networks.  SIF is simple to parse,
and easy to load into Cytoscape and other third-party applications.

<h2>SIF Format:</h2>

The SIF export from <%= webUiBean.getApplicationName()%> is formatted as:
<br><br>
<pre>
physical_entity_id &lt;relationship type&gt; physical_entity_id
</pre>
<br>
where physical_entity_id is a valid primary ID in <%= webUiBean.getApplicationName()%>,
and &lt;relationship type&gt; is one of the interaction inference rules specified below.

<h2>Inference Rules:</h2>

It is not possible to fully translate a BioPAX network (which is capable of storing rich biological
semantics) into a SIF binary network (which is only capable of storing pairwise interactions between physical
entities).  We therefore use an inference engine that examines a BioPAX network, applies specific
inference rules, and extracts relevant pairwise interactions.  By design, the translation
from BioPAX to SIF will therefore always result in the loss of information.  Nonetheless,
this SIF network remains useful for certain types of bioinformatic applications, and is much
easier to use than BioPAX.
<br><br>
For those commands that support SIF export, users can specify an optional
<%= ProtocolRequest.ARG_BINARY_INTERACTION_RULE %> argument.  This is a comma separated list
of inference rules (see table below) that are applied when binary interactions are requested.
If not specified, all binary interaction rules will be applied.
<br><br>
<div>
    <table border=0 cellspacing=0 cellpadding=5>
        <tr>
            <th><%= ProtocolRequest.ARG_BINARY_INTERACTION_RULE %></th>
            <th>Original BioPAX Interaction</th>
            <th>Inferred Set of Pairwise Interaction</th>
        </tr>
        <%
            int i = 0;
            for (String rule : binaryInteractionRules) {
                String ruleDesc = ExecuteBinaryInteraction.getRuleTypeDescription(rule);
                out.println("<tr>");
                out.println("<td valign=top>");
                out.println("<b>" + rule + "</b>:  ");
                out.println(ruleDesc + "</td>");
                out.println("<td valign=top align=center><img src='jsp/images/sif_rules/" + rule + "_BP.png'/>");
                out.println("<td valign=top align=center><img src='jsp/images/sif_rules/" + rule + "_SIF.png'/>");
                out.println("</tr>");
                out.println("<tr><td colspan=3><hr></td></tr>");
            }
        %>
    </table>
</div>

<h2>Legend</h2>
    
<img src="jsp/images/sif_rules/BP_LEGEND.png"/>

    <h2>Retrieving Physical Entity Details:</h2>

    In the SIF export defined above, note that physical entities are identified with physical_entity_id(s),
    and not with standard gene names or external identifiers.  To retrieve the identity of each physical
    entity, you must make an additional call back to <%= webUiBean.getApplicationName()%> and use the
    <a href="webservice.do?cmd=help#get_by_cpath_id"><%= ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID %></a>.
    In this case, you specify one or more internal IDs, and set <%= ProtocolRequest.ARG_OUTPUT%>=<%=ProtocolConstantsVersion1.FORMAT_BIO_PAX%>.
    This enables you to retrieve the full BioPAX for the physical entity only.

<p></p>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />