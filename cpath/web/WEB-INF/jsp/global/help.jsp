<%@ page import="org.mskcc.pathdb.protocol.ProtocolStatusCode,
                 java.util.ArrayList,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.protocol.ProtocolRequest"%>

<div id="apphead">
    <h2>Web Service API</h2>
</div>

<div class="h3">
    <h3>Introduction</h3>
</div>
This page provides a quick reference help guide to using
the cPath Web Service API.  The cPath Web Service provides programmatic
access to all data in cPath.  A client application requests data via a defined
set of URL parameters, and can receive data in multiple formats, including
XML and HTML.  The XML format is useful for those applications that want to
programmatically access cPath data for futher computation, whereas the HTML
format is useful for those web sites and/or applications that want to provide
link outs to cPath data.
<div class="h3">
    <h3>Issuing Client Requests</h3>
</div>
Client requests to the cPath Web Service are formed by specifying
URL parameters.  Parameters are as follows:
        <UL>
		    <LI><B><%= ProtocolRequest.ARG_COMMAND %></B>:  Indicates the command to execute.
            Current valid commands are defined in the Commands section below.
		    <LI><B>q</B>:  Indicates the query parameter.  Depending on the command,
            this is used to indicate one or more search terms or a unique ID.
            For example, "dna repair" or "P09097".
 		    <LI><B><%= ProtocolRequest.ARG_FORMAT %></B>:  Indicates the format of returned results.
            Current valid formats are as follows:
                <UL>
                <LI><%= ProtocolConstants.FORMAT_XML %>:  Interactions will be
                formatted in the
                <A HREF="http://psidev.sourceforge.net/">Proteomics
                Standards Intitiative Molecular Interaction (PSI-MI)</A>
                XML format.
                <LI><%= ProtocolConstants.FORMAT_HTML %>:  Interactions will be
                formatted in HTML, using the regular cPath Look and Feel.
                This is useful for creating link outs to cPath data.
                <LI><%= ProtocolConstants.FORMAT_COUNT_ONLY %>:  Returns a
                single integer value, representing the total number of matches
                for your query.  This is useful for using paged scroll results
                (see <A HREF="#large">Retrieving Large Sets of Data</A> below).
                </UL>
    		    <LI><B><%= ProtocolRequest.ARG_VERSION %></B>:  Indicates the
                version of the web service API.
                Must be specified.  The only supported version is "1.0".
                <LI><B><%= ProtocolRequest.ARG_ORGANISM %></B>:  an optional
                parameter used to filter for
                specific organisms.  The value of this parameter must be set
                to an <A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Taxonomy">
                NCBI Taxonomy Identifier</A>.   For example, organism=9606 will
                filter for Homo Sapiens.
                <LI><B><%= ProtocolRequest.ARG_MAX_HITS %></B>:
                Indicates the maximum number of interactions
                returned in the response.  If maxHits is not specified, it will
                default to:
                <%= ProtocolConstants.DEFAULT_MAX_HITS %>.  To prevent overloading
                of the system, clients are restricted to a maximum of
                <%= ProtocolConstants.MAX_NUM_HITS %> hits at a time.
                However, it is still possible to retrieve larger sets of data by
                using paged scroll results (see
                <A HREF="#large">Retrieving Large Sets of Data</A> below).
                <LI><B><%= ProtocolRequest.ARG_START_INDEX %></B>:  Indicates
                the start index to use in a set of paged results.  For full
                details, refer to <A HREF="#large">Retrieving Large Sets of Data</A>
                below.).
                </LI>
	    </UL>

<div class="h3">
    <h3>Commands </h3>
</div>
        <TABLE>
            <tr>
                <th>Command</font></th>
                <th>Description</font></th>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_HELP %></td>
                <td>Requests the current help page that you are now reading.</td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_KEYWORD  %></td>
                <td>Finds all interactions in cPath that contain the specified
                keyword(s) and / or boolean search phrases.  This is the most
                powerful search command, and can be used to perform advanced
                queries across multiple fields in cPath.  For full details
                regarding keyword searches, refer to the
                <A HREF="faq.do#construct">advanced search section of the
                cPath FAQ</A>.</td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME_XREF %></td>
                <td>Finds all interactions in cPath that reference the specified
                interactor.  Interactors can be referenced by name, description
                or external database reference.  For example, if you want to
                narrow your search to all intereractions associated with a
                specific SWISS-PROT ID, use this command.
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_ORGANISM %></td>
                <td>Finds all interactions in cPath for the specified organism.
                The organism value must be specified with an <A HREF=
                "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Taxonomy">NCBI
                Taxonomy ID</A>.  Note that you can also attach
                an organism filter to any search command by using the
                optional organism parameter (see URL parameters above).
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_EXPERIMENT_TYPE %></td>
                <td>
                Finds all interactions in cPath that were discovered by
                the specified Experiment Type.  For example, if you want to
                narrow your search to all interactions discovered via
                "Classical Two Hybrid", use this command.
                </td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_PMID %></td>
                <td>Finds all interactions in cPath that are associated with
                the specified PubMed ID.</td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_DATABASE %></td>
                <td>Finds all interactions in cPath that come from the specified
                database.  For example, to find all interactions from "DIP"
                (Database of Interacting Proteins), use this command.</td>
            </tr>
            <tr>
                <td><%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID %></td>
                <td>Finds all interactions in cPath that are associated with
                the specified interactor.  To use this option, you must know the
                internal cPath ID for the interactor.</td>
            </tr>
            </TABLE>

