<%@ page import="java.net.URLEncoder,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolConstants"%>
<FORM ACTION="webservice.do"  METHOD="GET">
<INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0">
<INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
    value="<%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME %>">

<TABLE WIDTH="100%" CELLPADDING=5 CELLSPACING=5 BGCOLOR="#9999cc">
    <TR>
        <TD class="table_data">
            <font color=#333366>Search:&nbsp;&nbsp;
            <INPUT TYPE=TEXT name="<%= ProtocolRequest.ARG_QUERY %>"
                value="">
            <INPUT TYPE=HIDDEN name="<%= ProtocolRequest.ARG_FORMAT %>"
                value="html">
            &nbsp;&nbsp;<INPUT TYPE="SUBMIT" value="Search">
            &nbsp;&nbsp;<FONT SIZE=-1>Sample Search:  dna repair.<FONT>
            &nbsp;&nbsp;[<A HREF="advancedSearch.do">Advanced Search</A>]
            </FONT>
        </TD>
    </TR>
</TABLE>
</FORM>