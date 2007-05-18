<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="java.util.Set"%>
<%
    // get ref to protocol request
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);

    // get the ids
    Set<Long> neighbors = (Set<Long>)request.getAttribute(BaseAction.ATTRIBUTE_NEIGHBORS);

    // set content type
    res.setContentType("text/plain");

    // write out the content
    for(Long neighbor : neighbors) {
	    out.println(neighbor);
    }
%>
