package org.mskcc.pathdb.taglib;

import org.mskcc.dataservices.schemas.psi.NamesType;
import org.mskcc.dataservices.schemas.psi.Organism;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Interactor Information.
 *
 * @author Ethan Cerami
 */
public class InteractorTable extends HtmlTable {
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
            String xml = record.getXmlContent();
            StringReader reader = new StringReader(xml);
            String title = "Interactor:  " + record.getName();
            ProteinInteractorType interactor =
                    ProteinInteractorType.unmarshalProteinInteractorType
                    (reader);
            outputHeader(title);
            outputName(interactor.getNames());
            outputOrganism(interactor.getOrganism());
            outputExternalReferences(id);
            endTable();
        }
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
}