package org.mskcc.pathdb.action.web_api;

import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.query.QueryManager;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Utility Methods Common to Multiple Web API Calls.
 *
 * @author Ethan Cerami
 */
public class WebApiUtil {

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
            e.printStackTrace();
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
            response.setContentType("text/xml");
            PrintWriter writer = response.getWriter();
            response.setContentLength(xmlString.length());
            writer.println(xmlString);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
