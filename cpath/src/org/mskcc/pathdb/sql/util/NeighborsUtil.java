// $Id: NeighborsUtil.java,v 1.1 2007-05-18 18:46:45 grossben Exp $
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
package org.mskcc.pathdb.sql.util;

// imports
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Utility class which supports nearest neighbors query.
 *
 * @author Benjamin Gross
 */
public class NeighborsUtil {

	/**
	 * ref to XDebug
	 */
    private XDebug xdebug;

	/**
	 * ref to DaoCPath
	 */
	private DaoCPath daoCPath;

	/**
	 * ref to DaoInternalLink
	 */
	private DaoInternalLink daoInternalLink;

	/**
	 * Constructor.
	 *
     * @param xdebug XDebug Object.
     */
    public NeighborsUtil(XDebug xdebug) {

		// init members
        this.xdebug = xdebug;

    }

	/**
	 * Given a physical entity, this method computes a list of neighbors.
	 * 
	 * When fully_connected is false, the physical entity and connections
	 * to its nearest neighbors are returned.
	 *
	 * When fully_connected is set, all connections between all physical
	 * entities are returned.
	 *
	 * @param physicalEntityID long
	 * @param fullyConnected boolean
	 * @return long[]
	 * @throws DaoException
	 */
	public long[] getNeighbors(long physicalEntityRecordID, boolean fullyConnected)
		throws DaoException {

		xdebug.logMsg(this, "entering NeighborsUtil.getNeighbors()");

		// init these here to reduce arguments to getInteractionRecordIDs
		daoCPath = (daoCPath == null) ? DaoCPath.getInstance() : daoCPath;
		daoInternalLink = (daoInternalLink == null) ? new DaoInternalLink() : daoInternalLink;

		// get this physical entities interaction records
		xdebug.logMsg(this, "getting interaction records for physical entity '" +
					  Long.toString(physicalEntityRecordID) + "'.");
		Set<Long> neighborRecordIDs = getInteractionRecordIDs(physicalEntityRecordID,
															  null,
															  physicalEntityRecordID,
															  fullyConnected,
															  true);
		neighborRecordIDs.addAll(getInteractionRecordIDs(physicalEntityRecordID,
														 null,
														 physicalEntityRecordID,
														 fullyConnected,
														 false));

		// convert Set<Long> into long[]
		int lc = -1;
		long[] toReturn = new long[neighborRecordIDs.size()];
		for (Long neighbor : neighborRecordIDs) {
			toReturn[++lc] = neighbor;
		}

		// outta here
		System.out.println("exiting NeighborsUtil.getNeighbors()");
		xdebug.logMsg(this, "exiting NeighborsUtil.getNeighbors()");
		return toReturn;
	}


