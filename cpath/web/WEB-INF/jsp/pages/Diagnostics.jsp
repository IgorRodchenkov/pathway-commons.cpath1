<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<TABLE WIDTH=100% CELLSPACING=2 CELLPADDING=2 BORDER=0>
    <TR>
        <TD>
            <H1>cPath Diagnostics</H1>
        </TD>
        <TD ALIGN=RIGHT>
            <SPAN CLASS="small">
            Return to [<A HREF="adminHome.do">Admin Page</A>]
            </SPAN>
        </TD>
    </TR>
</TABLE>
<cbio:diagnosticsTable />
<P>

<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
