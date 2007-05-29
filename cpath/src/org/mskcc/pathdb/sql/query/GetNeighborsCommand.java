// $Id: GetNeighborsCommand.java,v 1.3 2007-05-29 12:45:55 grossben Exp $
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
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;

import java.util.Set;
import java.util.HashSet;
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
		long neighbors[] = getNeighbors();

		// outta here
		return XmlAssemblyFactory.createXmlAssembly(neighbors,
													XmlRecordType.BIO_PAX,
													neighbors.length,
													XmlAssemblyFactory.XML_FULL,
													protocolRequest.getUseOptimizedCode(),
													xdebug);
    }

	/**
	 * A wrapper function to NeighborsUtil.getNeighbors(..)
	 *
	 * @return long[]
	 * @throws DaoException, NumberFormatException
	 */
	public long[] getNeighbors() throws DaoException, NumberFormatException {

		// get the physical entity id used in query
		long physicalEntityRecordID = getPhysicalEntityRecordID();

		// get neighbors - easy!
        NeighborsUtil util = new NeighborsUtil(xdebug);
		long neighborRecordIDs[] = util.getNeighbors(physicalEntityRecordID, fullyConnected);

		// filter by data sources
		//neighborRecordIDs = filterByDataSource(neighborRecordIDs);

		// cook output ids
		neighborRecordIDs = (cookOutputIDs) ? cookOutputIDs(neighborRecordIDs) : neighborRecordIDs;

		// outta here
		return neighborRecordIDs;
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
	 */
	private long getPhysicalEntityRecordID() throws NumberFormatException {

		long physicalEntityRecordID;
		if (cookInputID) {
			// some cooking here
			physicalEntityRecordID = Long.parseLong(protocolRequest.getQuery());
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
	 * @throws DaoException
	 */
	private Set<String> getDataSourceFilters() throws DaoException {

		// list to return
		Set<String> dataSourceFilterSet = new HashSet();

		// get datasource from protocol request object
		String dataSource = protocolRequest.getDataSource();
		if (dataSource != null && dataSource.length() > 0) {
			dataSourceFilterSet.add(dataSource);
			return dataSourceFilterSet;
		}

		// get filter settings from global filter settings
		HttpSession session = httpRequest.getSession();
		GlobalFilterSettings filterSettings = (GlobalFilterSettings) session.getAttribute
			(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
		if (filterSettings == null) {
			filterSettings = new GlobalFilterSettings();
			session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS,
								 filterSettings);
			//return dataSourceFilterSet;
		}

		//  create list of external db master terms
		DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
		Set<Long> filterIDs = filterSettings.getSnapshotIdSet();
		for (Long id : filterIDs) {
			ExternalDatabaseSnapshotRecord record = daoSnapshot.getDatabaseSnapshot(id);
			dataSourceFilterSet.add(record.getExternalDatabase().getMasterTerm());
		}

		// outta here
		return dataSourceFilterSet;
	}

	/**
	 * Method to cook output ids.
	 *
	 * @param neighbors long[]
	 * @return long[]
	 */
	private long[] cookOutputIDs(long[] neighbors) {

		return neighbors;
	}
}
