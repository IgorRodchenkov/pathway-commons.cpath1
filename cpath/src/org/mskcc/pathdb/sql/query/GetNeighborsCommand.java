// $Id: GetNeighborsCommand.java,v 1.4 2007-06-12 16:56:18 grossben Exp $
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
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.util.NeighborsUtil;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;

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
		Set<String> neighborRecordIDs = getNeighbors();

		// convert string hash set to long[]
		int lc = -1;
		long[] neighborsLong = new long[neighborRecordIDs.size()];
		for (String neighborRecordID : neighborRecordIDs) {
			neighborsLong[++lc] = Long.parseLong(neighborRecordID);
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
	 * @return Set<String>
	 * @throws DaoException, NumberFormatException
	 */
	public Set<String> getNeighbors() throws DaoException, NumberFormatException {

		// get the physical entity id used in query
		long physicalEntityRecordID = getPhysicalEntityRecordID();

		// get neighbors - easy!
        NeighborsUtil util = new NeighborsUtil(xdebug);
		long neighborRecordIDs[] = util.getNeighbors(physicalEntityRecordID, fullyConnected);

		// filter by data sources
		neighborRecordIDs = filterByDataSource(neighborRecordIDs);

		// cook output ids
		Set<String> toReturn = null;
		if (cookOutputIDs) {
			toReturn = cookOutputIDs(neighborRecordIDs);
		}
		else {
			for (long neighborRecordID : neighborRecordIDs) {
				toReturn.add(String.valueOf(neighborRecordID));
			}
		}

		// outta here
		return toReturn;
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
					   !inputIDType.equals(ProtocolRequest.INTERNAL_ID));

		// fully connected ?
		String fullyConnectedStr = protocolRequest.getFullyConnected();
		fullyConnected = (fullyConnectedStr != null && fullyConnectedStr.equalsIgnoreCase("yes"));

		// output id list ?
		String output = protocolRequest.getOutput();
		outputIDList = (output != null && output.equals(ProtocolRequest.ID_LIST));

		// cook id on the way out ?
		String outputIDType = protocolRequest.getOutputIDType();
		cookOutputIDs = (outputIDType != null &&
						 !outputIDType.equals(ProtocolRequest.INTERNAL_ID));
	}

	/**
	 * Method used to get record id of
	 * physical entity used for get neighbors command.
	 *
	 * @return long
	 * @throws NumberFormatException
	 * @throws DaoException
	 */
	private long getPhysicalEntityRecordID() throws NumberFormatException, DaoException {

		long physicalEntityRecordID = -1;
		if (cookInputID) {
			//  get all cPath Records that match external ID
			DaoExternalDb daoExternalDb = new DaoExternalDb();
			ExternalDatabaseRecord dbRecord = daoExternalDb.getRecordByTerm(protocolRequest.getInputIDType());
			DaoExternalLink daoExternalLinker = DaoExternalLink.getInstance();
			ArrayList<ExternalLinkRecord> externalLinkRecords =
				(ArrayList<ExternalLinkRecord>)daoExternalLinker.getRecordByDbAndLinkedToId(dbRecord.getId(),
																							protocolRequest.getQuery());
			// each external ID could map to multiple physical entities. take first id in list
			for (ExternalLinkRecord externalLinkRecord : externalLinkRecords) {
				physicalEntityRecordID = externalLinkRecord.getCpathId();
				break;
			}
		}
		else {
			physicalEntityRecordID = Long.parseLong(protocolRequest.getQuery());
		}

		// outta here
		return physicalEntityRecordID;
	}

	/**
	 * Method to filter neighbor list by data source.
	 *
	 * @param neighborRecordIDs long[]
	 * @return long[]
	 * @throws DaoException
	 */
	private long[] filterByDataSource(long[] neighborRecordIDs) throws DaoException {

		// get datasource filter list
		Set<String> dataSourceFilterSet = getDataSourceFilters();

		// if we have a filter set (which we should),
		// interate through neighbors, remove neighbors with differing datasource
		Set<Long> filteredNeighborIDs = new HashSet<Long>();
		if (dataSourceFilterSet.size() > 0) {
			// create ref to dao cpath
			DaoCPath daoCPath = DaoCPath.getInstance();
			DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
			// interate over records
			for (long neighborRecordID : neighborRecordIDs) {
				// get the cpath record
				CPathRecord cpathRecord = daoCPath.getRecordById(neighborRecordID);
				ExternalDatabaseSnapshotRecord snapshotRecord = daoSnapshot.getDatabaseSnapshot(cpathRecord.getSnapshotId());
				if (snapshotRecord == null) continue;
				if (dataSourceFilterSet.contains(snapshotRecord.getExternalDatabase().getMasterTerm())) {
					filteredNeighborIDs.add(neighborRecordID);
				}
			}
		}
		else {
			return neighborRecordIDs;
		}

		// convert array list to long[]
		int lc = -1;
		long[] toReturn = new long[filteredNeighborIDs.size()];
		for (Long filterNeighborID : filteredNeighborIDs) {
			toReturn[++lc] = filterNeighborID;
		}

		// outta here
		return toReturn;
	}

	/**
	 * Method used to construct list of datasource filters
	 *
	 * @return Set<String>
	 */
	private Set<String> getDataSourceFilters() {

		// list to return
		Set<String> dataSourceFilterSet = new HashSet();

		// get datasource from protocol request object
		String[] dataSources = protocolRequest.getDataSources();
		if (dataSources != null) {
			for (String dataSource : dataSources) {
				dataSourceFilterSet.add(dataSource);
			}

		}

		// outta here
		return dataSourceFilterSet;
	}

	/**
	 * Method to cook output ids.
	 *
	 * @param neighbors long[]
	 * @return Set<String>
	 * @throws DaoException
	 */
	private Set<String> cookOutputIDs(long[] neighborRecordIDs) throws DaoException {

		// set to return
		Set<String> cookedNeighborIDs = new HashSet<String>();

		// get output id db term
		String outputIDTerm = protocolRequest.getOutputIDType();
		
		// interate through all neighbor record ids, filter by external database terms
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoExternalLink daoExternalLinker = DaoExternalLink.getInstance();
		for (long neighborRecordID : neighborRecordIDs) {
			// get external link records associated with this cpath id
			ArrayList<ExternalLinkRecord> externalLinkRecords =
				daoExternalLinker.getRecordsByCPathId(neighborRecordID);
			for (ExternalLinkRecord externalLinkRecord : externalLinkRecords) {
				String masterTerm = externalLinkRecord.getExternalDatabase().getMasterTerm();
				if (masterTerm.equals(outputIDTerm)) {
					cookedNeighborIDs.add(externalLinkRecord.getLinkedToId());
					break;
				}
			}
		}

		// outta here
		return cookedNeighborIDs;
	}
}
