<TABLE WIDTH="100%" CELLPADDING=5 CELLSPACING=5>
	<TR>
		<TD>
		<B><BIG><U>Help Page</U></BIG></B>
        <BLOCKQUOTE>
		This page provides a quick reference help guide to using
		the CBio Data Service.

        <P>
		<B><U>URL Parameters:</U></B>
        <P>
		Requests to the CBio Data Service are formed by specifying URL parameters.  Parameters
		are as follows:
        <UL>
		    <LI>cmd:  Indicates the command to execute.  Current valid commands are:  "help", "retrieve_interactions" and "retrieve_go".
		    <LI>db:  Indicates the requested database.  Current valid databases are: "grid".
		    <LI>uid:  Indicates a unique identifier.  For example, "YCR038C".
		    <LI>format:  Indicates the format of returned results.  Current valid formats are:  "psi", "rs", and "html".
			<LI>version:  Indicates the version of the dataservice protocol.  Must be
			    specified.  The only supported version is "1.0".
		    </LI>
	    </UL>
        <P>
		<B><U>Examples of Usage:</U></B>
        <P>
		The following query requests all GRID interactions for gene YCR038C.  Data will be formatted in the PSI XML format.
<UL>
	<LI><SMALL><A HREF="/ds/dataservice?cmd=retrieve_interactions&db=grid&format=psi&version=1.0&uid=YCR038C">/ds/dataservice?cmd=retrieve_interactions&db=grid&format=psi&version=1.0&uid=YCR038C</A>
    </SMALL>
</UL>

		The following query requests all GRID interactions for gene YCR038C.  Data will be formatted in HTML.
<UL>
	<LI><SMALL><A HREF="/ds/dataservice?cmd=retrieve_interactions&db=grid&format=html&version=1.0&uid=YCR038C">/ds/dataservice?cmd=retrieve_interactions&db=grid&format=psi&version=1.0&uid=YCR038C</A>
        </SMALL>
</UL>
		The following query requests GO Terms for the gene YHR199W.  Data will be formatted in the default result set format:
<UL>
	<LI><SMALL><A HREF="/ds/dataservice?cmd=retrieve_go&db=grid&format=rs&version=1.0&uid=YHR119W">/ds/dataservice?cmd=retrieve_go&db=grid&format=rs&version=1.0&uid=YHR119W</A>
    </SMALL>
</UL>
		The following query requests all GRID interactions for an invalid Gene UID.  The data service will return
		an XML document with a specific error code and error message.
		<UL>
		<LI><SMALL><A HREF="/ds/dataservice?cmd=retrieve_interactions&db=grid&format=psi&version=1.0&uid=YCR038CEE">/ds/dataservice?cmd=retrieve_interactions&db=grid&format=psi&version=1.0&uid=YCR038CEE</A>
        </SMALL>
		</UL>
        </BLOCKQUOTE>
			</TD>
		</TR>
</TABLE>