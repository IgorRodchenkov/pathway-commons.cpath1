<%@ page import="org.mskcc.pathdb.sql.DatabaseImport,
                 org.mskcc.pathdb.model.ImportRecord"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<TABLE WIDTH=700>
<TR>
    <TD>Admin Tasks (this is a beta version of the cPath Administrator Page):
    <UL>
        <LI><A HREF="adminDisplay.do">Display Import Records</A>:
        Displays all import records currently in the cPath database.
        <LI><A HREF="adminTransfer.do">Transfer Import Records</A>
        Transfers all import records from cPath to GRID.  After transfer is
        complete, the record status will be set to "TRANSFERRED", and you
        should be able to view new interactors/interactions from the main
        cPath pages.
    </UL>
    </TD>
</TR>
</TABLE>
<P>
<cbio:importTable />
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
