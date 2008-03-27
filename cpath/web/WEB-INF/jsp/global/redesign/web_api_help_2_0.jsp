<%@ page import="org.mskcc.pathdb.sql.query.GetNeighborsCommand"%>
<%@ page import="org.mskcc.pathdb.util.ExternalDatabaseConstants"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.query.batch.PathwayBatchQuery"%>
<%@ page import="org.mskcc.pathdb.action.web_api.binary_interaction_mode.ExecuteBinaryInteraction"%>
<%@ page import="org.mskcc.pathdb.protocol.*"%>
<%
	// setup some globals
    WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
    ArrayList <String> supportedIdTypes = webUIBean.getSupportedIdTypes();
    DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
    ArrayList snapshotList = dao.getAllDatabaseSnapshots();
%>
<h1>Web Service API:</h1>
<p>
You can programmatically access pathway data via the Web Service API.
This page provides a reference guide to help you get started.

<ul>
<li><a href="#search">[1] Command:  <%= ProtocolConstantsVersion2.COMMAND_SEARCH %></a></li>
<li><a href="#get_pathways">[2] Command:  <%= ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST %></a</li>
<li><a href="#get_neighbors">[3] Command:  <%= ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS %></a></li>
<li><a href="#get_parents">[4]  Command:  <%= ProtocolConstantsVersion2.COMMAND_GET_PARENT_SUMMARIES %></a></li>
<li><a href="#get_by_cpath_id">[5] Command:  <%= ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID %></a></li>
<li><a href="#additional_params">[6] Additional Parameter Details</a></li>
<li><a href="#errors">[7] Error Codes</a></li>
</ul>

<h2><a NAME="search"></a>[1]  Command:  <%= ProtocolConstantsVersion2.COMMAND_SEARCH %></h2>

<h3>Summary:</h3>

Searches all physical entity records (e.g. proteins and small molecules) by keyword, name or
external identifier.  For example, retrieve a list of all physical entity records that contain the
word, "BRCA2".

<% if (CPathUIConfig.getWebSkin().equalsIgnoreCase("pathwaycommons")) { %>
This command enables third-party software to implement direct query interfaces to Pathway Commons.
<% } %>

<h3>Parameters:</h3>

<ul>
<li>[Required] <%= ProtocolRequest.ARG_COMMAND%>=<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %></li>
<li>[Required] <%= ProtocolRequest.ARG_VERSION%>=<%= ProtocolConstantsVersion2.VERSION_2 %></li>
<li>[Required] <%= ProtocolRequest.ARG_QUERY%>= a keyword, name or external identifier.</li>
<li>[Required] <%= ProtocolRequest.ARG_OUTPUT%> = <%=ProtocolConstantsVersion1.FORMAT_XML%>
<li>[Optional] <%= ProtocolRequest.ARG_ORGANISM %> = organism filter.  Must be specified as an
<a href="http://www.ncbi.nlm.nih.gov/Taxonomy/">NCBI Taxonomy</a> identifier, e.g. 9606 for human.</li>
</ul>

<h3>Output:</h3>

An XML file which follows the <a href="xml/SearchResponse.xsd">SearchResponse.xsd</a> XML Schema
[<a href="xml/search_response/SearchResponse.xsd.html">Full Documentation</a>].

<h3>Example Query:</h3>

Below is an example query.  Note: this query is not guaranteed to return results.<br/>

<a href="webservice.do?version=2.0&q=BRCA2&format=xml&cmd=search">
webservice.do?version=2.0&q=BRCA2&output=xml&cmd=search
</a>

<h2><a NAME="get_pathways"></a>[2]  Command:  <%= ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST %></h2>

<h3>Summary:</h3>

Retrieves all pathways involving a specified physical entity (e.g. protein or small molecule).
For example, get all pathways involving BRCA2.  Output is a tab-delimited text file,
designed for easy parsing in your favorite scripting language, such as Perl or Python.

<h3>Parameters:</h3>

