<%@ page import="org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecordType"%>
<%
    DaoCPath dao = new DaoCPath();
    int numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
    int numPhysicalEntities = dao.getNumEntities
            (CPathRecordType.PHYSICAL_ENTITY);

%>

<div id="dbstats" class="toolgroup">
    <div class="label">
        <strong>Database Stats</strong>
    </div>

    <div class="body">
        <div>
            # of Interactions:  <%= numInteractions %>
        </div>
        <div>
            # of Interactors: <%= numPhysicalEntities %>
        </div>
    </div>
</div>
