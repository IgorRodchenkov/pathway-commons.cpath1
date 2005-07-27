<%@ page import="org.mskcc.pathdb.model.ImportRecord,
                 java.util.Enumeration,
                 org.apache.struts.config.ActionConfig,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 java.text.NumberFormat,
                 java.text.DecimalFormat"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
    String title = "cPath::Administration";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title); %>

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
                <P>Currently, you can import data formatted in
                <A HREF="http://psidev.sourceforge.net/mi/xml/doc/user/">PSI-MI</A>
                Level 1 or <A HREF="http://www.biopax.org">BioPAX</A> Level 1,2
                Format.
                <P>
                <INPUT TYPE="FILE" SIZE=20 NAME="file">
                &nbsp;<INPUT TYPE="SUBMIT" VALUE="Go">

    </FORM>

    <div class="h3">
        <h3>Web Diagnostics</h3>
    </div>
    <P>
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

    <%
        String autoUpdate = (String) request.getAttribute
            (BaseAction.PAGE_AUTO_UPDATE);
        if (autoUpdate != null) { %>
        <small>Tasks are active.  This page will auto-update every 10 seconds
        until tasks are complete.&nbsp;
        [<A HREF="adminHome.do">Update Now</A>]</small>
        <% }
    %>

    <div class="h3">
        <h3>Java Virtual Machine (JVM) Memory Usage</h3>
    </div>
    <P>
    <%
        NumberFormat formatter = new DecimalFormat("#,###,###.##");
        double mb = 1048576.0;
        Runtime runTime = Runtime.getRuntime();
        long totalMemory = runTime.totalMemory();
        long maxMemory = runTime.maxMemory();
        long freeMemory = runTime.freeMemory();
        double totalMemoryMb = totalMemory / mb;
        double maxMemoryMb = maxMemory / mb;
        double freeMemoryMb = freeMemory / mb;
    %>
    <TABLE>
    <TR>
        <TD>Available Processors:</TD>
        <TD ALIGN=RIGHT><%= runTime.availableProcessors() %></TD>
    </TR>
    <TR>
        <TD>Total Memory in JVM:</TD>
        <TD ALIGN=RIGHT><%= formatter.format(totalMemoryMb) %> MB</TD>
    </TR>
    <TR>
        <TD>Max Memory allocated to JVM:</TD>
        <TD ALIGN=RIGHT><%= formatter.format(maxMemoryMb) %> MB</TD>
    </TR>
    <TR>
        <TD>Free Memory in JVM:</TD>
        <TD ALIGN=RIGHT><%= formatter.format(freeMemoryMb) %> MB</TD>
    </TR>
    </TABLE>
    <P>&nbsp;

<jsp:include page="../global/footer.jsp" flush="true" />
