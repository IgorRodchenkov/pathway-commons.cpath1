<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType"%>
<%
    DaoCPath dao = new DaoCPath();
    int numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
    int numPhysicalEntities = dao.getNumEntities
            (CPathRecordType.PHYSICAL_ENTITY);

%>

<TABLE WIDTH=100% CELLSPACING=4 CELLPADDING=4 BGCOLOR="#666699">
    <TR BGCOLOR=#9999cc>
        <TD COLSPAN=2><font color=#333366>DB Stats</font></TD>
    </TR>
    <TR VALIGN=TOP>
        <TD>Number of Interactions:</TD>
        <TD VALIGN=BOTTOM ALIGN=RIGHT><%= numInteractions %></TD>
    </TR>
    <TR VALIGN=TOP>
        <TD>Number of Physical Entities:</TD>
        <TD VALIGN=BOTTOM ALIGN=RIGHT><%= numPhysicalEntities %></TD>
    </TR>
</TABLE>
