// $Id: NeighborsUtil.java,v 1.2 2007-05-23 14:14:13 grossben Exp $
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

		// init these here to reduce arguments to getInteractionRecordIDs
		daoCPath = (daoCPath == null) ? DaoCPath.getInstance() : daoCPath;
		daoInternalLink = (daoInternalLink == null) ? new DaoInternalLink() : daoInternalLink;

		// get neighbors
		Set<Long> neighborRecordIDs = getNeighborRecordIDs(physicalEntityRecordID);
		//Set<Long> neighborRecordIDs = getInteractionParticipants(physicalEntityRecordID,
		//														 physicalEntityRecordID,
		//														 true);

		// convert Set<Long> into long[]
		int lc = -1;
		long[] toReturn = new long[neighborRecordIDs.size()];
		for (Long neighbor : neighborRecordIDs) {
			toReturn[++lc] = neighbor;
		}

		// outta here
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
	 * for more info, see http://cbiowiki.org/cgi-bin/moin.cgi/Network_Neighborhood
	 *
	 * @param physicalEntityRecordID long
	 * @return Set<Long> (interaction ids)
	 * @throws DaoException
	 */
	private Set<Long> getNeighborRecordIDs(long physicalEntityRecordID) throws DaoException {

		// init some vars
		Set<Long> returnRecordIDs = new HashSet<Long>();
		Set<Long> neighborRecordIDs = getInternalLinkIDs(physicalEntityRecordID, true);

		// interate over "neighbor" record id
		for (Long recordID : neighborRecordIDs) {

			// avoid infinite loop
			if (recordID == physicalEntityRecordID) continue;

			// get the cpath record
			CPathRecord cpathRecord = daoCPath.getRecordById(recordID);

			// add physical entity records - 
			if (cpathRecord.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
				returnRecordIDs.add(recordID);
			}
			// we also want to grab all participants of parent interaction
			else if (cpathRecord.getType().equals(CPathRecordType.INTERACTION)) {
				// add the interaction
				returnRecordIDs.add(recordID);
				// add the interaction participants
				returnRecordIDs.addAll(getInteractionParticipants(recordID, recordID, false));
				// if biochemical reaction, get our parents
				if (cpathRecord.getSpecificType().equals(BioPaxConstants.BIOCHEMICAL_REACTION)) {
					returnRecordIDs.addAll(getInteractionParticipants(recordID, recordID, true));
				}
			}
		}

		// outta here
		return returnRecordIDs;
	}

	private Set<Long> getInteractionParticipants(long startingInteractionRecordID,
												 long interactionRecordID,
												 boolean getParents) throws DaoException {

		// to return
		Set<Long> returnRecordIDs = new HashSet<Long>();
		
		// get participants
		Set<Long> participants = getInternalLinkIDs(interactionRecordID, getParents);

		// interate over physical entity link records
		for (Long recordID : participants) {

			// avoid infinite loop
			//if (recordID == startingInteractionRecordID) continue;

			// get the cpath record
			CPathRecord cpathRecord = daoCPath.getRecordById(recordID);

			// add physical entity records - 
			if (cpathRecord.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
				returnRecordIDs.add(recordID);
			}
			// if we have an interaction, get its participants
			else if (cpathRecord.getType().equals(CPathRecordType.INTERACTION) &&
					 recordID != startingInteractionRecordID) {
				// add the interaction
				returnRecordIDs.add(recordID);
				// add the interaction participants
				returnRecordIDs.addAll(getInteractionParticipants(startingInteractionRecordID, recordID, false));
				// if biochemical reaction, get our parents
				if (cpathRecord.getSpecificType().equals(BioPaxConstants.BIOCHEMICAL_REACTION)) {
					returnRecordIDs.addAll(getInteractionParticipants(startingInteractionRecordID, recordID, true));
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
	 * @param getParents boolean (if false, gets children)
	 * @return Set<Long>
	 * @throws DaoException
	 */
	private Set<Long> getInternalLinkIDs(long physicalEntityRecordID, boolean getParents) 
		throws DaoException {

		// set to return
		Set<Long> physicalEntityTargets = new HashSet<Long>();

		// get link records
		ArrayList<InternalLinkRecord> internalLinkRecords = (getParents) ?
			daoInternalLink.getSources(physicalEntityRecordID) :
			daoInternalLink.getTargets(physicalEntityRecordID);

		// extract ids
		for (InternalLinkRecord linkRecord : internalLinkRecords) {
			physicalEntityTargets.add((getParents) ?
									  linkRecord.getSourceId() : linkRecord.getTargetId());
		}

		// outta here
		return physicalEntityTargets;
	}
}