// $Id: ExecuteTextResponse.java,v 1.4 2007-12-28 21:09:08 cerami Exp $
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
package org.mskcc.pathdb.action.web_api.biopax_mode;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolConstantsVersion2;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.query.GetNeighborsCommand;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.query.batch.PathwayBatchQuery;
import org.mskcc.pathdb.query.batch.PhysicalEntityWithPathwayList;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;
import org.mskcc.pathdb.action.web_api.WebApiUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.ArrayList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * BioPAX Web Mode:  Response is of type Text.
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class ExecuteTextResponse {

    /**
     * Processes Client Request.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request         HttpServlet Request Object.
     * @param response        HttpServlet Response Object.
     * @param mapping         Struts Action Mapping Object.
     * @return Struts Action Forward Object.
     * @throws ProtocolException            Protocol Error.
     * @throws DaoException                 Database Error.
     * @throws IOException                  I/O Error.
     * @throws BioPaxRecordSummaryException Error creating BioPAX Record Summary.
     */
    public ActionForward processRequeset(XDebug xdebug, ProtocolRequest protocolRequest, HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping) throws ProtocolException, DaoException,
            IOException, BioPaxRecordSummaryException {

        if (protocolRequest.getCommand().equals(ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS)) {
            return getNeighborsHandler(protocolRequest, response, xdebug);
        } else if (protocolRequest.getCommand().equals
                (ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST)) {
            return getPathwayListHandler(protocolRequest, response);
        }
        return null;
    }

    /**
     * Special-Case handler for getNeighbors Command.
     */
    private ActionForward getNeighborsHandler(ProtocolRequest protocolRequest,
            HttpServletResponse response, XDebug xdebug) throws ProtocolException, IOException, DaoException {
        GetNeighborsCommand cmd = new GetNeighborsCommand(protocolRequest, xdebug);
        Set<GetNeighborsCommand.Neighbor> neighbors = cmd.getNeighbors();
        String table = cmd.outputTabDelimitedText(neighbors);
        WebApiUtil.returnText(response, table);
        return null;
    }

    /**
     * Special-Case handler for getPathwayList Command.
     */
    private ActionForward getPathwayListHandler(ProtocolRequest protocolRequest, HttpServletResponse response)
            throws ProtocolException, DaoException, BioPaxRecordSummaryException {
        PathwayBatchQuery batchQuery = new PathwayBatchQuery();
        //  Split by comma;  then make sure to trim
        String ids[] = protocolRequest.getQuery().split(",");
        for (int i=0; i< ids.length; i++) {
            ids[i] = ids[i].trim();
        }
        String dbTerm = protocolRequest.getInputIDType();
        if (dbTerm == null) {
            dbTerm = ExternalDatabaseConstants.INTERNAL_DATABASE;
        }
        String dataSources[] = protocolRequest.getDataSources();
        ArrayList<PhysicalEntityWithPathwayList> list;
        if (dataSources != null) {
            list = batchQuery.executeBatchQuery(ids, dbTerm, dataSources);
        } else {
            list = batchQuery.executeBatchQuery(ids, dbTerm);
        }
        String table = batchQuery.outputTabDelimitedText(list);
        WebApiUtil.returnText(response, table);
        return null;
	}
}
