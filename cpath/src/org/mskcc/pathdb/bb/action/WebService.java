package org.mskcc.pathdb.action.bb;

import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class HomeAction extends BaseAction {

    public ActionForward subExecute (ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, XDebug xdebug)
            throws Exception {
        String action = request.getParameter("action");
        if (action != null && action.equals("uniprot"))  {
           return executeUniProt(response);
        } else if (action != null && action.equals("protein_list")) {
            return showProteinList(response);
        } else {
            return showPathwayList(response);
        }
    }


    private ActionForward executeUniProt (HttpServletResponse response) throws IOException, DaoException {
        DaoCPath dao = DaoCPath.getInstance();
        PrintWriter writer = response.getWriter();
        ArrayList list = dao.getAllRecords(CPathRecordType.PHYSICAL_ENTITY);
        DaoExternalLink daoExternalLink = DaoExternalLink.getInstance();
        response.setContentType("text/plain");
        for (int i=0; i<list.size(); i++) {
            CPathRecord record = (CPathRecord) list.get(i);
            if (record.getSnapshotId() < 0) {
                ArrayList xrefList = daoExternalLink.getRecordsByCPathId(record.getId());
                for (int j=0; j<xrefList.size(); j++) {
                    ExternalLinkRecord xref = (ExternalLinkRecord) xrefList.get(j);
                    if (xref.getExternalDatabase().getMasterTerm().equals("UNIPROT")) {
                        writer.write(xref.getLinkedToId() +"\n");
                    }
                }
            }
        }
        return null;
    }


    private ActionForward showPathwayList (HttpServletResponse response) throws IOException, DaoException {
        PrintWriter writer = response.getWriter();
        writer.write("<html><head><title>Bare Bones:  Pathway Commons</title></head><body>");
        writer.write("<h2>Bare Bones:  Pathway Commons</h2>");
        writer.write("<h3>Bare Bones Web Service #1</h3>");
        writer.write("What pathways is Gene X involved in?");
        writer.write("<p>Enter an Entrez Gene ID, e.g. 6714, 5604, 3265: ");
        writer.write("<FORM ACTION=web_service.do>");
        writer.write("<INPUT TYPE=TEXT name=q>");
        writer.write("<INPUT TYPE=SUBMIT>");
        writer.write("<FORM>");
        writer.write("<h3>Bare Bones Web Service #2:</h3>");
        writer.write("What genes are involved in Pathway X?");
        DaoCPath dao = DaoCPath.getInstance();
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllRecords(CPathRecordType.PATHWAY);
        writer.write("<ul>");
        for (int i=0; i<list.size(); i++) {
            CPathRecord record = (CPathRecord) list.get(i);
            writer.write("<li><a href='web_service.do?action=getMembers&q="
                + record.getId() +"'> " + record.getName() + "</a>");
            ExternalDatabaseSnapshotRecord snapshot =
                    daoSnapshot.getDatabaseSnapshot(record.getSnapshotId());
            writer.write(" [" + snapshot.getExternalDatabase().getMasterTerm() + "]</li>");
        }
        writer.write("</ul>");
        writer.write("<h3>Other stuff:</h3>");
        writer.write("<UL><LI><a href='home.do?action=protein_list'>View protein list</a></LI>");
        writer.write("<LI><a href='home.do?action=uniprot'>View protein list:  UNIPROT IDs</a></LI>");
        writer.write("</UL>");
        return null;
    }

    private ActionForward showProteinList (HttpServletResponse response) throws IOException, DaoException {
        DaoCPath dao = DaoCPath.getInstance();
        PrintWriter writer = response.getWriter();
        writer.write("<html><head><title>Bare Bones:  Pathway Commons:: Protein List</title></head><body>");
        writer.write("<h3>Proteins:</h3>");
        ArrayList list = dao.getAllRecords(CPathRecordType.PHYSICAL_ENTITY);
        DaoExternalLink daoExternalLink = DaoExternalLink.getInstance();
        writer.write("<ol>");
        for (int i=0; i<list.size(); i++) {
            CPathRecord record = (CPathRecord) list.get(i);
                writer.write("<li>" + record.getSpecificType() + ": " + record.getName()
                        +" [cPath ID:  " + record.getId() + "]</li>");
                writer.write("<ul>");
                ArrayList xrefList = daoExternalLink.getRecordsByCPathId(record.getId());
                for (int j=0; j<xrefList.size(); j++) {
                    ExternalLinkRecord xref = (ExternalLinkRecord) xrefList.get(j);
                    writer.write("<li>");
                    writer.write(xref.getExternalDatabase().getMasterTerm() +":");
                    writer.write(xref.getLinkedToId());
                    writer.write("</li>");
                }
                writer.write("</ul>");
        }
        writer.write("</ol>");

        writer.write("</body></html>");
        return null;
    }
}
