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
    request.setAttribute("autoUpdate", "true");
%>
<%@ page errorPage = "JspError.jsp" %>


<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<TABLE WIDTH=100% CELLSPACING=2 CELLPADDING=2 BORDER=0>
    <TR>
        <TD>
            <H1>cPath Administrator</H1>
        </TD>
        <TD ALIGN=RIGHT>
            <SPAN CLASS="small">This page will auto-update every 10 seconds
        &nbsp;[<A HREF="adminHome.do">Update Now</A>]</SPAN>
        </TD>
    </TR>
</TABLE>

<TABLE WIDTH=100% CELLSPACING=2 CELLPADDING=2 BORDER=0>
    <TR BGCOLOR=#9999cc>
        <TD WIDTH="30%"><font color=#333366>Admin Tasks</font></TD>
        <TD WIDTH="70%"><font color=#333366>Active Task List</font></TD>
    </TR>
    <TR>
        <TD VALIGN=TOP BGCOLOR="#666699">
                  <UL>
                    <LI><A HREF="adminRunFullTextIndexer.do">Run Full Text Indexer</A>
                    </UL>
        </TD>
        <TD BGCOLOR=#DDDDDD VALIGN=TOP>
                 <cbio:taskTable/>
        </TD>
    </TR>

    <FORM ACTION="adminImportData.do" METHOD="POST"
        ENCTYPE="multipart/form-data">

    <TR>
        <TD COLSPAN=2><P>&nbsp;</TD>
    </TR>
    <TR>
        <TD COLSPAN=2><P>&nbsp;</TD>
    </TR>

    <TR BGCOLOR=#9999cc>
        <TD WIDTH="30%"><font color=#333366>DB Status</font></TD>
        <TD WIDTH="70%"><font color=#333366>Import Data</font></TD>
    </TR>

    <TR>
        <TD>

        <%
            DaoCPath dao = new DaoCPath();
            int numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
            int numPhysicalEntities = dao.getNumEntities
                    (CPathRecordType.PHYSICAL_ENTITY);

        %>

        <TABLE WIDTH=100% CELLSPACING=4 CELLPADDING=4 BGCOLOR="#666699">
            <TR VALIGN=TOP>
                <TD><SPAN CLASS="SMALL">Interactions:</SPAN></TD>
                <TD VALIGN=BOTTOM ALIGN=RIGHT><SPAN CLASS="SMALL"><%= numInteractions %>
                </SPAN></TD>
            </TR>
            <TR VALIGN=TOP>
                <TD><SPAN CLASS="SMALL">Physical Entities:</SPAN></TD>
                <TD VALIGN=BOTTOM ALIGN=RIGHT><SPAN CLASS="SMALL"><%= numPhysicalEntities %></SPAN></TD>
            </TR>
        </TABLE>


        </TD>
        <TD VALIGN=TOP>
                <INPUT TYPE="FILE" SIZE=10 NAME="file">
                &nbsp;<INPUT TYPE="SUBMIT" VALUE="Go">
                </TD>
    </TR>

    <TR BGCOLOR=#9999cc>
        <TD COLSPAN=2><font color=#333366>Web Diagnostics</font></TD>
    </TR>

    <TR>
        <TD COLSPAN=2>
        <SPAN CLASS="small">Web diagnostics are currently set to:
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
        </SPAN>
        </TD>
    </TR>

</TABLE>
<P>

<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
