package org.mskcc.pathdb.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.DatabaseImport;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AdminDisplay extends BaseAction {

    public ActionForward subExecute(ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        String importID = request.getParameter("import_id");
        if (importID != null) {
            DatabaseImport dbImport = new DatabaseImport ();
            int id = Integer.parseInt(importID);
            ImportRecord record = dbImport.getImportRecordById(id);
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(record.getData());
            return null;
        } else {
            return actionMapping.findForward("display");
        }
    }
}