<ul>
<li>[Required] <%= ProtocolRequest.ARG_COMMAND%>=<%= ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST %></li>
<li>[Required] <%= ProtocolRequest.ARG_VERSION%>=<%= ProtocolConstantsVersion2.VERSION_2 %></li>
<li>[Required] <%= ProtocolRequest.ARG_QUERY%>= a comma separated list of internal or external
identifiers (IDs), used to identify the physical entities of interest. For example, look up two distinct proteins by
their UniProt IDs using the following query: O14763, P55957.  To prevent system overload, clients
are currently restricted to a maximum of <%= ProtocolConstantsVersion2.MAX_NUM_IDS %>
IDs.</li>
<li>[Optional] <%= ProtocolRequest.ARG_INPUT_ID_TYPE %>= internal or external database.  For example,
to use UniProt IDs, set <%= ProtocolRequest.ARG_INPUT_ID_TYPE %>=<%= ExternalDatabaseConstants.UNIPROT %>.
See the <a href=#valid_input_id_type>valid values for
<%= ProtocolRequest.ARG_INPUT_ID_TYPE %> parameter</a> below.  If not specified, the internal
<%= ExternalDatabaseConstants.INTERNAL_DATABASE%> is assumed.</li>
<li>[Optional] <%= ProtocolRequest.ARG_DATA_SOURCE %> = a comma separated list of pathway data sources
to search.  For example, the following restricts your results to Reactome pathways only:
<%= ProtocolRequest.ARG_DATA_SOURCE %>=<%=ExternalDatabaseConstants.REACTOME %>.
See the <a href=#valid_data_source>valid values for <%= ProtocolRequest.ARG_DATA_SOURCE %> parameter</a> below.
If not specified, all pathway data sources will be searched.</li>
</ul>

<h3>Output:</h3>

Output is a tab-delimited text file with four data columns:

<ul>
<li>Database:ID:  External database identifier.  For example, <%= ExternalDatabaseConstants.UNIPROT %>:O14763.</li>
<li>Pathway_Name:  Pathway name.</li>
<li>Pathway_Database_Name:  Pathway database name.  For example, <%=ExternalDatabaseConstants.REACTOME %>.</li>
<li><%=ExternalDatabaseConstants.INTERNAL_DATABASE%>:  Internal ID, used to uniquely identify the pathway.  These IDs can be used to
create links to each pathway.  For example:  <a href="record2.do?id=1">record2.do?id=1</a>.
However, please note that these internal IDs (and any links created with them) are
<b>not</b> stable, and may change with each new data release.
</li>
</ul>

<h3>Detecting matches:</h3>

<ul>
<li>If a specified identifier can't be found, the second column
of the tab-delimited text file will contain the keyword:
<%= PathwayBatchQuery.ERROR_MSG_NO_MATCH_TO_PHYSICAL_ENTITY%>.</li>
<li>On the other hand, if a match is found for a specified
identifier, but the corresponding physical entity is not involved in any known pathways
or in any of the requested pathway databases, the second
column of the tab-delimited text file will contain the keyword:
<%= PathwayBatchQuery.ERROR_MSG_NO_PATHWAY_DATA%>.
</li>
</ul>

<h3>Example Query:</h3>

Below is an example query.  Note: this query is not guaranteed to return results. <br/>

<a href="webservice.do?cmd=get_pathways&version=2.0&q=O14763&input_id_type=<%= ExternalDatabaseConstants.UNIPROT%>">
webservice.do?cmd=get_pathways&version=2.0&q=O14763&input_id_type=<%= ExternalDatabaseConstants.UNIPROT %>
</a>

<h2><a NAME="get_neighbors"></a>[3]  Command:  <%= ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS %></h2>

<h3>Summary:</h3>

Retrieves the nearest neighbors of a given physical entity (e.g. gene, protein or small molecule).
For example, get all the neighbors of BRCA2.

<h3><a name='get_neighbors_parameters'></a>Parameters:</h3>

