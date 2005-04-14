package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.util.ArrayList;

/**
 * Bare Bones cPath Web Site.
 *
 * @author Ethan Cerami
 */
public class BareBonesWeb extends BaseAction {

    public ActionForward subExecute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws Exception {
        DaoCPath dao = new DaoCPath();
        String id = request.getParameter("id");
        CPathRecord record = null;
        if (id != null) {
            xdebug.logMsg(this, "Using cPath ID:  " + id);
            record = dao.getRecordById(Long.parseLong(id));
            xdebug.logMsg(this, "Got cPath Record:  " + record.getName());
            request.setAttribute("RECORD", record);
        } else {
            xdebug.logMsg(this, "Getting all pathways");
            ArrayList pathwayList = dao.getAllRecords(CPathRecordType.PATHWAY);
            xdebug.logMsg(this, "Number of Pathways Found:  "
                    + pathwayList.size());
            request.setAttribute("RECORDS", pathwayList);
        }

        String format = request.getParameter("format");
        if (format != null && format.equalsIgnoreCase("xml")) {
            response.setContentType("text/xml");
            ServletOutputStream stream = response.getOutputStream();
            stream.println(record.getXmlContent());
            stream.flush();
            stream.close();
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
