// $Id: GetNeighborsCommand.java,v 1.16 2009-03-13 12:40:15 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.sql.query;

// imports
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.util.NeighborsUtil;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Gets Neighbors of given physical entity.
 *
 * @author Benjamin Gross
 */
public class GetNeighborsCommand extends Query {

	/**
	 * ref to no neighbors found string
	 */
	public static String NO_NEIGHBORS_FOUND = "NO_NEIGHBORS_FOUND";

	/**
	 * ref to no matching external id string
	 */
	public static String NO_MATCHING_EXTERNAL_ID_FOUND = "EXTERNAL_ID_NOT_FOUND";

	/**
	 * ref to XDebug
	 */
    private XDebug xdebug;

	/**
	 * ref to httpservlet request
	 */
    private HttpServletRequest httpRequest;

	/**
	 * ref to protocol request
	 */
    private ProtocolRequest protocolRequest;

	/**
	 * cook input id ?
	 */
	private boolean cookInputID;

	/**
	 * cook output ids ?
	 */
	private boolean cookOutputIDs;

	/**
	 * fully connected ?
	 */
	private boolean fullyConnected;

	/**
	 * output biopax ?
	 */
	private boolean outputIDList;

	/**
	 * Inner class - Neighbor object - set of 
	 * these returned from getNeighbors query.
	 */
	public static class Neighbor {

		/**
		 * CPath Record name
		 */
		private String name;
		
		/**
		 * internal (cpath id)
		 */
		private long cpathID;

		/**
		 * external id - 
		 * requested external db id is parameter to get neighbor call
		 * - may be internal id
		 */
		private String externalID;

		/**
		 * Constructor.
		 *
		 * @param name String
		 * @param cpathID long
		 * @param externalID String
		 */
		public Neighbor(String name, long cpathID, String externalID) {

			// init members
			this.name = name;
			this.cpathID = cpathID;
			this.externalID = externalID;
		}

		/**
		 * Gets the record name (cpath record name).
		 *
		 * @return String
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the cpathRecord id.
		 *
		 * @return long
		 */
		public long getCPathID() {
			return cpathID;
		}

		/**
		 * Gets the external id.
		 *
		 * @return String
		 */
		public String getExternalID() {
			return externalID;
		}
	}

	/**
	 * Constructor.
	 *
	 * @param protocolRequest ProtocolRequest
	 * @param xdebug XDebug
     */
    public GetNeighborsCommand(ProtocolRequest protocolRequest, XDebug xdebug) {

		this(null, protocolRequest, xdebug);
    }

	/**
	 * Constructor.
	 *
	 * @param httpRequest HttpServletRequest
	 * @param protocolRequest ProtocolRequest
	 * @param xdebug XDebug
     */
    public GetNeighborsCommand(HttpServletRequest httpRequest, ProtocolRequest protocolRequest, XDebug xdebug) {

		// init members
        this.httpRequest = httpRequest;
        this.protocolRequest = protocolRequest;
        this.xdebug = xdebug;
		setFlags();
    }

    /**
     * Executes Query Sub Task.
     *
     * @return XmlAssembly Object.
	 * @throws DaoException, NumberFormatException
     */
    protected XmlAssembly executeSub() throws DaoException, NumberFormatException, AssemblyException {

		// get neighbors
		Set<Neighbor> neighbors = getNeighbors();

		// check size
		if (neighbors.size() == 0) {
			return null;
		}

		// convert string hash set to long[]
		int lc = -1; long[] neighborsLong = new long[neighbors.size()];
		for (Neighbor neighbor : neighbors) {
			neighborsLong[++lc] = neighbor.getCPathID();
		}

		// outta here
		return XmlAssemblyFactory.createXmlAssembly(neighborsLong,
													XmlRecordType.BIO_PAX,
													neighborsLong.length,
													XmlAssemblyFactory.XML_FULL,
													protocolRequest.getUseOptimizedCode(),
													xdebug);
    }

	/**
	 * A wrapper function to NeighborsUtil.getNeighbors(..)
	 *
	 * @return Set<Neighbor>
	 * @throws DaoException, NumberFormatException
	 */
	public Set<Neighbor> getNeighbors() throws DaoException, NumberFormatException {

		// create utility class
        NeighborsUtil util = new NeighborsUtil(xdebug);

		// get the physical entity id used in query
		long physicalEntityRecordID = util.getPhysicalEntityRecordID(protocolRequest, cookInputID);

		// get neighbors - easy!
		long neighborRecordIDs[] = util.getNeighbors(physicalEntityRecordID, fullyConnected);

		// filter by data sources
		neighborRecordIDs = util.filterByDataSource(protocolRequest, neighborRecordIDs);

		// outta here
		return getNeighborSet(neighborRecordIDs);
	}

