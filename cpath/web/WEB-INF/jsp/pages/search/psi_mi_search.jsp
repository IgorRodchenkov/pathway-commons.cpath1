<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 java.util.ArrayList,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.assembly.XmlAssembly"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "../JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Search Results"); %>
<jsp:include page="../../global/redesign/header.jsp" flush="true" />
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    XmlAssembly xmlAssembly = (XmlAssembly)
            request.getAttribute(BaseAction.ATTRIBUTE_XML_ASSEMBLY);
    ArrayList interactorList = (ArrayList)
            request.getAttribute(BaseAction.ATTRIBUTE_INTERACTOR_SET);
%>


<jsp:include page="../../global/redesign/psiMiLeftColumn.jsp" flush="true" />
<div class="splitcontentright">
<cbio:interactionTable xmlAssembly="<%= xmlAssembly %>"
    interactorList="<%= interactorList %>"
    protocolRequest="<%= protocolRequest %>"/>
</div>
<jsp:include page="../../global/redesign/footer.jsp" flush="true" />

