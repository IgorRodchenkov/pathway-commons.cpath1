<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion1"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolStatusCode"%>
<h1>For Computational Biologists and Software Developers:</h1>
<p>
If you wish to programmatically access our data, you can do so via our
Web Service API.  This page provides a quick reference guide to help you
get started.
</p>

<h1>Issuing Client Requests</h1>
<p>
Client requests to the Web Service are formed by specifying
URL parameters.  Parameters are as follows:
</p>
        <ul>
            <li><b><%= ProtocolRequest.ARG_COMMAND %></b>:  Indicates the
            command to execute.
            Current valid commands are defined in the commands section below.
            </li>
		    <li><b>q</b>:  Indicates the query parameter.  Depending on the command,
            this is used to indicate one or more search terms or a unique ID.
            For example, "dna repair" or "P09097".
            </li>
            <li><b><%= ProtocolRequest.ARG_VERSION %></b>:  Indicates the
            version of the web service API.
            Must be specified.  The only supported version is "<%= CPathUIConfig.getWebUIBean().getWebApiVersion() %>".
            </li>
             <li><b><%= ProtocolRequest.ARG_FORMAT %></b>:  Indicates the
            format of returned results.
            Current valid formats are as follows:
                <ul>
                <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) { %>
                    <li><%= ProtocolConstantsVersion1.FORMAT_PSI_MI %>:  Data will be
                    formatted in the
                    <a href="http://psidev.sourceforge.net/">Proteomics
                    Standards Initiative Molecular Interaction (PSI-MI)</a>
                    XML format.
                    </li>
                    <li><%= ProtocolConstants.FORMAT_HTML %>:  Data will be
                    formatted in HTML.  This is useful for creating link outs to
                    specific web pages.
                    </li>
                    <li><%= ProtocolConstantsVersion1.FORMAT_COUNT_ONLY %>:  Returns a
                    single integer value, representing the total number of matches
                    for your query.  This is useful for using paged scroll results
                    (see <a href="#large">Retrieving Large Sets of Data</A> below).
                    </li>
                <% } else { %>
                     <li><%= ProtocolConstantsVersion1.FORMAT_BIO_PAX %>:  Data will be
                        formatted in the <a href="http://www.biopax.org">BioPAX</a>
                        XML format.
                     </li>
                <% } %>
                </ul>
                </li>
                <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) { %>
                    <li><b><%= ProtocolRequest.ARG_ORGANISM %></b>:  an optional
                    parameter used to filter for
                    specific organisms.  The value of this parameter must be set
                    to an <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Taxonomy">
                    NCBI Taxonomy Identifier</a>.   For example, organism=9606 will
                    filter for Homo Sapiens.
                    </li>
                    <li><b><%= ProtocolRequest.ARG_MAX_HITS %></b>:
                    Indicates the maximum number of interactions
                    returned in the response.  If maxHits is not specified, it will
                    default to:
                    <%= ProtocolConstantsVersion1.DEFAULT_MAX_HITS %>.  To prevent overloading
                    of the system, clients are restricted to a maximum of
                    <%= ProtocolConstantsVersion1.MAX_NUM_HITS %> hits at a time.
                    However, it is still possible to retrieve larger sets of data by
                    using paged scroll results (see
                    <a HREF="#large">Retrieving Large Sets of Data</A> below).
                    </li>
                    <li><b><%= ProtocolRequest.ARG_START_INDEX %></b>:  Indicates
                    the start index to use in a set of paged results.  For full
                    details, refer to <A HREF="#large">Retrieving Large Sets of Data</A>
                    below.).
                    </li>
                <% } %>
	    </ul>

<h1>Commands </h1>
        <table>
            <tr>
                <th>Command</th>
                <th>Description</th>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_HELP %></td>
                <td>Requests the current help page that you are now reading.</td>
            </tr>
            <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) { %>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_KEYWORD  %></td>
                <td>Finds all interactions that contain
                the specified keyword(s) and / or boolean search phrases.
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstantsVersion1.COMMAND_GET_BY_INTERACTOR_NAME_XREF %></td>
                <td>Finds all interactions that reference the specified
                interactor.  Interactors can be referenced by name, description
                or external database reference.  For example, if you want to
                narrow your search to all interactions associated with a
                specific SWISS-PROT ID, use this command.
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstantsVersion1.COMMAND_GET_BY_ORGANISM %></td>
                <td>Finds all interactions for the specified organism.
                The organism value must be specified with an <a href=
                "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Taxonomy">NCBI
                Taxonomy ID</A>.  Note that you can also attach
                an organism filter to any search command by using the
                optional organism parameter (see URL parameters above).
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstantsVersion1.COMMAND_GET_BY_EXPERIMENT_TYPE %></td>
                <td>
                Finds all interactions that were discovered by
                the specified Experiment Type.  For example, if you want to
                narrow your search to all interactions discovered via
                "Classical Two Hybrid", use this command.
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstantsVersion1.COMMAND_GET_BY_PMID %></td>
                <td>Finds all interactions that are associated with
                the specified PubMed ID.</td>
            </tr>
            <tr>
                <td><%= ProtocolConstantsVersion1.COMMAND_GET_BY_DATABASE %></td>
                <td>Finds all interactions that originate from the specified
                database.  For example, to find all interactions from "DIP"
                (Database of Interacting Proteins), use this command.</td>
            </tr>
            <tr>
                <td><%= ProtocolConstantsVersion1.COMMAND_GET_BY_INTERACTOR_ID %></td>
                <td>Finds all interactions that are associated with
                the specified interactor.  To use this option, you must know the
                internal ID for the interactor.</td>
            </tr>
            <% } else { %>
            <tr>
                <td><%= ProtocolConstantsVersion1.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST %></td>
                <td>Returns a summary of all "top-level" pathways in the database.
                In the BioPAX ontology, pathways can contain sub-pathways.
                This command returns top-level pathways only, and filters
                out all sub-pathways.  The response is a BioPAX XML document.
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID %></td>
                <td>Retrieves the complete contents of the specified record.
                The response is a BioPAX XML document.</td>
            </tr>
            <% } %>
            </table>

