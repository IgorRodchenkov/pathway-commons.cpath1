package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.task.GlobalTaskList;
import org.mskcc.pathdb.xdebug.XDebug;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base Class for for Administrative Actions.
 *
 * @author Ethan Cerami
 */
public class AdminBaseAction extends BaseAction {

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
        LuceneIndexer indexer = new LuceneIndexer();
        String dir = indexer.getDirectory();
        xdebug.logMsg(this, "Lucene Index Directory:  " + dir);
        String action = request.getParameter("action");
        if (action != null) {
            if (action.equals("remove")) {
                removeTask(request, xdebug);
            }
        }
        return mapping.findForward("success");
    }

    private void removeTask(HttpServletRequest request, XDebug xdebug) {
        String index = request.getParameter("index");
        try {
            int i = Integer.parseInt(index);
            xdebug.logMsg(this, "Removing Task:  " + i);
            GlobalTaskList globalTaskList = GlobalTaskList.getInstance();
            globalTaskList.removeTask(i);
            setUserMessage(request, "Task Removed.");
        } catch (NumberFormatException e) {
            setUserMessage(request, "Invalid Index Number.");
        }
    }

    protected boolean isUserAuthorized(ActionMapping mapping,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws IOException {
        xdebug.logMsg(this, "Page is protected.  Available to Admin User only");
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            askForPassword(response);
            return false;
        } else {
            return validateUserPassword(authorization, xdebug, response);
        }
    }

    private boolean validateUserPassword(String authorization, XDebug xdebug,
            HttpServletResponse response) throws IOException {
        PropertyManager pManager = PropertyManager.getInstance();
        String adminUser = pManager.getProperty(BaseAction.PROPERTY_ADMIN_USER);
        String adminPassword = pManager.getProperty
                (BaseAction.PROPERTY_ADMIN_PASSWORD);
        String userInfo = authorization.substring(6).trim();
        BASE64Decoder decoder = new BASE64Decoder();
        String nameAndPassword =
                new String(decoder.decodeBuffer(userInfo));
        int index = nameAndPassword.indexOf(":");
        String user = nameAndPassword.substring(0, index);
        String password = nameAndPassword.substring(index + 1);
        xdebug.logMsg(this, "User Name:  " + user);
        xdebug.logMsg(this, "Password:  " + password);
        if (user.equals(adminUser) && password.equals(adminPassword)) {
            xdebug.logMsg(this, "User/Password Correct");
            return true;
        } else {
            askForPassword(response);
            return false;
        }
    }

    private void askForPassword(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate",
                "BASIC realm=\"cPath Admin\"");
    }
}
