package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.sql.transfer.TransferExternalLinks;
import org.mskcc.pathdb.sql.transfer.TransferImportToCPath;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;

/**
 * Action for Transferring Import Records.
 *
 * @author Ethan Cerami
 */
public class AdminTransfer extends BaseAction {

    /**
     * Executes Action.
     * @param mapping Struts ActionMapping Object.
     * @param form Struts ActionForm Object.
     * @param request Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        String action = request.getParameter("action");
        if (action.equals("transfer1")) {
            TransferImportToCPath transfer = new TransferImportToCPath
                    (false, xdebug);
            transfer.transferData();
        } else {
            TransferExternalLinks transfer = new TransferExternalLinks
                    (false, xdebug);
            transfer.transferData();
        }
        this.setUserMessage(request, "Transfer Complete");
        return mapping.findForward("display");
    }
}