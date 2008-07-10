// $Id: ExecuteBinaryInteraction.java,v 1.7 2008-07-10 15:14:58 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2008 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
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
package org.mskcc.pathdb.action.web_api.binary_interaction_mode;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.mskcc.pathdb.protocol.ProtocolConstantsVersion2;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.action.web_api.WebApiUtil;
import org.mskcc.pathdb.schemas.binary_interaction.util.BinaryInteractionUtil;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssembly;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssemblyFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;

/**
 * Binary Interaction Web Mode:  Response is Binary Interaction.
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class ExecuteBinaryInteraction {
    private static Logger log = Logger.getLogger(ExecuteBinaryInteraction.class);

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

        XmlAssembly xmlAssembly = WebApiUtil.fetchXmlAssembly(xdebug, protocolRequest);
        if (xmlAssembly == null || xmlAssembly.isEmpty()) {
            String q = protocolRequest.getQuery();
            throw new ProtocolException(ProtocolStatusCode.NO_RESULTS_FOUND,
                    "No Results Found for:  " + q);
        }

		// determine binary interaction assembly type
		BinaryInteractionAssemblyFactory.AssemblyType binaryInteractionAssemblyType = null;
		if (protocolRequest.getOutput().equals(ProtocolConstantsVersion2.FORMAT_BINARY_SIF)) {
			binaryInteractionAssemblyType = BinaryInteractionAssemblyFactory.AssemblyType.SIF;
		}

		// contruct rule types
		List<String> binaryInteractionRuleTypes = getRuleTypes(protocolRequest);

		// get binary interaction assembly
		BinaryInteractionAssembly assembly =
			BinaryInteractionAssemblyFactory.createAssembly(binaryInteractionAssemblyType,
															binaryInteractionRuleTypes,
															xmlAssembly.getXmlString());

		// write out the binary interaction text
        WebApiUtil.returnText(response, assembly.getBinaryInteractionString());

        //  Return null here, because we do not want Struts to do any forwarding.
        return null;
    }

	/**
	 * Gets list of binary interaction rule types for all rule classes
	 * display (ie web api docs, protocol request argument validation).
	 *
	 * Rules are in Rule.RuleType format, this code changes
	 * with "_".
	 *
	 * @return List<String>
	 */
	public static List<String> getRuleTypesForDisplay() {

		// get rule types
		List<String> ruleTypes = BinaryInteractionUtil.getRuleTypes();

		// sort
		Collections.sort(ruleTypes);

		// outta here
		return ruleTypes;
	}

	/**
	 * Given a rule type, returns the rule type description.
	 *
	 * @param ruleType String
	 * @return String
	 */
	public static String getRuleTypeDescription(String ruleType) {
		return BinaryInteractionUtil.getRuleTypeDescription(ruleType);
	}

	/*
	 * This code takes a rule in display format (getRuleTypesForDisplay()),
	 * and returns a rule type valid for paxtools consumption
	 *
	 * @param protocolRequest ProtocolRequest
	 * @return List<String>
	 */
	private List<String> getRuleTypes(ProtocolRequest protocolRequest) {

		// list to return
		List<String> toReturn = null;

        String[] binaryInteractionRules = protocolRequest.getBinaryInteractionRules();
		if (binaryInteractionRules != null) {
			toReturn = new ArrayList<String>();
			for (String ruleType : binaryInteractionRules) {
				toReturn.add(ruleType);
			}
		}
		else {
			toReturn = BinaryInteractionUtil.getRuleTypes();
		}

		// outta here
		return toReturn;
	}
}
