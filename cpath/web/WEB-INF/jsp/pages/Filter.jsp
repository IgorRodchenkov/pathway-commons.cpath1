<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.lucene.LuceneConfig"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoOrganism"%>
<%@ page import="java.util.List"%>
<%@ page import="org.mskcc.pathdb.model.Organism"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Filter"); %>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="content">
<h1>Restrict my search results to the following data sources:</h1>

<form action="storeFilters.do">
    <table CELLPADDING=0 CELLSPACING=5>
    <%
    String referer = request.getHeader("Referer");
    if (referer != null && ! (referer.indexOf("filter.do") > 0)) {
        session.setAttribute("Referer", referer);
    }
    GlobalFilterSettings settings = (GlobalFilterSettings)
            session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
    if (settings == null) {
        settings = new GlobalFilterSettings();
        session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, settings);
    }
    DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
    ArrayList list = dao.getAllDatabaseSnapshots();

    // process records
    if (list.size() == 0) {
        out.println("<TR>");
        out.println("<TD>No Data Sources Available</TD>");
        out.println("</TR>");
    } else {
        for (int i = 0; i < list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            out.println("<TR><TD>");
            out.println("<INPUT TYPE=CHECKBOX NAME=SNAPSHOT_ID VALUE="
                + snapshotRecord.getId());
            if (settings.isSnapshotSelected(snapshotRecord.getId())) {
                out.println(" CHECKED");
            }
            out.println(">");
            out.println("&nbsp;&nbsp;" +
                    DbSnapshotInfo.getDbSnapshotHtml(snapshotRecord.getId()));
            out.println("</INPUT>");
            out.println("</TD></TR>");
        }
    }
    %>
    </table>

<h1>Restrict my search results to the following organisms:</h1>
    <table CELLPADDING=0 CELLSPACING=5>
    <%
        DaoOrganism daoOrganism = new DaoOrganism();
        List organismList = daoOrganism.getAllOrganisms();
		ArrayList<StringBuffer> organismRadioButtonList = new ArrayList<StringBuffer>();
		boolean organismSelected = false;
        for (int i=0; i<organismList.size(); i++) {
			StringBuffer organismRadioButton = new StringBuffer();
            Organism organism = (Organism) organismList.get(i);
            organismRadioButton.append("<TR><TD>\n");
            organismRadioButton.append("<INPUT TYPE=RADIO NAME=ORGANISM_TAXONOMY_ID VALUE="
                + organism.getTaxonomyId());
            if (settings.isOrganismSelected(organism.getTaxonomyId())) {
                organismRadioButton.append(" CHECKED");
				organismSelected = true;
            }
            organismRadioButton.append(">\n");
            organismRadioButton.append("&nbsp;&nbsp;" + organism.getSpeciesName() + "\n");
            organismRadioButton.append("</INPUT>\n");
            organismRadioButton.append("</TD></TR>\n");
            organismRadioButtonList.add(organismRadioButton);
        }
		// create the "all organism" radio button
		String checked = (organismSelected) ? "" : " CHECKED";
		StringBuffer allOrganismsRadioButton = new StringBuffer();
		allOrganismsRadioButton.append("<TR><TD>\n<INPUT TYPE=RADIO NAME=ORGANISM_TAXONOMY_ID VALUE=" +
		String.valueOf(GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE) +
		checked + ">\n&nbsp;&nbsp;All organisms</INPUT>\n</TD></TR>\n");
		// prepend the "all organism" radio button to the organismRadioButtonList
        organismRadioButtonList.add(0, allOrganismsRadioButton);
		// lets output the entire organismRadioButtonList
		for (StringBuffer strBuffer : organismRadioButtonList) {
            out.println(strBuffer.toString());
        }
    %>
    </table>

<table CELLPADDING=0 CELLSPACING=5>
    <TR>
        <TD><BR>
            <INPUT TYPE=SUBMIT VALUE='Set Global Filters'>
        </TD>
    </TR>
</table>

</form>
</div>
<jsp:include page="../global/footer.jsp" flush="true" />
