<%@ page import="org.mskcc.pathdb.xdebug.XDebug,
                 org.mskcc.pathdb.xdebug.XDebugMessage,
                 org.mskcc.pathdb.xdebug.XDebugParameter,
                 java.util.Date,
                 java.util.Vector,
                 java.util.Enumeration,
                 org.mskcc.pathdb.action.admin.AdminWebLogging" %>
<P>&nbsp;
<P>&nbsp;
<P>&nbsp;
<%
XDebug xdebug= (XDebug) request.getAttribute("xdebug");
if (xdebug != null) {
    Enumeration enum = request.getAttributeNames();
    while (enum.hasMoreElements()) {
        String name = (String) enum.nextElement();
        Object value = request.getAttribute(name);
        xdebug.addParameter(XDebugParameter.REQUEST_ATTRIBUTE_TYPE, name,
                value.toString());
    }
%>
<%
    String xdebugSession = (String) session.getAttribute
            (AdminWebLogging.WEB_LOGGING);
    String xdebugParameter = request.getParameter(AdminWebLogging.WEB_LOGGING);
    if (xdebugSession != null || xdebugParameter != null) {
%>
<TABLE width="100%">
	<TR BGCOLOR="#9999cc">
		<TD>
            <FONT COLOR="#333366"><BIG><B>CPath Diagnostics</B></BIG></FONT>
		</TD>
		<TD COLSPAN=2>
            <FONT COLOR="#333366"><%= new Date() %></FONT>
        </TD>
	</TR>
	<TR BGCOLOR="#9999cc">
		<TD COLSPAN=3>
            <FONT COLOR="#333366">Performance Stats</FONT>
        </TD>
	</TR>
	<TR BGCOLOR="#ccccff">
		<TD>
            <FONT SIZE=1 COLOR=BLACK>Total Time to Generate Page</FONT>
        </TD>
		<TD COLSPAN=2>
            <FONT SIZE=1 COLOR=BLACK><%= xdebug.getTimeElapsed() %> ms</FONT>
        </TD>
	</TR>
	<TR BGCOLOR="#9999cc">
		<TD>
            <FONT COLOR="#333366">Class Name</FONT>
        </TD>
		<TD COLSPAN=2>
            <FONT COLOR="#333366">Message</FONT>
        </TD>
	</TR>

	<%--
				***********************************
				Output Log Messages
				***********************************
	--%>
	<%
		Vector messages = xdebug.getDebugMessages();
		for (int msgIndex=0; msgIndex<messages.size(); msgIndex++) {
			XDebugMessage msg = (XDebugMessage) messages.elementAt(msgIndex);
	%>
		<TR BGCOLOR="#ccccff" VALIGN=TOP>
			<TD WIDTH=30%>
                <FONT SIZE=1 COLOR=BLACK><%= msg.getClassName() %></FONT>
            </TD>
			<TD COLSPAN=2 WIDTH=70%>
				<FONT SIZE=1 COLOR="<%= msg.getColor() %>">
				<%= msg.getMessage() %>
				</FONT>
            </TD>
		</TR>
    <% } %>
	<%--
				***********************************
				Output Parameter Values
				***********************************
	--%>
	<TR BGCOLOR="#9999cc">
		<TD><FONT COLOR="#333366">Parameter Type</FONT></TD>
		<TD><FONT COLOR="#333366">Name</FONT></TD>
		<TD><FONT COLOR="#333366">Value</FONT></TD>
	</TR>
	<%
		Vector parameters = xdebug.getParameters();
		String bgcolor;
		for (int paramIndex=0; paramIndex<parameters.size(); paramIndex++) {
			XDebugParameter param = (XDebugParameter)
                    parameters.elementAt(paramIndex);
			if (param.getType()==XDebugParameter.USER_TYPE)
                bgcolor = "#C9FFD3";
			else if (param.getType()==XDebugParameter.HTTP_HEADER_TYPE)
                bgcolor="LIGHTBLUE";
			else if (param.getType()==XDebugParameter.COOKIE_TYPE)
                bgcolor="PINK";
			else if (param.getType()==XDebugParameter.SESSION_TYPE)
                bgcolor="#FFCBFF";
			else if (param.getType()==XDebugParameter.ENVIRONMENT_TYPE)
                bgcolor="LIGHTYELLOW";
			else if (param.getType()==XDebugParameter.HTTP_TYPE)
                bgcolor="LIGHTGREEN";
			else if (param.getType()==XDebugParameter.REQUEST_ATTRIBUTE_TYPE)
                bgcolor="LIGHTYELLOW";
            else if (param.getType()==XDebugParameter.SERVLET_CONTEXT_TYPE)
                bgcolor="LIGHTBLUE";
			else bgcolor = "WHITE";
	%>

		<TR BGCOLOR="<%= bgcolor %>">
			<TD VALIGN=TOP><FONT SIZE=1 COLOR=BLACK><%= param.getTypeName() %></FONT></TD>
			<TD VALIGN=TOP><FONT SIZE=1 COLOR=BLACK><%= param.getName() %></FONT></TD>
			<TD VALIGN=TOP><FONT SIZE=1 COLOR=BLACK><%= wrapText(param.getValue()) %></FONT></TD>
		</TR>
		<% } %>
</TABLE>
<% } %>
<% } %>

<%!
private String wrapText (String text) {
    if (text.length() > 200) {
        text = text.substring(0, 200) + " [data string truncated]...";
    }
    if (text.length() < 60) {
        return text;
    } else  {
        StringBuffer newText = new StringBuffer(text);
        for (int i=60; i<text.length(); i+=60) {
            newText.insert(i, "<BR>");
        }
        return newText.toString();
    }
}
%>