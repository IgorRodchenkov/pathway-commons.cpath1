<%@ page import="org.mskcc.pathdb.sql.DaoImport,
                 org.mskcc.pathdb.model.ImportRecord"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "Error.jsp" %>
<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<TABLE WIDTH=700>
<TR>
    <TD>Admin Tasks (this is a beta version of the cPath Administrator Page):
    <UL>
        <LI><A HREF="adminDisplay.do">Display Import Records</A>:
        Displays all import records currently in the cPath database.
        <LI><A HREF="adminTransfer.do?action=transfer1">Transfer #1:  Import Records</A>
        Transfers all import records from cPath to GRID.  After transfer is
        complete, the record status will be set to "TRANSFERRED", and you
        should be able to view new interactors/interactions from the main
        cPath pages.
        <LI><A HREF="adminTransfer.do?action=transfer2">Transfer #2:  External Links</A>
        Transfers all interactions external references to the External Reference
        tables.  After transfer is complete, you should be able to see new web
        links for all interators.
        <LI><A HREF="jsp/log/index.jsp">View Logs</A>:  View all log
        records from individual client requests.
    </UL>
    </TD>
</TR>
</TABLE>
<P>
<cbio:importTable />
<P>
<cbio:cacheTable />

<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
