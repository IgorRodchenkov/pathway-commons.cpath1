<%@ page import="org.mskcc.pathdb.sql.dao.DaoImport,
                 org.mskcc.pathdb.model.ImportRecord,
                 java.util.Enumeration,
                 org.apache.struts.config.ActionConfig,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
    request.setAttribute(BaseAction.PAGE_IS_ADMIN, "true");
    request.setAttribute(BaseAction.PAGE_AUTO_UPDATE, "true");
%>
<%@ page errorPage = "JspError.jsp" %>


<jsp:include page="../global/header.jsp" flush="true" />

<div id="apphead">
    <h2>cPath Administration</h2>
</div>

<cbio:taskTable/>

    <div class="h3">
        <h3>Import Data</h3>
    </div>
    <FORM ACTION="adminImportData.do" METHOD="POST"
        ENCTYPE="multipart/form-data">
                <P>Currently, you can import interaction
                records formatted in the PSI-MI XML Format (see
                <A HREF="jsp/sampleData/dipSample.xml">example</A>).
                You can also import text files containing
                external references (see
                <A HREF="jsp/sampleData/affySample.txt">example</A>).
                <P>
                <INPUT TYPE="FILE" SIZE=10 NAME="file">
                &nbsp;<INPUT TYPE="SUBMIT" VALUE="Go">

    </FORM>

    <div class="h3">
        <h3>Web Diagnostics</h3>
    </div>

    Web diagnostics are currently set to:
        <%
            String xdebugFlag = (String)
                    session.getAttribute(AdminWebLogging.WEB_LOGGING);
            if (xdebugFlag == null) {
                out.println("off.");
                out.println("&nbsp;&nbsp;[<A HREF='adminWebLogging.do'>Turn on</A>]");
            } else {
                out.println("on.");
                out.println("&nbsp;&nbsp;[<A HREF='adminWebLogging.do'>Turn off</A>]");
            }
        %>
        <P>&nbsp;
        <P>
<small>This page will auto-update every 10 seconds
&nbsp;[<A HREF="adminHome.do">Update Now</A>]</small>


<jsp:include page="../global/footer.jsp" flush="true" />
