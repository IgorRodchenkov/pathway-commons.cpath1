// $Id: ExecuteWebApi.java,v 1.1 2007-09-04 18:21:08 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.action.web_api;

import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.action.web_api.psi_mode.ExecuteHtmlResponse;
import org.mskcc.pathdb.action.web_api.psi_mode.ExecuteXmlResponse;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.*;
import org.mskcc.pathdb.util.security.XssFilter;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Central controller class for executing all Web API calls and all web search requests.
 *
 * @author Ethan Cerami, Benjamin Gross
 */
public class ExecuteWebApi extends BaseAction {
    private static Logger log = Logger.getLogger(ExecuteWebApi.class);

    /**
     * Executes Web API Call for Client.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions are thrown up to the JSP page, where a custom JSP Tag:  ErrorMessage.java
     *                   handles the presentation of the error.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        return processRequest(mapping, request, response, xdebug);
    }

    /**
     * Processes Client Request.
     */
    /**
     * Processes Client Request.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws org.mskcc.pathdb.protocol.ProtocolException
     *          ProtocolExceptions are thrown up to the JSP page,
     *          where a custom JSP Tag: ErrorMessage.java handles the presentation of the error.
     */
    private ActionForward processRequest(ActionMapping mapping,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws ProtocolException {
        ProtocolRequest protocolRequest = null;
//        try {
        //  Execute XssFilter, to prevent cross-site scripting attacks.
        HashMap parameterMap = XssFilter.filterAllParameters(request.getParameterMap());

        //  Generate a ProtocolRequest object;  will contain all client parameters for web service API.
        protocolRequest = new ProtocolRequest(parameterMap);

        //  Execute the appropriate query
        xdebug.logMsg(this, "Executing Web Service API Query:  " + protocolRequest.getUri());
        //return processQuery(mapping, protocolRequest, request, response, xdebug);
//        } catch (NeedsHelpException e) {
//            //  Forward to Help Page;  removing the search result flag enables the correct tab to be shown
//            //  on the help page.
//            request.removeAttribute(BaseAction.PAGE_IS_SEARCH_RESULT);
//            return mapping.findForward(BaseAction.FORWARD_HELP);
//        }
        return null;
    }

    /**
     * Processes Client Request.
     *
     * @param mapping         Struts ActionMapping Object.
     * @param protocolRequest ProtocolRequest Object.
     * @param request         Struts Servlet Request.
     * @param response        Struts Servelt Response.
     * @param xdebug          XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Errors.
     */
    private ActionForward processQuery(ActionMapping mapping, ProtocolRequest protocolRequest,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws Exception {

        //  Create the appropriate protocol validator, based on version # indicated by the client.
        ProtocolValidator validator = new ProtocolValidator(protocolRequest);

        //  Get requested return format
        String returnFormat = protocolRequest.getFormat();

        //  First, branch based on WebUI Mode
        if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) {
            xdebug.logMsg(this, "Branching based on web mode:  WEB_MODE_PSI_MI");

            //  Then, branch based on response type
            if (returnFormat != null && returnFormat.equals(ProtocolConstants.FORMAT_HTML)) {
                xdebug.logMsg(this, "Branching based on response type:  HTML");
                ExecuteHtmlResponse.processRequest(xdebug, protocolRequest, request, response, mapping);
            } else {
                ExecuteXmlResponse.processRequest(xdebug, protocolRequest, request, response, mapping);
            }

        } else {
            xdebug.logMsg(this, "Branching based on web mode:  WEB_MODE_BIOPAX");

            //  Then, branch based on response type
            if (returnFormat != null && returnFormat.equals(ProtocolConstants.FORMAT_HTML)) {
                xdebug.logMsg(this, "Branching based on response type:  HTML");

            } else if (isResponseText(protocolRequest)) {
                xdebug.logMsg(this, "Branching based on response type:  TEXT");

                //  Do something else

            } else {
                xdebug.logMsg(this, "Branching based on response type:  XML");

                //return processXmlRequest(protocolRequest, response, validator, xdebug);

            }
        }
        return null;
    }

    /**
     * Routine which checks if the specified web service command returns plain text.
     *
     * @param protocolRequest ProtocolRequest
     * @return boolean indicates that the specified web service command returns plain text.
     */
    private boolean isResponseText(ProtocolRequest protocolRequest) {
        String command = protocolRequest.getCommand();
        if (command != null) {
            if (command.equals(ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS)) {
                if (protocolRequest.getOutput() != null
                        && protocolRequest.getOutput().equals(ProtocolRequest.ID_LIST)) {
                    return true;
                }
            } else if (command.equals(ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST)) {
                return true;
            }
        }
        return false;
    }

}
