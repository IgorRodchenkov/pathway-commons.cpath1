package org.mskcc.pathdb.taglib;

import org.mskcc.dataservices.schemas.psi.NamesType;
import org.mskcc.dataservices.schemas.psi.Organism;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.query.ExecuteQuery;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryResult;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Interactor Information.
 *
 * @author Ethan Cerami
 */
public class CPathDetailsTable extends HtmlTable {
    private long cpathId;

    /**
     * Executes JSP Custom Tag
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        String id = pageContext.getRequest().getParameter("id");
        if (id != null) {
            cpathId = Integer.parseInt(id);
            DaoCPath cpath = new DaoCPath();
            CPathRecord record = cpath.getRecordById(cpathId);
            String title = "Details:  " + record.getName();
            outputHeader(title);

            if (record.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                String xml = record.getXmlContent();
                StringReader reader = new StringReader(xml);
                ProteinInteractorType interactor =
                        ProteinInteractorType.unmarshalProteinInteractorType
                        (reader);
                outputName(interactor.getNames());
                outputOrganism(interactor.getOrganism());
                outputExternalReferences(id);

            } else if (record.getType().equals(CPathRecordType.INTERACTION)) {
                startTable();
                String headers[] = {
                    "Interactor", "Interactor", "Experimental System",
                    "PubMed Reference"};
                createTableHeaders(headers);
                outputInteraction ();
            }
        }
        endTable();
    }

    private void outputHeader(String title) {
        this.createHeader(title);
        this.startTable();
    }

    private void outputName(NamesType names) {
        startRow(0);
        String shortLabel = names.getShortLabel();
        if (shortLabel != null && shortLabel.length() > 0) {
            outputDataField("Short Name:  ");
            outputDataField(shortLabel);
        }
        endRow();
        startRow(0);
        String fullName = names.getFullName();
        if (fullName == null || fullName.length() == 0) {
            fullName = "Not Specified";
        }
        outputDataField("Full Name:");
        outputDataField(fullName);
        endRow();
    }

    private void outputOrganism(Organism organism) {
        startRow(0);
        this.outputDataField("Organism:  ");
        NamesType names = organism.getNames();
        if (names != null) {
            String shortName = names.getShortLabel();
            String fullName = names.getFullName();
            append("<TD>");
            if (fullName != null && fullName.length() > 0) {
                append(fullName);
            } else if (shortName != null && shortName.length() > 0) {
                append(shortName);
            } else {
                append("Not Specified");
            }
        } else {
            append("Not specified");
        }
        append("</TD>");
        endRow();
    }

    /**
     * Outputs External References.
     */
    private void outputExternalReferences(String id) throws DaoException {
        startRow(1);
        outputDataField("External References:");
        DaoExternalLink dao = new DaoExternalLink();
        ArrayList links = dao.getRecordsByCPathId(cpathId);
        append("<TD VALIGN=TOP><UL>");
        for (int i = 0; i < links.size(); i++) {
            ExternalLinkRecord link = (ExternalLinkRecord) links.get(i);
            ExternalDatabaseRecord db = link.getExternalDatabase();
            append("<LI>" + db.getName() + ": ");
            if (link.getWebLink() != null) {
                outputLink(link.getLinkedToId(), link.getWebLink(),
                        db.getDescription());
            } else {
                append(link.getLinkedToId());
            }
        }
        if (links.size() == 0) {
            append("No External References Specified");
        }
        append("</UL></TD>");
        startRow(1);
        String url = this.getInteractionLink
                (id, ProtocolConstants.FORMAT_HTML);
        this.outputDataField("Interactions");
        this.outputDataField("View All Interactions", url);
        endRow();
    }

    private void outputInteraction() throws QueryException {
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery(Long.toString(cpathId));
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID);
        ExecuteQuery query = new ExecuteQuery(new XDebug());
        QueryResult result = query.executeQuery(request, true);
        ArrayList interactions = result.getInteractions();
        outputInteractions (interactions);
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputInteractions(ArrayList interactions) {
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            ArrayList interactors = interaction.getInteractors();

            startRow(i);
            Interactor interactor = (Interactor) interactors.get(0);
            outputInteractor(interactor);

            interactor = (Interactor) interactors.get(1);
            outputInteractor(interactor);

            String expSystem = (String) interaction.getAttribute
                    (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
            outputDataField(expSystem);
            String pmid = (String) interaction.getAttribute
                    (InteractionVocab.PUB_MED_ID);
            String url = getPubMedLink(pmid);
            outputDataField(pmid, url);
            endRow();
        }
    }

    private void outputInteractor(Interactor interactor) {
        if (interactor != null) {
            String url = "interactor.do?id=" + interactor.getAttribute
                    (InteractorVocab.LOCAL_ID);
            String name = interactor.getName();
            String desc = (String) interactor.getAttribute
                    (InteractorVocab.FULL_NAME);
            String org = (String) interactor.getAttribute
                    (InteractorVocab.ORGANISM_SPECIES_NAME);
            if (org == null) {
                org = (String) interactor.getAttribute
                        (InteractorVocab.ORGANISM_COMMON_NAME);
            }
            StringBuffer interactorHtml = new StringBuffer();
            interactorHtml.append("<A HREF='" + url + "'>"
                    + name + "</A><br/><ul>");
            if (desc != null) {
                interactorHtml.append("<li>" + desc + "</li>");
            }
            if (org != null) {
                interactorHtml.append("<li>Organism:  " + org + "</li>");
            }
            interactorHtml.append("</ul>");
            outputDataField(interactorHtml.toString());
        }
    }

    /**
     * Gets PubMedLink.
     * @param pmid PMID.
     * @return URL to PubMed.
     */
    private String getPubMedLink(String pmid) {
        String url = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?"
                + "cmd=Retrieve&db=PubMed&list_uids=" + pmid + "&dopt=Abstract";
        return url;
    }
}