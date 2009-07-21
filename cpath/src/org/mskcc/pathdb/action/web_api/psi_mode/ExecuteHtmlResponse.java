// $Id: ExecuteHtmlResponse.java,v 1.5 2009-07-21 16:52:57 cerami Exp $
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
import org.apache.lucene.queryParser.ParseException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.xdebug.XDebugUtil;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.action.web_api.WebApiUtil;
import org.mskcc.pathdb.lucene.PsiInteractorExtractor;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.MarshalException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * PSI_MI Web Mode:  Response is of type HTML.
 *
 * @author Ethan Cerami.
 */
public class ExecuteHtmlResponse {

    /**
     * Processes Client Request.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request         Servlet Request Object.
     * @param response        Servlet Response Object.
     * @param mapping         Struct Action Mapping Object.
     * @return ActionForward Class.
     * @throws ProtocolException   Protocol Error.
     * @throws ValidationException XML Validation Error.
     * @throws MarshalException    XML Marshaling Error.
     * @throws IOException         I/O Error.
     * @throws ParseException      XML Parsing Error.
     */
    public ActionForward processRequest(XDebug xdebug,
            ProtocolRequest protocolRequest, HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping) throws ProtocolException,
            ValidationException, MarshalException, IOException, ParseException {

        xdebug.logMsg(this, "Executing PSI-MI Search");

        //  Set PAGE_IS_SEARCH_RESULT Flag
        request.setAttribute(BaseAction.PAGE_IS_SEARCH_RESULT, BaseAction.YES);

        //  Fetch the PSI-MI XML Assembly w/ search results.
        boolean debugMode = XDebugUtil.xdebugIsEnabled(request);
        XmlAssembly xmlAssembly = WebApiUtil.fetchXmlAssembly(xdebug, protocolRequest, debugMode);
        request.setAttribute(BaseAction.ATTRIBUTE_XML_ASSEMBLY, xmlAssembly);

        //  Extract (Protein) Interactors, for display in left or right column.
        ArrayList interactorList = extractInteractors(xmlAssembly,
                protocolRequest, xdebug);
        if (interactorList != null) {
            request.setAttribute(BaseAction.ATTRIBUTE_INTERACTOR_SET, interactorList);
        }
        return mapping.findForward(CPathUIConfig.PSI_MI);
    }

    /**
     * Extracts (Protein) Interactors, for display in left or right column.
     *
     * @param xmlAssembly XMLAssembly Object.
     * @param request     Servlet Request Object.
     * @param xdebug      XDebug Object.
     * @return ArrayList of ProteinWithWeight Objects.
     * @throws ValidationException XML Validation Error.
     * @throws MarshalException    XML Marshaling Error.
     * @throws IOException         I/O Error.
     * @throws ParseException      XML Parsing Error.
     */
    private ArrayList extractInteractors(XmlAssembly xmlAssembly,
            ProtocolRequest request, XDebug xdebug) throws MarshalException,
            ValidationException, IOException, ParseException {
        EntrySet entrySet = (EntrySet) xmlAssembly.getXmlObject();
        if (entrySet != null) {
            xdebug.logMsg(this, "Extracting matching interactors");
            PsiInteractorExtractor interactorExtractor = new PsiInteractorExtractor(entrySet,
                    request.getQuery(), xdebug);
            return interactorExtractor.getSortedInteractors();
        } else {
            return new ArrayList();
        }
    }
}
