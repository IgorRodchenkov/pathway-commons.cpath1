package org.mskcc.pathdb.xdebug;

import org.mskcc.pathdb.action.admin.AdminWebLogging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * XDebug Utility.
 */
public class XDebugUtil {

    /**
     * Determines if XDebug is currently enabled.
     * @param request Http Servlet Request.
     * @return true or false.
     */
    public static boolean xdebugIsEnabled (HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null){
            queryString = "";
        }
        HttpSession session = request.getSession();
        String xdebugFlag = (String)session.getAttribute(AdminWebLogging.WEB_LOGGING);
        boolean debugMode = (queryString.indexOf("debug=1") != -1 || xdebugFlag != null);
        return debugMode;
    }
}