<h1>Error Codes:</h1>
<p>
If an error occurs while processing your request, you will
receive an XML document with detailed information about the cause of
the error.  Error documents have the following format:
</p>
<pre>
&lt;error&gt;
    &lt;error_code&gt;[ERROR_CODE]&lt;/error_code&gt;
    &lt;error_msg&gt;[ERROR_DESCRIPTION]&lt;/error_msg&gt;
    &lt;error_details&gt;[ADDITIONAL_ERROR _DETAILS]&lt;/error_details&gt;
&lt;/error&gt;
</pre>
<p>
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
                    %>
                <tr>
                    <td><%= errorCode %></td>
                    <td><%= errorMsg %></td>
                </tr>
            <% } %>
        </table>
</div>

<%
    ProtocolRequest pRequest = new ProtocolRequest();
%>
<h1>Examples of Usage</h1>

        <% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) {
            pRequest.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
            pRequest.setQuery("DNA");
            pRequest.setFormat(ProtocolConstantsVersion1.FORMAT_PSI_MI);  %>
		<p>The following example searches for the keyword "DNA".
        Data will be formatted in the PSI-MI XML format.
        </p>
        <ul>
            <li><small><a HREF="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></a>
            </small>
            </li>
        </ul>

<p>The following query searches for the keyword "DNA".
        Data will be formatted in HTML.
        <% pRequest.setFormat(ProtocolConstants.FORMAT_HTML); %>
</p>
        <ul>
            <li><small><a href="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></a>
            </small>
            </li>
        </ul>
        <% } else {
            pRequest.setCommand(ProtocolConstantsVersion1.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST);
            pRequest.setFormat(ProtocolConstantsVersion1.FORMAT_BIO_PAX);
        %>
<p>
    The following requests a summary of all top-level pathways
    in the database:
</p>
        <ul>
            <li><small><a href="<%=  pRequest.getUri() %>"><%= pRequest.getUri() %></a>.</small></li>
        </ul>
<p>The following requests the full BioPAX record for local ID 1:
</p>
        <%
            pRequest.setCommand(ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID);
            pRequest.setQuery("1");
        %>
        <ul>
            <li><small><a href="<%=  pRequest.getUri() %>"><%= pRequest.getUri() %></a>.</small></li>
        </ul>
        <% } %>
        <%
            pRequest.setFormat("svg");
        %>
<p>
    The following query specifies an invalid format.
    The web service will return an XML document with a specific error code
    and error message.
</p>
        <ul>
            <li><small><a href="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></a>
            </small></li>
        </ul>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) { %>

<h1><a name="large">Retrieving Large Sets of Data</a></h1>
<p>
    To prevent overloading of the system, clients are restricted to a maximum of
    <%= ProtocolConstantsVersion1.MAX_NUM_HITS %> hits at a time.
    However, it is still possible to retrieve larger sets of data
    (or even complete sets of data) by using an index value into a
    complete data set.  This functionality is identical to that provided
    by the web site.  For example, if you want to view all interactions,
    you can do so, but you will have to manually scroll
    through the results one page at a time.  To retrieve complete data sets
    via the Web Service API, you follow the same procedure and
    retrieve results one "page" at a time.  This requires multiple client
    requests, and some more intelligent client processing.
</p>
<%
    pRequest = new ProtocolRequest();
    pRequest.setCommand(ProtocolConstantsVersion1.COMMAND_GET_BY_ORGANISM);
    pRequest.setQuery("562");
    pRequest.setFormat(ProtocolConstantsVersion1.FORMAT_COUNT_ONLY);
%>
<p>For example, assume a client wishes to download the full set
    of interactions for E. coli.  Here's how such client processing would work:
</p>
    <ul>
    <li>First, find out how many interactions for E. coli exist.  To do so,
    issue a query with <%= ProtocolRequest.ARG_FORMAT %>
    set to "<%= ProtocolConstantsVersion1.FORMAT_COUNT_ONLY %>".
    For example:
        <ul>
            <li><small><a href="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></a>
            </small>
            </li>
        </ul>
    </li>
    You will receive back a single integer value, indicating the total
    number of matching interactions.
    <li>Next, create a while loop or a for loop for retrieving data
    sets in small bundles or "pages".  For example, if there are 1000
    interactions for E. coli, you could retrieve interactions in sets of 50.
    The client uses the <%= ProtocolRequest.ARG_START_INDEX %> parameter to specify a
    starting point in the result set.  For example, if
    <%= ProtocolRequest.ARG_START_INDEX %> is set to 100, and
    <%= ProtocolRequest.ARG_MAX_HITS%> is set to 50, you will retrieve interactions
    100-150 in the complete data set.
    </li>
    </ul>
</p>
<p>Complete psuedocode of the entire process looks like this:
</p>
<pre>
totalNumInteractions = [Issue search resuest with <%= ProtocolRequest.ARG_FORMAT %> set to <%= ProtocolConstantsVersion1.FORMAT_COUNT_ONLY %>.]
index = 0;
while (index < totalNumInteractions) {
    [Issue request with <%= ProtocolRequest.ARG_START_INDEX %> = index; and <%= ProtocolRequest.ARG_MAX_HITS %> = 50.]
    index += 50;
}
</pre>
<p>After the while loop exits, you have a complete set of E. Coli data.
</p>
<% } %>