<ul>
<li>[Required] <%= ProtocolRequest.ARG_COMMAND%> = <%= ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS %></li>
<li>[Required] <%= ProtocolRequest.ARG_VERSION%> = <%= ProtocolConstantsVersion2.VERSION_2 %></li>
<li>[Required] <%= ProtocolRequest.ARG_QUERY%> = an internal or external identifier (ID), corresponding to
the physical entity of interest. For example, the following query uses a UniProt identifier: O14763.</li>
<li>[Optional] <%= ProtocolRequest.ARG_INPUT_ID_TYPE %>= internal or external database.  For example,
    to use UniProt IDs, set <%= ProtocolRequest.ARG_INPUT_ID_TYPE %>=<%= ExternalDatabaseConstants.UNIPROT %>.
    See the <a href=#valid_input_id_type>valid values for
    <%= ProtocolRequest.ARG_INPUT_ID_TYPE %> parameter</a> below.  If not specified, the internal
    <%= ExternalDatabaseConstants.INTERNAL_DATABASE%> is assumed.</li>
<li>[Optional] <%= ProtocolRequest.ARG_OUTPUT%> = <%=ProtocolConstantsVersion1.FORMAT_BIO_PAX%> (default) or
<%=ProtocolConstantsVersion2.FORMAT_ID_LIST%> or <%=ProtocolConstantsVersion2.FORMAT_BINARY_SIF%>.  When set to <%=ProtocolConstantsVersion1.FORMAT_BIO_PAX%>, the client will receive a complete BioPAX representation of the neighborhood.  When set to <%=ProtocolConstantsVersion2.FORMAT_ID_LIST%>, the client will receive a simple text file that lists all the physical entities in the neighborhood.  When set to <%=ProtocolConstantsVersion2.FORMAT_BINARY_SIF%>, the client will receive a simple text file that lists all the interactions in the neighborhood in the following simple interaction format: <b>physical_entity_id &lt;relationship type&gt; physical_entity_id</b>, where physical_entity_id is a valid CPATH_ID.</li>
<li>[Optional] <%= ProtocolRequest.ARG_OUTPUT_ID_TYPE%> = internal or external database.
This option is only valid when the output parameter has been set to <%=ProtocolConstantsVersion2.FORMAT_ID_LIST%>,
and is used to specify which external identifiers should be used to identify the physical entities in the
neighborhood.  For example, to output UniProt IDs, use: <%= ExternalDatabaseConstants.UNIPROT %>.  See the <a href=#valid_output_id_type>valid values for
<%= ProtocolRequest.ARG_OUTPUT_ID_TYPE%> parameter</a> below.
If not specified, the internal <%= ExternalDatabaseConstants.INTERNAL_DATABASE%> is assumed.</li></li>
<li>[Optional] <%= ProtocolRequest.ARG_DATA_SOURCE %> = a comma separated list of pathway data
sources that you want to search.  For example, the following restricts your results to Reactome pathways
only: <%= ProtocolRequest.ARG_DATA_SOURCE %>=<%=ExternalDatabaseConstants.REACTOME %>.  See the
<a href=#valid_data_source>valid values for <%= ProtocolRequest.ARG_DATA_SOURCE %> parameter</a> below.
If not specified, all pathway data sources will be searched.</li>
</ul>

<h3>Output:</h3>

A complete <a href="http://www.biopax.org">BioPAX</a> representation of the network neighborhood
for the given physical entity (default) or a simple text file that lists all the physical entities
in the neighborhood.  The output can specified by setting the <%= ProtocolRequest.ARG_OUTPUT%> parameter.
See the <%= ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS %> command <a href=#get_neighbors_parameters>
parameter list for more information</a>.  The simple text file contains three columns of data:
<ul>
<li>Record Name:  Physical Entity name.</li>
<li><%=ExternalDatabaseConstants.INTERNAL_DATABASE%>:  Internal cPath ID, used to uniquely identify the physical entity.  These IDs can be used to create links to each pathway.  For example:  <a href="record2.do?id=1">record2.do?id=1</a>.  However, please note that these internal IDs (and any links created with them) are <b>not</b> stable, and may change with each new data release.
</li>
<li>Database:ID:  External database identifier.  For example, <%= ExternalDatabaseConstants.UNIPROT %>:O14763.</li>
</ul>

