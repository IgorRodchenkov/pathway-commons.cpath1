<%@ page import="org.mskcc.pathdb.protocol.ProtocolStatusCode,
                 java.util.ArrayList,
                 org.mskcc.pathdb.protocol.ProtocolConstants"%>

<div id="apphead">
    <h2>Web Service API Help</h2>
</div>

<div class="h3">
    <h3>Introduction</h3>
</div>
This page provides a quick reference help guide to using
the cPath Web Service API.

<div class="h3">
    <h3>URL Parameters</h3>
</div>

Requests to the cPath Web Service are formed by specifying
URL parameters.  Parameters are as follows:
        <UL>
		    <LI>cmd:  Indicates the command to execute.
            Current valid commands are:
                <UL>
                <LI><%= ProtocolConstants.COMMAND_HELP %>
                <LI><%= ProtocolConstants.COMMAND_GET_BY_ID %>
                <LI><%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME %>
                <LI><%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_TAX_ID %>
                <LI><%= ProtocolConstants.COMMAND_GET_BY_KEYWORD  %>
                <LI><%= ProtocolConstants.COMMAND_GET_BY_INTERACTION_DB %>
                <LI><%= ProtocolConstants.COMMAND_GET_BY_INTERACTION_PMID %>
            </UL>
		    <LI>q:  Indicates the query parameter.  Depending on the command,
            this is used to indicate a unique ID or a search term.
            For example, "P09097".
		    <LI>format:  Indicates the format of returned results.
            Current valid formats are:  "psi", and "html".
			<LI>version:  Indicates the version of the dataservice protocol.
                Must be specified.  The only supported version is "1.0".
		    </LI>
	    </UL>

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

<div class="h3">
    <h3>Examples of Usage</h3>
</div>
		The following query requests all cPath interactions for protein "P04273".
        Data will be formatted in the PSI XML format.
        <UL>
            <LI><SMALL><A HREF="webservice.do?cmd=get_by_interactor_name&format=psi&version=1.0&q=P04273">webservice.do?cmd=get_by_interactor_name&format=psi&version=1.0&q=P04273</A>
            </SMALL>
        </UL>

		The following query requests all cPath interactions for protein "P04273".
        Data will be formatted in HTML.
        <UL>
            <LI><SMALL><A HREF="webservice.do?cmd=get_by_interactor_name&format=html&version=1.0&q=P04273">webservice.do?cmd=get_by_interactor_name&format=html&version=1.0&q=P04273</A>
            </SMALL>
        </UL>
		The following query is invalid.
        The web service will return an XML document with a specific error code
        and error message.
        <UL>
            <LI><SMALL><A HREF="webservice.do?cmd=get_by_interactor_name&format=psi&version=0.9&q=P04273">webservice.do?cmd=get_by_interactor_name&format=psi&version=0.9&q=P04273</A>
            </SMALL>
        </UL>
        </BLOCKQUOTE>