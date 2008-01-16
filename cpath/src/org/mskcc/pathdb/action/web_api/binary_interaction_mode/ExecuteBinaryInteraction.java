// $Id: ExecuteBinaryInteraction.java,v 1.1 2008-01-16 03:15:12 grossben Exp $
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
import org.apache.lucene.queryParser.ParseException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.action.web_api.WebApiUtil;
import org.biopax.paxtools.io.sif.InteractionRule;
import org.biopax.paxtools.io.sif.level2.ControlRule;
import org.biopax.paxtools.io.sif.level2.ComponentRule;
import org.biopax.paxtools.io.sif.level2.ParticipatesRule;
import org.biopax.paxtools.io.sif.level2.ConsecutiveCatalysisRule;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.io.IOException;

/**
 * BioPAX Web Mode:  Response is of type BioPAX.
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class ExecuteBinaryInteraction {

    /**
     * Processes Client Request.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request         Http Servlet Request Object.
     * @param response        Http Servlet Response Object.
     * @param mapping         Struts Action Mapping Object.
     * @return Struts Action Forward Object.
     * @throws org.mskcc.pathdb.sql.query.QueryException
     *                                    Query Error.
     * @throws java.io.IOException        I/O Error.
     * @throws org.mskcc.pathdb.sql.assembly.AssemblyException
     *                                    XML Assembly Error.
     * @throws org.apache.lucene.queryParser.ParseException
     *                                    Lucene Parsing Error.
     * @throws org.mskcc.pathdb.protocol.ProtocolException
     *                                    Protocol Error.
     * @throws org.mskcc.pathdb.sql.dao.DaoException
     *                                    Database Error.
     * @throws CloneNotSupportedException Cloning Error.
     */
    public ActionForward processRequest(XDebug xdebug, ProtocolRequest protocolRequest,
            HttpServletRequest request, HttpServletResponse response, ActionMapping mapping)
            throws QueryException, IOException, AssemblyException, ParseException, ProtocolException,
            DaoException, CloneNotSupportedException {
        String xml;
        XmlAssembly xmlAssembly;
        xmlAssembly = WebApiUtil.fetchXmlAssembly(xdebug, protocolRequest);
        if (xmlAssembly == null || xmlAssembly.isEmpty()) {
            String q = protocolRequest.getQuery();
            throw new ProtocolException(ProtocolStatusCode.NO_RESULTS_FOUND,
                    "No Results Found for:  " + q);
        }

		// determine binary interaction assembly type
		BinaryInteractionAssembly.Assembly binaryInteractionAssemblyType = null;
		if (protocolRequest.getOutput().equals(ProtocolConstantsVersion2.FORMAT_BINARY_SIF)) {
			type = BinaryInteractionAssemblyFactory.AssemblyType.SIF
		}

		// contruct rule types
		List<String> binaryInteractionRuleTypes = getBinaryInteractionRuleTypes(protocolRequest);

		// get binary interaction assembly
		BinaryInteractionAssembly assembly =
			BinaryInteractionAssemblyFactory.createAssembly(binaryInteractionAssemblytype,
															binaryInteractionRuleTypes,
															xmlAssembly.getXmlString());

		// write out the binary interaction text
        WebApiUtil.returnText(response, xml);

        //  Return null here, because we do not want Struts to do any forwarding.
        return null;
    }

	private List<String> getBinaryInteractionRuleTypes(ProtocolRequest protocolRequest) {

		// list to return
		List<String> toReturn = new ArrayList<String>();

		// possible rules
		List<InteractionRule> possibleRules = Arrays.asList(new ComponentRule(),
															new ConsecutiveCatalysisRule(),
															new ControlRule(),
															new ParticipatesRule());

		// interate through each rule class and get each rule type
		for (InteractionRule rule : possibleRules) {
			for (String ruleType : rule.getRuleTypes()) {
				toReturn.add(ruleType);
			}
		}

		// outta here
		return toReturn;
	}
}
