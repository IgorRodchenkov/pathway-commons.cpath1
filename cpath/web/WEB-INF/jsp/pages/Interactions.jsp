<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 java.util.ArrayList,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.assembly.XmlAssembly"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath Search Results"); %>
<jsp:include page="../global/header.jsp" flush="true" />
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    XmlAssembly xmlAssembly = (XmlAssembly)
            request.getAttribute(BaseAction.ATTRIBUTE_XML_ASSEMBLY);
    ArrayList interactorList = (ArrayList)
            request.getAttribute(BaseAction.ATTRIBUTE_INTERACTOR_SET);
%>

<cbio:interactionTable xmlAssembly="<%= xmlAssembly %>"
    interactorList="<%= interactorList %>"
    protocolRequest="<%= protocolRequest %>"/>
<jsp:include page="../global/footer.jsp" flush="true" />
