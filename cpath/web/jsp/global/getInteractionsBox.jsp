<%@ page import="java.net.URLEncoder,
                 org.mskcc.pathdb.controller.ProtocolRequest"%><FORM ACTION="webservice"  METHOD="GET">
<INPUT TYPE="hidden" name="version" value="1.0">
<INPUT TYPE="hidden" name="cmd" value="retrieve_interactions">

<%
    String sessionSearchTerm = "cpath.searchTerm";
    ProtocolRequest protocolRequestLocal = (ProtocolRequest)
            request.getAttribute("protocol_request");
    String uid = null;
    if (protocolRequestLocal != null) {
        uid = protocolRequestLocal.getUid();
        if (uid != null) {
            session.setAttribute(sessionSearchTerm, uid);
        }
    }
    if (uid == null || uid.length() == 0) {
        uid = (String) session.getAttribute(sessionSearchTerm);
        if (uid == null) {
            uid = new String("");
        }
    }
%>
<TABLE WIDTH="100%" CELLPADDING=5 CELLSPACING=5 BGCOLOR="#9999cc">
    <TR>
        <TD class="table_data">
            <font color=#333366>Search:&nbsp;&nbsp;
            <INPUT TYPE=TEXT name="uid" value='<%= uid %>'>
            <INPUT TYPE=HIDDEN name="format" value="html">
            &nbsp;&nbsp;<INPUT TYPE="SUBMIT" value="Search">
            &nbsp;&nbsp;<FONT SIZE=-1>Sample Search:  dna repair.<FONT>
            </FONT>
        </TD>
    </TR>
</TABLE>
</FORM>