<%@ page import="org.mskcc.pathdb.controller.ProtocolException,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />

<table width=100% cellpadding=3 cellspacing=3 border=0>
    <tr>
        <td>
            <h1>Welcome to cPath</H1>
        </td>
        <td align=right>
            <SPAN CLASS="medium">cPath Version:  0.1 Beta</SPAN>
        </td>
    </tr>
    <tr>
        <td colspan=2>
            <TABLE WIDTH=200 CELLSPACING=1 CELLPADDING=1
                    BGCOLOR=#DDDDDD ALIGN=RIGHT HSPACE=5>
                <TR>
                <TD>
                <jsp:include page="../global/dbStats.jsp" flush="true" />
                </TD>
                </TR>
            </TABLE>
            cPath is a freely available cancer pathway database.  We
            are building this as a resource to the community.  Currently,
            basic protein-protein interaction functionality is available.
            We expect to make a preliminary version of cPath available to
            the public in early 2004.  In the near future, we also plan
            to make cPath <A HREF="http://www.biopax.org">BioPax</A>
            compatibile.
            <P>
            cPath is currently being developed by the
            <A HREF="http://cbio.mskcc.org">Computational Biology Center</A>
            of Memorial Sloan-Kettering Cancer Center.
            <P>

        </td>
    </tr>
</table>
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />