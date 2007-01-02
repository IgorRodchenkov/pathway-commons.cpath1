<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoOrganism"%>
<%@ page import="java.util.Map"%>
<%@ page import="org.mskcc.pathdb.model.Organism"%>
<%
    GlobalFilterSettings settings = (GlobalFilterSettings)
            session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);

    if (settings == null) {
        settings = new GlobalFilterSettings();
        session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, settings);
    }
    DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
    Iterator iterator = settings.getSnapshotIdSet().iterator();
%>
<h3>Filter Settings: [<a href='filter.do'>Update</a>]</h3>
<%
    out.println("<ul>");
    while (iterator.hasNext()) {
        Long snapshotId = (Long) iterator.next();
        ExternalDatabaseSnapshotRecord snapshotRecord = dao.getDatabaseSnapshot(snapshotId);
        out.println("<li>" + snapshotRecord.getExternalDatabase().getName());
    }
    Iterator organismIterator = settings.getOrganismTaxonomyIdSet().iterator();
    if (organismIterator.hasNext()) {
        Integer ncbiTaxonomyId = (Integer) organismIterator.next();
        if (ncbiTaxonomyId == GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE) {
            out.println("<li>All organisms</li>");
        } else {
            DaoOrganism daoOrganism = new DaoOrganism();
            Organism organism = daoOrganism.getOrganismByTaxonomyId(ncbiTaxonomyId);
            out.println("<li>" + organism.getSpeciesName() + "</li>");
        }
    }
    out.println("</ul>");
%>