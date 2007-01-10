<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary"%>
<%@ page import="org.mskcc.pathdb.taglib.ReactomeCommentUtil"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils"%>
<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.model.*"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="org.mskcc.pathdb.taglib.ReferenceUtil"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
BioPaxRecordSummary bpSummary = (BioPaxRecordSummary) request.getAttribute("BP_SUMMARY");
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<div class="splitcontentright">
<h1>Out of scope error:</h1>
<p>The record you have requested is from:
<%= bpSummary.getExternalDatabaseSnapshotRecord().getExternalDatabase().getName()%>.
However, your filter settings (shown on the left) currently exclude data from this data source.
</p>
<p>Please try <a href="filter.do">updating your current filter settings</a> and try again.
</p>
</div>
<div class="splitcontentleft">
<jsp:include page="../global/currentFilterSettings.jsp" flush="true" />
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