<div class="h3">
    <h3>Error Codes</h3>
</div>
If an error occurs while processing your request, you will
receive an XML document with detailed information about the cause of
the error.  Error documents have the following format:

<PRE>
&lt;error&gt;
    &lt;error_code&gt;[ERROR_CODE]&lt;/error_code&gt;
    &lt;error_msg&gt;[ERROR_DESCRIPTION]&lt;/error_msg&gt;
    &lt;error_details&gt;[ADDITIONAL_ERROR _DETAILS]&lt;/error_details&gt;
&lt;/error&gt;
</PRE>

        The Table below provides a list of error codes, with their
        descriptions.
        <P>
        <TABLE>
            <tr>
                <th>Error Code</font></th>
                <th>Error Description</font></th>
            </TR>
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
                <TR>
                    <TD><%= errorCode %></TD>
                    <TD><%= errorMsg %></TD>
                </TR>
            <% } %>
        </TABLE>


<%
    ProtocolRequest pRequest = new ProtocolRequest();
    pRequest.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME_XREF);
    pRequest.setQuery("P04273");
    pRequest.setFormat(ProtocolConstants.FORMAT_XML);
%>
<div class="h3">
    <h3>Examples of Usage</h3>
</div>
		The following query requests all cPath interactions for protein "P04273".
        Data will be formatted in the PSI XML format.
        <UL>
            <LI><SMALL><A HREF="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></A>
            </SMALL>
        </UL>

		The following query requests all cPath interactions for protein "P04273".
        Data will be formatted in HTML.
        <% pRequest.setFormat(ProtocolConstants.FORMAT_HTML); %>
        <UL>
            <LI><SMALL><A HREF="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></A>
            </SMALL>
        </UL>

        <%
            pRequest.setVersion("0.9");
            pRequest.setFormat(ProtocolConstants.FORMAT_XML);
        %>
        The following query is invalid.
        The web service will return an XML document with a specific error code
        and error message.
        <UL>
            <LI><SMALL><A HREF="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></A>
            </SMALL>
        </UL>
<div class="h3">
    <h3><A NAME="large">Retrieving Large Sets of Data</A></h3>
</div>
    To prevent overloading of the system, clients are restricted to a maximum of
    <%= ProtocolConstants.MAX_NUM_HITS %> hits at a time.
    However, it is still possible to retrieve larger sets of data
    (or even complete sets of data) by using an index value into a
    complete data set.  This functionality is identical to that provided
    by the cPath web site.  For example, if you want to view all interactions
    in cPath, you can do so, but you will have to manually scroll
    through the results one page at a time.  To retrieve complete data sets
    via the Web Service API, you follow the same procedure and
    retrieve results one "page" at a time.  This requires multiple client
    requests to cPath, and some more intelligent client processing.

<%
    pRequest = new ProtocolRequest();
    pRequest.setCommand(ProtocolConstants.COMMAND_GET_BY_ORGANISM);
    pRequest.setQuery("562");
    pRequest.setFormat(ProtocolConstants.FORMAT_COUNT_ONLY);
%>

    <P>For example, assume a client wishes to download the full set
    of interactions for E. coli.  Here's how such client processing would work:
    <UL>
    <LI>First, find out how many interactions for E. coli exist.  To do so,
    issue a query with <%= ProtocolRequest.ARG_FORMAT %>
    set to "<%= ProtocolConstants.FORMAT_COUNT_ONLY %>".
    For example:
        <UL>
            <LI><SMALL><A HREF="<%= pRequest.getUri() %>"><%= pRequest.getUri() %></A>
            </SMALL>
        </UL>
    You will receive back a single integer value, indicating the total
    number of matching interactions.
    <LI>Next, create a while loop or a for loop for retrieving data
    sets in small bundles or "pages".  For example, if there are 1000
    interactions for E. coli, you could retrieve interactions in sets of 50.
    The client uses the <%= ProtocolRequest.ARG_START_INDEX %> parameter to specify a
    starting point in the result set.  For example, if
    <%= ProtocolRequest.ARG_START_INDEX %> is set to 100, and
    <%= ProtocolRequest.ARG_MAX_HITS%> is set to 50, you will retrieve interactions
    100-150 in the complete data set.
    </UL>
    <P>Complete psuedocode of the entire process looks like this:
    <PRE>
totalNumInteractions = [Issue search resuest with <%= ProtocolRequest.ARG_FORMAT %> set to <%= ProtocolConstants.FORMAT_COUNT_ONLY %>.]
index = 0;
while (index < totalNumInteractions) {
    [Issue request with <%= ProtocolRequest.ARG_START_INDEX %> = index; and <%= ProtocolRequest.ARG_MAX_HITS %> = 50.]
    index += 50;
}</PRE>
    After the while loop exits, you have a complete set of E. Coli data.
    </UL>
</div>
</BLOCKQUOTE>