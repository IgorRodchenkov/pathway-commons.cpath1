// $Id: GetNeighborsCommand.java,v 1.2 2007-05-21 12:30:08 grossben Exp $
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
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.util.NeighborsUtil;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.protocol.ProtocolRequest;

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
	 * ref to protocol request
	 */
    private ProtocolRequest request;

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
	 * filter by data source ?
	 */
	private boolean filterByDataSource;

	/**
	 * Constructor.
	 *
	 * @param request ProtocolRequest
	 * @param xdebug XDebug
     */
    public GetNeighborsCommand(ProtocolRequest request, XDebug xdebug) {

		// init members
        this.request = request;
        this.xdebug = xdebug;
		setFlags(request);
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
													request.getUseOptimizedCode(),
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
		long neighbors[] = util.getNeighbors(physicalEntityRecordID, fullyConnected);

		// filter by data sources
		neighbors = (filterByDataSource) ? filterByDataSource(neighbors) : neighbors;

		// cook output ids
		neighbors = (cookOutputIDs) ? cookOutputIDs(neighbors) : neighbors;

		// outta here
		return neighbors;
	}

	/**
	 * Method to set member bools base on
	 * relevant parameters from request object.
	 *
	 * @param request ProtocolRequest
	 */
	private void setFlags(ProtocolRequest request) {

		// check args
		if (request == null) return;

		// cook input id ?
		String inputIDType = request.getInputIDType();
		cookInputID = (inputIDType != null &&
					   !inputIDType.equals(ProtocolRequest.INTERNAL_ID));

		// fully connected ?
		String fullyConnectedStr = request.getFullyConnected();
		fullyConnected = (fullyConnectedStr != null && fullyConnectedStr.equalsIgnoreCase("yes"));

		// output id list ?
		String output = request.getOutput();
		outputIDList = (output != null && output.equals(ProtocolRequest.ID_LIST));

		// cook id on the way out ?
		String outputIDType = request.getOutputIDType();
		cookOutputIDs = (outputIDType != null &&
						 !outputIDType.equals(ProtocolRequest.INTERNAL_ID));

		// data source
		String dataSource = request.getDataSource();
		filterByDataSource = (dataSource != null && dataSource.length() > 0);
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
			physicalEntityRecordID = Long.parseLong(request.getQuery());
		}
		else {
			physicalEntityRecordID = Long.parseLong(request.getQuery());
		}

		// outta here
		return physicalEntityRecordID;
	}

	/**
	 * Method to filter neighbor list by data source.
	 *
	 * @param neighbors long[]
	 * @return long[]
	 */
	private long[] filterByDataSource(long[] neighbors) {

		// get datasource
		String dataSource = request.getDataSource();

		// interate through neighbors, remove neighbors with differing datasource

		// outta here
		return neighbors;
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
