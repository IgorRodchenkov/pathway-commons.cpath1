package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Action for Displaying Import Record XML.
 *
 * @author Ethan Cerami
 */
public class DisplayImportXml extends AdminBaseAction {

    /**
     * Executes Action.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        String importID = request.getParameter("import_id");
        if (importID != null) {
            DaoImport dbImport = new DaoImport();
            int id = Integer.parseInt(importID);
            ImportRecord record = dbImport.getRecordById(id);
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(record.getData());
        }
        return null;
    }
}