<%@ page import="java.io.PrintWriter"%>
<%@ page isErrorPage = "true" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />

<TABLE WIDTH="100%" CELLPADDING=5 CELLSPACING=5>
	<TR>
		<TD>
		<B><BIG><U>An Error has Occurred:</U>
        <%= exception.getMessage() %></BIG></B>
    	</TD>
	</TR>
    <TR>
        <TD>
            <FONT SIZE=-1>
            <PRE>
<% exception.printStackTrace(new PrintWriter(out)); %>
            </PRE>
            </FONT>
        </TD>
    </TR>
</TABLE>

<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