	/**
	 * Returns list of cpath record ids that interact
	 * with the given physical entity (source or targets
	 * within the internal link table), following rules
	 * described below.	
	 *
	 * Rules:
	 *
	 * if A is part of a [complex] (A:B),
	 * (A:B) is included in the neighborhood,
	 * but none of the interactions involving (A:B) are included.
	 *
	 * if A is a [CONTROLLER] for a [control] interaction,
	 * the reaction that is [CONTROLLED] (and all the participants in that reaction)
	 * are included in the neighborhood.
	 *
	 * if A participates in a [conversion] reaction,
	 * and this reaction is [CONTROLLED] by another interaction,
	 * the [control] interaction (plus its [CONTROLLER]) are
	 * included in the neighborhood.
	 *
	 * fullyConnected parameter:
	 *
	 * when set all connections between all physical entities are returned,
	 * else the physical entity and connections to its nearest neighbors are returned.
	 *
	 * for more info, see http://cbiowiki.org/cgi-bin/moin.cgi/Network_Neighborhood
	 *
	 * @param startingPhysicalEntityRecordID: record id passed in from getNeighbors - prevent infinite loop
	 * @param physicalEntityRecordID long
	 * @param fullyConnected boolean 
	 * @param getSources boolean (if set, get sources, else get targets)
	 * @return Set<Long> (interaction ids)
	 * @throws DaoException
	 */
	private Set<Long> getInteractionRecordIDs(long startingPhysicalEntityRecordID,
											  Set<Long> startingPhysicalEntityTargets,
											  long physicalEntityRecordID,
											  boolean fullyConnected,
											  boolean getSources) throws DaoException {

		// init some vars
		Set<Long> returnRecordIDs = new HashSet<Long>();
		CPathRecord physicalEntityRecord = daoCPath.getRecordById(physicalEntityRecordID);
		Set<Long> physicalEntityLinkIDs = getInternalLinkIDs(physicalEntityRecordID, getSources);
		boolean firstLevel = (startingPhysicalEntityRecordID == physicalEntityRecordID);
		startingPhysicalEntityTargets = (startingPhysicalEntityTargets == null) ?
			getInternalLinkIDs(startingPhysicalEntityRecordID, false) : startingPhysicalEntityTargets;

		// interate over physical entity link records
		for (Long recordID : physicalEntityLinkIDs) {
			// if this is the starting record, skip it
			if (recordID == startingPhysicalEntityRecordID) continue;
			// add this record to return set if:
			// - we are at first level, in which case all records directly connect to physical entity are added
			// - we are fully connected, and this record connects to a record directly connected to physical entity
			// - we are a participant of a control or conversion interaction
			if (firstLevel ||
				(fullyConnected && startingPhysicalEntityTargets.contains(recordID)) ||
				(physicalEntityRecord.getType().equals(CPathRecordType.INTERACTION) &&
				 (physicalEntityRecord.getSpecificType().equals(BioPaxConstants.CONTROL) ||
				  physicalEntityRecord.getSpecificType().equals(BioPaxConstants.CONVERSION)))) {
				returnRecordIDs.add(recordID);
			}
			// get the cpath record
			CPathRecord cpathRecord = daoCPath.getRecordById(recordID);
			// we only want to get links to/from the starting physical entity
			if (firstLevel) {
				// we are only concerned with interactions
				if (cpathRecord.getType().equals(CPathRecordType.INTERACTION)) {
					// if we are a controller for a control interaction,
					// get participants of interaction
					if (cpathRecord.getSpecificType().equals(BioPaxConstants.CONTROL)) {
						returnRecordIDs.addAll(getInteractionRecordIDs(startingPhysicalEntityRecordID,
																	   startingPhysicalEntityTargets,
																	   recordID,
																	   fullyConnected, false));
					}
					// if we participate in a conversion,
					// get interaction that controls us
					if (cpathRecord.getSpecificType().equals(BioPaxConstants.CONVERSION)) {
						returnRecordIDs.addAll(getInteractionRecordIDs(startingPhysicalEntityRecordID,
																	   startingPhysicalEntityTargets,
																	   recordID,
																	   fullyConnected, false));
					}
				}
			}
		}

		// outta here
		return returnRecordIDs;
	}

	/**
	 * Given a cPath id, returns list of targets or sources.
	 *
	 * @param physicalEntityID long
	 * @return Set<Long>
	 * @throws DaoException
	 */
	private Set<Long> getInternalLinkIDs(long physicalEntityRecordID, boolean getSources) 
		throws DaoException {

		// set to return
		Set<Long> physicalEntityTargets = new HashSet<Long>();

		// get link records
		ArrayList<InternalLinkRecord> internalLinkRecords = (getSources) ?
			daoInternalLink.getSources(physicalEntityRecordID) :
			daoInternalLink.getTargets(physicalEntityRecordID);
		
		// extract ids
		for (InternalLinkRecord linkRecord : internalLinkRecords) {
			physicalEntityTargets.add((getSources) ?
									  linkRecord.getSourceId() : linkRecord.getTargetId());
		}

		// outta here
		return physicalEntityTargets;
	}
}