// $Id: ExecutePsiMiResponse.java,v 1.3 2007-09-12 14:57:22 cerami Exp $
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
package org.mskcc.pathdb.action.web_api.psi_mode;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.*;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.action.web_api.WebApiUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PSI_MI Web Mode:  Response is of type: PSI-MI.
 *
 * @author Ethan Cerami.
 */
public class ExecutePsiMiResponse {

    /**
     * Processes Client Request.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request         Servlet Request Object.
     * @param response        Servlet Response Object.
     * @param mapping         Struct Action Mapping Object.
     * @return ActionForward Class.
     * @throws ProtocolException Protocol Error.
     */
    public ActionForward processRequest(XDebug xdebug,
            ProtocolRequest protocolRequest, HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping) throws ProtocolException {
        String xml = null;
        XmlAssembly xmlAssembly = null;
        xmlAssembly = WebApiUtil.fetchXmlAssembly(xdebug, protocolRequest);
        if (xmlAssembly == null || xmlAssembly.isEmpty()) {
            String q = protocolRequest.getQuery();
            if (q == null && protocolRequest.getOrganism() != null) {
                q = protocolRequest.getOrganism();
            } else if (q == null) {
                q = protocolRequest.getCommand();
            }
            throw new ProtocolException(ProtocolStatusCode.NO_RESULTS_FOUND,
                    "No Results Found for:  " + q);
        }
        //  Return Number of Hits Only or Complete XML.
        xml = xmlAssembly.getXmlString();
        if (protocolRequest.getFormat() != null && protocolRequest.getFormat().
                equals(ProtocolConstantsVersion1.FORMAT_COUNT_ONLY)) {
            WebApiUtil.returnText(response, Integer.toString(xmlAssembly.getNumHits()));
        } else {
            WebApiUtil.returnXml(response, xml);
        }

        //  Return null here, because we do not want Struts to do any forwarding.
        return null;
    }
}
