<%@ page import="org.mskcc.pathdb.controller.ProtocolException,
                 org.mskcc.pathdb.controller.ProtocolStatusCode"%>
<%
    ProtocolException e = (ProtocolException)
            request.getAttribute("exception");
%>
<% ProtocolStatusCode statusCode = e.getStatusCode(); %>
<BLOCKQUOTE>
    <BIG><BIG>An Error has Occurred:</BIG></BIG>
    <P>
    Status Code:  <%= statusCode.getErrorCode() %>
    <BR>
    Error Message:  <%= statusCode.getErrorMsg() %>
    <BR>
    Error Details:  <%= e.getDetails() %>
</BLOCKQUOTE>
