package org.mskcc.pathdb.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.DatabaseImport;
import org.mskcc.pathdb.sql.TransferImportToGrid;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AdminTransfer extends BaseAction {

    public ActionForward subExecute(ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        TransferImportToGrid transfer = new TransferImportToGrid(true);
        transfer.transferData();
        this.setUserMessage(request, "Transfer Complete");
        return actionMapping.findForward("display");
    }
}