	/**
	 * Given a set of neighbor objects, outputs a tab-delimeted summary of the data.
	 *
	 * @param neighbors Set<Neighbor>
	 * @return String
	 */
	public String outputTabDelimitedText(Set<Neighbor> neighbors) {

		// buffer to return
		StringBuffer toReturn = new StringBuffer();

		// check args
		if (neighbors.size() == 0) {
			return NO_NEIGHBORS_FOUND;
		}

		// header
		toReturn.append ("Record Name" + "\t" +
						 ExternalDatabaseConstants.INTERNAL_DATABASE + "\t" +
						 "Database:ID" + "\n");

		// body
		String minLongStr = String.valueOf(Long.MIN_VALUE);
		String outputIDTerm = protocolRequest.getOutputIDType();
		outputIDTerm = (outputIDTerm == null) ? ExternalDatabaseConstants.INTERNAL_DATABASE : outputIDTerm;
		for (Neighbor neighbor : neighbors) {
			String externalID = neighbor.getExternalID();
			externalID = (externalID.equals(minLongStr)) ? NO_MATCHING_EXTERNAL_ID_FOUND : externalID;
			String output = (neighbor.getName() + "\t" +
							 String.valueOf(neighbor.getCPathID()) + "\t" +
							 outputIDTerm + ":" + externalID + "\n");
			toReturn.append(output);
		}

		// outta here
		return toReturn.toString();
	}

	/**
	 * Method to set member bools base on
	 * relevant parameters from request object.
	 */
	private void setFlags() {

		// check args
		if (protocolRequest == null) return;

		// cook input id ?
		String inputIDType = protocolRequest.getInputIDType();
		cookInputID = (inputIDType != null &&
					   !inputIDType.equals(ExternalDatabaseConstants.INTERNAL_DATABASE));

		// fully connected ?
		String fullyConnectedStr = protocolRequest.getFullyConnected();
		fullyConnected = (fullyConnectedStr != null && fullyConnectedStr.equalsIgnoreCase("yes"));

		// output id list ?
		String output = protocolRequest.getOutput();
		outputIDList = (output != null && output.equals(ProtocolRequest.ID_LIST));

		// cook id on the way out ?
		String outputIDType = protocolRequest.getOutputIDType();
		cookOutputIDs = (outputIDType != null &&
						 !outputIDType.equals(ExternalDatabaseConstants.INTERNAL_DATABASE));
	}

	/**
	 * Generates set of neighbors.
	 *
	 * @param neighbors long[]
	 * @return Set<Neighbor>
	 * @throws DaoException
	 */
	private Set<Neighbor> getNeighborSet(long[] neighborRecordIDs) throws DaoException {

		// set to return
		Set<Neighbor> neighborSet = new HashSet<Neighbor>();

		// get refs to needed dao's.
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoExternalLink daoExternalLinker = DaoExternalLink.getInstance();

		// which external id do we want (if any)
		String outputIDTerm = protocolRequest.getOutputIDType();

		// iterate through all neighbor record ids
		for (long neighborRecordID : neighborRecordIDs) {
			// neighborRecordID as string
			String neighborRecordIDStr = String.valueOf(neighborRecordID);
			// get cpath record
			CPathRecord record = daoCPath.getRecordById(neighborRecordID);
			// get external id - default to internal id unless user requested something else
			String externalID = neighborRecordIDStr;
			if (cookOutputIDs) {
				// get external link records associated with this cpath id
				ArrayList<ExternalLinkRecord> externalLinkRecords =
					daoExternalLinker.getRecordsByCPathId(neighborRecordID);
				for (ExternalLinkRecord externalLinkRecord : externalLinkRecords) {
					String masterTerm = externalLinkRecord.getExternalDatabase().getMasterTerm();
					if (masterTerm.equals(outputIDTerm)) {
						externalID = externalLinkRecord.getLinkedToId();
						break;
					}
				}
				// made it here - we should be an external id,
				// but none was found
				if (externalID.equals(neighborRecordIDStr)) {
					externalID = NO_MATCHING_EXTERNAL_ID_FOUND;
				}
			}
			// create neighbor object & add to return set
			Neighbor neighbor = new Neighbor(record.getName(), record.getId(), externalID);
			neighborSet.add(neighbor);
		}

		// outta here
		return neighborSet;
	}
}
