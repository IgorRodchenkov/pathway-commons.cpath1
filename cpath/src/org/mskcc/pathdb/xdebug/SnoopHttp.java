package org.mskcc.pathdb.xdebug;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

/**
 * Action for Snooping on the Current HTTP Request.
 * The Snoop class "snoops" on the current HTTP Request, and places
 * its results in the XDebug object.  The class currently snoops on
 * the following sets of data:
 * <UL>
 * <LI>Server Information, including server name, server port, and
 * whether the request is secure or not
 * <LI>Client Information, including the remote IP address, and
 * remote host name
 * <LI>All HTTP Information, including the servlet name, protocol version.
 * and the HTTP method type (e.g. POST or GET)
 * <LI>All HTTP Headers, including the user-agent for identifying
 * the browser type
 * <LI>All HTTP Parameters
 * <LI>All Cookies
 * </UL>
 * @author Ethan Cerami
 */
public class SnoopHttp {
    private XDebug xdebug;

    /**
     *  Constructor
     *  @param xdebug XDebug object for real-time debugging
     */
    public SnoopHttp(XDebug xdebug) {
        this.xdebug = xdebug;
    }

    /**
     * Performs snooping on the current HTTP Request
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @throws IOException if an input/output error occurs
     * @throws ServletException if a servlet exception occurs
     */
    public void process(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        extractServerInformation(request);
        extractClientInformation(request);
        extractHTTPInformation(request);
        extractUserParameters(request);
        extractCookies(request);
        extractSession(request);
    }

    /**
     * Extracts Server Information from the current HTTP Request.
     * @param request The HTTP request we are processing
     */
    private void extractServerInformation(HttpServletRequest request) {
        xdebug.addParameter(XDebugParameter.ENVIRONMENT_TYPE,
                "Server Name", request.getServerName());
        xdebug.addParameter(XDebugParameter.ENVIRONMENT_TYPE,
                "Server Port", request.getServerPort());
        xdebug.addParameter(XDebugParameter.ENVIRONMENT_TYPE,
                "Request Is Secure", request.isSecure());
    }

    /**
     * Extracts Client Information from the current HTTP Request.
     * @param request The HTTP request we are processing
     */
    private void extractClientInformation(HttpServletRequest request) {
        xdebug.addParameter(XDebugParameter.ENVIRONMENT_TYPE,
                "Remote IP Address", request.getRemoteAddr());
        xdebug.addParameter(XDebugParameter.ENVIRONMENT_TYPE,
                "Remote Host Name", request.getRemoteHost());
    }

    /**
     * Extracts HTTP Information and Headers from the current HTTP Request.
     * @param request The HTTP request we are processing
     */
    private void extractHTTPInformation(HttpServletRequest request) {
        xdebug.addParameter(XDebugParameter.HTTP_TYPE,
                "Servlet Name", request.getServletPath());
        xdebug.addParameter(XDebugParameter.HTTP_TYPE,
                "Protocol", request.getProtocol());
        xdebug.addParameter(XDebugParameter.HTTP_TYPE,
                "HTTP Method", request.getMethod());
        Enumeration enum = request.getHeaderNames();
        while (enum.hasMoreElements()) {
            String headerName = (String) enum.nextElement();
            String value = request.getHeader(headerName);
            //  Filter out referer;  not really important
            if (!(headerName.equals("referer")
                    || headerName.equals("accept"))) {
                xdebug.addParameter(XDebugParameter.HTTP_HEADER_TYPE,
                        headerName, value);
            }
        }
    }

    /**
     * Extracts User Parameters from the current HTTP Request.
     * @param request The HTTP request we are processing
     */
    private void extractUserParameters(HttpServletRequest request) {
        //xdebug.logMsg (this, "Extracting User Parameters");
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = request.getParameter(name);
            xdebug.addParameter(XDebugParameter.USER_TYPE, name, value);
        }
    }

    /**
     * Extracts Cookies from the current HTTP Request.
     * @param request The HTTP request we are processing
     */
    private void extractCookies(HttpServletRequest request) {
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                String value = new String("Value:  " + cookie.getValue());
                xdebug.addParameter(XDebugParameter.COOKIE_TYPE,
                        cookie.getName(), value);
            }
        }
    }

    /**
     * Extracts Session Information for the current HTTP Request.
     * @param request The HTTP request we are processing
     */
    private void extractSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            xdebug.addParameter(XDebugParameter.SESSION_TYPE,
                    "Session", "No Session Available");
        } else {
            xdebug.addParameter(XDebugParameter.SESSION_TYPE,
                    "Session", "Session Created:  "
                    + new Date(session.getCreationTime()));
            Enumeration enum = session.getAttributeNames();
            while (enum.hasMoreElements()) {
                String name = (String) enum.nextElement();
                Object object = session.getAttribute(name);
                xdebug.addParameter(XDebugParameter.SESSION_TYPE,
                        name, object.toString());
            }
        }
    }
}