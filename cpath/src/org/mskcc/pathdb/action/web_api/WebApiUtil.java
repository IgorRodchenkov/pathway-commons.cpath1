// $Id: WebApiUtil.java,v 1.9 2008-07-04 14:29:59 cerami Exp $
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

import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.query.QueryManager;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * Utility Methods Common to Multiple Web API Calls.
 *
 * @author Ethan Cerami
 */
public class WebApiUtil {
    private static Logger log = Logger.getLogger(WebApiUtil.class);

    /**
     * Fetches XML Assembly:  PSI-MI or BioPAX.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest ProtocolRequest Object.
     * @return XMLAssembly:  PSI-MI or BioPAX.
     * @throws ProtocolException Protocol Error.
     */
    public static XmlAssembly fetchXmlAssembly(XDebug xdebug,
            ProtocolRequest protocolRequest) throws ProtocolException {
        XmlAssembly xmlAssembly;
        try {
            QueryManager queryManager = new QueryManager(xdebug);
            xmlAssembly = queryManager.executeQuery(protocolRequest,
                    protocolRequest.getCheckXmlCache());
        } catch (QueryException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        }
        return xmlAssembly;
    }

    /**
     * Returns Text Response to Client.
     *
     * @param response Servlet Response.
     * @param text     Text String.
     */
    public static void returnText(HttpServletResponse response, String text) {
        try {
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.println(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Returns XML Response to Client.
     *
     * @param response  Servlet Response.
     * @param xmlString XML String
     */
    public static void returnXml(HttpServletResponse response, String xmlString) {
        try {
            log.info("Begin Return XML Document");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(xmlString.getBytes());
            response.setContentType("text/xml");
            log.info("Content Length:  " + out.size());
            response.setContentLength(out.size());
            out.writeTo(response.getOutputStream());
            out.flush();
            out.close();
            log.info("End Return XML Document:  completed without any errors.");
        } catch (IOException e) {
            log.error("IO Error", e);
        }
    }
}
