// $Id: PhysicalEntitySetQuery.java,v 1.1 2006-11-03 14:54:01 grossb Exp $
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
package org.mskcc.pathdb.query;

// imports
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Utility class which supports physical entities set queries.
 *
 * @author Benjamin Gross
 */
public class PhysicalEntitySetQuery {

	// number of physical entities that must be in interactio
	// for the interaction to be considered part of the result set
	private static final int INTERACTIONS_QUERY_RESULT_THRESHOLD = 2;

	/**
	 * Inner class - encapsulates result set
	 * of getPhysicalEntitySetInteractions query.
	 */
	public static class PhysicalEntitySetInteractionsQueryResult {

		// interaction record id
		private long interactionRecordID;

		// ref to intersection set
		// (the set of physicial entities that participate in this interaction)
		private long[] intersectionSet;

		/**
		 * Constructor.
		 *
		 * @param interactionRecordID long
		 * @param intersectionSet long[] the set of physicial entities that participate in this interaction
		 */
		public PhysicalEntitySetInteractionsQueryResult(long interactionRecordID, long[] intersectionSet) {

			// init members
			this.interactionRecordID = interactionRecordID;
			this.intersectionSet = intersectionSet;
		}

		/**
		 * Gets the interaction record id.
		 *
		 * @return long
		 */
		public long getInteractionRecordID() {
			return interactionRecordID;
		}

		/**
		 * Gets the intersection set.
		 *
		 * @return long[]
		 */
		public long[] getIntersectionSet() {
			return intersectionSet;
		}
	}
	
	/**
	 * Returns a set of interactions involving at least two of 
	 * the physical entities in the given physical entities set.
	 *
	 * @param array of physical entities ids
	 * @return set of PhysicalEntitySetInteractionsQueryResult objects
	 * @throws DaoException
	 * @throws IOException
	 */
	public static Set<PhysicalEntitySetInteractionsQueryResult> getPhysicalEntitySetInteractions(long[] physicalEntities)
		throws DaoException, IOException {

		// init some vars
        DaoCPath daoCPath = DaoCPath.getInstance();
		DaoInternalLink daoInternalLink = new DaoInternalLink();
		Set<Long> physicalEntitiesAsLong = createPhysicalEntitySet(physicalEntities);
		Set<Long> allInteractions = new HashSet<Long>();
		Set<PhysicalEntitySetInteractionsQueryResult> interactionSet = new HashSet<PhysicalEntitySetInteractionsQueryResult>();

		// build a list union of all interactions across the physical entities set
		for (Long physicalEntityID : physicalEntitiesAsLong) {
			allInteractions.addAll(getInteractionRecords(daoCPath, daoInternalLink, physicalEntityID));
		}

		// for each interaction, determine interaction intersections
		for (Long interactionRecordID : allInteractions) {
			// get interaction participants (physical entity participants)
			Set<Long> interactionParticipants = getInteractionParticipants(daoCPath, daoInternalLink, interactionRecordID);
			// get interaction result and add to return set if necessary
			PhysicalEntitySetInteractionsQueryResult result =
				getInteractionResult(interactionRecordID, physicalEntitiesAsLong, interactionParticipants);
			if (result.getIntersectionSet().length >= INTERACTIONS_QUERY_RESULT_THRESHOLD) interactionSet.add(result);
		}

		// outta here
		return interactionSet;
	}
	
	/**
	 * Creates a Set<Long> given a long[].
	 *
	 * @param physicalEntities long[]
	 * @return Set<Long>
	 */
	private static Set<Long> createPhysicalEntitySet(long[] physicalEntities) {
		
		// set to return
		Set<Long> returnSet = new HashSet<Long>(physicalEntities.length);

		// interate through long[] and populate Set<Long>
		for (int lc = 0; lc < physicalEntities.length; lc++) {
			returnSet.add(physicalEntities[lc]);
		}

		// outta here
		return returnSet;
	}

	/**
	 * Returns list of interactions that given gene participates in.
	 *
	 * @param daoCPath DaoCPath
	 * @param daoInternalLink DaoInternalLink
	 * @param physicalEntityID Long
	 * @return Set<Long> (interaction ids)
	 * @throws DaoException
	 */
	private static Set<Long> getInteractionRecords(DaoCPath daoCPath, DaoInternalLink daoInternalLink, Long physicalEntityID)
		throws DaoException {

		// set off interaction ids to return
		Set<Long> interactions = new HashSet<Long>();

		// interate through all sources of given physical entity
		// if record is an interaction, add to return set
		ArrayList<InternalLinkRecord> internalLinkRecords = daoInternalLink.getSources(physicalEntityID);
		for (InternalLinkRecord linkRecord : internalLinkRecords) {
			long sourceID = linkRecord.getSourceId();
			CPathRecord cpathRecord = daoCPath.getRecordById(sourceID);
            if (cpathRecord.getType().equals(CPathRecordType.INTERACTION)) {
				interactions.add(sourceID);
			}
		}

		// outta here
		return interactions;
	}

	/**
	 * Returns list of interaction participants
	 *
	 * @param daoCPath DaoCPath
	 * @param daoInternalLink DaoInternalLink
	 * @param physicalEntityID Long
	 * @return Set<Long> (interaction ids)
	 * @throws DaoException
	 */
	private static Set<Long> getInteractionParticipants(DaoCPath daoCPath, DaoInternalLink daoInternalLink, Long interactionRecordID)
		throws DaoException {

		// set to return
		Set<Long> returnSet = new HashSet<Long>();

		// interate through all targets of given interaction
		// add record directly to return set if it is physical interaction
		// or recurse if it is interaction
		ArrayList<InternalLinkRecord> internalLinkRecords = daoInternalLink.getTargets(interactionRecordID);
		for (InternalLinkRecord linkRecord : internalLinkRecords) {
			long targetID = linkRecord.getTargetId();
			CPathRecord cpathRecord = daoCPath.getRecordById(targetID);
            if (cpathRecord.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
				returnSet.add(targetID);
			}
            else if (cpathRecord.getType().equals(CPathRecordType.INTERACTION)) {
				returnSet.addAll(getInteractionParticipants(daoCPath, daoInternalLink, targetID));
			}
		}
		
		// outta here
		return returnSet;
	}

	/*
	 * Determines the intersection of interaction participants and 
	 * physical entities set members for a given interaction.
	 *
	 * @param interactionRecordID long
	 * @param physicalEntities Set<Long>
	 * @param interactionParticipants Set<Long>
	 * @return GeneSetInteractionResult
	 */
	private static PhysicalEntitySetInteractionsQueryResult getInteractionResult(Long interactionRecordID,
																			 Set<Long> physicalEntities, Set<Long> interactionParticipants) {

		// intersection set that gets stuffed
		// into GeneSetInteractionResult
		Set<Long> intersectionSetAsLong = new HashSet<Long>();

		// interate through interaction participants
		// if participant is member of geneset, add it to intersectionSet
		for (Long interactionParticipant : interactionParticipants) {
			if (physicalEntities.contains(interactionParticipant)) {
				intersectionSetAsLong.add(interactionParticipant);
			}
		}

		// convert Set<Long> into long[] to stuff into GeneSetInteractionResult
		int lc = -1;
		long[] intersectionSet = new long[intersectionSetAsLong.size()];
		for (Long intersectionSetMember : intersectionSetAsLong) {
			intersectionSet[++lc] = intersectionSetMember.longValue();
		}

		// outta here
		return new PhysicalEntitySetQuery.PhysicalEntitySetInteractionsQueryResult(interactionRecordID, intersectionSet);
	}
}
