<%@ page import="org.mskcc.pathdb.controller.ProtocolRequest,
                 java.util.ArrayList,
                 org.mskcc.pathdb.controller.ProtocolConstants,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.assembly.XmlAssembly"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath Search Results"); %>
<jsp:include page="../global/header.jsp" flush="true" />
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute("protocol_request");
    XmlAssembly xmlAssembly = (XmlAssembly)
            request.getAttribute(BaseAction.ATTRIBUTE_XML_ASSEMBLY);
%>

<cbio:interactionTable xmlAssembly="<%= xmlAssembly %>"
        protocolRequest="<%= protocolRequest %>"/>
<jsp:include page="../global/footer.jsp" flush="true" />
