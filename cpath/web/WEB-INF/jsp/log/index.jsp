<%@ page import="org.mskcc.pathdb.sql.dao.DaoLog,
                 java.util.ArrayList,
                 org.mskcc.pathdb.logger.LogRecord"%>
<%
    response.setHeader("Cache-control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    DaoLog adminLogger = new DaoLog();
    String userMsg = null;
%>


<HTML>
<HEADER>
	<TITLE>Data Services Log</TITLE>
	</TITLE>
	<link rel="stylesheet" type="text/css" href="jsp/style.css" />
</HEADER>
<BODY>
<H3>Data Services Log</H3>
<%
    String adminCommand = request.getParameter("admin");
    if (adminCommand != null && adminCommand.equals("dump")) {
        adminLogger.deleteAllLogRecords();
        userMsg = "All Log Records have been deleted.";
    }
    ArrayList logRecords = adminLogger.getLogRecords();
%>
<TABLE WIDTH=100% CELLSPACING=4 CELLPADDING=4>
    <%
        if (userMsg != null) { %>
            <TR BGCOLOR=#EEEEEE>
                <TD COLSPAN=4><FONT COLOR=RED><%= userMsg %></TD>
            </TR>
    <% } %>
    <TR BGCOLOR=#EEEEEE>
        <TD COLSPAN=2>&nbsp;</TD>
        <TD><A HREF="index.jsp">View all current log records</A></TD>
        <TD><A HREF="index.jsp?admin=dump">Delete all currrent log records</A></TD>
    </TR>
	<TR BGCOLOR=#DDDDDD>
		<TH>Timestamp</TH>
		<TH>Priority</TH>
		<TH>Logger</TH>
		<TH>Message</TH>
	</TR>
    <% for (int i=0; i<logRecords.size(); i++) {
        LogRecord record = (LogRecord) logRecords.get(i);
    %>
	<TR BGCOLOR=#EEEEEE>
		<TD><%= record.getDate() %></TD>
		<TD><%= record.getPriority() %></TD>
        <TD><%= record.getLogger() %></TD>
        <TD><%= record.getMessage() %></TD>
	</TR>
    <% } %>
</TABLE>
</BODY>
</HTML>
