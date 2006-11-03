// $Id: PhysicalEntitySetQuery.java,v 1.3 2006-11-03 21:34:09 grossb Exp $
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

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;

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
	 * @param physicalEntityRecordIDs long[]
	 * @return Set<PhysicalEntitySetInteractionsQueryResult>
	 * @throws DaoException
	 */
	public static Set<PhysicalEntitySetInteractionsQueryResult> getPhysicalEntitySetInteractions(long[] physicalEntityRecordIDs)
		throws DaoException {

		// init some vars
        DaoCPath daoCPath = DaoCPath.getInstance();
		DaoInternalLink daoInternalLink = new DaoInternalLink();
		Set<Long> physicalEntityRecordIDsAsLong = createPhysicalEntitySet(physicalEntityRecordIDs);
		Set<Long> allInteractionRecordIDs = new HashSet<Long>();
		Set<PhysicalEntitySetInteractionsQueryResult> interactionsQueryResultSet = new HashSet<PhysicalEntitySetInteractionsQueryResult>();

		// build a list (union) of all interactions across the physical entities set
		for (Long physicalEntityRecordID : physicalEntityRecordIDsAsLong) {
			allInteractionRecordIDs.addAll(getCPathRecordIds(daoCPath, daoInternalLink, physicalEntityRecordID, CPathRecordType.INTERACTION));
		}

		// for each interaction, determine interaction intersections
		for (Long interactionRecordID : allInteractionRecordIDs) {
			// get interaction participants (physical entity participants)
			Set<Long> interactionParticipantRecordIDs = getInteractionParticipantRecordIDs(daoCPath, daoInternalLink,
																				   interactionRecordID, physicalEntityRecordIDsAsLong);
			// get interaction result and add to return set if necessary
			PhysicalEntitySetInteractionsQueryResult result =
				getInteractionResult(interactionRecordID, physicalEntityRecordIDsAsLong, interactionParticipantRecordIDs);
			if (result.getIntersectionSet().length >= INTERACTIONS_QUERY_RESULT_THRESHOLD) interactionsQueryResultSet.add(result);
		}

		// outta here
		return interactionsQueryResultSet;
	}

	/**
	 * Returns a set of pathway record ids (rank by physical entity membership).
	 *
     * @param physicalEntityRecordIDs long[]
	 * @return long[]
	 * @throws DaoException
	 */
	public static long[] getPhysicalEntitySetPathways(long[] physicalEntityRecordIDs) throws DaoException {

		// init some vars
        DaoCPath daoCPath = DaoCPath.getInstance();
		DaoInternalLink daoInternalLink = new DaoInternalLink();
		Set<Long> physicalEntityRecordIDsAsLong = createPhysicalEntitySet(physicalEntityRecordIDs);
		Set<Long> allPathwayRecordIDs = new HashSet<Long>();

		// build a list (union) of all pathways that the physical entities are members
		for (Long physicalEntityRecordID : physicalEntityRecordIDsAsLong) {
			allPathwayRecordIDs.addAll(getCPathRecordIds(daoCPath, daoInternalLink,
														 physicalEntityRecordID, CPathRecordType.PATHWAY));
		}

		// for each pathway, we have to determine number of physical entity participant it contains
		Map <Long,Long> pathwayMembershipRecordIDsMap = new TreeMap<Long,Long>();
		for (Long pathwayRecordID : allPathwayRecordIDs) {
			Set<Long> pathwayMembershipRecordIDs = getPathwayMembershipRecordIDs(daoInternalLink, pathwayRecordID,
																				 physicalEntityRecordIDsAsLong);
			// store the pathway record id and number of physical entity members into map
			pathwayMembershipRecordIDsMap.put(pathwayRecordID, new Long(pathwayMembershipRecordIDs.size()));
		}

		// outta here
		return rankPathwayMembershipRecordIDs(pathwayMembershipRecordIDsMap);
	}
	
	/**
	 * Creates a Set<Long> given a long[].
	 *
	 * @param physicalEntityRecordIDs long[]
	 * @return Set<Long>
	 */
	private static Set<Long> createPhysicalEntitySet(long[] physicalEntityRecordIDs) {
		
		// set to return
		Set<Long> returnSet = new HashSet<Long>(physicalEntityRecordIDs.length);

		// interate through long[] and populate Set<Long>
		for (int lc = 0; lc < physicalEntityRecordIDs.length; lc++) {
			returnSet.add(physicalEntityRecordIDs[lc]);
		}

		// outta here
		return returnSet;
	}

	/**
	 * Returns list of cpath record ids that point to the given physical entity.
	 *
	 * @param daoCPath DaoCPath
	 * @param daoInternalLink DaoInternalLink
	 * @param physicalEntityRecordID Long
	 * @param recordType CPathRecordType
	 * @return Set<Long> (interaction ids)
	 * @throws DaoException
	 */
	private static Set<Long> getCPathRecordIds(DaoCPath daoCPath, DaoInternalLink daoInternalLink,
											   Long physicalEntityRecordID, CPathRecordType recordType)
		throws DaoException {

		// set of cpath record ids to return
		Set<Long> recordIds = new HashSet<Long>();

		// interate through all sources of given physical entity
		// if record is an interaction, add to return set
		ArrayList<InternalLinkRecord> internalLinkRecords = daoInternalLink.getSources(physicalEntityRecordID);
		for (InternalLinkRecord linkRecord : internalLinkRecords) {
			long sourceID = linkRecord.getSourceId();
			CPathRecord cpathRecord = daoCPath.getRecordById(sourceID);
            if (cpathRecord.getType().equals(recordType)) {
				recordIds.add(sourceID);
			}
		}

		// outta here
		return recordIds;
	}

	/**
	 * Returns list of interaction participants
	 * Note: participants must be a member of given physical entity set.
	 *
	 * @param daoCPath DaoCPath
	 * @param daoInternalLink DaoInternalLink
	 * @param interactionRecordID Long
	 * @param physicalEntityRecordIDs Set<Long>
	 * @return Set<Long> (interaction ids)
	 * @throws DaoException
	 */
	private static Set<Long> getInteractionParticipantRecordIDs(DaoCPath daoCPath, DaoInternalLink daoInternalLink,
														Long interactionRecordID, Set<Long> physicalEntityRecordIDs) throws DaoException {

		// set to return
		Set<Long> returnSet = new HashSet<Long>();

		// interate through all targets of given interaction
		// add record directly to return set if it is physical interaction 
		// ontained in physicalEntityRecordIds or recurse if it is interaction
		ArrayList<InternalLinkRecord> internalLinkRecords = daoInternalLink.getTargets(interactionRecordID);
		for (InternalLinkRecord linkRecord : internalLinkRecords) {
			long targetID = linkRecord.getTargetId();
			CPathRecord cpathRecord = daoCPath.getRecordById(targetID);
            if (physicalEntityRecordIDs.contains(targetID)) {
				returnSet.add(targetID);
			}
            else if (cpathRecord.getType().equals(CPathRecordType.INTERACTION)) {
				returnSet.addAll(getInteractionParticipantRecordIDs(daoCPath, daoInternalLink,
																	targetID, physicalEntityRecordIDs));
			}
		}
		
		// outta here
		return returnSet;
	}

	/**
	 * Returns list of pathway members.
	 * Note: pathway members must be a member of given physical entity set.
	 *
	 * @param daoInternalLink DaoInternalLink
	 * @param pathwayRecordID Long
	 * @param physicalEntityRecordIDs Set<Long>
	 * @return Set<Long> (interaction ids)
	 * @throws DaoException
	 */
	private static Set<Long> getPathwayMembershipRecordIDs(DaoInternalLink daoInternalLink,
														   Long pathwayRecordID, Set<Long> physicalEntityRecordIDs) throws DaoException {

		// set to return
		Set<Long> returnSet = new HashSet<Long>();

		// interate through all targets of given pathway
		// add record directly to return set if it is physical entity
		// contained in physicalEntityRecordIds
		ArrayList<InternalLinkRecord> internalLinkRecords = daoInternalLink.getTargets(pathwayRecordID);
		for (InternalLinkRecord linkRecord : internalLinkRecords) {
			long targetID = linkRecord.getTargetId();
            if (physicalEntityRecordIDs.contains(targetID)) {
				returnSet.add(targetID);
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
	 * @param physicalEntityRecordIDs Set<Long>
	 * @param interactionParticipantRecordIDs Set<Long>
	 * @return GeneSetInteractionResult
	 */
	private static PhysicalEntitySetInteractionsQueryResult getInteractionResult(Long interactionRecordID, Set<Long> physicalEntityRecordIDs,
																				 Set<Long> interactionParticipantRecordIDs) {

		// intersection set that gets stuffed
		// into GeneSetInteractionResult
		Set<Long> intersectionSetAsLong = new HashSet<Long>();

		// interate through interaction participants
		// if participant is member of geneset, add it to intersectionSet
		for (Long interactionParticipantRecordID : interactionParticipantRecordIDs) {
			if (physicalEntityRecordIDs.contains(interactionParticipantRecordID)) {
				intersectionSetAsLong.add(interactionParticipantRecordID);
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

	/*
	 * Returns Set of pathway record id's ranked by physical entity membership.
	 *
	 * @param pathwayMembershipRecordIDsMap Map<Long,Long>
	 * @return long[]
	 */
	private static long[] rankPathwayMembershipRecordIDs(Map<Long,Long> pathwayMembershipRecordIDsMap) {

		// init some vars
		int lc = -1;
		long[] rankedPathwayRecordIDs = new long[pathwayMembershipRecordIDsMap.size()];

		// define our own TreeSet<Long> given it comparator that sorts (descending) by map value
        TreeSet<Map.Entry> mapEntries = new TreeSet(new Comparator() {
            public int compare(Object obj1, Object obj2) {
                return ((Comparable) ((Map.Entry) obj2).getValue()).compareTo(((Map.Entry) obj1).getValue());
            }
        });
        
		// populate our TreeSet with the map entries (will be sorted using our compare method)
        mapEntries.addAll(pathwayMembershipRecordIDsMap.entrySet());

		// interate over the TreeSet and create our long[] to return
		for (Iterator i = mapEntries.iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
			Long key = (Long)entry.getKey();
			rankedPathwayRecordIDs[++lc] = key.longValue();
        }

		// outta here
		return rankedPathwayRecordIDs;
	}
}