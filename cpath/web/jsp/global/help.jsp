<TABLE WIDTH="100%" CELLPADDING=5 CELLSPACING=5>
	<TR>
		<TD>
		<B><BIG><U>cPath Web Service API:  Help Page</U></BIG></B>
        <BLOCKQUOTE>
		This page provides a quick reference help guide to using
		the cPath Web Service API.

        <P>
		<B><U>URL Parameters:</U></B>
        <P>
		Requests to the cPath Web Service are formed by specifying
        URL parameters.  Parameters are as follows:
        <UL>
		    <LI>cmd:  Indicates the command to execute.
            Current valid commands are:  "help",
            and "retrieve_interactions".
		    <LI>uid:  Indicates a unique identifier.  For example, "P09097".
		    <LI>format:  Indicates the format of returned results.
            Current valid formats are:  "psi", and "html".
			<LI>version:  Indicates the version of the dataservice protocol.
                Must be specified.  The only supported version is "1.0".
		    </LI>
	    </UL>
        <P>
        <B><U>Error Codes:</U></B>
        <P>
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
        <TABLE CELLPADDING=3 CELLSPACING=3>
            <tr bgcolor=#9999cc>
                <TD><font color=#333366>Error Code</font></TD>
                <TD><font color=#333366>Error Description</font></TD>
            </TR>
            <TR>
                <TD>400</TD>
                <TD>Bad Command (command not recognized)</TD>
            </TR>
            <TR>
                <TD>450</TD>
                <TD>Bad UID (UID not available in specified database)</TD>
            </TR>
            <TR>
                <TD>451</TD>
                <TD>Bad Data Format (data format not recognized)</TD>
            </TR>
            <TR>
                <TD>452</TD>
                <TD>Bad Request (missing arguments)</TD>
            </TR>
            <TR>
                <TD>453</TD>
                <TD>Version Not Supported</TD>
            </TR>
            <TR>
                <TD>500</TD>
                <TD>Internal Server Error</TD>
            </TR>
        </TABLE>
        <P>
		<B><U>Examples of Usage:</U></B>
        <P>
		The following query requests all cPath interactions for protein "P09097".
        Data will be formatted in the PSI XML format.
        <UL>
            <LI><SMALL><A HREF="webservice?cmd=retrieve_interactions&format=psi&version=1.0&uid=P09097">webservice?cmd=retrieve_interactions&format=psi&version=1.0&uid=P09097</A>
            </SMALL>
        </UL>

		The following query requests all cPath interactions for protein "P09097".
        Data will be formatted in HTML.
        <UL>
            <LI><SMALL><A HREF="webservice?cmd=retrieve_interactions&format=html&version=1.0&uid=P09097">webservice?cmd=retrieve_interactions&format=html&version=1.0&uid=P09097</A>
                </SMALL>
        </UL>
		The following query requests all cPath interactions for an invalid UID.
        The web service will return an XML document with a specific error code
        and error message.
		<UL>
		    <LI><SMALL><A HREF="webservice?cmd=retrieve_interactions&format=psi&version=1.0&uid=YCR038CEE">webservice?cmd=retrieve_interactions&format=psi&version=1.0&uid=YCR038CEE</A>
                </SMALL>
		</UL>
        </BLOCKQUOTE>
			</TD>
		</TR>
</TABLE>