<h3>Detecting matches:</h3>

<ul>
<li>If we are unable to find an external database identifier for a specified record, the third column of the tab-delimited text file will contain the keyword: <%= GetNeighborsCommand.NO_MATCHING_EXTERNAL_ID_FOUND %></li>
</ul>

<h3>Example Query:</h3>

Below is an example query.  Note: this query is not guaranteed to return results.<br>

<a href="webservice.do?version=2.0&cmd=get_neighbors&q=9854">webservice.do?version=2.0&cmd=get_neighbors&q=9854</a>

<h2><a NAME="get_parents"></a>[4]  Command:  <%= ProtocolConstantsVersion2.COMMAND_GET_PARENT_SUMMARIES %></h2>

<h3>Summary:</h3>

Retrieves a summary of all records which contain or reference the specified record.  For example, assume
that internal ID 145 refers to the BRCA2 gene.  If you request
<%= ProtocolConstantsVersion2.COMMAND_GET_PARENT_SUMMARIES %> for this ID, you will receive a list
of all interactions and complexes that include BRCA2.

<h3>Parameters:</h3>

<ul>
<li>[Required] <%= ProtocolRequest.ARG_COMMAND%>=<%= ProtocolConstantsVersion2.COMMAND_GET_PARENT_SUMMARIES %></li>
<li>[Required] <%= ProtocolRequest.ARG_VERSION%>=<%= ProtocolConstantsVersion2.VERSION_2 %></li>
<li>[Required] <%= ProtocolRequest.ARG_QUERY%>= an internal identifier, used to identify the physical entity
    or interaction of interest.</li>
<li>[Required] <%= ProtocolRequest.ARG_OUTPUT%> = <%=ProtocolConstantsVersion1.FORMAT_XML%>
</ul>

<h3>Output:</h3>

An XML file which follows the <a href="xml/SummaryResponse.xsd">SummaryResponse.xsd</a> XML Schema
[<a href="xml/summary_response/SummaryResponse.xsd.html">Full Documentation</a>].

<h3>Example Query:</h3>

Below is an example query.  Note: this query is not guaranteed to return results.<br/>

<a href="webservice.do?version=2.0&q=145&output=xml&cmd=<%= ProtocolConstantsVersion2.COMMAND_GET_PARENT_SUMMARIES%>">
webservice.do?version=2.0&q=145&output=xml&cmd=<%= ProtocolConstantsVersion2.COMMAND_GET_PARENT_SUMMARIES%></a>

<h2><a NAME="get_by_cpath_id"></a>[5]  Command:  <%= ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID %></h2>

<h3>Summary:</h3>

Retrieves details regarding one or more records, such as a pathway, interaction or physical entity.
For example, get the complete Apoptosis pathway from Reactome.

<h3>Parameters:</h3>

<ul>
<li>[Required] <%= ProtocolRequest.ARG_COMMAND%>=<%= ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID %></li>
<li>[Required] <%= ProtocolRequest.ARG_VERSION%>=<%= ProtocolConstantsVersion2.VERSION_2 %></li>
<li>[Required] <%= ProtocolRequest.ARG_QUERY%>= a comma delimited list of internal identifiers, used to identify the pathways, interactions
or physical entities of interest.</li>
<li>[Required] <%= ProtocolRequest.ARG_OUTPUT%> = <%=ProtocolConstantsVersion1.FORMAT_BIO_PAX%> or
 <%=ProtocolConstantsVersion2.FORMAT_BINARY_SIF%>.  When set to <%=ProtocolConstantsVersion1.FORMAT_BIO_PAX%>, the client will receive a complete BioPAX representation of the desired record.  When set to <%=ProtocolConstantsVersion2.FORMAT_BINARY_SIF%>, the client will receive a simple text file that lists all the interactions in the following simple interaction format: <b>physical_entity_id &lt;relationship type&gt; physical_entity_id</b>, where physical_entity_id is a valid CPATH_ID.  Note that <%=ProtocolConstantsVersion2.FORMAT_BINARY_SIF%> is only relevant if the query parameter <%= ProtocolRequest.ARG_QUERY%> corresponds to a pathway or interaction record.</li>
