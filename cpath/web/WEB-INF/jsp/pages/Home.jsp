<%@ page import="org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
    String title = "cPath Home";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title); %>

<jsp:include page="../global/header.jsp" flush="true" />

<div id="apphead">
    <h2>Welcome to cPath</h2>
</div>

<div class="h3">
    <h3>About cPath</h3>
</div>

cPath aims to be a freely available cancer pathway database. Currently, only
information about protein-protein interactions collected from major
interaction databases that support the PSI-MI format is available. cPath is
open-source and is easy to locally install for private management of
protein-protein interactions. Future directions include support for the <a
href="http://biopax.org">BioPAX format</a> so that entire pathways can be
stored, queried and presented.
<P/>
cPath is currently being developed by the
<A HREF="http://cbio.mskcc.org">Computational Biology Center</A>
of Memorial Sloan-Kettering Cancer Center.
<P/>

<div class="h3">
    <h3>About cBio</h3>
</div>

Computational biology research at Memorial Sloan-Kettering Cancer
Center (MSKCC) aims to analyze and simulate biological processes
at different levels of organization, predict the results of
interventions in biological systems and help improve the prevention,
diagnosis, prognosis and therapy of cancer. Close collaboration with
experimental and clinical groups using high-throughput and functional
genomics data is essential to the achievement of these goals.

<jsp:include page="../global/footer.jsp" flush="true" />