<%@ page import="org.mskcc.pathdb.xdebug.XDebug,
                 org.mskcc.pathdb.xdebug.XDebugMessage,
                 org.mskcc.pathdb.xdebug.XDebugParameter,
                 java.util.Date,
                 java.util.Vector,
                 java.util.Enumeration,
                 org.mskcc.pathdb.action.admin.AdminWebLogging" %>
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

<div id="bodycol">
<div id="projecthome" class="app">
<div class="h3">
    <h3>cPath Web Diagnostics</h3>
</div>

<TABLE width="100%">
	<TR>
		<TH>
            Total Time to Generate Page
        </TH>
		<TD COLSPAN=2>
            <%= xdebug.getTimeElapsed() %> ms
        </TD>
    </TR>
    <TR>
		<TH>
            Current Time
        </TH>
		<TD COLSPAN=2>
            <%= new Date() %> ms
        </TD>
	</TR>
	<TR>
		<TH>
            Class Name
        </TH>
		<TH COLSPAN=2>
            Message
        </TH>
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
                <%= msg.getClassName() %>
            </TD>
			<TD COLSPAN=2 WIDTH=70%>
				<FONT COLOR="<%= msg.getColor() %>">
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
	<TR>
		<TH>Parameter Type</FONT></TH>
		<TH>Name</FONT></TH>
		<TH>Value</FONT></TH>
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
			<TD VALIGN=TOP><SMALL><%= param.getTypeName() %></SMALL></TD>
			<TD VALIGN=TOP><SMALL><%= param.getName() %></SMALL></TD>
			<TD VALIGN=TOP><SMALL><%= wrapText(param.getValue()) %></SMALL></TD>
		</TR>
		<% } %>
</TABLE>
<% } %>
<% } %>

<%!
private String wrapText (String text) {
    if (text != null) {
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
    } else {
        return new String ("Not Available");
    }
}
%>
<P>&nbsp;
<P>&nbsp;
</div>
</div>