</ul>

<h3>Output:</h3>

An XML file in the Biological Pathway Exchange (<a href="http://biopax.org">BioPAX</a>) format.

<h3>Example Query:</h3>

Below is an example query.  Note: this query is not guaranteed to return results. <br/>

<a href="webservice.do?cmd=get_record_by_cpath_id&version=2.0&q=1&output=biopax">
webservice.do?cmd=get_record_by_cpath_id&version=2.0&q=1&output=biopax
</a>

<h2><a NAME="additional_params"></a>[6]  Additional Parameter Details:</h2>

<h3><a name='valid_input_id_type'></a>Valid values for the <%= ProtocolRequest.ARG_INPUT_ID_TYPE %> parameter:</h3>
<ul>
<%
    if (supportedIdTypes != null && supportedIdTypes.size() > 0) {
        for (int i=0; i<supportedIdTypes.size(); i++) {
            out.println("<LI>" + supportedIdTypes.get(i) + "</LI>");
        }
    } else {
        out.println("<LI>None specified.</LI>");
    }
%>
</ul>

<h3><a name='valid_output_id_type'></a>Valid values for the <%= ProtocolRequest.ARG_OUTPUT_ID_TYPE %> parameter:</h3>
<ul>
<%
    if (supportedIdTypes != null && supportedIdTypes.size() > 0) {
        for (int i=0; i<supportedIdTypes.size(); i++) {
            out.println("<LI>" + supportedIdTypes.get(i) + "</LI>");
        }
    } else {
        out.println("<LI>None specified.</LI>");
    }
%>
</ul>

<h3><a name='valid_data_source'></a>Valid values for the <%= ProtocolRequest.ARG_DATA_SOURCE %> parameter:</h3>
<ul>
<%
    for (int i=0; i < snapshotList.size(); i++) {
        ExternalDatabaseSnapshotRecord snapshotRecord = (ExternalDatabaseSnapshotRecord)
                snapshotList.get(i);
        String masterTerm = snapshotRecord.getExternalDatabase().getMasterTerm();
        out.println("<li>" + masterTerm + "</li>");
    }
%>
</ul>

<h2><a NAME="errors"></a>[7]  Error Codes:</h2>
<p>
An error while processing a request is reported
as an XML document with information about the error cause
in the following format:
</p>
<pre>
&lt;error&gt;
    &lt;error_code&gt;[ERROR_CODE]&lt;/error_code&gt;
    &lt;error_msg&gt;[ERROR_DESCRIPTION]&lt;/error_msg&gt;
    &lt;error_details&gt;[ADDITIONAL_ERROR _DETAILS]&lt;/error_details&gt;
&lt;/error&gt;
</pre>
<p>
Only the first error encountered is reported.
The table below provides a list of error codes, with their
        descriptions.
</p>
<div>
    <table>
            <tr>
                <th>Error Code</th>
                <th>Error Description</th>
            </tr>
            <%
                ArrayList statusCodes = ProtocolStatusCode.getAllStatusCodes();
            %>
            <%
                for (int i=0; i<statusCodes.size(); i++) {
                    ProtocolStatusCode code =
                            (ProtocolStatusCode) statusCodes.get(i);
                    int errorCode = code.getErrorCode();
                    String errorMsg = code.getErrorMsg();
                    if (errorCode == 450 || errorCode == 452 || errorCode == 453
                        || errorCode == 460 || errorCode == 470 || errorCode == 500) {
                    %>
                    <tr>
                        <td><%= errorCode %></td>
                        <td><%= errorMsg %></td>
                    </tr>
                    <% } %>
            <% } %>
        </table>
</div>

