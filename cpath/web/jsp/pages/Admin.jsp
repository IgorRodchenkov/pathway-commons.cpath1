<%@ page import="org.mskcc.pathdb.sql.dao.DaoImport,
                 org.mskcc.pathdb.model.ImportRecord,
                 java.util.Enumeration,
                 org.apache.struts.config.ActionConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "Error.jsp" %>
<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<TABLE WIDTH=700>
<TR>
    <TD><H1>Admin Tasks:</H1>
    <TABLE WIDTH=100%>
    <TR VALIGN=TOP>
        <TD WIDTH=35%>
            <H3>
            <A HREF="adminViewImportRecords.do">View Import Records</A>
            </H3>
        </TD>
        <TD>
            <H3>
            View all import records currently in the cPath database.
            </H3>
        </TD>
    </TR>

    <TR VALIGN=TOP>
        <TD WIDTH=35%>
            <H3>
            <A HREF="adminRunFullTextIndexer.do">Run Full Text Indexer</A>
            </H3>
        </TD>
        <TD>
            <H3>
            Runs Full Text Indexer on all interactor records
            currently in the cPath database.
            </H3>
        </TD>
    </TR>

    <TR VALIGN=TOP>
        <TD>
            <H3>
            <A HREF="adminViewLogRecords.do">View Logs</A>
            </H3>
        </TD>
        <TD>
            <H3>
            View all log records from individual client requests.
            </H3>
        </TD>
    </TR>
    <TR VALIGN=TOP>
        <TD>
            <H3>
            <A HREF="adminPurgeLogRecords.do">Purge Logs</A>
            </H3>
        </TD>
        <TD>
            <H3>
            Purge all existing log records.
            </H3>
        </TD>
    </TR>
</TABLE>
</TD></TR>
</TABLE>
<P>
<%
    ActionConfig actionConfig = (ActionConfig) request.getAttribute
            ("org.apache.struts.action.mapping.instance");
    String path = actionConfig.getPath();
%>

<% if (path.equals("/adminViewLogRecords")) { %>
    <cbio:logTable/>
<% } else if (path.equals("/adminViewImportRecords")) { %>
    <cbio:importTable adminView="yes"/>
<% } %>
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
