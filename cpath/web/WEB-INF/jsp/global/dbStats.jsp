<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.util.XssFilter"%>
<%
    DaoCPath dao = new DaoCPath();
    int numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
    int numPhysicalEntities = dao.getNumEntities
            (CPathRecordType.PHYSICAL_ENTITY);
    String interactions = XssFilter.filter(Integer.toString(numInteractions));
    String interactors = XssFilter.filter(Integer.toString(numPhysicalEntities));

%>

<div id="dbstats" class="toolgroup">
    <div class="label">
        <strong>Database Stats</strong>
    </div>

    <div class="body">
        <div>
            # of Interactions:  <%= interactions %>
        </div>
        <div>
            # of Interactors: <%= interactors %>
        </div>
    </div>
</div>
