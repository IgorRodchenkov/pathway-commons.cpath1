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

cPath is a freely available cancer pathway database.  We
are building this as a resource to the community.  Currently,
basic protein-protein interaction functionality is available.
We expect to make a preliminary version of cPath available to
the public in early 2004.  In the near future, we also plan
to make cPath <A HREF="http://www.biopax.org">BioPax</A>
compatibile.
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