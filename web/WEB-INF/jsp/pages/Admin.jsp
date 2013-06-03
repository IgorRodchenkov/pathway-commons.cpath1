<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 java.text.NumberFormat,
                 java.text.DecimalFormat,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="net.sf.ehcache.CacheManager"%>
<%@ page import="net.sf.ehcache.Cache"%>
<%@ page import="org.mskcc.pathdb.util.cache.EhCache"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>
<%@ page errorPage = "JspError.jsp" %>

<%  request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Administration"); %>
<jsp:include page="../global/redesign/header.jsp" flush="true" />
<html:errors/>

<jsp:include page="../global/redesign/adminLeft.jsp" flush="true" />

<div class="splitcontentright">
<h1>cPath Administration</h1>

<cbio:taskTable/>

<h2>Web Diagnostics</h2>
    <p>
    Web diagnostics are currently set to:
        <%
            String xdebugFlag = (String)
                    session.getAttribute(AdminWebLogging.WEB_LOGGING);
            if (xdebugFlag == null) {
                out.println("off.");
                out.println("&nbsp;&nbsp;[<a href='adminWebLogging.do'>Turn on</a>]");
            } else {
                out.println("on.");
                out.println("&nbsp;&nbsp;[<a href='adminWebLogging.do'>Turn off</a>]");
            }
        %>
    </p>
    <%
        String autoUpdate = (String) request.getAttribute
            (BaseAction.PAGE_AUTO_UPDATE);
        if (autoUpdate != null) { %>
        <p>Tasks are active.  This page will auto-update every 10 seconds
        until tasks are complete.&nbsp;
        [<a href="adminHome.do">Update Now</a>]</p>
        <% }
    %>

<h2>Web Site Status</h2>

<%
    // get WebUIBean
    out.println("<p>Web site is currently:  ");
    if (CPathUIConfig.isOnline()) {
        out.println("online");
        out.println(" [<a href='adminHome.do?action=toggleWebStatus'>Take Site Offline</a>]");
    } else {
        out.println("offline");
        out.println(" [<a href='adminHome.do?action=toggleWebStatus'>Take Site Online</a>]");
    }
    out.println("</p>");
%>

<h2>Global Cache Stats</h2>
    <%
        CacheManager manager = CacheManager.getInstance();
        Cache cache0 = manager.getCache(EhCache.PERSISTENT_CACHE);
    %>
    <table>
        <tr>
            <td>Name:</td>
            <td><%= cache0.getName() %></td>
        </tr>
        <tr>
            <td>Status:</td>
            <td><%= cache0.getStatus()%></td>
        </tr>
        <tr>
            <td>Cache overflow is stored to disk:</td>
            <td><%= cache0.isOverflowToDisk() %></td>
        </tr>
        <tr>
            <td>Cache overflow to disk is persistent:</td>
            <td><%= cache0.isDiskPersistent()%></td>
        </tr>
        <tr>
            <td>Number of elements currently in memory:</td>
            <td><%= cache0.getMemoryStoreSize() %></td>
        </tr>
        <tr>
            <td>Number of elements currently in disk store:</td>
            <td><%= cache0.getDiskStoreSize() %></td>
        </tr>
        <tr>
            <td>Maximum number of elements that can be stored in memory:</td>
            <td><%= cache0.getMaxElementsInMemory() %></td>
        </tr>
    </table>

<h2>Java Virtual Machine (JVM) Memory Usage</h2>
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
    <table>
    <tr>
        <td>Available Processors:</td>
        <td align="right"><%= runTime.availableProcessors() %></td>
    </tr>
    <tr>
        <td>Total Memory in JVM:</td>
        <td align="right"><%= formatter.format(totalMemoryMb) %> MB</td>
    </tr>
    <tr>
        <td>Max Memory allocated to JVM:</td>
        <td align="right"><%= formatter.format(maxMemoryMb) %> MB</td>
    </tr>
    <tr>
        <td>Free Memory in JVM:</td>
        <td align="right"><%= formatter.format(freeMemoryMb) %> MB</td>
    </tr>
    </table>
    <p>&nbsp;</p>
</div>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />
