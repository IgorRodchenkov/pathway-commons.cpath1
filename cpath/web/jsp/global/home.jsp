<%@ page import="org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
    String numInteractions = (String) request.getAttribute
            (HomeAction.NUM_INTERACTIONS);
    String numPhysicalEntities = (String) request.getAttribute
            (HomeAction.NUM_PHYSICAL_ENTITIES);
%>
<table width=100% cellpadding=3 cellspacing=3 border=0>
    <tr>
        <td bgcolor=#666699>
        <B><FONT SIZE=+3>Welcome to cPath</FONT></B>
        </td>
        <td align=right bgcolor=#666699>
        <B><FONT SIZE=+1>cPath Version:  0.1 Beta</FONT></B>
        </td>
    </tr>
    <tr>
        <td colspan=2>
            <TABLE WIDTH=200 CELLSPACING=1 CELLPADDING=1
                    BGCOLOR=BLACK ALIGN=RIGHT HSPACE=5>
                <TR><TD>
                <TABLE WIDTH=199 CELLSPACING=4 CELLPADDING=4 BGCOLOR="#9999cc">
                <TR VALIGN=TOP>
                    <TD COLSPAN=2><h3><U><font color=#333366>
                        Database Metrics:
                        </U></h3></font>
                    </TD>
                </TR>
                <TR VALIGN=TOP>
                    <TD><font color=#333366>Number of Interactions:</font></TD>
                    <TD VALIGN=BOTTOM ALIGN=RIGHT><font color=#333366><%= numInteractions %></font></TD>
                </TR>
                <TR VALIGN=TOP>
                    <TD><font color=#333366>Number of Physical Entities:</font></TD>
                    <TD VALIGN=BOTTOM ALIGN=RIGHT><font color=#333366><%= numPhysicalEntities %></font></TD>
                </TR>
                </TABLE>
                </TD>
                </TR>
            </TABLE>
            cPath is a freely available cancer pathway database.  We
            are building this as a resource to the community.  Currently,
            basic protein-protein interaction functionality is available.
            We expect to make a preliminary version of cPath available to
            the public in early 2004.  In the near future, we also plan
            to make cPath <A HREF="http://www.biopax.org">BioPax</A>
            compatibile.
            <P>
            cPath is currently being developed by the
            <A HREF="http://cbio.mskcc.org">Computational Biology Center</A>
            of Memorial Sloan-Kettering Cancer Center.
            <P>
            <FONT SIZE=-1>The beta version of cPath currently provides
            real-time web diagnostics.  This enables you to help diagnose
            errors if they occur. Web diagnostics are currently set to:
            <%
                String xdebugFlag = (String)
                        session.getAttribute(AdminWebLogging.WEB_LOGGING);
                if (xdebugFlag == null) {
                    out.println("off.");
                    out.println("&nbsp;&nbsp;[<A HREF='adminWebLogging.do'>Activate</A>]");
                } else {
                    out.println("on.");
                    out.println("&nbsp;&nbsp;[<A HREF='adminWebLogging.do'>Deactivate</A>]");
                }
            %>
            </FONT>
        </td>
    </tr>
</table>
<cbio:importTable adminView="no"/>