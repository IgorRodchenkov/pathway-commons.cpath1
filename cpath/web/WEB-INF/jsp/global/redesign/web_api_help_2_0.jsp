<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion2"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.util.ExternalDatabaseConstants"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<h1>Introduction to Web Service API:</h1>
<p>
If you wish to programmatically access pathway data, you can do so via the Web Service API.
This page provides a quick reference guide to help you get started.
</p>

<h1>Command:  <%= ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST %></h1>

<h2>Summary:</h2>

Retrieves all pathways involving a specified physical entity (e.g. gene, protein or small molecule).
Output of this command is a tab-delimited text file.

<h2>Parameters:</h2>

<ul>
<li>[Required] <%= ProtocolRequest.ARG_COMMAND%>=<%= ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST %></li>
<li>[Required] <%= ProtocolRequest.ARG_VERSION%>=<%= ProtocolConstantsVersion2.VERSION_2 %></li>
<li>[Required] <%= ProtocolRequest.ARG_QUERY%>= external identifier, used to identify the physical entity of interest.
For example, O14763.</li>
<li>[Required] <%= ProtocolRequest.ARG_INPUT_ID_TYPE %>= external database name.  For example:
<%= ExternalDatabaseConstants.UNIPROT %>.  See the <a href=#valid_input_id_type>valid values for
<%= ProtocolRequest.ARG_INPUT_ID_TYPE %> parameter</a> below.</li>
<li>[Optional] <%= ProtocolRequest.ARG_DATA_SOURCE %> = a comma separated list of pathway data sources that you want
to search.  For example, the following restricts your results to Reactome pathways only.
<%= ProtocolRequest.ARG_DATA_SOURCE %>=<%=ExternalDatabaseConstants.REACTOME %>.
See the <a href=#valid_data_source>valid values for <%= ProtocolRequest.ARG_DATA_SOURCE %> parameter</a> below.
If not specified, all pathway data sources will be searched.</li>
</ul>

<h2>Output:</h2>

Output of this command is a tab-delimited text file with four columns of data:

<ul>
<li>Database:ID:  External database identifier.  For example, <%= ExternalDatabaseConstants.UNIPROT %>:O14763.</li>
<li>Pathway_Name:  Pathway name.</li>
<li>Pathway_Database_Name:  Pathway database name.  For example, <%=ExternalDatabaseConstants.REACTOME %>.</li>
<li>Internal_ID:  Internal ID, used to uniquely identify the pathway.  Please note that these internal
IDs are <b>not</b> stable, and may change after each new release of data.
</li>
</ul>

<h2>Example Query:</h2>

Below is an example query.  Note that this query is not guaranteed to return results.

<a href="webservice.do?cmd=get_pathway_list&version=2.0&q=O14763&input_id_type=<%= ExternalDatabaseConstants.UNIPROT%>">
webservice.do?cmd=get_pathway_list&version=2.0&q=O14763&input_id_type=<%= ExternalDatabaseConstants.UNIPROT %>
</a>

<h1>Additional Parameter Details:</h1>

<h2><a name='valid_input_id_type'></a>Valid values for the <%= ProtocolRequest.ARG_INPUT_ID_TYPE %> parameter:</h2>
<ul>
<%
    WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
    ArrayList <String> supportedIdTypes = webUIBean.getSupportedInputIdTypes();
    if (supportedIdTypes != null && supportedIdTypes.size() > 0) {
        for (int i=0; i<supportedIdTypes.size(); i++) {
            out.println("<LI>" + supportedIdTypes.get(i) + "</LI>");
        }
    } else {
        out.println("<LI>None specified.</LI>");
    }
%>
</ul>

<h2><a name='valid_data_source'></a>Valid values for the <%= ProtocolRequest.ARG_DATA_SOURCE %> parameter:</h2>
<ul>
<%
    DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
    ArrayList list = dao.getAllDatabaseSnapshots();
    for (int i=0; i<list.size(); i++) {
        ExternalDatabaseSnapshotRecord snapshotRecord = (ExternalDatabaseSnapshotRecord)
                list.get(i);
        String masterTerm = snapshotRecord.getExternalDatabase().getMasterTerm();
        out.println("<li>" + masterTerm + "</li>");
    }
%>
</ul>

